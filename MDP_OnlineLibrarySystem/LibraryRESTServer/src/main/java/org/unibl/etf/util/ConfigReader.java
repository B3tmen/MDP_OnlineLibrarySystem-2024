package org.unibl.etf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;


public class ConfigReader {
	private static ConfigReader instance = null;
	private static final String CONFIG_PATH = "config.properties";
	public Properties properties;

	private ConfigReader() {
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(CONFIG_PATH)){
			properties = new Properties();
			properties.load(is);
		} catch(IOException e) {
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

	public String getUserPath() {
		return this.properties.getProperty("USERS_PATH");
	}

	public String getMulticastAddress() {
		return this.properties.getProperty("MULTICAST_IP");
	}

	public String getMulticastPort() {
		return this.properties.getProperty("MULTICAST_SERVER_PORT");
	}

	public String getLibraryServerPort() {
		return this.properties.getProperty("LIBRARY_SERVER_PORT");
	}

	public int getRedisPort() {
		return Integer.parseInt(this.properties.getProperty("REDIS_PORT"));
	}


	public String getBookContentsPath(){
		return this.getProperty("BOOKS_CONTENT_PATH");
	}
}
