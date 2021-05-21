package rmi;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class ServerLauncher{
    private static final int TIMEOUT = 1000;
    private static final int SLEEP = 1000;
    private int rmiport;
    private int udpport;
    private String url;
    private String host;

    private Server server;

    public static void main(String[] args){
        ServerLauncher launch = new ServerLauncher();
        launch.run();
    }

    public void run(){
        loadConfig();

        Scanner keyboard = new Scanner(System.in);

        System.out.print("\nPress any key to continue...");
        keyboard.nextLine();
        clearScreen();
        System.out.println("Rmi - Searching for Server...\n");
        
		try(DatagramSocket aSocket = new DatagramSocket()){
            InetAddress ahost = InetAddress.getByName(host);

            int seq = 1; 
            int failed = 5;         
            long responseTime;   

			byte[] buffer = new byte[1000];
			while(failed != 0){
                //Get timestamp for current packet
                Date time = new Date();
                long ms = time.getTime();
                //Generate ping message
                String s = String.format("PING %d %d", seq, ms);
				byte [] m = s.getBytes();
                //Send ping to server
				DatagramPacket ping = new DatagramPacket(m,m.length, ahost, udpport);
				aSocket.send(ping);
                try{
                    //Set TIMEOUT
                    aSocket.setSoTimeout(TIMEOUT);
                    //Wait for reply 
                    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);	
                    aSocket.receive(reply);

                    s = new String(reply.getData(), 0, reply.getLength());

                    String tokens[] = s.split(" ");
                    responseTime = Long.parseLong(tokens[2]) - ms;

                    System.out.println(String.format("Packet %s: response time %d", tokens[1], responseTime));
                }
                catch(SocketTimeoutException e){
                    System.out.println("TIMEOUT package " + seq + " not received!");
                    failed--;
		        }
                Thread.sleep(SLEEP);
                seq++;
			}
		}
        catch(InterruptedException e){
            System.out.println("sleep: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        catch (SocketException e){
            System.out.println("Socket: " + e.getMessage());
		}
        catch (IOException e){
            System.out.println("IO: " + e.getMessage());
		}

        try{
            System.getProperties().put("java.security.policy", "rmi/policy.all");
            System.setSecurityManager(new SecurityManager());
            
            LocateRegistry.createRegistry(rmiport);
            server = new Server(url);
		}
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Server Launching Failed!");
            return;
        }
        
        clearScreen();
        System.out.println("Rmi - Online\n");

        Thread t = new Thread(){
            @Override
            public void run(){
                while(true){
                    server.updateData();
                }
            }
        };

        t.start();

        try(DatagramSocket aSocket = new DatagramSocket(udpport)){
            String s;
            //send data to server
			while(true){
                try{
                    byte[] buffer = new byte[1000]; 	
                    //Waits for secondary server request
                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(request);

                    //Parse ping -> PING PACKET_NUMBER TIMESTAMP
                    s = new String(request.getData(), 0, request.getLength());

                    String tokens[] = s.split(" ");
                    //Get timestamp for current packet
                    Date time = new Date();
                    long ms = time.getTime();
                    //generate message
                    s = String.format("RESPONSE %s %d", tokens[1], ms);
                    buffer = s.getBytes();
                    Thread.sleep(TIMEOUT);
                    //If is sync simply reply with PING PACKET_NUMBER TIMESTAMP
                    DatagramPacket reply = new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort());
                    aSocket.send(reply);
                }
                catch(InterruptedException e){
                    Thread.currentThread().interrupt();
                }
                catch (SocketException e){
                    System.out.println("Socket: " + e.getMessage());
                }
                catch (IOException e){
                    System.out.println("IO: " + e.getMessage());
                }
			}
		}
        catch (SocketException e){
            System.out.println("Socket: " + e.getMessage());
		}
    }

    private void loadConfig(){
        Path path = Paths.get("rmi/server.config");

        try(Scanner scanner = new Scanner(path, StandardCharsets.UTF_8.name())){
            Locale loc = new Locale("pt", "PT");
            scanner.useLocale(loc);
        
            host = scanner.nextLine();
            String service = scanner.nextLine();
            rmiport = scanner.nextInt(); 
            scanner.nextLine();
            udpport = scanner.nextInt();
            String address = InetAddress.getByName(host).getHostAddress();

            url = String.format("rmi://%s:%d/%s",address, rmiport, service);

            System.out.println("RMI - Configs\n");
            System.out.printf("Host: %s\n", host);
            System.out.printf("Address: %s\n", address);
            System.out.printf("Service: %s\n", service);
            System.out.printf("RMI port: %d\n", rmiport);
            System.out.printf("URL: %s\n", url);
            System.out.printf("UDP port: %d\n", udpport);
        }
        catch(IOException  e){
            System.out.println("File not found!");
        }
    }

    public static void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    } 
}