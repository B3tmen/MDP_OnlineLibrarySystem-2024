package org.unibl.etf.service;

import org.unibl.etf.dao.LibrarianDAOImpl;
import org.unibl.etf.exceptions.MemberNotFoundException;
import org.unibl.etf.model.user.librarian.Librarian;
import org.unibl.etf.model.user.librarian.LibrarianLoginDTO;

import java.io.IOException;

public class LibrarianService {
    private LibrarianDAOImpl librarianDAO;

    public LibrarianService() {
        this.librarianDAO = new LibrarianDAOImpl();
    }

    public Librarian checkLogin(LibrarianLoginDTO librarianLoginDTO) throws MemberNotFoundException {
        Librarian librarian;
        try {
            librarian = librarianDAO.getByUsernameAndPassword(librarianLoginDTO.getUsername(), librarianLoginDTO.getPassword());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (librarian == null) {
            throw new MemberNotFoundException();
        }

        return librarian;
    }


}
