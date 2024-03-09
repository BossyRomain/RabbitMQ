package PublishSubscribe;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class ReceiveLogs {
    private static final String EXCHANGE_NAME = "logs";
    private static final String EXCHANGE_NAME2 = "numbers";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        channel.exchangeDeclare(EXCHANGE_NAME2, "fanout");
        String queueName2 = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME2, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            System.out.println("Message from : " + delivery.getEnvelope().getExchange());
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received on logs '" + message + "'");
        };
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            System.out.println(delivery.getEnvelope().getRoutingKey());
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received on numbers '" + message + "'");
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        channel.basicConsume(queueName2, true, deliverCallback2, consumerTag -> { });
    }
}