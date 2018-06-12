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

    private ReadFilesService readFilesService = new ReadFilesService();
    public void openFile()
    {
        Path path = Paths.get("D:\\Download\\bible.txt");
        try
        {
            Stream<String> lines = readFilesService.readFileLine(path);
            lines.forEach(this::sendToMappers);
        }
        catch (IOException io)
        {
            io.printStackTrace();
            System.err.println("File not reachable or readable");
        }
    }

    private void sendToMappers(String toSend)
    {
        master.tell(toSend, self());
    }


    public void start()
    {

    }
}
