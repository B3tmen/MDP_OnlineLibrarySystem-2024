package org.unibl.etf.util.book;

import org.unibl.etf.util.MyLogger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;

public class BookDownloader {
    private static final String FRONT_PAGE_IMG_PATH = "SupplierServer/src/main/resources/books/front_pages/";

    public static String downloadBookContentFromURL(String bookURL){
        URL book = null;
        String content = "";
        BufferedReader br = null;
        try{
            book = new URL(bookURL);
            br = new BufferedReader(new InputStreamReader(book.openStream()));

            String inputLine = "";
            while((inputLine = br.readLine()) != null){
                content += inputLine + "\n";
            }

            br.close();
        } catch (IOException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }


        return content;
    }

    public static String downloadFrontPageImageFromURL(String bookName, String frontPageURL){
        String filename = FRONT_PAGE_IMG_PATH + bookName + ".jpg";
        File file = null;
        try{
            URL url = new URL(frontPageURL);
            BufferedImage bufferedImage = ImageIO.read(url);
            file = new File(filename);

            ImageIO.write(bufferedImage, "jpg", file);

            return file.getAbsolutePath();
        } catch (IOException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();

            return null;
        }
    }
}
