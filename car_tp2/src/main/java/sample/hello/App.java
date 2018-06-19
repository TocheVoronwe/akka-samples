package sample.hello;

import akka.actor.*;
import akka.actor.dsl.Creators;
import com.typesafe.config.ConfigFactory;
import sample.hello.actors.Mapper;
import sample.hello.actors.Master;
import sample.hello.actors.Reducer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {
    public static final String MAPPER_PATH = "akka.tcp://MappersSystem@127.0.0.1:3002";
    public static final String REDUCERS_PATH = "akka.tcp://ReducersSystem@127.0.0.1:3001";

    public static final int NB_MAPPERS = 3;
    public static final int NB_REDUCERS = 4;


    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("MasterSystem", ConfigFactory.load("master"));
    }

    public static void startMasterSystem() {
        final ActorSystem system = ActorSystem.create("MasterSystem", ConfigFactory.load("master"));
        final ActorRef master = system.actorOf(Master.props(), "master");

        for (int i = 0; i < NB_REDUCERS; i++)
            system.actorOf(Reducer.props(), "reducer_" + i);
        master.tell("src\\main\\resources\\text.txt", ActorRef.noSender());
        master.tell(Reducer.msg.DISPLAY, ActorRef.noSender());
    }

    public static void startMapperSystem() {
        ActorSystem mapperSystem = ActorSystem.create("MapperSystem", ConfigFactory.load("mappers"));

        for (int i = 0; i < NB_MAPPERS; i++)
            mapperSystem.actorOf(Mapper.props(), "mapper_" + i);
    }
}
