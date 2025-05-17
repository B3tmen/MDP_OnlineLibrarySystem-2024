package org.unibl.etf.service;

import org.unibl.etf.dao.BookDAOImpl;
import org.unibl.etf.dao.DAO;
import org.unibl.etf.model.Book;
import org.unibl.etf.util.MyLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class BookService {
    private DAO bookDAO;

    public BookService() {
        this.bookDAO = new BookDAOImpl();
    }

    public List<Book> getAll() {
        List<Book> books = new ArrayList<>();
        try{
            books = bookDAO.getAll();
        } catch(IOException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        return books;
    }

    public int insert(Book object) {
        int inserted = 0;
        try{
            inserted = bookDAO.insert(object);
        } catch(IOException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        return inserted;
    }

    public int update(Book object) {
        int updated = 0;
        try{
            updated = bookDAO.update(object);
        } catch(IOException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        return updated;
    }

    public int delete(Book object) {
        int deleted = 0;
        try{
            deleted = bookDAO.delete(object);
        } catch(IOException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        return deleted;
    }
}
