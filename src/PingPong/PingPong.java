package PingPong;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PingPong implements DeliverCallback {
    private enum STATES {
        IDLE,
        WAITING,
        STARTED
    }

    private static final String INIT_CONN = "InitConn";
    private static final String START = "Start";
    private static final String OK_CONN = "Ok Conn";
    private static final String MESSAGES = "Messages";

    private STATES state;
    private final long ID;
    private final String QUEUE_NAME;
    private final Channel channel;

    public PingPong() throws Exception {
        this.state = STATES.IDLE;
        this.ID = System.nanoTime();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        channel = connection.createChannel();

        this.QUEUE_NAME = channel.queueDeclare().getQueue();
        channel.exchangeDeclare(INIT_CONN, "fanout");
        channel.exchangeDeclare(START, "fanout");
        channel.exchangeDeclare(OK_CONN, "fanout");
        channel.exchangeDeclare(MESSAGES, "fanout");

        channel.queueBind(QUEUE_NAME, INIT_CONN, "");
        channel.queueBind(QUEUE_NAME, START, "");
        channel.queueBind(QUEUE_NAME, OK_CONN, "");
        channel.queueBind(QUEUE_NAME, MESSAGES, "");

        channel.basicConsume("", true, this, consumerTag -> {});
        this.channel.basicPublish(START, "", null, new byte[0]);
    }

    @Override
    public void handle(String s, Delivery delivery) throws IOException {
        final String exchange = delivery.getEnvelope().getExchange();

        // Lors de la réception d'un START
        if (exchange.equals(START) && this.state == STATES.IDLE) {
            this.state = STATES.WAITING;
            this.channel.basicPublish(INIT_CONN, "", null, Long.toString(this.ID).getBytes());
            System.out.println("Receive a START");
        }
        // Lors de la réception d'un INIT_CONN(ID)
        else if (exchange.equals(INIT_CONN)) {
            final long OTHER_ID = Long.parseLong(new String(delivery.getBody(), StandardCharsets.UTF_8));
            System.out.println("ID:" + OTHER_ID);

            if (OTHER_ID > this.ID) {
                System.out.println("Receive a INIT_CONN and i lose");
                this.state = STATES.STARTED;
                this.channel.basicPublish(OK_CONN, "", null, "".getBytes());
            } else if (OTHER_ID < this.ID && this.state == STATES.IDLE) {
                System.out.println("Receive a INIT_CONN and i win");
                this.state = STATES.WAITING;
                this.channel.basicPublish(INIT_CONN, "", null, Long.toString(this.ID).getBytes());
            }
        }
        // Lors de la réception d'un OK_CONN
        else if (exchange.equals(OK_CONN) && this.state == STATES.WAITING) {
            this.state = STATES.STARTED;
            System.out.println("Receive a OK_CONN");
            System.out.println("STARTING SENDING PING");
            sendMessage("PING");
        }
        // Lors de la réception d'un PING ou d'un PONG
        else if(exchange.equals(MESSAGES) && this.state == STATES.STARTED) {
            String[] args = new String(delivery.getBody(), StandardCharsets.UTF_8).split("\\|");
            if(Long.parseLong(args[0]) != this.ID) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }

                switch (args[1]) {
                    case "PING":
                        System.out.println("I received a PING");
                        sendMessage("PONG");
                        break;
                    case "PONG":
                        System.out.println("I received a PONG");
                        sendMessage("PING");
                        break;
                }
            }
        }
    }

    private void sendMessage(String message) {
        try {
            String body = this.ID + "|" + message;
            this.channel.basicPublish(MESSAGES, "", null, body.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) throws Exception {
        PingPong pingPong = new PingPong();
    }
}
