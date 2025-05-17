package org.unibl.etf.service;

import org.unibl.etf.dao.LibraryMemberDAOImpl;
import org.unibl.etf.exceptions.MemberAlreadyExistsException;
import org.unibl.etf.exceptions.MemberNotActivatedException;
import org.unibl.etf.exceptions.MemberNotFoundException;
import org.unibl.etf.model.user.LibraryUser;
import org.unibl.etf.model.user.member.LibraryMember;
import org.unibl.etf.model.user.member.LibraryMemberLoginDTO;
import org.unibl.etf.util.MyLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class LibraryMemberService {
    private LibraryMemberDAOImpl memberDAO;
    private List<LibraryUser> members;

    public LibraryMemberService() {
        this.memberDAO = new LibraryMemberDAOImpl();
        this.members = new ArrayList<>();
    }

    public List<LibraryUser> getAll(){
        try {
            return this.memberDAO.getAll();
        } catch (IOException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public int deleteMember(LibraryUser member){
        int deleted = 0;
        try{
            deleted = memberDAO.delete(member);
        } catch (IOException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        return deleted;
    }

    public boolean registerMember(LibraryMember user){
        this.members = getAll();
        boolean isRegistered = false;

        try{
            if(this.members.contains(user)){
                throw new MemberAlreadyExistsException();
            }

            this.members.add(user);
            this.memberDAO.insertAll(this.members);
            isRegistered = true;
        } catch (IOException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        return isRegistered;
    }

    public LibraryMember checkLogin(LibraryMemberLoginDTO memberLoginDTO) throws MemberNotFoundException, MemberNotActivatedException {
        this.members = this.getAll();
        //System.out.println(this.members);
        LibraryMember member = (LibraryMember) this.members.stream().filter(
                        user -> user instanceof LibraryMember && user.getUsername().equals(memberLoginDTO.getUsername()) && user.getPassword().equals(memberLoginDTO.getPassword()))
                .findFirst().orElseThrow(MemberNotFoundException::new);

        if (!member.isActiveAccount())
            throw new MemberNotActivatedException();

        //System.out.println("member from login: " + member);

        return member;
    }

    public boolean acceptRegistration(String memberUsername){
        boolean accepted = false;
        try{
            accepted = this.memberDAO.acceptRegistration(memberUsername);

        } catch (IOException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        return accepted;
    }

    public boolean rejectRegistration(String memberUsername){
        boolean rejected = false;
        try{
            rejected = this.memberDAO.rejectRegistration(memberUsername);

        } catch (IOException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        return rejected;
    }

    public boolean blockMember(String memberUsername){
        boolean blocked = false;
        try{
            blocked = this.memberDAO.blockMember(memberUsername);

        } catch (IOException e) {
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        return blocked;
    }


}
