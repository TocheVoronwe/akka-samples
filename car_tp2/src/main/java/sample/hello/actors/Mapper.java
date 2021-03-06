package sample.hello.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import sample.hello.App;

import java.util.List;

public class Mapper extends AbstractActor {

    static public Props props() {
        return Props.create(Mapper.class, Mapper::new);
    }

    public Mapper() {
        System.out.println("Created " + getSelf());
    }

    private void sendEOF()
    {
        System.out.println("Mapper : display");
        for (int m = 0; m < App.NB_REDUCERS; m++)
            getReducer(m).tell(Reducer.msg.DISPLAY, getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::separate)
                .matchEquals(Reducer.msg.DISPLAY, m -> this.sendEOF())
                .build();
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
}
