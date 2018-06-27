package sample.cluster.transformation;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import sample.cluster.actors.Mapper;
import sample.cluster.actors.Reducer;

import static sample.cluster.transformation.App.CLUSTER_SYSTEM;
import static sample.cluster.transformation.App.NB_REDUCERS;

public class StartReducers {

    public static void startMapper(String port) {
        final Config config = ConfigFactory.parseString(
                "akka.remote.netty.tcp.port=" + port + "\n" +
                        "akka.remote.artery.canonical.port=" + port)
                .withFallback(ConfigFactory.parseString("akka.cluster.roles = [reducer]"))
                .withFallback(ConfigFactory.load());

        ActorSystem system = ActorSystem.create("ClusterSystem", config);
        for(int i = 0; i < NB_REDUCERS; i++)
            system.actorOf(Props.create(Reducer.class), "reducer_" + i);
    }
}
