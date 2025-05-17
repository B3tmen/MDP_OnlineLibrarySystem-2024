package org.unibl.etf.dao;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.unibl.etf.model.user.LibraryUser;
import org.unibl.etf.model.user.member.LibraryMember;
import org.unibl.etf.model.user.member.LibraryUserList;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.XMLSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LibraryMemberDAOImpl implements DAO<LibraryUser> {
    private static ConfigReader config = ConfigReader.getInstance();
    private static final Path resourcePath = Paths.get(config.getUserPath());

    public LibraryMemberDAOImpl(){}


    @Override
    public LibraryUser get(int id) throws IOException {
        return null;
    }

    @Override
    public List<LibraryUser> getAll() throws IOException {
        return XMLSerializer.deserializeXML(config.getUserPath());
    }

    @Override
    public int insert(LibraryUser object) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        LibraryUserList list;

        // Read the existing XML content
        String existingContent = Files.readString(resourcePath).trim();
        if (existingContent.isEmpty()) {
            list = new LibraryUserList(new ArrayList<>());
        } else {
            list = xmlMapper.readValue(existingContent, LibraryUserList.class);
        }

        // Check for existing member with the same username
        if (list.getLibraryUsers() != null) {
            for (LibraryUser users : list.getLibraryUsers()) {
                if (users.equals(object)) {
                    return 0; // Return 0 to indicate failure due to duplicate username
                }
            }
        }

        if (list.getLibraryUsers() == null) {
            list.setUsers(new ArrayList<>());
        }
        list.getLibraryUsers().add(object);

        XMLSerializer.serializeObjectList(list.getLibraryUsers(), resourcePath.toString());

        return 1;
    }

    @Override
    public int update(LibraryUser object) throws IOException {
        return 0;
    }

    @Override
    public int delete(LibraryUser object) throws IOException {
        List<LibraryUser> members = getAll();
        boolean deleted = members.remove(object);
        if (deleted) {
            XMLSerializer.serializeObjectList(members, resourcePath.toString());
            return 1;
        }
        else {
            return 0;
        }
    }

    public int insertAll(List<LibraryUser> objects) throws IOException {
        boolean valid = XMLSerializer.serializeObjectList(objects, resourcePath.toString());

        if (valid) {
            return 1;
        }
        else{
            return 0;
        }
    }

    public boolean acceptRegistration(String memberUsername) throws IOException {
        boolean accepted = false;
        List<LibraryUser> users = getAll();
        for (LibraryUser user : users) {
            if(user instanceof LibraryMember member) {
                if (member.getUsername().equals(memberUsername)) {
                    member.setActiveAccount(true);
                    accepted = true;
                    break;
                }
            }

        }

        XMLSerializer.serializeObjectList(users, resourcePath.toString());

        return accepted;
    }

    public boolean rejectRegistration(String memberUsername) throws IOException {
        boolean rejected = false;
        List<LibraryUser> users = getAll();
        for (LibraryUser user : users) {
            if(user instanceof LibraryMember member) {
                if (member.getUsername().equals(memberUsername)) {
                    member.setActiveAccount(false);
                    rejected = true;
                    break;
                }
            }

        }

        XMLSerializer.serializeObjectList(users, resourcePath.toString());

        return rejected;
    }

    public boolean blockMember(String memberUsername) throws IOException {
        boolean blocked = false;
        List<LibraryUser> users = getAll();
        for (LibraryUser user : users) {
            if(user instanceof LibraryMember member) {

                if (member.getUsername().equals(memberUsername)) {
                    member.setActiveAccount(false);
                    member.setBlocked(true);
                    blocked = true;
                    break;
                }
            }


        }

        XMLSerializer.serializeObjectList(users, resourcePath.toString());

        return blocked;
    }
}
