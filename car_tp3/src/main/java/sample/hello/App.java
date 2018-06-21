package sample.hello;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import sample.hello.actors.Mapper;
import sample.hello.actors.Master;
import sample.hello.actors.Reducer;


public class App {
    public static final String MASTER_SYSTEM = "Master_System";
    public static final String MAPPER_SYSTEM = "Mapper_System";
    public static final String MAPPER_PATH = "akka.tcp://" + MAPPER_SYSTEM + "@127.0.0.1:3002";
    public static final String REDUCERS_PATH = "akka.tcp://" + MASTER_SYSTEM + "@127.0.0.1:3001";

    public static final int NB_MAPPERS = 3;
    public static final int NB_REDUCERS = 4;


    public static void main(String[] args) {
        System.out.println(args[0]);
        if (args.length == 0 || args[0].equals("master"))
            startMasterSystem();
        else if (args.length == 0 || args[0].equals("mapper"))
            startMapperSystem();
    }

    public static void startMasterSystem() {
        System.out.println("starting master");
        ActorSystem system = ActorSystem.create(MASTER_SYSTEM, ConfigFactory.load("reducers"));
        ActorRef master = system.actorOf(Master.props(), "master");

        for (int i = 0; i < NB_REDUCERS; i++)
            system.actorOf(Reducer.props(), "reducer_" + i);
        master.tell("src\\main\\resources\\text.txt", ActorRef.noSender());
    }

    public static void startMapperSystem() {
        System.out.println("starting mapper");
        ActorSystem mapperSystem = ActorSystem.create(MAPPER_SYSTEM, ConfigFactory.load("mappers"));

        for (int i = 0; i < NB_MAPPERS; i++)
            mapperSystem.actorOf(Mapper.props(), "mapper_" + i);
    }
}
