package org.unibl.etf.model.user.member;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.unibl.etf.model.user.LibraryUser;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "LibraryUsers")
public class LibraryUserList {
//    @JacksonXmlElementWrapper(useWrapping = false)
//    @JacksonXmlProperty(localName = "User")
    private List<LibraryUser> users;

    // Default constructor is needed for deserialization
    public LibraryUserList() {
        users = new ArrayList<>();
    }

    public LibraryUserList(List<LibraryUser> users) {
        this.users = users;
    }

    public List<LibraryUser> getLibraryUsers() {
        return users;
    }

    public void setUsers(List<LibraryUser> users) {
        this.users = users;
    }
}
