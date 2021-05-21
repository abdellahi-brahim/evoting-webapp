package actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.opensymphony.xwork2.ActionSupport;
import models.PersonModel;

public class RegisterAction extends ActionSupport {
    private static final long serialVersionUID = 1L;
    
    private transient PersonModel personBean;
    
    private List<String>userTypes;
    private List<String>faculties;
    private List<String>departments;

    public RegisterAction(){
        String url = "rmi://127.0.0.1:6666/vote";

        faculties = new ArrayList<>();
        faculties.add("Faculdade de Ciências e Tecnologias");
        faculties.add("Faculdade de Direito");

        departments = new ArrayList<>();
        departments.add("Departamento de Informática");
        departments.add("Departamento de Matemática");

        userTypes = new ArrayList<>();
        userTypes.add("Student");
        userTypes.add("Teacher");
        userTypes.add("Employee");
    }

    @Override
    public void validate(){
        if (personBean.getFirstName().length() == 0) {
            addFieldError("personBean.firstName", "First name is required.");
        }
    
        if (personBean.getLastName().length() == 0) {
            addFieldError("personBean.lastName", "Last name is required.");
        }

        if(personBean.getAddress().length() == 0){
            addFieldError("personBean.address", "Address is required.");
        }

        if(personBean.getFaculty().equals("-1")){
            addFieldError("personBean.faculty", "Faculty is required.");
        }

        if(personBean.getDepartment().equals("-1")){
            addFieldError("personBean.department", "Department is required.");
        }

        if(personBean.getType() == null){
            addFieldError("personBean.type", "Type is required.");
        }

        if(personBean.getIdValidity().before(new Date())){
            addFieldError("personBean.faculty", "Id is expired.");
        }
    }

    @Override
    public String execute() throws Exception {
        return SUCCESS;
    }
    
    public PersonModel getPersonBean() {
        return personBean;
    }
    
    public void setPersonBean(PersonModel person) {
        personBean = person;
    }

    public List<String> getFaculties(){
        return faculties;
    } 

    public List<String> getDepartments(){
        return departments;
    } 

    public List<String> getUserTypes(){
        return userTypes;
    }

    public String display() {
        return NONE;
    }
}