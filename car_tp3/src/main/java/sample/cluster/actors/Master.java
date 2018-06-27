package sample.cluster.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.Member;
import sample.cluster.service.ReadFilesService;
import sample.cluster.transformation.App;
import sample.cluster.transformation.TransformationMessages;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Master extends AbstractActor {
    int i = 0;
    List<ActorRef> mappers = new ArrayList<>();
    private ReadFilesService readFilesService = new ReadFilesService();

    static public Props props() {
        return Props.create(Master.class, Master::new);
    }

    private Master() {
    }

    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .matchEquals(Reducer.msg.DISPLAY, l -> sendEOF())
                .matchEquals(Reducer.msg.REGISTER, msg -> {
                    getContext().watch(sender());
                    mappers.add(sender());
                    sendFileToMappers();
                    System.err.println("Mapper Logged");
                })
                .match(String.class, this::openFile).build();
    }

    private void sendEOF() {
        mappers.get(i % App.NB_MAPPERS).forward(Reducer.msg.DISPLAY, getContext());
    }

    private void openFile(String p) {
        System.out.println("//////////SENDING FILE/////////////");
        Path path = Paths.get(p);
        try {
            Stream<String> lines = readFilesService.readFileLine(path);
            lines.forEach(this::sendToMappers);
            sendEOF();
        } catch (IOException io) {
            io.printStackTrace();
            System.err.println("File not reachable or readable");
            System.exit(-1);
        }
    }

    private void sendFileToMappers()
    {
        if (mappers.size() == App.NB_MAPPERS)
            openFile("src\\main\\resources\\text.txt");
    }

    private void register(Member member)
    {
        if (member.hasRole("mapper"))
            getContext().actorSelection(member.address() + "/user/mapper").tell("", self());
    }

    private void sendToMappers(String str) {
        System.out.println("sending " + str + " to mapper_" + i % App.NB_MAPPERS);
        mappers.get(i % App.NB_MAPPERS).forward(str, getContext());
        ++i;
    }
}
