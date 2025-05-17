package org.unibl.etf.rabbitmq;

import com.google.gson.Gson;
import com.rabbitmq.client.*;
import javafx.application.Platform;
import org.unibl.etf.controllers.PendingBookOrdersController;
import org.unibl.etf.model.BookOrder;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.MyLogger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

public class RabbitMQMessageReceiver extends Thread {
    private static final ConfigReader configReader = ConfigReader.getInstance();
    private final String QUEUE_NAME = configReader.getMQQueueName();

    private PendingBookOrdersController controller;

    public RabbitMQMessageReceiver(PendingBookOrdersController controller) {
        this.controller = controller;

    }

    @Override
    public void run() {
        System.out.println("[RabbitMQ]: Waiting for messages. To exit press CTRL+C");
        ConnectionFactory factory = RabbitMQConnectionFactoryProvider.createConnectionFactory();

        receiveMessages(factory);
    }

    private void receiveMessages(ConnectionFactory factory) {
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            //channel.basicQos(1);

            Consumer consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String jsonOrder = new String(body, StandardCharsets.UTF_8);
                    System.out.println("[RabbitMQ]: Received Order '" + jsonOrder + "'");

                    //try {
                        sendToPendingOrdersController(jsonOrder);
                    //} finally {
                        System.out.println("[RabbitMQ]: Done");
                    //}
                }
            };

            channel.basicConsume(QUEUE_NAME, true, consumer);
        }
        catch (IOException | TimeoutException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

    }

    private void sendToPendingOrdersController(String json) {
        Gson gson = new Gson();
        BookOrder order = gson.fromJson(json, BookOrder.class);

        Platform.runLater(() -> {
            controller.addPendingOrder(order);
        });
    }
}
