package rmi;

import common.Faculty;
import common.Election;
import common.ElectionInfo;
import common.Lista;
import common.Person;
import common.PersonInfo;
import common.Candidate;

import common.ServerI;
import common.Vote;
import common.RemoteObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.Registry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class Station implements Serializable{
    private static final long serialVersionUID = 1L;

    private Faculty faculty;
    private String department;
    private String id;
    private transient RemoteObject remote;

    public Station(Faculty faculty, String department, String id){
        this.faculty = faculty;
        this.department = department;
        this.id = id;
    }

    public Station(Faculty faculty, String department, String id, RemoteObject remote){
        this.faculty = faculty;
        this.department = department;
        this.id = id;
        this.remote = remote;
    }
    
    /** 
     * @return RemoteObject
     */
    public RemoteObject getRemote(){
        return remote;
    }
    
    /** 
     * @return Faculty
     */
    public Faculty getFaculty(){
        return faculty;
    }

    /** 
     * @return String
     */
    public String getDepartment(){
        return department;
    }
    
    /** 
     * @return String
     */
    public String getId(){
        return id;
    }
}

class Data implements Serializable{
    private static final long serialVersionUID = 1L;

    private List<Person>users;
    private List<Election>elections;
    private List<Lista>lists;
    private List<Faculty>faculties;

    public Data(){
        users = new CopyOnWriteArrayList<>();
        elections = new CopyOnWriteArrayList<>();
        lists = new CopyOnWriteArrayList<>();
        faculties = new CopyOnWriteArrayList<>();
    }

    public void addUser(Person user){
        users.add(user);
    }
    
    public void addElection(Election election){
        elections.add(election);
    }

    public void addList(Lista list){
        lists.add(list);
    }

    public void addFaculty(Faculty faculty){
        faculties.add(faculty);
    }

    public List<Person>getUsers(){
        return users;
    }

    public List<Election>getElections(){
        return elections;
    }

    public List<Lista>getLists(){
        return lists;
    }

    public List<Faculty>getFaculties(){
        return faculties;
    }
}

public class Server extends UnicastRemoteObject implements ServerI{
    private static final long serialVersionUID = 1L;

    private transient List<RemoteObject>adminConsoles;

    private List<Station>stations;
    private List<Election>onGoing;

    private transient Timer schedule;
    private Semaphore semaphore;
    private Election nextElectionScheduled;

    private Data data;
    /**
     * Initializes RMI Server and loads previous data save
     * @throws RemoteException
     */
    public Server() throws RemoteException{
        semaphore = new Semaphore(1);

        adminConsoles = new CopyOnWriteArrayList<>();
        stations = new CopyOnWriteArrayList<>();
        onGoing = new CopyOnWriteArrayList<>();
        stations = new CopyOnWriteArrayList<>();

        if(!loadFromObjectFile()){
            data = new Data();
            loadDataFromTextFile();
            exportToObjectFile();
        }
    }

    /**
     * Binds RMI server to an initialized registry, and provides the service name
     * @param r
     * @param service
     * @throws RemoteException
     */

    public Server(String url) throws RemoteException, MalformedURLException{
        this();
        Naming.rebind(url, this);
    }

    public Server(Registry r, String service) throws RemoteException {
        this();
        rebind(r, service);
    }

    /**
     * Binds this server with Registry r and name it service
     * @param r
     * @param service
     * @throws RemoteException
     */
    public void rebind(Registry r, String service) throws RemoteException {
        r.rebind(service, this);
    }

    class Notification extends TimerTask{
        public void run(){
            String message = String.format("Election %s has finished", nextElectionScheduled.getTitle());
            
            notifyAdminConsoles(message);
            System.out.println(message);

            onGoing.remove(nextElectionScheduled);

            if(!onGoing.isEmpty()){
                nextElectionScheduled = onGoing.get(0);
                setSchedule(nextElectionScheduled.getEnd());
            }
            else{
                nextElectionScheduled = null;
            }
        }
    }

