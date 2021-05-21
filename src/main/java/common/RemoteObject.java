package common;

import java.rmi.*;

public interface RemoteObject extends Remote{
    public boolean isConnected() throws RemoteException;
    public void send(String message) throws RemoteException;
}
