package org.unibl.etf.rabbitmq;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import org.unibl.etf.model.BookOrder;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.MyLogger;

import java.io.IOException;
import java.io.ObjectInputFilter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

public class OrderService {
    private static final ConfigReader configReader = ConfigReader.getInstance();

    public BookOrder popOrder() {
        BookOrder order = null;
        ConnectionFactory factory = RabbitMQConnectionFactoryProvider.createConnectionFactory();
        try{
            try(Connection connection = factory.newConnection()){
                Channel channel = connection.createChannel();
                channel.queueDeclare(configReader.getMQQueueName(), false, false, false, null);
                GetResponse response = channel.basicGet(configReader.getMQQueueName(), false);

                if(response != null){
                    Gson gson = new Gson();
                    String jsonOrder = new String(response.getBody(), StandardCharsets.UTF_8);
                    long deliveryTag = response.getEnvelope().getDeliveryTag();

                    channel.basicAck(deliveryTag, false);
                    order = gson.fromJson(jsonOrder, BookOrder.class);
                    System.out.println("Order got: " + order);
                }

                channel.close();
            }
        } catch (IOException | TimeoutException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        return order;
    }
}
