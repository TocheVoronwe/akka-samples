package sample.hello;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import sample.hello.service.ReadFilesService;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Master extends AbstractActor{
    private ReadFilesService readFilesService = new ReadFilesService();

    @Override
    public Receive createReceive() {

        openFile();
        return receiveBuilder().build();
    }

    public void openFile()
    {
        Path path = Paths.get("D:\\Download\\bible.txt");
        try
        {
            Stream<String> lines = readFilesService.readFileLine(path);
            lines.forEach(System.out::println);
        }
        catch (IOException io)
        {
            io.printStackTrace();
            System.err.println("File not reachable or readable");
        }
    }
}
