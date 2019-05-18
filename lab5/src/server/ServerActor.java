package server;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import utils.Request;
import utils.RequestType;
import scala.concurrent.duration.Duration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private Map<String, ActorRef> actors = new HashMap<>();
    private int counter = 0;

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, req -> {
                    if (req.getType() == RequestType.CHECK) {
                        String name = String.valueOf(counter++);
                        req.setActor(name);
                        ActorRef newActor = getContext().actorOf(Props.create(CheckActor.class), name);
                        actors.put(name, newActor);
                        newActor.tell(req, getSender());
                    } else if (req.getType() == RequestType.ORDER) {
                        String name = String.valueOf(counter++);
                        req.setActor(name);
                        ActorRef newActor = getContext().actorOf(Props.create(OrderActor.class), name);
                        actors.put(name, newActor);
                        newActor.tell(req, getSender());
                    } else if (req.getType() == RequestType.STREAM) {
                        String name = String.valueOf(counter++);
                        req.setActor(name);
                        ActorRef newActor = getContext().actorOf(Props.create(StreamActor.class), name);
                        actors.put(name, newActor);
                        newActor.tell(req, getSender());
                    } else if (req.getType() == RequestType.STOP) {
                        getContext().stop(actors.get(req.getActor()));
                        actors.remove(req.getActor());
                    }
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    private static SupervisorStrategy strategy = new OneForOneStrategy(10, Duration.create("1 minute"),
            DeciderBuilder.match(FileNotFoundException.class, e -> SupervisorStrategy.escalate())
                    .match(IOException.class, e -> SupervisorStrategy.restart())
                    .matchAny(o -> SupervisorStrategy.restart())
                    .build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }
}
