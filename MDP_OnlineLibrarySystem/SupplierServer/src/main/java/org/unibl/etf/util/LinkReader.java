package org.unibl.etf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

public class LinkReader {
    private static final ConfigReader config = ConfigReader.getInstance();

    public static ArrayList<String> readLinksFromFile(){
        String fileName = config.getBookLinksPath();
        System.out.println("filenname: " + fileName);

        ArrayList<String> links = new ArrayList<>();

        try{
            File file = new File(fileName);
            if(file.exists()){
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = "";
                while((line = br.readLine()) != null ){
                    links.add(line);
                }
            }
            else{
                return null;
            }

        } catch (IOException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
            return null;
        }

        return links;
    }
}
