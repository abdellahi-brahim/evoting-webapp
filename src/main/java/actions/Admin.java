package actions;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionSupport;

import org.apache.struts2.interceptor.SessionAware;

import common.ElectionInfo;
import common.Faculty;
import common.PersonInfo;
import common.ServerI;

import models.Person;
import models.Election;
import models.Lista;

public class Admin extends ActionSupport implements SessionAware{
    private static final long serialVersionUID = 1L;

    private transient Map<String, Object> session;
    
    private transient Person person;
    private transient Election election;
    private transient Lista list;

    /*Displayed data*/
    private List<String>userTypes;
    private List<String>faculties;
    private List<String>departments;
    private List<String>users;
    private List<String>people;
    private List<String>onGoing;
    private List<String>editElections;

    /*Server Data*/
    private List<PersonInfo>rawPeople;
    private List<Faculty>rawFaculties;
    private List<ElectionInfo>rawOnGoing;
    private List<ElectionInfo>rawEditElections;

    public Admin(){
        faculties = new ArrayList<>();
        departments = new ArrayList<>();
        userTypes = new ArrayList<>();
        users = new ArrayList<>();
        people = new ArrayList<>();
        onGoing = new ArrayList<>();
        editElections = new ArrayList<>();
        people = new ArrayList<>();
    }

    private void staticInit(){
        faculties.add("Faculdade de Ciências e Tecnologias");
        faculties.add("Faculdade de Direito");
        departments.add("Departamento de Informática");
        departments.add("Departamento de Matemática");
        userTypes.add("Student");
        userTypes.add("Teacher");
        userTypes.add("Employee");
    }

    public String createUser(){
        boolean invalid = false;

        if (person.getFirstName().length() == 0) {
            addFieldError("person.firstName", "First name is required.");
            invalid = true;
        }
    
        if (person.getLastName().length() == 0) {
            addFieldError("person.lastName", "Last name is required.");
            invalid = true;
        }

        if(person.getAddress().length() == 0){
            addFieldError("person.address", "Address is required.");
            invalid = true;
        }

        if(person.getFaculty().equals("-1")){
            addFieldError("person.faculty", "Faculty is required.");
            invalid = true;
        }

        if(person.getDepartment().equals("-1")){
            addFieldError("person.department", "Department is required.");
            invalid = true;
        }

        if(person.getType() == null){
            addFieldError("person.type", "Type is required.");
            invalid = true;
        }

        if(person.getIdValidity().before(new Date())){
            addFieldError("person.faculty", "Id is expired.");
            invalid = true;
        }

        if(invalid){
            return INPUT;
        }

        ServerI server = (ServerI)session.get("Connection");
        
        try{
            server.newPerson(
                person.getFirstName() + " " + person.getLastName(), 
                person.getPhoneNumber(),
                person.getAddress(),
                person.getIdNumber(), 
                person.getIdValidity(), 
                person.getPassword(),
                person.getUserName(), 
                person.getFaculty(), 
                person.getDepartment(), 
                person.getType());

            return SUCCESS;
        }
        catch(RemoteException e){
            return "failure";
        }
    }

    public String createElection(){
        boolean invalid = false;

        if(election.getTitle().length() == 0){
            addFieldError("election.faculty", "Title is required.");
            invalid = true;
        }

        if(election.getDescription().length() == 0){
            addFieldError("election.faculty", "Description is required.");
            invalid = true;
        }

        if(election.getStartDate().before(new Date())){
            addFieldError("election.faculty", "Start date must be after today.");
            invalid = true;
        }

        if(election.getEndDate().after(new Date())){
            addFieldError("election.faculty", "End date must be before today.");
            invalid = true;
        }

        if(person.getFaculty().equals("-1")){
            addFieldError("election.faculty", "Faculty is required.");
            invalid = true;
        }

        if(person.getDepartment().equals("-1")){
            addFieldError("election.department", "Department is required.");
            invalid = true;
        }

        if(person.getType() == null){
            addFieldError("election.type", "Type is required.");
            invalid = true;
        }

        if(invalid){
            return INPUT;
        }

        if(election.getStartTime() != null){
            election.getStartDate().setHours(election.getStartTime().getHours());
            election.getStartDate().setMinutes(election.getStartTime().getMinutes());
        }

        if(election.getEndTime() != null){
            election.getEndDate().setHours(election.getEndTime().getHours());
            election.getEndDate().setMinutes(election.getEndTime().getMinutes());
        }   

        ServerI server = (ServerI)session.get("Connection");
        
        try{
            server.newElection(election.getTitle(), 
                election.getDescription(), 
                election.getStartDate(),
                election.getEndDate(),
                election.getType());

            return SUCCESS;
        }
        catch(RemoteException e){
            return "failure";
        }
    }

    public String createList(){
        return SUCCESS;
    }

    @Override
    public String execute() throws Exception {
        ServerI server = (ServerI)session.get("Connection");

        /*Retrieve data from remote server*/
        try{
            rawPeople = server.getUsers();
            rawOnGoing = server.getOnGoingElections();
            rawEditElections = server.getEditableElections();
            rawFaculties = server.getFaculties();
        }
        catch(RemoteException e){
            staticInit();
        }

        /*Send data to lists with info*/
        for(PersonInfo p: rawPeople){
            users.add(p.getName());
        }

        for(ElectionInfo el: rawOnGoing){
            onGoing.add(el.getTitle());
        }

        for(ElectionInfo el: rawEditElections){
            editElections.add(el.getTitle());
        }

        for(Faculty faculty: rawFaculties){
            editElections.add(faculty.getName());
        }

        return SUCCESS;
    }

    /*Getters and Setters*/
    public void setPerson(Person person) {
        this.person = person;
    }
    
    public Person getPerson() {
        return person;
    }

    public Lista getLista() {
		return list;
	}

    public void setLista(Lista list) {
		this.list = list;
	}
    
    public void setElection(Election election) {
        this.election = election;
    }

    public Election getElection() {
        return election;
    }
    /*Lists getters*/
    public List<String>getUsers(){
        return users;
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

    public List<String>getOnGoing(){
        return onGoing;
    }

    public String display() {
        return NONE;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}