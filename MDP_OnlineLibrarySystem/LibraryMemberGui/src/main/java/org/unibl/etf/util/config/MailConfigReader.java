package org.unibl.etf.util.config;

import org.unibl.etf.util.MyLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;


public class MailConfigReader {
	private static final String CONFIG_PATH = "LibraryRESTServer/src/main/resources/mail-config.properties";
	private static MailConfigReader instance = null;

	private Properties properties = new Properties();

	private MailConfigReader() {

		try (FileInputStream input = new FileInputStream(CONFIG_PATH)) {
			this.properties.load(input);
		} catch (IOException e) {
			MyLogger.logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
	}

	public static MailConfigReader getInstance() {
		if (instance == null)
			instance = new MailConfigReader();

		return instance;
	}

	public Properties getProperties() {
		return properties;
	}

	public String getProperty(String key) {
		return this.properties.getProperty(key);
	}

	public String getUsername() {
		return this.properties.getProperty("mail.username");
	}

	public String getPassword() {
		return this.properties.getProperty("mail.password");
	}


}
