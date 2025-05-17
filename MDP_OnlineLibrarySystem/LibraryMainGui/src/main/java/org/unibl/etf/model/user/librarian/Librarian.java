package org.unibl.etf.model.user.librarian;

import org.unibl.etf.model.user.LibraryUser;

import java.util.Objects;

public class Librarian extends LibraryUser {

    public Librarian() {
        super();
    }

    public Librarian(String username, String password) {
        super(username, password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Librarian that = (Librarian) o;
        return Objects.equals(getUsername(), that.getUsername());
    }

    @Override
    public String toString() {
        return "Librarian -> username = " + getUsername();
    }
}
