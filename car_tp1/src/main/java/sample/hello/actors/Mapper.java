package sample.hello.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.List;

public class Mapper extends AbstractActor {
    private final List<ActorRef> reducers;

    static public Props props(List<ActorRef> reducers) {
        return Props.create(Mapper.class, () -> new Mapper(reducers));
    }

    public Mapper(List<ActorRef> actorRefs) {
        this.reducers = actorRefs;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::separate)
                .matchEquals(Reducer.msg.DISPLAY, m -> this.showDictionnary())
                .build();
    }

    private void separate(String line) {
        String words[] = line.split("\\s+");
        for (String word : words) {
            int index = Math.abs(word.hashCode() % 4);
            reducers.get(index).tell(word, ActorRef.noSender());
        }
    }

    private void showDictionnary() {
        reducers.forEach(r -> {
            r.tell(Reducer.msg.DISPLAY, ActorRef.noSender());
        });
    }
}
