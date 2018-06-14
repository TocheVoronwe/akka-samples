package sample.hello;

import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;

public class StartMappers {
    public static void main(String[] args)
    {
        final ActorSystem system = ActorSystem.create("MapperSystem", ConfigFactory.load("mapper"));
    }
}
