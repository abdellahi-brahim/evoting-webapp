package multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import common.Faculty;
import common.PersonInfo;
import common.RemoteObject;
import common.ServerI;

/**
* Class with static methods for automatic UdpProtocol message generation
* @author  Abdellahi Brahim, Maria Dias
* @version 1.0
* @since   2021-04-01 
*/
class ServerUdpProtocol{
    private ServerUdpProtocol(){}
    /** 
     * This method parse an UdpProtocol message to tokens
     * @param buffer This is the UdpProtocol message to parse
     * @return String[] This returns the tokens inside the message
     */
    public static String[] parse(String buffer){
        return buffer.split(";");
    }
    /** 
     * This method gets the id of an UdpProtocol message
     * @param buffer This is the UdpProtocol message
     * @return String This returns the id in the UdpProtocol message
     */
    public static String getId(String buffer){
        return buffer.split(";")[0];
    }

    
    /** 
     * This method generates an UdpProtocol message to a failed login
     * @param id This is the user id
     * @return String This is the UdpProtocol message
     */
    public static String failedLogin(String id){
        return String.format("%s;status;off", id);
    }

    
    /** 
     * This method generates an UdpProtocol message to a sucessfull login
     * @param id This is the user id
     * @return String This is the UdpProtocol message
     */
    public static String successLogin(String id){
        return String.format("%s;status;on", id);
    }

    
    /** 
     * This method converts an CopyOnWriteArrayList list of elements to an
     * UdpProtocol message
     * to an ArrayList
     * @param id This is the user id
     * @param CopyOnWriteArrayListlist
     * @return String This is the Udp
     */
    public static String toList(String id, List<String>list){
        StringBuilder out = new StringBuilder(String.format("%s;item_list;%d", id, list.size()));

        for(String str: list)
            out.append(String.format(";%s", str));

        return out.toString();
    }

    public static String emptyList(String id){
        return String.format("%s;item_list;0", id);
    }
    /** 
     * This method generates a sucess message for a vote in the UdpProtocol 
     * format
     * @param id This is the user id
     * @return String This is the UdpProtocol message
     */
    public static String successVote(String id){
        return String.format("%s;success", id);
    }
}

/**
* Terminal class that contains Information about the user connected to it
* the id of the terminal and it the user has login or not
*
* @author  Abdellahi Brahim, Maria Dias
* @version 1.0
* @since   2021-04-01 
*/
class Terminal{
    private PersonInfo info;
    private String id;
    private boolean logged;

    public Terminal(PersonInfo info, String id){
        this.info = info;
        this.id = id;
        this.logged = false;
    }

    public PersonInfo getInfo(){
        return this.info;
    }

    public String getId(){
        return this.id;
    }

    public void log(){
        this.logged = true;
    }

    public boolean isLogged(){
        return this.logged;
    }
}

/**
* Contains one Terminal and a UdpProtocol message with some 
* instruction to me made
*
* @author  Abdellahi Brahim, Maria Dias
* @version 1.0
* @since   2021-04-01 
*/
class Command{
    private Terminal terminal;
    private String command;

    public Command(String command){
        this.command = command;
    }

    public void setTerminal(Terminal terminal){
        this.terminal = terminal;
    }

    public Terminal getTerminal(){
        return this.terminal;
    }

    public String getCommand(){
        return this.command;
    }
}

/**
* Multicast Server class that sends message to 2 multicast groups
* and has 2 DatagramSockets binded. It also have one queue of 
* commands and one list of connected terminals
*
* @author  Abdellahi Brahim, Maria Dias
* @version 1.0
* @since   2021-04-01 
*/
public class MulticastServer extends UnicastRemoteObject implements RemoteObject{
    private static final long serialVersionUID = 1L;

    private LinkedBlockingQueue <Command> commands;
    private List <Terminal> terminals;

    private transient ServerI server;
    private String url;

    private String faculty;
    private String department;
    private String id;

    //Multicast 1
    private String ADDRESS1;
    private transient DatagramSocket socket1;
    private InetAddress ip1;
    private int PORT1;
    private int PORT2;

    //Multicast 2
    private String ADDRESS2;
    private transient DatagramSocket socket2;
    private InetAddress ip2;
    private int PORT3;
    private int PORT4;

    private transient Scanner keyboard;

    public MulticastServer() throws RemoteException{
        super();

        commands = new LinkedBlockingQueue<>();
        terminals = new CopyOnWriteArrayList<>();
    }

    public boolean isConnected() throws RemoteException{
        return true;
    }

    public void send(String message) throws RemoteException{
        System.out.printf("Notification: %s\n", message);
    }

    public static void main(String[] args){
        try{
            MulticastServer server = new MulticastServer();
            server.run();
        }
        catch(RemoteException e){}
    }

