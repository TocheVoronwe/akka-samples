package sample.hello.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import sample.hello.actors.Master;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static akka.actor.TypedActor.self;

public class SendToMasters {
    ActorSystem system = ActorSystem.create("Hello");
    ActorRef master = system.actorOf(Props.create(Master.class), "master");



    private void sendToMappers(String toSend)
    {
        master.tell(toSend, self());
    }


    public void start()
    {

    }
}
