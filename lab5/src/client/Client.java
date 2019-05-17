package client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import utils.Request;
import utils.RequestType;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class Client {
    public static void main(String[] args) throws Exception {
        File configFile = new File("./src/client/client.conf");
        Config config = ConfigFactory.parseFile(configFile);

        final ActorSystem system = ActorSystem.create("client_system", config);
        final ActorRef client = system.actorOf(Props.create(ClientActor.class), "client");

        System.out.print("check <name> - check the price of the book\n" +
                "order <name> - orders the book\n" +
                "stream <name> - streams the book's content\n" +
                "_____________________________________________\n");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            if (line.startsWith("check")) {
                client.tell(new Request(RequestType.CHECK, line), null);
            } else if (line.startsWith("order")) {
                client.tell(new Request(RequestType.ORDER, line), null);
            } else if (line.startsWith("stream")) {
                client.tell(new Request(RequestType.STREAM, line), null);
            } else if (line.equals("q")) {
                break;
            } else {
                System.out.println("Please provide a valid argument");
            }
        }

        system.terminate();
    }
}
