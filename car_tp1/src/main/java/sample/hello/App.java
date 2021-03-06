package sample.hello;

import akka.actor.*;
import akka.actor.dsl.Creators;
import sample.hello.actors.Mapper;
import sample.hello.actors.Master;
import sample.hello.actors.Reducer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {



  public static void main(String[] args) {
    final ActorSystem system = ActorSystem.create("car_tp1");

    final ActorRef reducer1 = system.actorOf(Props.create(Reducer.class));
    final ActorRef reducer2 = system.actorOf(Props.create(Reducer.class));
    final ActorRef reducer3 = system.actorOf(Props.create(Reducer.class));
    final ActorRef reducer4 = system.actorOf(Props.create(Reducer.class));
    List<ActorRef> reducers = Arrays.asList(reducer1, reducer2, reducer3, reducer4);

    final ActorRef mapper1 = system.actorOf(Mapper.props(reducers));
    final ActorRef mapper2 = system.actorOf(Mapper.props(reducers));
    final ActorRef mapper3 = system.actorOf(Mapper.props(reducers));
    List<ActorRef> mappers = Arrays.asList(mapper1, mapper2, mapper3);

    final ActorRef master = system.actorOf(Master.props(mappers));

    master.tell("src\\main\\resources\\text.txt", ActorRef.noSender());
  }

}
