package org.unibl.etf.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.MyLogger;

import java.util.logging.Level;

public class MessagePublisher {
    private static final ConfigReader configReader = ConfigReader.getInstance();
    private final String QUEUE_NAME = configReader.getMQQueueName();

    public void publishMessage(String message) {
        try {
            ConnectionFactory factory = RabbitMQConnectionFactoryProvider.createConnectionFactory();
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                System.out.println("[RabbitMQ Sender]: Message sent - " + message);
            }
        } catch (Exception e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }
}
