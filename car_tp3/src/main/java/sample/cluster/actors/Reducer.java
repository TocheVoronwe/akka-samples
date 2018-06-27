package sample.cluster.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import sample.cluster.transformation.App;

import java.util.HashMap;
import java.util.Map;

public class Reducer extends AbstractActor {
    public static enum msg {
        DISPLAY, END, REGISTER
    }

    Cluster cluster = Cluster.get(getContext().system());

    Map<String, Integer> dictionnary = new HashMap<>();

    static public Props props() {
        return Props.create(Reducer.class, Reducer::new);
    }

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
                .matchEquals(msg.DISPLAY, m -> this.showDictionnary())
                .match(String.class, this::putInMap)
                .match(ClusterEvent.MemberUp.class, mUp -> {
                    System.err.println("Member up");
                    register(mUp.member());
                })
                .build();
    }

    private void putInMap(String str) {
        System.out.println("putting " + str + " in reducer");
        if (!dictionnary.containsKey(str))
            dictionnary.put(str, 1);
        else
            dictionnary.put(str, dictionnary.get(str) + 1);
    }

    private void showDictionnary() {
        System.out.println("displaying dictionnary in reducer");
        dictionnary.forEach((word, count) -> {
            System.out.println(getSelf() + " : " + word + " : " + count);
        });
    }

    void register(Member member) {
        System.out.println("reducer - role : " + member.getRoles());
        if (member.hasRole("mapper"))
            for (int i = 0; i < App.NB_MAPPERS; i++)
            getContext().actorSelection(member.address() + "/user/mapper_" + i).tell(
                    Reducer.msg.REGISTER, self());
    }
}
