package server;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import utils.CheckBook;
import utils.Request;
import utils.RequestType;
import utils.Response;

public class CheckActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, req -> {
                    String title = req.getTitle();

                    CheckBook checkBook1 = new CheckBook();
                    CheckBook checkBook2 = new CheckBook();

                    Thread thread1 = new Thread(checkBook1);
                    Thread thread2 = new Thread(checkBook2);

                    thread1.start();
                    thread2.start();

                    thread1.join();
                    thread2.join();

                    int price1 = checkBook1.check(title, 1);
                    int price2 = checkBook2.check(title, 2);

                    int price = Math.max(price1, price2);

                    if (price == -1)
                        getSender().tell(new Response("Could not find the book"), getSelf());
                    else
                        getSender().tell(new Response(String.valueOf(price)), getSelf());
                    req.setType(RequestType.STOP);
                    getContext().parent().tell(req, null);
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}

