package org.unibl.etf.dao;

import org.unibl.etf.model.Book;
import org.unibl.etf.redis.RedisBookService;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookDAOImpl implements DAO<Book>{
    private RedisBookService redisBookService;

    public BookDAOImpl() {
        this.redisBookService = new RedisBookService();
    }

    @Override
    public Book get(int id) throws IOException {
        return null;
    }

    @Override
    public List<Book> getAll() throws IOException {
        List<Book> books = redisBookService.getAllBooks();

        return books;
    }

    @Override
    public int insert(Book object) throws IOException {
        redisBookService.saveBook(object);
        return 1;
    }

    @Override
    public int update(Book object) throws IOException {
        AtomicBoolean updatedBook = new AtomicBoolean(false);
        List<Book> books = redisBookService.getAllBooks();
        books.forEach(book -> {
            if(book.equals(object)) {
                String isbn = object.getIsbn();
                String bookTitle = object.getBookTitle();
                String author = object.getAuthor();
                Date publicationDate = object.getPublicationDate();
                String language = object.getLanguage();
                String frontPage = object.getFrontPage();
                String content = object.getContent();

                book.updateFields(isbn, bookTitle, author, publicationDate, language, frontPage, content);
                redisBookService.saveBook(book);
                updatedBook.set(true);
            }
        });

        if(updatedBook.get()) {
            return 1;
        }
        else{
            return 0;
        }
    }

    @Override
    public int delete(Book object) throws IOException {
        List<Book> books = redisBookService.getAllBooks();
        removeContentFile(object.getContentFilePath());
        boolean isDeleted = books.remove(object);

        if(isDeleted) {
            redisBookService.deleteBook(object);
            return 1;
        }
        else{
            return 0;
        }
    }

    private boolean removeContentFile(String path) throws IOException {
        File file = new File(path);
        boolean deleted = false;
        if(file.exists()){
            System.out.println("content file deleted");
            deleted = file.delete();
        }

        return deleted;
    }
}
