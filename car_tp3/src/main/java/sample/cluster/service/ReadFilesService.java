package sample.cluster.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class ReadFilesService {


    public Stream<String> readFileLine(Path path) throws IOException
    {
       return Files.lines(path);
    }

    public void readFromInputWriteOnOutput(InputStream inputStream, OutputStream outputStream, int size) throws IOException {
        byte[] buffer = new byte[size];
        int nbReadBytes;
        while ((nbReadBytes = inputStream.read(buffer)) > 0)
            outputStream.write(buffer, 0, nbReadBytes);
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }


}
