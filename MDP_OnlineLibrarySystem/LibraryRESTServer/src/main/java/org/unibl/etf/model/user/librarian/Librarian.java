package org.unibl.etf.model.user.librarian;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.unibl.etf.model.user.LibraryUser;
import org.unibl.etf.model.user.member.LibraryMember;

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
