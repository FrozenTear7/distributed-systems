package server;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.FileWriter;
import java.io.IOException;

public class OrderActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    try {
                        FileWriter fw = new FileWriter("./orders.txt", true);
                        fw.write(s.split(" ")[1] + "\n");
                        fw.close();
                    } catch (IOException e) {
                        throw new IOException();
                    } finally {
                        getSender().tell("done", getSelf());
                    }
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}
