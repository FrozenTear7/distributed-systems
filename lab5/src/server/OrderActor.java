package server;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import utils.Request;
import utils.RequestType;
import utils.Response;

import java.io.IOException;

import static utils.UtilFunctions.writeOrder;

public class OrderActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, req -> {
                    try {
                        writeOrder(req.getTitle());
                    } catch (IOException e) {
                        throw new IOException();
                    } finally {
                        getSender().tell(new Response("Done"), getSelf());
                        req.setType(RequestType.STOP);
                        getContext().parent().tell(req, null);
                    }
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}
