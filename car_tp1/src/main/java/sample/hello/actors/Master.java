package sample.hello.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import sample.hello.Greeter;
import sample.hello.service.ReadFilesService;

public class Master extends AbstractActor{
    private ReadFilesService readFilesService = new ReadFilesService();
    private final ActorRef mapper = getContext().actorOf(Props.create(Mapper.class), "mapper_1");
    private final ActorRef mapper2 = getContext().actorOf(Props.create(Mapper.class), "mapper_2");
    private final ActorRef mapper3 = getContext().actorOf(Props.create(Mapper.class), "mapper_3");

    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .matchAny(this::sendToMappers)
                .build();
    }

    private void sendToMappers(Object str)
    {
        int val = str.hashCode();
        switch (val % 3)
        {
            case 1:
                mapper.tell(str, self());
                break;
            case 2:
                mapper2.tell(str, self());
                break;
            case 3:
                mapper3.tell(str, self());
                break;
        }
    }

    @Override
    public void preStart()
    {
        final ActorRef mapper = getContext().actorOf(Props.create(Mapper.class), "mapper");
        mapper.tell(Greeter.Msg.GREET, self());
    }
}
