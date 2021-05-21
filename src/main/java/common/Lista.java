package common;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.Serializable;

public class Lista implements Serializable{
    private static final long serialVersionUID = 1L;
    private String name;

    private List<Person>members;

    public Lista(String name){
        this.name = name;
        this.members = new CopyOnWriteArrayList<>();
    }

    public String getName(){
        return name;
    }

    public void addMember(Person member){
        members.add(member);
    } 

    public List<Person> getMembers(){
        return members;
    }
}