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
                .matchAny(System.out::println)
                .build();
    }

    private void separate(String line) {
        String words[] = line.split("\\s+");
        for (String word : words) {
            int index = Math.abs(word.hashCode() % 4);
            System.out.println(index);
            reducers.get(index).tell(word, ActorRef.noSender());
        }
    }
}
