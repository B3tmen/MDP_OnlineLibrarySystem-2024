package org.unibl.etf.server;

public class ProtocolMessages {
    public static final String MSG_SEPARATOR = "#";
    public static final String GET_AVAILABLE_BOOKS = "GET_AVAILABLE_BOOKS";         // for Supplier Gui
    public static final String GET_SUPPLIER_BOOKS_REQUEST = "GET_SUPPLIER_BOOKS_REQUEST";
    public static final String GET_SUPPLIER_BOOKS_RESPONSE = "GET_SUPPLIER_BOOKS_RESPONSE";
    public static final String SEND_AVAILABLE_BOOKS = "SEND_AVAILABLE_BOOKS";
    public static final String SEND_ORDER_TO_LIB = "SEND_ORDER_TO_LIB";
    public static final String SEND_SUPPLIER_INFO = "SEND_SUPPLIER_INFO";

    public static final String AUTH_LIBRARIAN = "AUTH_LIBRARIAN";
    public static final String LIB_GET_AVAILABLE_BOOKS_REQUEST = "LIB_GET_AVAILABLE_BOOKS_REQUEST";     // for Library Main Gui
    public static final String LIB_GET_AVAILABLE_BOOKS_RESPONSE = "LIB_GET_AVAILABLE_BOOKS_RESPONSE";
    public static final String LIB_SEND_BOOK_ORDER = "LIB_SEND_BOOK_ORDER";
    public static final String GET_ACTIVE_SUPPLIERS = "GET_ACTIVE_SUPPLIERS";
    public static final String ACTIVE_SUPPLIERS = "ACTIVE_SUPPLIERS";

    public static final String END = "END";

}
