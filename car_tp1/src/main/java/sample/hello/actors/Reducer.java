package sample.hello.actors;

import akka.actor.AbstractActor;
import sample.hello.Greeter;

import java.util.Map;

public class Reducer extends AbstractActor {
    Map<String, Integer> dictionnary;
    @Override
    public Receive createReceive()
    {
        return receiveBuilder()
                .match(String.class, this::putInMap)
                .build();
    }

    public void putInMap(String str)
    {
        if (!dictionnary.containsKey(str))
            dictionnary.put(str, 1);
        else
            dictionnary.put(str, dictionnary.get(str) + 1);
    }
}
