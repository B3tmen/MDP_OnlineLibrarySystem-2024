package org.unibl.etf.supplier;

public class SupplierServerProtocolMessages {
    public static final String MSG_SEPARATOR = "#";
    public static final String AUTH_LIBRARIAN = "AUTH_LIBRARIAN";
    public static final String LIB_GET_AVAILABLE_BOOKS_REQUEST = "LIB_GET_AVAILABLE_BOOKS_REQUEST";     // for Library Main Gui
    public static final String LIB_GET_AVAILABLE_BOOKS_RESPONSE = "LIB_GET_AVAILABLE_BOOKS_RESPONSE";
    public static final String LIB_SEND_BOOK_ORDER = "LIB_SEND_BOOK_ORDER";
    public static final String GET_ACTIVE_SUPPLIERS = "GET_ACTIVE_SUPPLIERS";
    public static final String ACTIVE_SUPPLIERS = "ACTIVE_SUPPLIERS";

    public static final String END = "END";
}
