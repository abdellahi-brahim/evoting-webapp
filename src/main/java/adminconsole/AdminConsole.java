package adminconsole;

import common.RemoteObject;
import common.ServerI;

import common.Faculty;
import common.ElectionInfo;
import common.PersonInfo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;
import java.util.Locale;
import java.text.ParseException;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.text.SimpleDateFormat;

class Menu{
    private String title;
    private String type;
    private ArrayList<String>notifications;
    private ArrayList<String>options;
    private Scanner keyboard;

    public Menu(String title, String type, Scanner keyboard){
        this.title = title;
        this.type = type;
        this.keyboard = keyboard;

        notifications = new ArrayList<>();
    }

    public Menu(String title, String type, String[] options, Scanner keyboard){
        this(title, type, keyboard);
        this.options = new ArrayList<>(Arrays.asList(options));
    }

    public Menu(String title, String type, List<String>options, Scanner keyboard){
        this(title, type, keyboard);
        this.options = new ArrayList<>(options);
    }

    public Menu(String title, String type, String[] options, List<String> notifications, Scanner keyboard){
        this(title, type, options, keyboard);
        this.notifications = new ArrayList<>(notifications);
    }

    public Menu(String title, String type, List<String>options, List<String> notifications, Scanner keyboard){
        this(title, type, options, keyboard);
        this.notifications = new ArrayList<>(notifications);
    }

    public void updateNotifications(List<String>notifications){
        this.notifications = new ArrayList<>(notifications);
    }

    private void clearScreen(){  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    } 

    private void renderList(boolean invalid){
        System.out.println(title+"\n");

        if(!notifications.isEmpty()){
            System.out.println("Notifications received:");
            int index = 1;
            for(String notification: notifications){
                System.out.printf("%d. %s\n", index++, notification);
            }
        }
        if(invalid) System.out.println("Invalid Option");

        int index = 1;
        for(String option: options){
            System.out.printf("%d. %s\n", index++, option);
        }

        System.out.printf("\nPlease select one %s: ", type);
    }
    
    public int renderSelectionMenu(){
        String buffer;
        int option = 0;
        boolean invalid = false;
        do{
            clearScreen();
            renderList(invalid);
            invalid = true;
            buffer = keyboard.nextLine();
            try{
                option = Integer.parseInt(buffer);
            }
            catch(NumberFormatException e){}
        }
        while(option < 1 && option > options.size());

        return option;
    } 
}

public class AdminConsole extends UnicastRemoteObject implements RemoteObject{
    private static final long serialVersionUID = 1L;

    private static final int SLEEP = 1000;
    private transient Scanner keyboard;
    private transient ServerI server;
    private ArrayList<String>notifications;
    private String url;

    public AdminConsole() throws RemoteException{
        super();
        notifications = new ArrayList<>();
    }

    public void connect(){
        boolean disconnected = true;

        while(disconnected){
            try{
                server = (ServerI) Naming.lookup(url);
                disconnected = false;
            }
            catch(Exception e){
                clearScreen();
                System.out.println("Admin Console\n");
                System.out.println("Connecting...");
                disconnected = true;
            }
            try{
                Thread.sleep(SLEEP);
            }
            catch(InterruptedException e){
                System.out.println("Interrupted");
                Thread.currentThread().interrupt();
            }
        }
    }
    /** 
     * Runs the Thread AdminConsole
     * @param args
     */
    public static void main(String[] args) {
        try{
            AdminConsole adminConsole = new AdminConsole();
            adminConsole.run();
        }
        catch(RemoteException e){
            System.out.println("AdminConsole Initialization Failed!");
        }
    }

    /** 
     * Remote method that returns true for connection checking
     * @return boolean Returns true
     * @throws RemoteException
     */
    public boolean isConnected() throws RemoteException{
        return true;
    }
    /**     
     * Remote method that prints notification on admin console
     * @param message
     * @throws RemoteException
     */
    public void send(String message) throws RemoteException{
        System.out.println(message);
        notifications.add(message);
    }

    public void pressAnyKeyToContinue(){
        System.out.print("Press any key to continue...");
        keyboard.nextLine();
    }

