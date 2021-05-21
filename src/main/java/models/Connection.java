package models;

import java.rmi.Naming;
import java.rmi.RemoteException;

import common.PersonInfo;
import common.ServerI;

public class Connection{
    ServerI server;
    PersonInfo user;
    String url;

    public Connection(String url){
        this.url = url;
    }

    public boolean login(String username, String password){
        if(connect()){
            try{
               if(server.login(username, password)){
                   user = server.getPersonByUsername(username);
                   return true;
               }
            }
            catch(RemoteException e){}
        }
        return false;
    }

    public boolean connect(){
        try{
            server = (ServerI)Naming.lookup(url);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
}   