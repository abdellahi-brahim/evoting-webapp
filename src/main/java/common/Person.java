package common;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class Person implements Serializable{
    private static final long serialVersionUID = 1L;
    private List<Vote>votes;
    private PersonInfo info;
    private String password;

    public Person(String name, int phone, String address, int id, Date expire, String password, String username, Faculty faculty, String department, String type){
        votes = new CopyOnWriteArrayList<>();
        info = new PersonInfo(name, phone, address, new CC(id, expire), username, faculty, department, type);

        this.password = password;
    }

    public boolean isAdministrator(){
        return info.isAdministrator();
    }

    public List<Vote> getElections(){
        return votes;
    }
    public void addElection(Vote vote){
        votes.add(vote);
    }

    public PersonInfo getInfo(){
        return info;
    }

    public boolean login(String password){
        return this.password.equals(password);
    }

    public String toString(){
        return String.format("%s%nPassword: %s", this.info.toString(), this.password);
    }
}