package sample.hello.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.HashMap;
import java.util.Map;

public class Reducer extends AbstractActor {
    public static enum msg {
        DISPLAY, END
    }

    Map<String, Integer> dictionnary = new HashMap<>();

    static public Props props() {
        return Props.create(Reducer.class, Reducer::new);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::putInMap)
                .matchEquals(msg.DISPLAY, m -> this.showDictionnary())
                .build();
    }

    private void putInMap(String str) {
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
}
