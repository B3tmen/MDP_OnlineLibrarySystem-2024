package org.unibl.etf.util;

public class ConnectionParameters {
    private static final ConfigReader reader = ConfigReader.getInstance();

    public static final int CHAT_PORT = reader.getChatPort();
}
