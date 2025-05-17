package org.unibl.etf.exceptions;

public class MemberNotActivatedException extends Exception{

    public MemberNotActivatedException() {
        super("Member account was not activated");
    }

    public MemberNotActivatedException(String message) {
        super(message);
    }
}
