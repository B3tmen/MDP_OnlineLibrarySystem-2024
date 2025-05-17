package org.unibl.etf.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

public class ConfigReader {
    private static final String CONFIG_PATH = "ChatServer/src/main/resources/config.properties";
    private static ConfigReader instance = null;

    private Properties properties = new Properties();

    private ConfigReader() {

        try (FileInputStream input = new FileInputStream(CONFIG_PATH)) {
            this.properties.load(input);
        } catch (IOException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    public static ConfigReader getInstance() {
        if (instance == null)
            instance = new ConfigReader();

        return instance;
    }

    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    public String getKeyStorePath() {
        return this.properties.getProperty("KEYSTORE_PATH");
    }

    public String getKeyStorePassword() {
        return this.properties.getProperty("KEYSTORE_PASS");
    }

    public int getChatPort() {
        return Integer.parseInt(this.properties.getProperty("CHAT_PORT"));
    }

    public int getRedisPort() {
        return Integer.parseInt(this.properties.getProperty("REDIS_PORT"));
    }
}
