package sample.cluster.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import sample.cluster.transformation.App;

import java.util.List;

import static sample.cluster.transformation.TransformationMessages.BACKEND_REGISTRATION;

public class Mapper extends AbstractActor {

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
                    register(mUp.member());
                })
                .build();
    }

    private void sendEOF()
    {
        System.out.println("Mapper : display");
        for (int m = 0; m < App.NB_REDUCERS; m++)
            getReducer(m).tell(Reducer.msg.DISPLAY, getSelf());
    }

    private ActorSelection getReducer(int i)
    {
        return getContext().actorSelection(App.REDUCERS_PATH + "/user/reducer_" + i);
    }

    private void separate(String line) {
        String words[] = line.split("\\s+");
        for (String word : words) {
            int index = Math.abs(word.hashCode() % App.NB_REDUCERS);
            ActorSelection reducer = getReducer(index);
            reducer.tell(word, getSelf());
        }
    }

    void register(Member member) {
        if (member.hasRole("master"))
            getContext().actorSelection(member.address() + "/user/master").tell(
                    BACKEND_REGISTRATION, self());
    }
}
