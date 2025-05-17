package org.unibl.etf.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import org.unibl.etf.util.ConfigReader;

public class RabbitMQConnectionFactoryProvider {
    private static final ConfigReader configReader = ConfigReader.getInstance();

    private static final String HOST = configReader.getMQHost();
    private static final String USERNAME = configReader.getMQUsername();
    private static final String PASSWORD = configReader.getMQPassword();


    public static ConnectionFactory createConnectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        return factory;
    }
}
