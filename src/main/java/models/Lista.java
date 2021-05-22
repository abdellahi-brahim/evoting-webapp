package models;

import java.util.ArrayList;
import java.util.List;

import common.PersonInfo;

public class Lista {
    private String name;
    private List<PersonInfo> members;

    public List<PersonInfo> getMembers(){
        return members;
    }

    public void setMembers(List<PersonInfo>members){
        this.members = new ArrayList<>(members);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
