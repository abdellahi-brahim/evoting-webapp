package models;

import java.io.Serializable;
import java.util.Date;

public class Person implements Serializable{
    private String firstName;
    private String lastName;
    private int phoneNumber;
    private String address;
    private int idNumber;
    private Date idValidity;
    private String userName;
    private String password;
    private String faculty;
    private String department;
    private String type;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getPhoneNumber(){
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getAddress(){
        return address;
    }

    public int getIdNumber(){
        return idNumber;
    }

    public void setIdNumber(int idNumber){
        this.idNumber = idNumber;
    }

    public Date getIdValidity(){
        return idValidity;
    }

    public void setIdValidity(Date idValidity){
        this.idValidity = idValidity;
    }
    
    public String getUserName(){
        return userName;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getFaculty(){
        return faculty;
    }

    public void setFaculty(String faculty){
        this.faculty = faculty;
    }

    public String getDepartment(){
        return department;
    }

    public void setDepartment(String department){
        this.department = department;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String toString() {
        return String.format("Name: %s %s\nPhone number: %d\nAddress: %s\nId number: %d\nId validity: %s\nUsername: %s\nPassword: %s\nFaculty: %s\nDepartment: %s\nType: %s", firstName, lastName, phoneNumber, address, idNumber, idValidity, userName, password, faculty, department, type);
    }
}