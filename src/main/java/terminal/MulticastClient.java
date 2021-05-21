package terminal;

import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.UUID;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.IOException;

class ClientUdpProtocol{
    
    /** 
     * Generates login message in UdpProtocol format
     * @param id Id of Terminal
     * @param username Username of Person to Login 
     * @param password Password of Person do Login
     * @return String Login message in UdpProtocol format
     */
    public static String login(String id, String username, String password){
        return String.format("%s;login;%s;%s", id, username, password);
    }

    
    /** 
     * Checks if UdpProtocol message refeers to login
     * @param buffer
     * @return boolean
     */
    public static boolean checkLogin(String buffer){
        String token[] = buffer.split(";");

        if(token[1].equals("status") && token[2].equals("on"))
            return true;

        return false;
    }

    private static boolean listIsEmpty(String buffer){
        String tokens[] = buffer.split(";");

        if(tokens[2].equals("0")) 
            return true;

        return false;
    }

    
    /** 
     * Converts UdpProtocol message with list to ArrayList
     * @param buffer UdpProtocol message
     * @return ArrayList<String> ArrayList of messages
     */
    public static ArrayList<String> toArrayList(String buffer){
        if(listIsEmpty(buffer))
            return null;

        String token[] = buffer.split(";");

        return new ArrayList<String>(Arrays.asList(token));
    }

    
    /** 
     * Splits UdpProtocol into tokens
     * @param buffer
     * @return String[]
     */
    public static String[] parse(String buffer){
        return buffer.split(";");
    }

    
    /** 
     * Gets id from UdpProtocol message
     * @param buffer
     * @return String
     */
    public static String getId(String buffer){
        return buffer.split(";")[0];
    }

    
    /** 
     * Converts a election and list to UdpProtocol vote message
     * @param id
     * @param election
     * @param list
     * @return String
     */
    public static String vote(String id, String election, String list){
        return String.format("%s;vote;%s;%s", id, election, list);
    }

    
    /** 
     * Generates a request for all elections in UdpProtocol format
     * @param id
     * @return String
     */
    public static String getElections(String id){
        return String.format("%s;get;elections", id);
    }

    
    /** 
     * Geterates a request for all lists of an election in UdpProtocol format
     * @param id
     * @param election
     * @return String
     */
    public static String getElection(String id, String election){
        return String.format("%s;get;election;%s", id, election);
    }
}

public class MulticastClient extends Thread{
    private String address1 = "224.0.224.0";
    private String address2 = "224.0.224.1";
    private int multicastPort1 = 4321;
    private int multicastPort2 = 4322;
    private int unicastPort;
    private String id;

    private MulticastSocket multicast;
    private DatagramSocket unicast;
    private InetAddress multicastip;
    private InetAddress unicastip;
    private Scanner keyboard;

    public MulticastClient(){

    }

    public static void main(String[] args) {
        MulticastClient client = new MulticastClient();
        client.run();
    }

    public void run() {
        id = UUID.randomUUID().toString();

        init(address1, multicastPort1);
        
        keyboard = new Scanner(System.in, "UTF-8");

        while(true) {
            clearScreen();
            System.out.println("Terminal Blocked!");

            DatagramPacket packet = receiveDatagram();
            String message = new String(packet.getData(), 0, packet.getLength());

            unicastip = packet.getAddress();
            unicastPort = packet.getPort();

            sendDatagram(id, unicastip, unicastPort);
            
            packet = receiveDatagram();

            message = new String(packet.getData(), 0, packet.getLength());

            if(message.equals(id)){
                init(address2, multicastPort2);

                packet = receiveDatagram();

                unicastip = packet.getAddress();
                unicastPort = packet.getPort();

                message = new String(packet.getData(), 0, packet.getLength());

                clearScreen();

                login();

                clearScreen();
                System.out.print("Welcome to eVoting! Press any key to continue...");
                
                keyboard.nextLine();

                while(menu());
            }
        }
    }

