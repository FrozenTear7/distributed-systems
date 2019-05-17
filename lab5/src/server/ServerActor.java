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

public class ServerActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    //    private Map<String, ActorRef> actors = new HashMap<>();
    private ActorRef orderActor = getContext().actorOf(Props.create(OrderActor.class), "order");
    private ActorRef checkActor = getContext().actorOf(Props.create(CheckActor.class), "check");
    private ActorRef streamActor = getContext().actorOf(Props.create(StreamActor.class), "stream");

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, req -> {
                    if (req.getType() == RequestType.CHECK) {
                        checkActor.tell(req, getSender());
                    } else if (req.getType() == RequestType.ORDER) {
                        orderActor.tell(req, getSender());
                    } else if (req.getType() == RequestType.STREAM) {
                        streamActor.tell(req, getSender());
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
