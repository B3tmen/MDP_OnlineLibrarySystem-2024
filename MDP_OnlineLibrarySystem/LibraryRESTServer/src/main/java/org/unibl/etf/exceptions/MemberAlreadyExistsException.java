package org.unibl.etf.exceptions;

public class MemberAlreadyExistsException extends RuntimeException {
    public MemberAlreadyExistsException() {
        super("A Library Member already exists with that username");
    }

    public MemberAlreadyExistsException(String message) {
        super(message);
    }
}
