package org.unibl.etf.util.config;

import org.unibl.etf.util.MyLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;


public class ConfigReader {
	private static final String CONFIG_PATH = "LibraryMemberGui/src/main/resources/config.properties";
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

	public String getBookContentsPath() {
		return this.getProperty("BOOK_CONTENTS_PATH");
	}

	public String getLoginURL() {
		return this.properties.getProperty("LOGIN_URL");
	}

	public String getRegisterURL() {
		return this.properties.getProperty("REGISTER_URL");
	}

	public String getTrustStorePath() {
		return this.properties.getProperty("TRUSTSTORE_PATH");
	}

	public String getTrustStorePassword() {
		return this.properties.getProperty("TRUSTSTORE_PASS");
	}

	public int getChatPort() {
		return Integer.parseInt(this.properties.getProperty("CHAT_SERVER_PORT"));
	}

	public int getRedisPort() {
		return Integer.parseInt(this.properties.getProperty("REDIS_PORT"));
	}

	public String getMulticastGroup() {
		return this.getProperty("MULTICAST_GROUP");
	}

	public int getMulticastPort() {
		return Integer.parseInt(this.getProperty("MULTICAST_SERVER_PORT"));
	}

	public int getMulticastLibraryPort() {
		return Integer.parseInt(this.getProperty("MULTICAST_LIBRARY_PORT"));
	}

    public String getEmailURL() {
		return this.getProperty("SEND_EMAIL_URL");
    }

	public String getBooksURL() {
		return this.getProperty("BOOKS_URL");
	}
}
