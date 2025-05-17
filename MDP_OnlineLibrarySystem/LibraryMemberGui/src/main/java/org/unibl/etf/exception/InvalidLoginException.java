package org.unibl.etf.exception;

public class InvalidLoginException extends Exception {
    public InvalidLoginException() {
        super("An Error occured logging in with specified username and password");
    }

    public InvalidLoginException(String message) {
        super(message);
    }
}
