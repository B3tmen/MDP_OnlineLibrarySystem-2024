package org.unibl.etf.util;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.unibl.etf.model.user.LibraryUser;
import org.unibl.etf.model.user.member.LibraryMember;
import org.unibl.etf.model.user.member.LibraryUserList;

import java.io.*;
import java.util.List;
import java.util.logging.Level;

public class XMLSerializer {
    private static final XmlMapper xmlMapper = new XmlMapper();

    static{
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

//    public static boolean serializeObjectList(List<LibraryUser> users, String filePath) {
//        LibraryUserList list = new LibraryUserList(users);
//        try {
//            xmlMapper.writeValue(new File(filePath), list);
//            return true;
//        } catch (IOException e) {
//            MyLogger.logger.log(Level.SEVERE, e.getMessage());
//            e.printStackTrace();
//            return false;
//        }
//    }

    public static boolean serializeObjectList(List<LibraryUser> users, String filePath) {
        LibraryUserList list = new LibraryUserList(users);
        try {
            // Get the file from resources directory
            File file = new File(getResourceFilePath(filePath));

            xmlMapper.writeValue(file, list);
            return true;
        } catch (IOException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<LibraryUser> deserializeXML(String filePath) {
        try (InputStream inputStream = XMLSerializer.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found: " + filePath);
            }
            LibraryUserList list = xmlMapper.readValue(inputStream, LibraryUserList.class);
            list.getLibraryUsers().forEach(System.out::println);
            return list.getLibraryUsers();
        }
        catch (IOException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static String getResourceFilePath(String filename) {
        // Get the path to the resources directory
        String resourcesPath = XMLSerializer.class.getClassLoader().getResource("").getPath();
        System.out.println(resourcesPath + filename);
        return resourcesPath + filename;
    }
}
