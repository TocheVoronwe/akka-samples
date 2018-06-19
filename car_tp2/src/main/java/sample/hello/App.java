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
  public static final String MASTER_PATH = "akka.tcp://MasterSystem@127.0.0.1:3001";
  public static final String MAPPER_PATH = "akka.tcp://MappersSystem@127.0.0.1:3002";
  public static final String EOF = "END OF FILE";

  public static final int NB_MAPPERS = 3;
  public static final int NB_REDUCERS = 4;


  public static void main(String[] args) {
    final ActorSystem system = ActorSystem.create("MasterSystem", ConfigFactory.load("master"));

    final ActorRef reducer1 = system.actorOf(Props.create(Reducer.class));
    final ActorRef reducer2 = system.actorOf(Props.create(Reducer.class));
    final ActorRef reducer3 = system.actorOf(Props.create(Reducer.class));
    final ActorRef reducer4 = system.actorOf(Props.create(Reducer.class));
    List<ActorRef> reducers = Arrays.asList(reducer1, reducer2, reducer3, reducer4);

    final ActorRef mapper1 = system.actorOf(Mapper.props(reducers));
    final ActorRef mapper2 = system.actorOf(Mapper.props(reducers));
    final ActorRef mapper3 = system.actorOf(Mapper.props(reducers));
    List<ActorRef> mappers = Arrays.asList(mapper1, mapper2, mapper3);



  }

  public static void startMasterSystem()
  {
    final ActorSystem system = ActorSystem.create("MasterSystem", ConfigFactory.load("master"));
    final ActorRef master = system.actorOf(Master.props(), ActorRef.noSender());
    master.tell("src\\main\\resources\\text.txt", ActorRef.noSender());
  }

  public static void startMapperSystem()
  {
    ActorSystem mapperSystem = ActorSystem.create("MapperSystem", ConfigFactory.load("mappers"));

    for (int i = 0; i < NB_MAPPERS; i++)
      mapperSystem.actorOf(Mapper.props(), "mapper_" + i);
  }

}
