package org.unibl.etf.util;

import com.google.gson.Gson;
import org.unibl.etf.model.book.Book;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipArchiver {
    public static final String ZIP_PATH = "LibraryMemberGui/src/main/resources/book-zips/";

    /**ZIPs a list of books into json files
     * @Returns The absolute path of the zipped file
     * */
    public static String zipBooks(List<Book> books, String username) {
        Gson gson = new Gson();
        String zipPath = ZIP_PATH + username + ".zip";

        try (FileOutputStream fos = new FileOutputStream(zipPath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (Book book : books) {
                String json = gson.toJson(book);
                ZipEntry zipEntry = new ZipEntry("book_" + book.getIsbn() + ".json");       // Adding book info

                File file = new File(book.getContentFilePath());        // Adding book content text file
                if (file.exists()) {
                    ZipEntry zipEntryContent = new ZipEntry(file.getName());
                    zos.putNextEntry(zipEntryContent);

                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            zos.write(buffer, 0, bytesRead);
                        }
                    }
                }

                zos.putNextEntry(zipEntry);
                zos.write(json.getBytes());
                zos.closeEntry();
            }

        } catch (IOException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        return Paths.get(zipPath).toAbsolutePath().toString();
    }
}
