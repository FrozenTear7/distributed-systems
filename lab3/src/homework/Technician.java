package homework;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class Technician {
    private static String topic1;
    private static String topic2;
    private static String EXCHANGE_LOG = "log";
    private static String EXCHANGE_LOG2 = "log2";

    private static Connection connection;
    private static Channel channel;

    private static String processRequest(String message) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException _ignored) {
            Thread.currentThread().interrupt();
        }

        return message + " done";
    }

    public DeliverCallback adminLog = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        System.out.println("Received from admin: " + message);
    };

    public Technician() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.queueDeclare(topic1, false, false, false, null);
        channel.queuePurge(topic1);

        channel.queueDeclare(topic2, false, false, false, null);
        channel.queuePurge(topic2);

        channel.exchangeDeclare(EXCHANGE_LOG, BuiltinExchangeType.FANOUT);
        channel.exchangeDeclare(EXCHANGE_LOG2, BuiltinExchangeType.FANOUT);

        String queueLog = channel.queueDeclare().getQueue();
        channel.queueBind(queueLog, EXCHANGE_LOG2, "");

        channel.basicConsume(queueLog, true, adminLog, consumerTag -> {
        });

        channel.basicQos(1);
    }

    public static void main(String[] argv) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Topic 1:");
        topic1 = br.readLine();
        System.out.println("Topic 2:");
        topic2 = br.readLine();

        Technician server = new Technician();

        System.out.println("Technician waiting for requests");

        Object monitor = new Object();
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();

            String response = "";

            try {
                String message = new String(delivery.getBody(), "UTF-8");
                channel.basicPublish(EXCHANGE_LOG, "", null, ("Technician - " + message).getBytes("UTF-8"));
                System.out.println("Received from doctor: " + message);
                response += processRequest(message);
            } catch (RuntimeException e) {
                System.out.println(" [.] " + e.toString());
            } finally {
                channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                synchronized (monitor) {
                    monitor.notify();
                }
            }
        };

        channel.basicConsume(topic1, false, deliverCallback, (consumerTag -> {
        }));
        channel.basicConsume(topic2, false, deliverCallback, (consumerTag -> {
        }));

        while (true) {
            synchronized (monitor) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}