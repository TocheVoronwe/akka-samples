package sample.hello.actors;

import akka.actor.*;
import akka.actor.dsl.Creators;
import sample.hello.App;
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
    private Receive active;
    private ReadFilesService readFilesService = new ReadFilesService();

    static public Props props(){
       return Props.create(Master.class, () -> new Master());
    }

    private Master()
    {
    }


    /*private void sendIdentifyRequest() {
        getContext().actorSelection(App.MAPPER_PATH).tell(new Identify(path), self());
        getContext()
                .system()
                .scheduler()
                .scheduleOnce(Duration.create(3, SECONDS), self(),
                        ReceiveTimeout.getInstance(), getContext().dispatcher(), self());
    }*/

    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .matchEquals(App.EOF, l -> sendEOF())
                .match(String.class, this::openFile).build();
    }

    private void sendEOF()
    {
        for (int m = 0; m < App.NB_MAPPERS; m++)
            getContext().actorSelection(App.MAPPER_PATH + "/user/mapper_" + i % App.NB_MAPPERS).tell(App.EOF, getSelf());
    }

    private void openFile(String p)
    {
        Path path = Paths.get(p);
        try
        {
            Stream<String> lines = readFilesService.readFileLine(path);
            lines.forEach(this::sendToMappers);
            //mappers.get(0).tell(Reducer.msg.DISPLAY, ActorRef.noSender());
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
        getContext().actorSelection(App.MAPPER_PATH + "/user/mapper_" + i % App.NB_MAPPERS).tell(str, getSelf());
        ++i;
    }
}
