package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Election{
    private String title;
    private String description;
    private String type;
    private Date startDate;
    private Date endDate;
    private String faculty;
    private String department;

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
        try{
            this.startDate = formatter.parse(startDate);
        }
        catch(Exception e){
            System.out.println("Error Parsing date");
        }
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
        try{
            this.endDate = formatter.parse(endDate);
        }
        catch(Exception e){
            System.out.println("Error Parsing date");
        }
    }

    public String toString(){
        return String.format("Title: %s\nDescription: %s\nType: %s\nStart Date: %s\nEnd Date: %s\nType: %s\nFaculty: %s\nDepartment: %s", title, description, type, startDate, endDate, type, faculty, department);
    }

}