package org.unibl.etf.model;

public class LibraryMemberLoginDTO {
    private String username;
    private String password;

    public LibraryMemberLoginDTO() {
    }

    public LibraryMemberLoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
