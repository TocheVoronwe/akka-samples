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
    int lineNb = 0;
    ActorSystem system = ActorSystem.create("Hello");
    ActorRef master = system.actorOf(Props.create(Master.class), "master_0");
    ActorRef master2 = system.actorOf(Props.create(Master.class), "master_1");
    ActorRef master3 = system.actorOf(Props.create(Master.class), "master_2");

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
        switch (lineNb % 3) {
            case 0:
            master.tell(toSend, self());
            break;
            case 1:
            master2.tell(toSend, self());
            break;
            case 2:
            master3.tell(toSend, self());
            break;
        }
        ++lineNb;
    }


    public void start()
    {

    }
}
