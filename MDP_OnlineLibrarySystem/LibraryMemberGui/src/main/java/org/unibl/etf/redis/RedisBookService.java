package org.unibl.etf.redis;

import com.google.gson.Gson;
import org.unibl.etf.model.book.Book;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.ArrayList;
import java.util.List;

public class RedisBookService {
    private RedisConnection redisConnection;

    public RedisBookService() {
        this.redisConnection = new RedisConnection();
    }

    public void saveBook(Book book){
        String bookKey = "book:" + book.getIsbn();
        Gson gson = new Gson();
        String bookJson = gson.toJson(book);
        redisConnection.getJedis().set(bookKey, bookJson);
    }

    public List<Book> getAllBooks(){
        // Initialize cursor for SCAN
        String cursor = "0";
        List<Book> books = new ArrayList<>();
        Gson gson = new Gson();
        Jedis jedis = redisConnection.getJedis();

        do {
            // Set SCAN parameters to match book keys
            ScanParams scanParams = new ScanParams().match("book:*");
            ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
            cursor = scanResult.getCursor();

            for (String key : scanResult.getResult()) {
                String json = jedis.get(key);
                Book book = gson.fromJson(json, Book.class);
                books.add(book);
            }
        } while (!cursor.equals("0"));

        return books;
    }
}
