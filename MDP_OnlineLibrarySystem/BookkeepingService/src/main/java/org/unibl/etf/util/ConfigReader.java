package org.unibl.etf.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;


public class ConfigReader {
	private static final String CONFIG_PATH = "BookkeepingService/src/main/resources/config.properties";
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

	public String getReceiptsPath() {
		return this.getProperty("RECEIPTS_PATH");
	}

	public int getRMIPort(){
		return Integer.parseInt(this.getProperty("RMI_PORT"));
	}

	public String getRMIServerName(){
		return this.getProperty("RMI_SERVER_NAME");
	}
}
