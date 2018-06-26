package sample.cluster.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import sample.hello.App;
import sample.hello.service.ReadFilesService;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Master extends AbstractActor {
    int i = 0;
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
                .match(String.class, this::openFile).build();
    }

    private void sendEOF() {
        getContext().actorSelection(App.MAPPER_PATH + "/user/mapper_" + i % App.NB_MAPPERS).tell(Reducer.msg.DISPLAY, getSelf());
    }

    private void openFile(String p) {
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

    private void sendToMappers(String str) {
        getContext().actorSelection(App.MAPPER_PATH + "/user/mapper_" + i % App.NB_MAPPERS).tell(str, getSelf());
        ++i;
    }
}
