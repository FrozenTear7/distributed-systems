package homework;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Admin {
    public static void main(String[] argv) throws Exception {
        System.out.println("Admin starting");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        String EXCHANGE_LOG = "log";
        channel.exchangeDeclare(EXCHANGE_LOG, BuiltinExchangeType.FANOUT);

        String EXCHANGE_LOG2 = "log2";
        channel.exchangeDeclare(EXCHANGE_LOG2, BuiltinExchangeType.FANOUT);

        String queueLog = channel.queueDeclare().getQueue();
        channel.queueBind(queueLog, EXCHANGE_LOG, "");

        DeliverCallback consumer = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("Received: " + message);
        };

        channel.basicConsume(queueLog, true, consumer, consumerTag -> {
        });


        System.out.println("Admin listening for messages:");

        channel.basicConsume(queueLog, true, consumer, consumerTag -> {
        });

        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String message = br.readLine();

            if (message.equals("q")) {
                channel.close();
                connection.close();
                return;
            }

            channel.basicPublish(EXCHANGE_LOG2, "", null, message.getBytes("UTF-8"));
        }
    }
}