    /** 
     * Initializes and starts AdminConsole
     * @throws RemoteException
     */
    public void run() throws RemoteException{
        clearScreen();
        
        loadConfigFromTextFile();
        
        keyboard = new Scanner(System.in);
        
        pressAnyKeyToContinue();
        
        connect();

        server.subscribeAdminConsole(this);

        String[] options = {"Register new user", 
                            "Create Election", 
                            "Manage Election", 
                            "Show Voting Stations", 
                            "Manage database", 
                            "Query user"};
        
        Menu menu = new Menu("Admin Console", "option", options, keyboard);

        while(true){
            try{
                server.isConnected();
            }
            catch(RemoteException e){
                connect();
            }

            menu.updateNotifications(notifications);

            switch(menu.renderSelectionMenu()){
                //Register new user
                case 1:
                    newUser();
                    break;
                //Create election
                case 2:
                    createElection();
                    break;
                case 3:
                    manageElection();
                    break;
                case 4:
                    manageVotingStations();
                    break;
                case 5:
                    manageDatabase();
                    break;
                case 6:
                    queryUsers();
                    break;
                case 0:
                    break;
                //Trolling linter
                default:
                    return;
            }
        }
    }

    /**
     * Static method that scrolls the screen
     */
    public void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    } 
    
    /** 
     * Prints all Vote Stations connected to RMI Server
     * 
     * @throws RemoteException
     */
    public void manageVotingStations() throws RemoteException{
        clearScreen();

        System.out.println("Admin Console\n");
        System.out.println("Current Voting Stations");

        try{
            server.isConnected();
        }
        catch(RemoteException e){
            connect();
        }
        
        String stations = server.stationsToString();

        String message = stations.equals("")? "No voting stations connected": stations;

        System.out.print(message+"\nPress any key to continue...");

        keyboard.nextLine();
    }
    
    /** 
     * Creates new user
     * @throws RemoteException
     */
    private void newUser() throws RemoteException{
        int option;
        String buffer;
        boolean invalid = false;

        do{
            clearScreen();

            System.out.println("New User\n");
            if(invalid) System.out.println("Invalid Option");

            System.out.println("1.Student");
            System.out.println("2.Teacher");
            System.out.println("3.Employee\n");
            System.out.print("Enter your choice: ");
            buffer = keyboard.nextLine();

            invalid = true;

            try{
                option = Integer.parseInt(buffer);
            }
            catch(NumberFormatException e){
                option = 0;
            }
        }
        while(option < 1 && option > 3);

        
        clearScreen();
        System.out.println("New User\n");
        System.out.print("Insert your name: ");
        String name = keyboard.nextLine();
        
        clearScreen();
        System.out.println("New User\n");
        System.out.print("Insert your address: ");
        String address = keyboard.nextLine();

        int number = 0;
        invalid = false;

        do{
            clearScreen();
            System.out.println("New User\n");
            if(invalid) System.out.println("Wrong number");

            System.out.print("Insert your phone number: ");
        
            buffer = keyboard.nextLine();
            try{
                number = Integer.parseInt(buffer);                
                invalid = buffer.length() == 9? false: true;
            }
            catch(NumberFormatException e){
                invalid = true;
            }
        }
        while(invalid);

        int id = 0;
        invalid = false;

        do{
            clearScreen();
            System.out.println("New User\n");
            if(invalid) System.out.println("Wrong number");
            System.out.print("Insert your id number: ");

            buffer = keyboard.nextLine();

            try{
                id = Integer.parseInt(buffer);        
                System.out.println(buffer.length());  

                invalid = buffer.length() != 8;
            }
            catch(NumberFormatException  e){
                invalid = true;
            }
        }
        while(invalid);

        Date expire = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");  
        invalid = false;

        do{
            clearScreen();
            System.out.println("New User\n");
            if(invalid) System.out.println("Wrong format");

            System.out.print("Insert your id expire date (dd/MM/yyyy): ");
        
            buffer = keyboard.nextLine();
            try{
                expire = dateFormat.parse(buffer);
                invalid = false;
            }
            catch(ParseException e){
                invalid = true;
            }
        }
        while(invalid);

        clearScreen();
        System.out.println("New User\n");
        System.out.print("Insert your password: ");
        String password = keyboard.nextLine();

        clearScreen();
        System.out.println("New User\n");
        System.out.print("Insert your username: ");
        String username = keyboard.nextLine();

        //Listar departamentos e faculdades disponíveis
        try{
            server.isConnected();
        }
        catch(RemoteException e){
            connect();
        }
        CopyOnWriteArrayList <Faculty> faculties = new CopyOnWriteArrayList<>(server.getFaculties());
        int index = 1;
        invalid = false;
        do{
            clearScreen();
            System.out.println("New User:\n");
            if(invalid) System.out.println("Wrong option");
            
            index = 1;
            for (Faculty faculty : faculties) {
                System.out.println(index+". " + faculty.getName());
                index++;
            }

            System.out.print("\nSelect your faculty: ");
            buffer = keyboard.nextLine();
            invalid = true;
            
            try{
                Integer.parseInt(buffer);
            }
            catch(NumberFormatException e){
                option = 0;
            }
        }
        while(option < 1 && option > index-1);

        Faculty faculty = faculties.get(option-1);

        invalid = false;

        do{
            clearScreen();
            System.out.println("New User\n");
            if(invalid) System.out.println("Wrong Option");

            
            index = 1;
            for (String department : faculty.getDepartments()) {
                System.out.println(index+". "+department);
                index++;
            }

            System.out.print("\nSelect your department: ");

            buffer = keyboard.nextLine();
            invalid = true;

            try{
                option = Integer.parseInt(buffer);
            }
            catch(NumberFormatException e){
                option = 0;
            }
        }
        while(option < 1 && option > index-1);

        String department = faculty.getDepartments().get(option-1);
        
        try{
            server.isConnected();
        }
        catch(RemoteException e){
            connect();
        }

        String[] type = {"Student", "Employee", "Teacher"};

        server.newPerson(name, number, address, id, expire, password, username, faculty, department, type[option-1]);

        clearScreen();

        System.out.println("New User created!\n");
        System.out.println("Name: " + name);
        System.out.println("Number: " + number);
        System.out.println("Id: " + id);
        System.out.println("Expire date: " + expire);
        System.out.println("Address: " + address);
        System.out.println("Password: " + password);
        System.out.println("Username: " + username);
        System.out.println("Faculty: " + faculty.getName());
        System.out.println("Department: " + department + "\n");

        pressAnyKeyToContinue();
    }

    
    /** 
     * Creates new election
     * @throws RemoteException
     */
    private void createElection() throws RemoteException{
        clearScreen();
        System.out.println("New Election:\n");
        System.out.print("Insert election title: ");
        String title = keyboard.nextLine();

        clearScreen();
        System.out.println("New Election:\n");
        System.out.print("Insert election description: ");
        String description = keyboard.nextLine();

        Date start = null;
        String buffer;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");  
        boolean invalid = false;

        do{
            clearScreen();
            System.out.println("New Election:\n");
            if(invalid) System.out.println("Wrong format");

            System.out.print("Insert start date (dd/MM/yyyy): ");

            buffer = keyboard.nextLine();
            try{
                start = dateFormat.parse(buffer);
                invalid = false;
            }
            catch(ParseException e){
                invalid = true;
            }
        }
        while(invalid);

        do{
            clearScreen();
            System.out.println("New Election:\n");
            if(invalid) System.out.println("Wrong format");

            System.out.print("Insert start hour: ");

            buffer = keyboard.nextLine();
            try{
                int hour = Integer.parseInt(buffer);
                start.setHours(Math.abs(hour));
                invalid = hour > 24;
            }
            catch(NumberFormatException e){
                invalid = true;
            }
        }
        while(invalid);

        do{
            clearScreen();
            System.out.println("New Election:\n");
            if(invalid) System.out.println("Wrong format");

            System.out.print("Insert start minute: ");

            buffer = keyboard.nextLine();
            try{
                int minute = Integer.parseInt(buffer);
                start.setMinutes(Math.abs(minute));
                invalid = minute > 60;
            }
            catch(NumberFormatException e){
                invalid = true;
            }
        }
        while(invalid);

        Date end = null;
        invalid = false;

        do{
            clearScreen();
            System.out.println("New Election:\n");
            if(invalid) System.out.println("Wrong format");
            System.out.print("Insert end date (dd/MM/yyyy): ");
            
            buffer = keyboard.nextLine();
            try{
                end = dateFormat.parse(buffer);
                invalid = false;
            }
            catch(ParseException e){
                invalid = true;
            }
        }
        while(invalid);

        do{
            clearScreen();
            System.out.println("New Election:\n");
            if(invalid) System.out.println("Wrong format");

            System.out.print("Insert end hour: ");

            buffer = keyboard.nextLine();
            try{
                int hour = Integer.parseInt(buffer);
                end.setHours(Math.abs(hour));

                invalid = hour > 25;
            }
            catch(NumberFormatException e){
                invalid = true;
            }
        }
        while(invalid);

        do{
            clearScreen();
            System.out.println("New Election:\n");
            if(invalid) System.out.println("Wrong format");

            System.out.print("Insert end minute: ");

            buffer = keyboard.nextLine();
            try{
                int minute = Integer.parseInt(buffer);
                end.setMinutes(Math.abs(minute));
                invalid = minute > 60;
            }
            catch(NumberFormatException e){
                invalid = true;
            }
        }
        while(invalid);

        try{
            server.isConnected();
        }
        catch(RemoteException e){
            connect();
        }

        server.newElection(title, description, start, end, "Student");

        clearScreen();
        System.out.println("New Election created!\n");
        System.out.println("Title: " + title);
        System.out.println("Description: " + description);
        System.out.println("Start date: " + start);
        System.out.println("End date: " + end);

        pressAnyKeyToContinue();
    }

    /** 
     * Selects a selection from RMI Server and edits it
     * @throws RemoteException
     */
    private void manageElection() throws RemoteException{
        try{
            server.isConnected();
        }
        catch(RemoteException e){
            connect();
        }
        CopyOnWriteArrayList<ElectionInfo> elections = new CopyOnWriteArrayList<>(server.getElections());

        String buffer;

        int option = 0;
        int index = 1;

        boolean invalid = false;

        do{
            clearScreen();
            System.out.println("Manage Election:\n");
            if(invalid) System.out.println("Wrong option.");

            invalid = true;
            index = 1;
            for (ElectionInfo election : elections) {
                System.out.println(index + ". " + election.getTitle());
                index++;
            }

            System.out.print("\nSelect the election you want to edit: ");

            buffer = keyboard.nextLine();

            try{
                option = Integer.parseInt(buffer);
            }
            catch(NumberFormatException e){
                option = 0;
            }
        }
        while(option < 1 && option > index-1);
        
        ElectionInfo election = elections.get(option-1);
        invalid = false;

        do{
            clearScreen();
            System.out.println("Manage election:\n");
            if(invalid) System.out.println("Wrong option");

            System.out.println("1.Edit title");
            System.out.println("2.Edit description");
            System.out.println("3.Add list");
            System.out.println("4.Remove list");
            System.out.println("5.Return to main menu");
            System.out.print("\nEnter your choice: ");

            invalid = true;

            buffer = keyboard.nextLine();

            try{
                option = Integer.parseInt(buffer);
            }
            catch(NumberFormatException e){
                option = 0;
            }
        }
        while(option < 1 && option > 5);

        if(option == 5) return;

        clearScreen();

        switch(option){
            case 1: 
                System.out.println("Manage election:\n");
                System.out.println("Current title: " + election.getTitle());
                System.out.print("Insert new title: ");

                buffer = keyboard.nextLine();
                
                try{
                    server.isConnected();
                }
                catch(RemoteException e){
                    connect();
                }

                server.changeTitle(election.getTitle(), buffer);
                break;
            case 2: 
                System.out.println("Manage election:\n");
                System.out.println("Current description: " + election.getDescription());
                System.out.print("Insert new description: ");

                try{
                    server.isConnected();
                }
                catch(RemoteException e){
                    connect();
                }

                server.changeDescription(election.getTitle(), buffer);

                buffer = keyboard.nextLine();
                election.setDescription(buffer);
                break;
            
            case 3:
                try{
                    server.isConnected();
                }
                catch(RemoteException e){
                    connect();
                }

                CopyOnWriteArrayList<String> lists = new CopyOnWriteArrayList<>(server.getLists());
                invalid = false;

                do{
                    System.out.println("Manage election:\n");
                    if(invalid) System.out.println("Invalid option!");

                    index = 1;

                    for(String list: lists){
                        System.out.println(index + ". " + list);
                        index++;
                    }

                    System.out.print("\nSelect one list to add to election: ");

                    invalid = true;
                    buffer = keyboard.nextLine();

                    try{
                        option = Integer.parseInt(buffer);
                    }
                    catch(NumberFormatException e){
                        option = 0;
                    }
                }
                while(option < 0 && option > index-1);
                
                try{
                    server.isConnected();
                }
                catch(RemoteException e){
                    connect();
                }

                String list = lists.get(option-1);
                server.addListElection(election.getTitle(), list);

                System.out.println("List " + list + " added to election " + election.getTitle());
                System.out.println("Press any key to continue...");

                keyboard.nextLine();
                break;
            case 4:
                invalid = false;
                do{
                    System.out.println("Manage election:\n");
                    if(invalid) System.out.println("Invalid option");

                    index = 1;

                    if(election.getLists().isEmpty()){
                        System.out.print("This election doesn't have lists. Press any key to continue...");
                        keyboard.nextLine();
                    }

                    for (String l : election.getLists()){
                        System.out.println(index+". " + l);
                        index++;
                    }

                    System.out.print("\nSelect one list to remove from election: ");

                    invalid = true;
                    buffer = keyboard.nextLine();

                    try{
                        option = Integer.parseInt(buffer);
                    }
                    catch(NumberFormatException e){
                        option = 0;
                    }
                }
                while(option < 1 && option > index - 1);

                try{
                    server.isConnected();
                }
                catch(RemoteException e){
                    connect();
                }

                server.removeListElection(election.getTitle(), election.getLists().get(option-1));
                break;
            case 5: 
                return;
            
            default:
                break;
        }
    }
    /** 
     * Adds Faculties, Departments or Lists to RMI Server
     * @throws RemoteException
     */
    private void manageDatabase() throws RemoteException{
        int option;
        boolean invalid = false;
        String buffer;
        do{
            clearScreen();
            System.out.println("Manage Database:\n");
            if(invalid) System.out.println("Invalid option");

            System.out.println("1.Add Faculty");
            System.out.println("2.Add Department");
            System.out.println("3.Add List\n");
            System.out.print("Enter your choice: ");

            buffer = keyboard.nextLine();
            try{
                option = Integer.parseInt(buffer);
            }
            catch(NumberFormatException e){
                option = 0;
            }
        }
        while(option < 1 && option > 3);
        
        switch(option){
            case 1: 
                clearScreen();
                System.out.println("Manage Database:\n");
                System.out.print("Enter faculty name: ");
                buffer = keyboard.nextLine();

                try{
                    server.isConnected();
                }
                catch(RemoteException e){
                    connect();
                }

                server.newFaculty(buffer);
                break;
            
            case 2: 
                try{
                    server.isConnected();
                }
                catch(RemoteException e){
                    connect();
                }

                CopyOnWriteArrayList <Faculty> faculties = new CopyOnWriteArrayList<>(server.getFaculties());
                invalid = false;
                int index = 0;
                do{
                    clearScreen();
                    System.out.println("Manage Database:\n");
                    if(invalid) System.out.println("Wrong option");
                    
                    index = 1;
                    for(Faculty faculty: faculties){
                        System.out.println(index + ". " + faculty.getName());
                    }
                    System.out.println("Select your faculty\n");

                    buffer = keyboard.nextLine();

                    try{
                        option = Integer.parseInt(buffer);
                    }
                    catch(NumberFormatException e){
                        option = 0;
                    }
                }
                while(option < 1 && option > index-1);

                Faculty faculty = faculties.get(index-1);

                clearScreen();
                System.out.println("Manage Database:\n");
                System.out.print("Enter new department name: ");

                buffer = keyboard.nextLine();

                try{
                    server.isConnected();
                }
                catch(RemoteException e){
                    connect();
                }

                server.newDepartment(faculty.getName(), buffer);
                break;

            case 3:
                clearScreen();
                System.out.println("Manage Database:\n");
                System.out.print("Enter new list name: ");

                buffer = keyboard.nextLine();

                try{
                    server.isConnected();
                }
                catch(RemoteException e){
                    connect();
                }

                server.addList(buffer);
                break;
            default:
                break;
        }
    }
    
    /** 
     * Prints all users stored in RMI Storage
     * @throws RemoteException
     */
    public void queryUsers() throws RemoteException{
        try{
            server.isConnected();
        }
        catch(RemoteException e){
            connect();
        }

        CopyOnWriteArrayList <PersonInfo> users = new CopyOnWriteArrayList<>(server.getUsers());

        clearScreen();
        System.out.println("Query Users\n");

        if(users.isEmpty()){
            System.out.print("No users registered.");
            pressAnyKeyToContinue();
        }

        int index = 1;

        for(PersonInfo user: users){
            System.out.printf("%d. %s\n", index, user.getName());
            index++;
        }

        pressAnyKeyToContinue();
    }

    public void loadConfigFromTextFile(){
        try{
            Locale loc = new Locale("pt", "PT");
            Path path = Paths.get("adminconsole.config");
            Scanner scanner = new Scanner(path, StandardCharsets.UTF_8.name());
            scanner.useLocale(loc);
        
            url = scanner.nextLine();

            System.out.println("Admin Console - Configs\n");
            System.out.printf("Host: %s\n", url);
            scanner.close();
        }
        catch(IOException  e){
            System.out.println("File not found!");
        }
    }
}