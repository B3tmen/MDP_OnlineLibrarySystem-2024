
package org.unibl.etf.model;

public class RestMessage {
    private String message;

    public RestMessage() {
    }

    public RestMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
