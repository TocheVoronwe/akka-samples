package sample.hello.actors;

import akka.actor.AbstractActor;
import sample.hello.Greeter;

public class Mapper extends AbstractActor {
    @Override
    public Receive createReceive() {
        System.out.println("PLLOOOOOOOP");
        return receiveBuilder()
                .matchEquals(Greeter.Msg.DONE, m -> {
                    getContext().stop(self());
                })
                .matchAny(str -> {
                    System.err.println(str);
                })
                .build();
    }
}
