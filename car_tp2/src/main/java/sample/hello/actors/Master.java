package sample.hello.actors;

import akka.actor.*;
import akka.actor.dsl.Creators;
import sample.hello.service.ReadFilesService;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Master extends AbstractActor{
    int i = 0;
    private final String path;
    private Receive active;
    private ReadFilesService readFilesService = new ReadFilesService();
    private final List<ActorRef> mappers;

    static public Props props(List<ActorRef> mappers, String path){
       return Props.create(Master.class, () -> new Master(mappers, path));
    }

    public Master(List<ActorRef> mappers, String path)
    {
        this.mappers = mappers;
        this.active = receiveBuilder()
                .match(String.class,
                        this::openFile)
                .build();
        this.path = path;
        sendIdentifyRequest();
    }


    private void sendIdentifyRequest() {
        getContext().actorSelection(path).tell(new Identify(path), self());
        getContext()
                .system()
                .scheduler()
                .scheduleOnce(Duration.create(3, SECONDS), self(),
                        ReceiveTimeout.getInstance(), getContext().dispatcher(), self());
    }

    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .match(ActorIdentity.class, identity -> {
                    ActorRef a = identity.getRef();
                    if (a == null) {
                        System.out.println("Remote actor not available: " + path);
                    } else {
                        mappers.forEach(m -> getContext().watch(m));
                        getContext().become(active, true);
                    }
                })
                .match(ReceiveTimeout.class, x -> {
                    sendIdentifyRequest();
                })
                .build();
    }

    private void openFile(String p)
    {
        Path path = Paths.get(p);
        try
        {
            Stream<String> lines = readFilesService.readFileLine(path);
            lines.forEach(this::sendToMappers);
            mappers.get(0).tell(Reducer.msg.DISPLAY, ActorRef.noSender());
            System.out.println("THE END");
           // System.exit(1);
        }
        catch (IOException io)
        {
            io.printStackTrace();
            System.err.println("File not reachable or readable");
            System.exit(-1);
        }
    }

    private void sendToMappers(String str)
    {
        mappers.get(i%3).tell(str, ActorRef.noSender());
        ++i;

    }

    @Override
    public void preStart()
    {

    }
}
