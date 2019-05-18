package server;

import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import utils.Request;
import utils.RequestType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, req -> {
                    try (Stream<String> stream = Files.lines(Paths.get(String.format("./src/bookstore/%s.txt", req.getTitle())))) {
                        final Materializer materializer = ActorMaterializer.create(getContext().getSystem());
                        Source<String, NotUsed> source = Source.from(stream.collect(Collectors.toList()));
                        source.map(String::toString)
//                                .throttle(1, Duration.ofSeconds(1))
                                .runWith(Sink.actorRef(getSender(), "\n"), materializer);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        req.setType(RequestType.STOP);
                        getContext().parent().tell(req, null);
                    }


                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}