package sample.hello.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.dsl.Creators;
import sample.hello.service.ReadFilesService;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class Master extends AbstractActor{
    int i = 0;
    private ReadFilesService readFilesService = new ReadFilesService();
    private final List<ActorRef> mappers;

    static public Props props(List<ActorRef> mappers){
       return Props.create(Master.class, () -> new Master(mappers));
    }

    public Master(List<ActorRef> mappers)
    {
        this.mappers = mappers;
    }

    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .match(String.class,
                    this::openFile)
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
