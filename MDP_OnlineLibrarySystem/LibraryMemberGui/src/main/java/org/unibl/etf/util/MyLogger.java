package org.unibl.etf.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyLogger {
	public static Logger logger = Logger.getLogger("CustomerLogger");
	private static final String LOG_FILE = "customer_logs.log";
	
	static {
		FileHandler fileHandler;
		try {
			fileHandler = new FileHandler(LOG_FILE, true);
			logger.addHandler(fileHandler);

			SimpleFormatter formatter = new SimpleFormatter();
			fileHandler.setFormatter(formatter);

			logger.setUseParentHandlers(false);
		} catch (IOException e) {
			System.exit(-1);
		}
	}	
}

