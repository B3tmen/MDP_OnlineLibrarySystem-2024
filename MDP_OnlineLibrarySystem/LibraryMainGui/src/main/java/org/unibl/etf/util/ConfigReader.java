package org.unibl.etf.util;

import org.unibl.etf.util.MyLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;


public class ConfigReader {
	private static final String CONFIG_PATH = "LibraryMainGui/src/main/resources/config.properties";
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

	public String getBookContentsPath(){
		return this.getProperty("BOOK_CONTENTS_PATH");
	}

	public String getMembersURL() {
		return this.getProperty("MEMBERS_URL");
	}

    public String getBooksURL() {
		return this.getProperty("BOOKS_URL");
    }

	public int getSupplierServerPort() {
		return Integer.parseInt(this.getProperty("SUPPLIER_SERVER_PORT"));
	}

	public String getMulticastGroup() {
		return this.getProperty("MULTICAST_GROUP");
	}

	public int getMulticastPort() {
		return Integer.parseInt(this.getProperty("MULTICAST_SERVER_PORT"));
	}

	public int getMulticastMemberPort() {
		return Integer.parseInt(this.getProperty("MULTICAST_MEMBER_PORT"));
	}

	public String getMQHost() {
		return this.getProperty("MQ_HOST");
	}

	public String getMQUsername() {
		return this.getProperty("MQ_USERNAME");
	}

	public String getMQPassword() {
		return this.getProperty("MQ_PASSWORD");
	}

	public String getMQQueueName() {
		return this.getProperty("MQ_QUEUE_NAME");
	}

    public String getLoginURL() {
		return this.getProperty("LIBRARIAN_LOGIN_URL");
    }
}
