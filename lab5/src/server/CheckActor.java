package server;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CheckActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private File db1 = new File("./src/bookstore/database1.txt");
    private File db2 = new File("./src/bookstore/database2.txt");

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    String title = s.split(" ")[1];
                    int price = -1;

                    BufferedReader br = new BufferedReader(new FileReader(db1));

                    String st;
                    while ((st = br.readLine()) != null) {
                        if (st.startsWith(title)) {
                            price = Integer.parseInt(st.split(" ")[1]);
                            break;
                        }
                    }

                    if (price == -1) {
                        br = new BufferedReader(new FileReader(db2));

                        while ((st = br.readLine()) != null) {
                            if (st.startsWith(title)) {
                                price = Integer.parseInt(st.split(" ")[1]);
                                break;
                            }
                        }
                    }

                    getSender().tell(price, getSelf());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}

