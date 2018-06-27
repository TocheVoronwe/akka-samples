package sample.cluster.transformation;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import sample.cluster.actors.Mapper;

public class StartMappers {

    public static void startMapper(String port) {
        final Config config = ConfigFactory.parseString(
                "akka.remote.netty.tcp.port=" + port + "\n" +
                        "akka.remote.artery.canonical.port=" + port)
                .withFallback(ConfigFactory.parseString("akka.cluster.roles = [mapper]"))
                .withFallback(ConfigFactory.load());

        ActorSystem system = ActorSystem.create("ClusterSystem", config);
        system.actorOf(Props.create(Mapper.class), "mapper");
    }

}
