package org.unibl.etf.model.user;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.unibl.etf.model.user.librarian.Librarian;
import org.unibl.etf.model.user.member.LibraryMember;

import java.util.Objects;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LibraryMember.class, name = "member"),
        @JsonSubTypes.Type(value = Librarian.class, name = "librarian")
})
public class LibraryUser {
    private static int counter = 0;

    private int id;
    private String username;
    private String password;

    public LibraryUser(){

    }

    public LibraryUser(String username, String password) {
        this.id = counter++;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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


    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }
}
