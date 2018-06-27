package sample.cluster.transformation;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import sample.cluster.actors.Mapper;
import sample.cluster.actors.Reducer;

import static sample.cluster.transformation.App.CLUSTER_SYSTEM;
import static sample.cluster.transformation.App.NB_REDUCERS;
import static sample.cluster.transformation.App.REDUCERS_PATH;

public class StartMappers {

    public static void startMapper(String port) {
        final Config config = ConfigFactory.parseString(
                "akka.remote.netty.tcp.port=" + port + "\n" +
                        "akka.remote.artery.canonical.port=" + port)
                .withFallback(ConfigFactory.parseString("akka.cluster.roles = [mapper]"))
                .withFallback(ConfigFactory.load());

        /*createReducers();*/
        ActorSystem system = ActorSystem.create("ClusterSystem", config);
        system.actorOf(Props.create(Mapper.class), "mapper_1");
        system.actorOf(Props.create(Mapper.class), "mapper_2");
        system.actorOf(Props.create(Mapper.class), "mapper_3");
    }

    private static void createReducers()
    {
        ActorSystem system = ActorSystem.create(CLUSTER_SYSTEM, ConfigFactory.load("reducers"));
        for (int i = 0; i < NB_REDUCERS; i++)
            system.actorOf(Reducer.props(), "reducer_" + i);
        System.out.println(system.deadLetters());
        System.out.println("Reducer !! " + system.actorSelection(App.REDUCERS_PATH + "/reducer_" + 1));
        System.out.println(system.actorSelection(App.REDUCERS_PATH + "/user/reducer_" + 2));
        System.out.println(system.actorSelection(App.REDUCERS_PATH + "/user/reducer_" + 3));
    }

}
