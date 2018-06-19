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

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::separate)
                /*.matchEquals(Reducer.msg.DISPLAY, m -> this.showDictionnary())*/
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

/*    private void showDictionnary() {
        reducers.forEach(r -> {
            r.tell(Reducer.msg.DISPLAY, ActorRef.noSender());
        });
    }*/
}
