package homework;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class Doctor implements AutoCloseable {
    private Connection connection;
    private Channel channel;
    private String topic1 = "hip";
    private String topic2 = "knee";
    private String topic3 = "elbow";
    private String EXCHANGE_LOG = "log";
    private String EXCHANGE_LOG2 = "log2";

    public Doctor() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_LOG, BuiltinExchangeType.FANOUT);
        channel.exchangeDeclare(EXCHANGE_LOG2, BuiltinExchangeType.FANOUT);

        String queueLog = channel.queueDeclare().getQueue();
        channel.queueBind(queueLog, EXCHANGE_LOG2, "");

        channel.basicConsume(queueLog, true, adminLog, consumerTag -> {
        });
    }

    public DeliverCallback adminLog = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        System.out.println("Received from admin: " + message);
    };

    public static void main(String[] argv) throws Exception {
        try (Doctor client = new Doctor()) {
            while (true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Provide request");
                String message = br.readLine();

                if (message.equals("q")) {
                    return;
                } else if (!message.startsWith("hip") && !message.startsWith("knee") && !message.startsWith("elbow")) {
                    continue;
                }

                client.runThatBruh(message);
            }
        }
    }

    public void runThatBruh(String message){
        AsyncRequest thread = new AsyncRequest(message);
        new Thread(thread).start();
    }

    public class AsyncRequest implements Runnable {
        String message;

        AsyncRequest(String message) {
            this.message = message;
        }

        public void run() {
            try {
                final String corrId = UUID.randomUUID().toString();

                String replyQueueName = channel.queueDeclare().getQueue();
                AMQP.BasicProperties props = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(corrId)
                        .replyTo(replyQueueName)
                        .build();

                if (message.startsWith("hip")) {
                    channel.basicPublish("", topic1, props, message.getBytes("UTF-8"));
                } else if (message.startsWith("knee")) {
                    channel.basicPublish("", topic2, props, message.getBytes("UTF-8"));
                } else if (message.startsWith("elbow")) {
                    channel.basicPublish("", topic3, props, message.getBytes("UTF-8"));
                }

                channel.basicPublish(EXCHANGE_LOG, "", null, ("Doctor - " + message).getBytes("UTF-8"));

                final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

                String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
                    if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                        response.offer(new String(delivery.getBody(), "UTF-8"));
                    }
                }, consumerTag -> {
                });

                String result = response.take();
                channel.basicCancel(ctag);

                channel.basicPublish(EXCHANGE_LOG, "", null, ("Doctor - " + result).getBytes("UTF-8"));

                System.out.println("Response: " + result);
            } catch (Exception ignored) {

            }
        }
    }

    public void close() throws IOException {
        connection.close();
    }
}