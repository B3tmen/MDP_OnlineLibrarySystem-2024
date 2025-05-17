package org.unibl.etf.dao;

import org.unibl.etf.model.user.LibraryUser;
import org.unibl.etf.model.user.librarian.Librarian;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.MyLogger;
import org.unibl.etf.util.XMLSerializer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class LibrarianDAOImpl implements DAO<Librarian> {
    private static ConfigReader config = ConfigReader.getInstance();

    @Override
    public Librarian get(int id) throws IOException {
        return null;
    }

    @Override
    public List<Librarian> getAll() throws IOException {
        List<LibraryUser> users = XMLSerializer.deserializeXML(config.getUserPath());
        List<Librarian> librarians = new ArrayList<>();

        for (LibraryUser user : users) {
            if (user instanceof Librarian) {
                librarians.add((Librarian) user);
            }
        }

        return librarians;
    }

    @Override
    public int insert(Librarian object) throws IOException {
        return 0;
    }

    @Override
    public int update(Librarian object) throws IOException {
        return 0;
    }

    @Override
    public int delete(Librarian object) throws IOException {
        return 0;
    }

    public Librarian getByUsernameAndPassword(String username, String password) throws IOException {
        List<Librarian> librarians = getAll();

        for (Librarian librarian : librarians) {
            System.out.println(librarian.getUsername());
            if (librarian.getUsername().equals(username) && librarian.getPassword().equals(password)) {
                return librarian;
            }
        }

        return null;
    }
}
