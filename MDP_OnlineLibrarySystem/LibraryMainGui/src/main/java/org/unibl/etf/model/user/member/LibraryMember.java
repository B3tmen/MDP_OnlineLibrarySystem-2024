package org.unibl.etf.model.user.member;


import org.unibl.etf.model.user.LibraryUser;
import org.unibl.etf.model.user.librarian.Librarian;

import java.util.Objects;

public class LibraryMember  {
    private static int counter = 0;

    private int id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private boolean activeAccount;
    private boolean blocked;

    private boolean onlineStatus;

    public LibraryMember(){
        super();
    }

    public LibraryMember(String username, String password, String firstName, String lastName, String address, String email) {
        this.id = counter++;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
        this.activeAccount = false;
        this.onlineStatus = false;
        this.blocked = false;
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

    public boolean isActiveAccount() {
        return activeAccount;
    }
    public void setActiveAccount(boolean activeAccount) {
        this.activeAccount = activeAccount;
    }

    public boolean isBlocked() {
        return blocked;
    }
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
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