    public void run(){
        System.out.println("Multicast Server\nCurrent Configs:\n");
        loadConfig();

        this.keyboard = new Scanner(System.in, "UTF-8");

        System.out.print("\nPlease press any key to continue...");
        this.keyboard.nextLine();
        clearScreen();

        System.out.println("Multicast Server\nConnecting to RMI Server...\n");
        connectRmi();

        clearScreen();
        System.out.println("Multicast Server\nConnected to RMI Server!\n");
        System.out.print("Please press any key to continue...");
        this.keyboard.nextLine();
        clearScreen();

        
        this.id = UUID.randomUUID().toString();

        registerStation();
        initSocket();

        boolean invalid = false;
        String buffer;
        boolean threads = true;

        Thread saveCommands = new Thread(() ->{
            while(threads){
                DatagramPacket packet = receivePacket2();
                this.commands.add(new Command(new String(packet.getData(), 0, packet.getLength())));
            }
        });

        Thread execCommands = new Thread(()->{
            while(threads){
                Command command = null;

                try {
                    command = this.commands.take();
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }

                String id = ServerUdpProtocol.getId(command.getCommand());

                for(Terminal terminal: terminals){
                    if(terminal.getId().equals(id)){
                        command.setTerminal(terminal);
                        break;
                    }
                }

                String[] token = ServerUdpProtocol.parse(command.getCommand());
                
                for(String t: token)
                    System.out.println(t);

                boolean success = true;

                String message = "";
                
                do{
                    try{
                        switch(token[1]){
                            case "login":
                                String username = token[2];
                                String password = token[3];

                                if(username.equals(command.getTerminal().getInfo().getUsername())){
                                    server.login(username, password);
                                    message = ServerUdpProtocol.successLogin(id);
                                    command.getTerminal().log();
                                }
                                else{
                                    message = ServerUdpProtocol.failedLogin(id);
                                }
                                break;
                            case "get":
                                String type = token[2];

                                switch(type){
                                    case "elections":
                                        //TO-DO: GET ON GOING ELECTIONS
                                        break;
                                    case "election":
                                        String key = token[3];
                                        //TO-DO: GET LISTS FROM ELECTIONS
                                        break;
                                }
                                break;

                            case "vote":
                                String election = token[2];
                                String list = token[3];
                                //To-do
                                message = ServerUdpProtocol.successLogin(id);
                                break;
                        }
                    }
                    catch(RemoteException e){
                        connectRmi();
                        success = false;
                    }
                }
                while(!success);
                sendMulticast2(message);
            }
        });

        saveCommands.start();
        execCommands.start();

        while(true){
            connectRmi();

            clearScreen();
            System.out.println("Multicast Server\nType Q to quit\n");
            if(invalid) System.out.println("Wrong id");
            System.out.print("Insert your id: ");
            buffer = this.keyboard.nextLine();

            if(buffer.equals("Q")) break;

            if(buffer.length() != 8) {
                invalid = true;
                continue;
            }

            int id = 0;
            try{
                id = Integer.parseInt(buffer);
            }
            catch(NumberFormatException e){
                invalid = true;
                continue;   
            }

            try{
                this.server.isConnected();
            }
            catch(RemoteException e){
                connectRmi();
            }

            PersonInfo info = null;

            try{
                info = server.getPersonById(id);
            }
            catch(RemoteException e){}

            if(info == null) {
                clearScreen();
                System.out.printf("The id %d is not registered. Press any key to continue...", id);
                keyboard.nextLine();
                continue;
            }

            if(!info.getFaculty().equals(this.faculty)){
                clearScreen();
                System.out.println("This person don't belong to this faculty. Press any key to continue...");
                keyboard.nextLine();
                continue;
            }

            else if(!info.getDepartment().equals(this.department)){
                try{
                    this.server.isConnected();
                }
                catch(RemoteException e){
                    connectRmi();
                }

                try{
                    if(this.server.hasStation(info.getFaculty(), info.getDepartment())){
                        clearScreen();
                        System.out.println("This person's department has a station! Press any key to continue...");
                        keyboard.nextLine();
                        continue;
                    }
                }
                catch(RemoteException e){}
            }

            clearScreen();

            System.out.printf("Assigning %s to Terminal...\n", info.getName());
            sendMulticast1("I need some user please");

            DatagramPacket packet = receivePacket1();

            buffer = new String(packet.getData(), 0, packet.getLength());

            this.terminals.add(new Terminal(info, buffer));

            clearScreen();
            System.out.printf("Multicast Server\nThe terminal %s was assigned to %s\n", buffer, info.getUsername());

            sendMulticast1(buffer);

            try {
                Thread.sleep(100); 
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }

            sendMulticast2("Welcome " + buffer);

            keyboard.nextLine();
        }
        keyboard.close();
    }

    public void connectRmi(){
        boolean disconnected = true;

        while(disconnected){
            try{
                server = (ServerI) Naming.lookup(url);
                disconnected = false;
            }
            catch(Exception e){
                clearScreen();
                System.out.println("Multicast Server\n");
                System.out.println("Connecting...");
                disconnected = true;
            }
            try{
                Thread.sleep(1000);
            }
            catch(InterruptedException e){
                System.out.println("Interrupted");
                Thread.currentThread().interrupt();
            }
        }
    }

