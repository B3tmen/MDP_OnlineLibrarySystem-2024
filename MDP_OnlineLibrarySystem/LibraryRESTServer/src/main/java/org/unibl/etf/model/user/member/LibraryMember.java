package org.unibl.etf.model.user.member;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.unibl.etf.model.user.LibraryUser;

import java.util.Objects;

//@JacksonXmlRootElement(localName = "LibraryMember")
public class LibraryMember extends LibraryUser {
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private boolean blocked;
    private boolean activeAccount;

    private boolean onlineStatus;

    public LibraryMember() {
        super();
    }

    public LibraryMember(String username, String password, String firstName, String lastName, String address, String email) {
        super(username, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
        this.activeAccount = true;
        this.onlineStatus = false;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isBlocked() {
        return blocked;
    }
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isActiveAccount() {
        return activeAccount;
    }
    public void setActiveAccount(boolean activeAccount) {
        this.activeAccount = activeAccount;
    }

    public boolean isOnlineStatus() {
        return onlineStatus;
    }
    public void setOnlineStatus(boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibraryMember that = (LibraryMember) o;
        return Objects.equals(getUsername(), that.getUsername());
    }

    @Override
    public String toString() {
        return "LibraryMember -> username = " + getUsername() + ", firstName = " + firstName + ", lastName = " + lastName + ", address = " + address + ", email = " + email;
    }
}
