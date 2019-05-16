package client;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ClientActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    System.out.println(s);
                    getContext().actorSelection("akka.tcp://server_system@127.0.0.1:8100/user/server").tell(s, getSelf());
                })
                .match(Integer.class, System.out::println)
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}