    private void setSchedule(Date time){
        System.out.printf("Setting scheduler to %s\n", time.toString());
        schedule = new Timer();
        schedule.schedule(new Notification(), time);
    }

    /**
     * 
     * Method to update data
     */
    public void updateData(){
        try{
            semaphore.acquire();
        }
        catch(InterruptedException e){
            System.out.println("Semaphore failed");
            Thread.currentThread().interrupt();
        }
        //Sort out on going elections by date
        Date now = new Date();
        Predicate<Election> byDate = election -> election.getStart().before(now) && election.getEnd().after(now);
        onGoing = data.getElections().stream().filter(byDate).collect(Collectors.toCollection(CopyOnWriteArrayList::new));

        if(!onGoing.isEmpty()){
            Election election = onGoing.get(0);
            if(nextElectionScheduled != election){
                if(schedule != null) schedule.cancel();
                nextElectionScheduled = election;
                setSchedule(election.getEnd());
                System.out.printf("Election %s scheduled!\n", election.getTitle());
            }
        }
        //Export data to ObjectFile
        exportToObjectFile();

        System.out.println("Data Updated");
    }
    /**
     * Prints all data that the RMI Server has stored
     */
    private void printServerData(){
        System.out.println("-----USERS-----\n");
        for(Person person: data.getUsers()){
            System.out.println(person);
        }
        System.out.println("\n\n-----ELECTIONS-----\n");
        for(Election election : data.getElections()) {
            System.out.println(election);
        }
        System.out.println("\n\n-----LISTS-----\n");
        for(Lista list: data.getLists()){
            System.out.println(list);
        }
        System.out.println("\n\n-----FACULTIES-----\n");
        for(Faculty faculty: data.getFaculties()){
            System.out.println(faculty);
        }
    }
    /**
     * Loads faculties from text file
     */
    private void loadFacultiesFromTextFile(String facultyPath){
        try{
            //Load User Names
            Locale loc = new Locale("pt", "PT");
            Path path = Paths.get(facultyPath);
            Scanner scanner = new Scanner(path, StandardCharsets.UTF_8.name());
            scanner.useLocale(loc);

            String current = scanner.nextLine();;
            
            while(scanner.hasNextLine()){
                if(current.equals("F")){
                    Faculty faculty = new Faculty(scanner.nextLine());
                    current = scanner.nextLine();
                    while(!current.equals("F") && scanner.hasNextLine()){
                        faculty.addDepartment(current);
                        current = scanner.nextLine();
                    }
                    this.data.addFaculty(faculty);
                }
            }
            scanner.close();
        }
        catch(IOException e){
            System.out.println("Faculties loading failed...");
        }
    }
    /**
     * Loads previous saves in object file to Server.data
     * @return true if succeeded false if not
    */
    private boolean loadFromObjectFile(){
        try(
            FileInputStream f = new FileInputStream(new File("rmi/serverData")); 
            ObjectInputStream o = new ObjectInputStream(f)
        ){
            this.data = (Data)o.readObject();
        } 
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Error!");
            return false;
        }
        return true;
    }
    /**
     * Saves data in object file
     */
    private void exportToObjectFile(){
        try(
            FileOutputStream f = new FileOutputStream("rmi/serverData");
            ObjectOutputStream o = new ObjectOutputStream(f)
        ){
            o.writeObject(data);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        }

        System.out.println("Saved!");
    }
    
    private void loadDataFromTextFile(){
        String facultyPath = "rmi/faculties";
        String usersPath = "rmi/users";
        
        loadFacultiesFromTextFile(facultyPath);
        loadUsersFromTextFile(usersPath);

        createLists();
        createElections();
    }

    private void loadUsersFromTextFile(String path){
        File file = new File(path);

        try {
            Scanner sc = new Scanner(file, StandardCharsets.UTF_8.name());

            while (sc.hasNextLine()){
                String type = sc.nextLine();
                try {
                    String name = sc.nextLine();
                    String aux = sc.nextLine();
                    int phone = Integer.parseInt(aux);
                    aux = sc.nextLine();
                    int id = Integer.parseInt(aux);
                    Date expire = new SimpleDateFormat("dd-MM-yyyy").parse(sc.nextLine());
                    String address = sc.nextLine();
                    String username = sc.nextLine();
                    String password = sc.nextLine();
                    aux = sc.nextLine();
                    String department = sc.nextLine();

                    Faculty faculty = getFaculty(aux, department);

                    if(faculty == null) continue;

                    data.addUser(new Person(name, phone, address, id, expire, password, username, faculty, department, type));

                } catch (ParseException e){
                    System.out.println("Error reading user info");
                }
            }
            sc.close();

        } catch (IOException e){
            System.out.println("File not found");
        }
    }

    private void createLists() {
        Lista a = new Lista("A");
        int i = 0;
        data.addList(a);
        for (Person person: data.getUsers()){
            
            if (i < 4){
                a.addMember(person);
            }
            i++;
        }
        Lista b = new Lista("B");
        data.addList(b);
        i = 0;
        for (Person person: data.getUsers()){
            
            if (i > 4 && i < 10){
                b.addMember(person);
            }
            i++;
        }
        Lista c = new Lista("C");
        data.addList(c);
        i = 0;
        for (Person person: data.getUsers()){

            if (i > 10 && i < 15)
                c.addMember(person);
            i++;
        }
    }

    private void createElections(){
        try {
            Date begin = new SimpleDateFormat("dd-MM-yyyy").parse("01-04-2021");
            Date end = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse("10-04-2021 12:06");

            Election election = new Election("A. Estudantes", "description", begin, end, "Student");
            data.addElection(election);
            for (Lista l : data.getLists()){
                election.addList(l);
            }
            
            election = new Election("N. Estudantes", "description", begin, end, "Student");
            data.addElection(election);
            int i = 0;
            for (Lista l : data.getLists()){
                if (i < 2)
                    election.addList(l);
                i++;
            }
        } catch (ParseException e ){
            System.out.println("Couldn't create elections");
        }
    }

    private Faculty getFaculty(String faculty, String department){
        for(Faculty fac: data.getFaculties()){
            if(fac.getName().equals(faculty)){
                for(String dep: fac.getDepartments()){
                    if(dep.equals(department)){
                        return fac;
                    }
                }
            }
        }
        return null;
    }
    /**
     * Gets all stations connected to RMI Server and returns their location(Faculty, Department)
     * @return String 
     */
    public String stationsToString() throws RemoteException{
        String out = "";
        int index = 1;

        for(Station station: stations){
            out += String.format("\n-> Station %d\n\tFaculty: %s\n\tDepartment: %s\n", index, station.getFaculty().getName(), station.getDepartment());
            index+=1;
        }

        return out;
    }

    /**
     * Get all users from data
     */
    public List<PersonInfo> getUsers() throws RemoteException{
        CopyOnWriteArrayList<PersonInfo> info = new CopyOnWriteArrayList<>();

        for(Person user: data.getUsers()){
            info.add(user.getInfo());
        }

        return info;
    }
    /**
     * Function tho check connection
     * @return Returns current date in TIMESTAMP format
     */
    public long isConnected() throws RemoteException{
        return new Date().getTime();
    }

    /**
     * Function that calls Remote Method from all Stations
     * to check if they are connected
     */
    public void checkStationsConnection(){
        for(Station station: stations){
            try{
                station.getRemote().isConnected();
            }
            catch(RemoteException e){
                stations.remove(station);
            }
        }
    }

    /**
     * Broadcasts message to all connected AdminConsoles
     * @param message
    */
    public void notifyAdminConsoles(String message){
        for(RemoteObject adminConsole: adminConsoles){
            try{
                adminConsole.send(message);
            }
            catch(RemoteException e){
                adminConsoles.remove(adminConsole);
            }
        }
    }

    /**
     * Subscribes Admin Console to Server Notification System
     */
    public void subscribeAdminConsole(RemoteObject obj) throws RemoteException{
        adminConsoles.add(obj);
        semaphore.release();
    }

    /**
     * Subscribes station to RMI Server
     */
    public boolean addStation(String faculty, String department, String id, RemoteObject remote) throws RemoteException {
        for(Station station: stations){
            if(station.getFaculty().getName().equals(faculty) && station.getDepartment().equals(department))
                return false;
        }

        for(Faculty fac: data.getFaculties()){
            if(fac.getName().equals(faculty)){
                stations.add(new Station(fac, department, id, remote));
                semaphore.release();
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if given department in faculty already has department 
     */
    public boolean hasStation(String faculty, String department) throws RemoteException {
        for(Station station: stations){
            if(station.getFaculty().getName().equals(faculty) && station.getDepartment().equals(department))
                return true;
        }
        return false;
    }

    /**
     * Reconnect Station to Rmi Server Subscription
     */
    public boolean reconnectStation(String faculty, String department, String id) throws RemoteException{
        for(Station station: stations){
            if(station.getFaculty().getName().equals(faculty) && station.getDepartment().equals(department) && station.getId().equals(id))
                return true;
        }
        return false;
    }

    /**
     * Checks if Person username and password match
     */
    public boolean login(String username, String password) throws RemoteException{
        for(Person person: data.getUsers()){
            if(person.getInfo().getUsername().equals(username)){
                return person.login(password);
            }
        }
        return false;
    }

    public boolean login(int id, String username, String password) throws RemoteException{
        Person person = queryPersonById(id);

        if(person == null)
            return false;

        return person.getInfo().getUsername().equals(username) && person.login(password);
    }

    /**
     * Query Person by id
     * @param id
     * @return
     */
    private Person queryPersonById(int id){
        for(Person person: data.getUsers()){
            if(person.getInfo().getId().getNumber() == id){
                return person;
            }
        }
        return null;
    }

    /**
     * Gets all candidates for an election
     */
    public List<String> getElectionLists(String title) throws RemoteException{
        for(Election election: data.getElections()){
            if(election.getTitle().equals(title)){
                return election.getLists();
            }
        }
        return Collections.emptyList();
    }

    public void newPerson(String name, int phone, String address, int id, Date expire, String password, String username, Faculty faculty, String department, String type) throws RemoteException{
        Person person = new Person(name, phone, address, id, expire, password, username, faculty, department, type);
        data.addUser(person);
        semaphore.release();
    }

    public void newPerson(String name, int phone, String address, int id, Date expire, String password, String username, String faculty, String department, String type) throws RemoteException{
        for(Faculty f: data.getFaculties()){
            if(f.getName().equals(faculty)){
                Person person = new Person(name, phone, address, id, expire, password, username, f, department, type);
                data.addUser(person);
                semaphore.release();
                return;
            }
        }
    }
    /**
     * Creates a new election
     */
    public void newElection(String title, String description, Date start, Date end, String type) throws RemoteException{
        data.getElections().add(new Election(title, description, start, end, type));
        semaphore.release();
    }
    /**
     * Remote method to query a person by id
     */
    public PersonInfo getPersonById(int id) throws RemoteException{
        Person person = queryPersonById(id);

        if(person == null){
            return null;
        }

        return person.getInfo();
    }

    public PersonInfo getPersonByUsername(String username) throws RemoteException{
        for(Person person: data.getUsers()){
            if(person.getInfo().getUsername().equals(username)){
                return person.getInfo();
            }
        }
        return null;
    }
    /**
     * Get all elections
     */
    public List<ElectionInfo> getElections() throws RemoteException{
        List<ElectionInfo>elections = new ArrayList<>();

        for(Election election: data.getElections()){
            elections.add(election.getInfo());
        }

        return elections;
    }

    public List<ElectionInfo> getOnGoingElections() throws RemoteException{
        List<ElectionInfo>elections = new ArrayList<>();

        for(Election election: onGoing){
            elections.add(election.getInfo());
        }
        
        return elections;
    }

    public List<ElectionInfo> getEditableElections() throws RemoteException{
        List<ElectionInfo>elections = new ArrayList<>();

        Date now = new Date();

        for(Election election: data.getElections()){
            if(election.getStart().before(now)){
                elections.add(election.getInfo());
            }
        }

        return elections;
    }

    public List<ElectionInfo> getOnGoingElections(int id, String type) throws RemoteException{
        Person person = queryPersonById(id);

        if(person == null){
            return Collections.emptyList();
        }

        List<ElectionInfo>elections = new ArrayList<>();

        for(Election election: onGoing){
            if(election.getType().equals(type)){
                elections.add(election.getInfo());
            }
        }

        for(Vote vote: person.getElections()){
            if(elections.contains(vote.getElection().getInfo())){
                elections.remove(vote.getElection().getInfo());
            }
        }

        return elections;
    }
    /**
     * Change a title of election
     */
    public void changeTitle(String title, String newTitle) throws RemoteException{
        for(Election election: data.getElections()){
            if(election.getTitle().equals(title)){
                election.setTitle(newTitle);
                semaphore.release();
                return;
            }
        }
    }
    /**
     * Change the description of an election
     */
    public void changeDescription(String title, String description) throws RemoteException{
        for(Election election: data.getElections()){
            if(election.getTitle().equals(title)){
                election.setDescription(description);
                semaphore.release();
                return;
            }
        }
    }
    /**
     * Adds a list to an elections
     */
    public void addListElection(String title, String list) throws RemoteException{
        for(Election e: data.getElections()){
            if(e.getTitle().equals(title)){
                //Check if list is already in election
                for(Candidate candidate: e.getCandidates()){
                    if(candidate.getName().equals(list)){
                        return;
                    }
                }
                //Search for list in data and add it to election
                for(Lista l: data.getLists()){
                    if(l.getName().equals(list)){
                        e.addList(l);
                        return;
                    }
                }
                semaphore.release();
                return;
            }
        }
    }

    /**
     * Removes a list from election
     */
    public void removeListElection(String title, String list) throws RemoteException{
        for(Election election: data.getElections()){
            if(election.getTitle().equals(title)){
                for(Candidate candidate: election.getCandidates()){
                    if(candidate.getName().equals(list)){
                        election.getCandidates().remove(candidate);
                        semaphore.release();
                        return;
                    }
                }
            }
        }
    }
    /**
     * Get all lists registered in data
     */
    public List<String> getLists() throws RemoteException{
        List<String>lists = new ArrayList<>();

        for(Lista list: data.getLists()){
            lists.add(list.getName());
        }

        return lists;
    }

    /**
     * Adds a new list to the data
     */
    public void addList(String name) throws RemoteException{
        data.addList(new Lista(name));        
        semaphore.release();
    }

    /**
     * Adds a member to a list
     */
    public void addMemberList(String listName, int id) throws RemoteException{
        Person person = queryPersonById(id);

        if(person == null) return;

        for(Lista list: data.getLists()){
            if(list.getName().equals(listName)){
                list.addMember(person);
                semaphore.release();
                return;
            }
        }
    }
    /**
     * Removes member from list
     */
    public void removeMemberList(String listName, int id) throws RemoteException{
        Person person = queryPersonById(id);

        if(person == null) return;

        for(Lista list: data.getLists()){
            if(list.getName().equals(listName)){
                list.getMembers().remove(person);
                semaphore.release();
                return;
            }
        }
    }
    /**
     * Adds vote to election
     */
    public void vote(String election, String list) throws RemoteException{
        System.out.println("Not implemented");
    }
    /**
     * Gets all faculties
     */
    public List<Faculty> getFaculties() throws RemoteException{
        return data.getFaculties();
    }
    /**
     * Registers new faculty
     */
    public void newFaculty(String name) throws RemoteException{
        data.getFaculties().add(new Faculty(name));
        semaphore.release();
    }
    /**
     * Registers new department
     */
    public void newDepartment(String faculty, String department) throws RemoteException{
        for (Faculty currFaculty : data.getFaculties()) {
            if(currFaculty.getName().equals(faculty)){
                currFaculty.addDepartment(department);
                semaphore.release();
                return;
            }
        }
    }
}