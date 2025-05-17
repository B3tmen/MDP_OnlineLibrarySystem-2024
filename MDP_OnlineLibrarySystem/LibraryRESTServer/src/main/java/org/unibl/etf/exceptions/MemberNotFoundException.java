package org.unibl.etf.exceptions;

public class MemberNotFoundException extends Exception {
    public MemberNotFoundException(){
        super("Member with given credentials not found");
    }

    public MemberNotFoundException(String message) {
        super(message);
    }
}