    public void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    } 
    
    private void initSocket(){
        try{
            this.socket1 = new DatagramSocket(this.PORT3);
            this.ip1 = InetAddress.getByName(this.ADDRESS1);
            this.socket2 = new DatagramSocket(this.PORT4);
            this.ip2 = InetAddress.getByName(this.ADDRESS2);
        }
        catch(IOException e){
            System.out.println("Socket wasn't initialized");
        }
    }

    private void sendMulticast1(String message){
        try{
            byte[] data = message.getBytes();  
            DatagramPacket packet = new DatagramPacket(data, data.length, this.ip1, this.PORT1);
            this.socket1.send(packet);
        }
        catch(IOException e){
            System.out.println("Message was not sent");
            sendMulticast1(message);
        }
    }
    
    private void sendMulticast2(String message){
        try{
            byte[] data = message.getBytes();  
            DatagramPacket packet = new DatagramPacket(data, data.length, this.ip2, this.PORT2);
            this.socket2.send(packet);
        }
        catch(IOException e){
            System.out.println("Message was not sent");
            sendMulticast2(message);
        }
    }

    private DatagramPacket receivePacket1(){
        try{
            byte[] data = new byte[256];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            this.socket1.receive(packet);
            return packet;  
        }
        catch(IOException e){
            System.out.println("Problem receiving message");
        }
        return null;
    }

    private DatagramPacket receivePacket2(){
        try{
            byte[] data = new byte[256];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            this.socket2.receive(packet);
            return packet;  
        }
        catch(IOException e){
            System.out.println("Problem receiving message");
        }
        return null;
    }

    private void registerStation(){
        CopyOnWriteArrayList <Faculty> faculties = null;

        try{
            faculties = new CopyOnWriteArrayList<>(this.server.getFaculties());
        }
        catch(RemoteException e){}

        String buffer;
        int option = 0;
        int index = 0;
        boolean invalid = false;

        do{
            clearScreen();
            
            System.out.println("Multicast Server\n");
            if(invalid) System.out.println("Invalid option");

            invalid = true;

            index = 1;
            for(Faculty faculty: faculties){
                System.out.printf("%d. %s\n", index, faculty.getName());
                index++;
            }

            System.out.print("\nSelect your faculty: ");

            buffer = this.keyboard.nextLine();

            try{
                option = Integer.parseInt(buffer);
            }
            catch(NumberFormatException e){
                option = 0;
            }
        }
        while(option < 0 && option > index-1);

        Faculty fac = faculties.get(option-1);
        this.faculty = fac.getName();

        CopyOnWriteArrayList<String>departments = new CopyOnWriteArrayList<>(fac.getDepartments());

        invalid = false;

        do{
            clearScreen();
            System.out.println("Multicast Server\n");
            if(invalid) System.out.println("Invalid option");
            index = 1;
            for(String department: departments){
                System.out.printf("%d. %s\n", index, department);
                index++;
            }

            System.out.print("\nSelect your department: ");

            buffer = this.keyboard.nextLine();

            try{
                option = Integer.parseInt(buffer);
            }
            catch(NumberFormatException e){
                option = 0;
            }
        }
        while(option < 0 && option > index-1);

        this.department = departments.get(option-1);

        try{
            this.server.isConnected();
        }
        catch(RemoteException e){
            connectRmi();
        }

        try{
            if(!server.addStation(this.faculty, this.department, this.id, (RemoteObject)this)){
                clearScreen();
                System.out.println("Your department already have a voting station!");
                keyboard.close();
            }
            else{
                clearScreen();
                System.out.println("Station connected! Press any key to continue!");
                keyboard.nextLine();
            }
        }
        catch(RemoteException e){}
    }

    private void loadConfig(){
        try{
            Locale loc = new Locale("pt", "PT");
            Path path = Paths.get("multicast.config");
            Scanner scanner = new Scanner(path, StandardCharsets.UTF_8.name());
            scanner.useLocale(loc);
        
            this.ADDRESS1 = scanner.nextLine();
            this.ADDRESS2 = scanner.nextLine();
            this.PORT1 = scanner.nextInt();
            scanner.nextLine();
            this.PORT2 = scanner.nextInt();
            scanner.nextLine();
            this.PORT3 = scanner.nextInt();
            scanner.nextLine();
            this.PORT4 = scanner.nextInt();
            scanner.nextLine();
            this.url = scanner.nextLine();
            
            System.out.println("Multicast 1");
            System.out.println("Address: " + this.ADDRESS1);
            System.out.println("Port:" + this.PORT1);
            System.out.println("\nMulticast 2");
            System.out.println("Address: " + this.ADDRESS2);
            System.out.println("Port:" + this.PORT2);
            System.out.println("\nUDP 1");
            System.out.println("Port:" + this.PORT3);
            System.out.println("\nUDP 2");
            System.out.println("Port:" + this.PORT4);
            System.out.println("\nRMI");
            System.out.println("Url: " + this.url);

            scanner.close();
        }
        catch(IOException  e){
            System.out.println("File not found!");
        }
    }
}