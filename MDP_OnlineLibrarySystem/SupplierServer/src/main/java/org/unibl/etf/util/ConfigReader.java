package org.unibl.etf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

public class ConfigReader {
    private static final String CONFIG_PATH = "SupplierServer/src/main/resources/config.properties";
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

    public int getSupplierServerPort() {
        return Integer.parseInt(this.properties.getProperty("SUPPLIER_SERVER_PORT"));
    }

    public String getBookLinksPath() {
        return getProperty("BOOK_LINKS_PATH");
    }

    public int getRedisPort() {
        return Integer.parseInt(getProperty("REDIS_PORT"));
    }

    public String getBookContentsPath() {
        return this.getProperty("BOOKS_CONTENT_PATH");
    }
}
