package client;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import utils.Request;
import utils.Response;

public class ClientActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, req -> getContext().actorSelection("akka.tcp://server_system@127.0.0.1:8100/user/server").tell(req, getSelf()))
                .match(Response.class, res -> System.out.println("Received from server: " + res.getMessage() + "\n"))
                .match(String.class, System.out::println)
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}
