package sample.hello.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.dsl.Creators;
import sample.hello.Greeter;

import java.util.ArrayList;
import java.util.List;

public class Mapper extends AbstractActor {
    private final List<ActorRef> reducers;

    static public Props props(List<ActorRef> reducers) {
        return Props.create(Mapper.class, new Mapper(reducers));
    }

    public Mapper(List<ActorRef> actorRefs) {
        this.reducers = actorRefs;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::separate)
                .matchEquals(Greeter.Msg.DONE, m ->
                        getContext().stop(self())
                )
                .matchAny(System.out::println)
                .build();
    }

    private void separate(String line)
    {
        String words [] = line.split("\\s+");
        for (String word: words)
        {
            int index = word.hashCode() % 4;
            reducers.get(index).tell(word, ActorRef.noSender());
        }
    }
}
