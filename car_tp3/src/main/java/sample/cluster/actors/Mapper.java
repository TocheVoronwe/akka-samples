package sample.cluster.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.remote.SystemMessageFormats;
import sample.cluster.transformation.App;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Mapper extends AbstractActor {
    List<ActorRef> reducers = new ArrayList<>();
    static public Props props() {
        return Props.create(Mapper.class, Mapper::new);
    }

    public Mapper() {
        System.out.println("Created " + getSelf());
    }


    Cluster cluster = Cluster.get(getContext().system());

    //subscribe to cluster changes, MemberUp
    @Override
    public void preStart() {
        cluster.subscribe(self(), ClusterEvent.MemberUp.class);
    }

    //re-subscribe when restart
    @Override
    public void postStop() {
        cluster.unsubscribe(self());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::separate)
                .matchEquals(Reducer.msg.DISPLAY, m -> this.sendEOF())
                .match(ClusterEvent.MemberUp.class, mUp -> {
                    System.err.println("Mapper : Member up");
                    register(mUp.member());
                })
                .matchEquals(Reducer.msg.REGISTER, msg -> {
                    System.out.println("REGISTERING " + sender());
                    getContext().watch(sender());
                    reducers.add(sender());
                    System.err.println("Reducer Logged");
                })
                .build();
    }

    private void sendEOF()
    {
        System.out.println("Mapper : display");
        for (int m = 0; m < App.NB_REDUCERS; m++)
            getReducer(m).forward(Reducer.msg.DISPLAY, getContext());
    }

    private ActorRef getReducer(int i)
    {
        //return getContext().actorSelection(App.REDUCERS_PATH + "/user/reducer_" + i);
        System.out.println("REDUCERSIZE ====== " + reducers.size());
        return reducers.get(i);
    }

    private void separate(String line) {
        System.out.println(self() + " receive " + line);
        String words[] = line.split("\\s+");
        for (String word : words) {
            int index = Math.abs(word.hashCode() % App.NB_REDUCERS);
            ActorRef reducer = getReducer(index);
            System.out.println("sending to reducer " + reducer);
            reducer.forward(word, getContext());
        }
    }

    void register(Member member) {
        System.out.println("mapper - role : " + member.getRoles());
        if (member.hasRole("master"))
            getContext().actorSelection(member.address() + "/user/master").tell(
                    Reducer.msg.REGISTER, self());
    }
}
