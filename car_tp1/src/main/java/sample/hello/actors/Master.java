package sample.hello.actors;

import akka.actor.*;
import sample.hello.Greeter;
import sample.hello.service.ReadFilesService;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Master extends AbstractActor{
    private ReadFilesService readFilesService = new ReadFilesService();
    private final ActorRef mapper = getContext().actorOf(Props.create(Mapper.class), "mapper");

    @Override
    public Receive createReceive() {

        return receiveBuilder().match(Greeter.Msg.DONE, m->
        {
           sender().tell(Greeter.Msg.DONE, self());
        })
                .matchAny(this::sendToMappers)
                .build();
    }

    private void sendToMappers(Object str)
    {

    }

    @Override
    public void preStart()
    {
        final ActorRef mapper = getContext().actorOf(Props.create(Mapper.class), "mapper");
        mapper.tell(Greeter.Msg.GREET, self());
    }
}
