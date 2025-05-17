package org.unibl.etf.server;

import org.unibl.etf.model.Book;
import org.unibl.etf.model.Supplier;
import org.unibl.etf.temp.RedisBookService;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.ISBNGenerator;
import org.unibl.etf.util.LinkReader;
import org.unibl.etf.util.MyLogger;
import org.unibl.etf.util.book.BookDownloader;
import org.unibl.etf.util.book.BookParser;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;

public class SupplierServer {
    private static final ConfigReader configReader = ConfigReader.getInstance();

    public static void main(String[] args) {
        int port = configReader.getSupplierServerPort();

        //generateTestBooks();

        startServer(port);
    }

    private static void startServer(int port){
        System.out.println("[SupplierServer]: Server started on port - " + port);

        try(ServerSocket ss = new ServerSocket(port)){
            while(true){
                Socket socket = ss.accept();

                new SupplierServerThread(new ClientHandler(socket));
            }
        } catch (IOException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void generateTestBooks(){
        List<String> links = LinkReader.readLinksFromFile();
        String bookUrl = links.get(0);

        RedisBookService redisBookService = new RedisBookService();
        for (String link : links) {
            BookParser parser = new BookParser(link);
            Book book = parser.parseBookFromContent();
            String frontPageImageUrl = BookDownloader.downloadFrontPageImageFromURL(book.getBookTitle(), book.getFrontPage());
            System.out.println("front page url: " + frontPageImageUrl);
            book.setFrontPage(frontPageImageUrl);

            redisBookService.saveBook(book);
            //System.out.println("book was saved content is: \n" + book.getContent());
        }
    }
}