    /**
     * CLI Menu for user to see elections, lists and vote
     * 
     */
    public boolean menu(){
        boolean invalid = false;

        String message = ClientUdpProtocol.getElections(id);
        sendDatagram(message, unicastip, unicastPort);

        message = receive();

        ArrayList<String>elections = ClientUdpProtocol.toArrayList(message);

        if(elections == null){
            clearScreen();
            System.out.print("Terminal\nThere are no elections to vote for\n\nPress any key to continue...");
            keyboard.nextLine();
            return false;
        }

        elections.remove(0);
        elections.remove(0);
        elections.remove(0);

        int option = 0;
        int index = 1;
        do{
            clearScreen();

            System.out.println("Terminal\nPress Q to quit\n");
            if(invalid) System.out.println("Invalid Option");

            index = 1;

            for(String election: elections){
                System.out.printf("%d. %s\n", index, election);
                index++;
            }

            System.out.print("Select the election you want to vote for: ");
            
            String buffer = keyboard.nextLine();

            if(buffer.equals("Q")) return false;

            try{
                option = Integer.parseInt(buffer);
            }
            catch(NumberFormatException e){ 
                option = 0;
                invalid = false;
            }
        }
        while(option < 0 && option > index-1);

        String election = elections.get(index-1);

        message = ClientUdpProtocol.getElection(id, election);
        sendDatagram(message, unicastip, unicastPort);

        message = receive();

        ArrayList<String>lists = ClientUdpProtocol.toArrayList(message);
        if(lists == null){
            System.out.print("Terminal\nThis election have no candidates!\n\nPress any key to continue...");

            keyboard.nextLine();
            return true;
        }

        lists.remove(0);
        lists.remove(0);
        lists.remove(0);
        
        do{
            clearScreen();

            System.out.println("Terminal\nPress Q to quit\n");
            if(invalid) System.out.println("Invalid Option");

            index = 1;

            for(String list: lists){
                System.out.printf("%d. %s\n", index, list);
                index++;
            }

            System.out.print("Select the list you want to vote for: ");
            
            String buffer = keyboard.nextLine();

            try{
                option = Integer.parseInt(buffer);
            }
            catch(NumberFormatException e){ 
                option = 0;
                invalid = false;
            }
        }
        while(option < 0 && option > index-1);

        String list = lists.get(index-1);

        message = ClientUdpProtocol.vote(id, election, list);
        sendDatagram(message, unicastip, unicastPort);

        message = receive();

        return true;
    }

    /**
     * Receives string from packet sent to multicast group
     * @return
     */
    public String receive(){
        String message = null;
        DatagramPacket packet = null;
        while(true){
            packet = receiveDatagram();
            message = new String(packet.getData(), 0, packet.getLength());
            if(ClientUdpProtocol.getId(message).equals(id))
                break;
        }

        return message;
    }

    /**
     * CLI application for user to login in with his credentials
     */
    public void login(){
        boolean invalid = false;

        do{
            clearScreen();

            System.out.println("Terminal\nPress Q to quit\n");
            if(invalid) System.out.println("Wrong Credentials!");

            System.out.print("Please type your username: ");
            String username = keyboard.nextLine();

            if(username.equals("Q")) return;

            System.out.print("Please type your password: ");
            String password = keyboard.nextLine();

            if(password.equals("Q")) return;

            String message = ClientUdpProtocol.login(id, username, password);
            sendDatagram(message, unicastip, unicastPort);

            message = receive();

            invalid = !ClientUdpProtocol.checkLogin(message);
        }
        while(invalid);
    }

    public static void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    } 

    /** 
     * Initializes multicast and unicast sockets
     * @param address
     * @param port
    */
    private void init(String address, int port){
        try{
            multicast = new MulticastSocket(port);
            multicastip = InetAddress.getByName(address);
            multicast.joinGroup(multicastip);

            unicast = new DatagramSocket();
        }
        catch(IOException e){
            System.out.println("Multicast wasn't initialized");
        }
    }

    /**
     * Sends a packet with a message to the address ip and port port
     * @param message
     * @param ip
     * @param port
     */
    private void sendDatagram(String message, InetAddress ip, int port){
        try{
            byte[] data = message.getBytes();  
            DatagramPacket packet = new DatagramPacket(data, data.length, unicastip, port);
            unicast.send(packet);
        }
        catch(IOException e){
            System.out.println("Message was not sent");
        }
    }

    /**
     * Receives a packet from multicast pool
     * @return Packet with info sent to multicast group
     */
    private DatagramPacket receiveDatagram(){
        try{
            byte[] data = new byte[256];
            DatagramPacket packet = new DatagramPacket(data, data.length);

            multicast.receive(packet);

            return packet;
        }
        catch(IOException e){
            System.out.println("Problem receiving message");
        }
        return null;
    }   
}
