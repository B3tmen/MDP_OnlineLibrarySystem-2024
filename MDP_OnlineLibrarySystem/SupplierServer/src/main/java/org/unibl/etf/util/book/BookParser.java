package org.unibl.etf.util.book;

import org.unibl.etf.model.Book;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookParser {
    private static final String TITLE_PREFIX = "Title";
    private static final String AUTHOR_PREFIX = "Author";
    private static final String RELEASE_DATE_PREFIX = "Release date";
    private static final String LANGUAGE_PREFIX = "Language";
    private static final String FRONT_PAGE_IMAGE_SUFFIX = ".cover.medium.jpg";
    //private static final String TITLE_PREFIX = "Title:";

    private String bookPageContent;
    private String bookURL;

    public BookParser(String bookURL) {
        this.bookURL = bookURL;
        this.bookPageContent = BookDownloader.downloadBookContentFromURL(bookURL);
    }

    public Book parseBookFromContent(){
        System.out.println();
        //System.out.println(bookPageContent);
        System.out.println();

        String titlePattern = "^Title: (.+)$";
        String authorPattern = "^Author: (.+)$";
        String languagePattern = "^Language: (.+)$";
        String releaseDatePattern = "^Release date: (.+)$";


        String bookTitle = getBookComponentFromRegex(Pattern.compile(titlePattern, Pattern.MULTILINE), TITLE_PREFIX);
        String author = getBookComponentFromRegex(Pattern.compile(authorPattern, Pattern.MULTILINE), AUTHOR_PREFIX);
        Date publicationDate = getReleaseDateFromRegex(Pattern.compile(releaseDatePattern, Pattern.MULTILINE), RELEASE_DATE_PREFIX);
        String language = getBookComponentFromRegex(Pattern.compile(languagePattern, Pattern.MULTILINE), LANGUAGE_PREFIX);

        String parts[] = bookURL.split(".txt");
        String frontPage = parts[0] + FRONT_PAGE_IMAGE_SUFFIX;

        String contentPattern = "(?<=\\*\\*\\* START OF THE PROJECT GUTENBERG EBOOK " + bookTitle.toUpperCase() + " \\*\\*\\*)[\\s\\S]*?(?=\\*\\*\\* END OF THE PROJECT GUTENBERG EBOOK " + bookTitle.toUpperCase() + " \\*\\*\\*)";
        String content = getBookComponentFromRegex(Pattern.compile(contentPattern, Pattern.MULTILINE), "Content");

        return new Book(bookTitle, author, publicationDate, language, frontPage, content);
    }

    private String getBookComponentFromRegex(Pattern pattern, String label){
        Matcher matcher = pattern.matcher(bookPageContent);
        String component = "";
        if (matcher.find()) {
            if("Content".equals(label)){
                component = matcher.group(0);
            }
            else{
                component = matcher.group(1);       // because of (.)+
                System.out.println(label + ": " + component);
            }
        } else {
            System.out.println(label + " not found.");
        }

        return component;
    }

    private Date getReleaseDateFromRegex(Pattern pattern, String label) {
        Matcher matcher = pattern.matcher(bookPageContent);
        Date date = null;
        if (matcher.find()) {
            String dateString = matcher.group(1);
            //System.out.println(label + ": " + dateString);
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                date = formatter.parse(dateString);
                //System.out.println("Parsed Date: " + date);
            } catch (Exception e) {
                System.out.println("Failed to parse date: " + e.getMessage());
            }
        } else {
            System.out.println(label + " not found.");
        }

        return date;
    }



}
