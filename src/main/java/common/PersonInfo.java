package common;

import java.io.Serializable;

public class PersonInfo implements Serializable{
    private static final long serialVersionUID = 1L;

    private String name;
    private int phone;
    private String address;
    private CC id;
    private String username;
    private Faculty faculty;
    private String department;
    private String type;
    private boolean administrator;

    public PersonInfo(String name, int phone, String address, CC id, String username, Faculty faculty, String department, String type){
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.id = id;
        this.username = username;
        this.faculty = faculty;
        this.department = department;
        this.type = type;
        
        administrator = false;
    }

    public void setAdministrator(){
        administrator = true;
    }

    public boolean isAdministrator(){
        return administrator;
    }

    public String getType(){
        return type;
    }

    public String getName(){
        return name;
    }

    public int getPhone(){
        return phone;
    }

    public String getAddress(){
        return address;
    }

    public CC getId(){
        return id;
    }

    public String getUsername(){
        return username;
    }

    public String getDepartment(){
        return department;
    }

    public String getFaculty(){
        return faculty.getName();
    }

    public String toString(){
        return String.format("Name: %s%nPhone: %d%nAddress: %s%nUsername: %s%nId: %d%nExpire date: %s%nFaculty: %s%nDepartment: %s", this.name, this.phone, this.address, this.username, this.id.getNumber(), this.id.getValidity(), this.faculty.getName(), this.department);
    }
}