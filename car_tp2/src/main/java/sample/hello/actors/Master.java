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
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Master extends AbstractActor{
    int i = 0;
    private ReadFilesService readFilesService = new ReadFilesService();

    static public Props props(){
       return Props.create(Master.class, Master::new);
    }

    private Master()
    {
    }

    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .matchEquals(Reducer.msg.DISPLAY, l -> sendEOF())
                .match(String.class, this::openFile).build();
    }

    private void sendEOF()
    {
        System.out.println("DISPLAY");
        for (int m = 0; m < App.NB_MAPPERS; m++)
            getContext().actorSelection(App.REDUCERS_PATH + "/user/reducer_" + m % App.NB_MAPPERS).tell(Reducer.msg.DISPLAY, getSelf());
    }

    private void openFile(String p)
    {
        Path path = Paths.get(p);
        try
        {
            Stream<String> lines = readFilesService.readFileLine(path);
            lines.forEach(this::sendToMappers);
            TimeUnit.SECONDS.sleep(1);
        }
        catch (IOException io)
        {
            io.printStackTrace();
            System.err.println("File not reachable or readable");
            System.exit(-1);
        }
        catch (InterruptedException ie)
        {
            ie.printStackTrace();
        }
        sendEOF();
    }

    private void sendToMappers(String str)
    {
        getContext().actorSelection(App.MAPPER_PATH + "/user/mapper_" + i % App.NB_MAPPERS).tell(str, getSelf());
        ++i;
    }
}
