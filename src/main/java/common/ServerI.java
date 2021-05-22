package common;

import java.rmi.*;
import java.util.Date;
import java.util.List;

public interface ServerI extends Remote {
    public String stationsToString() throws RemoteException;
    public List<PersonInfo> getUsers() throws RemoteException;
    public long isConnected() throws RemoteException;
    public void subscribeAdminConsole(RemoteObject obj) throws RemoteException;
    public boolean addStation(String faculty, String department, String id, RemoteObject remote) throws RemoteException ;
    public boolean hasStation(String faculty, String department) throws RemoteException ;
    public boolean reconnectStation(String faculty, String department, String id) throws RemoteException;
    public boolean login(String username, String password) throws RemoteException;
    public boolean login(int id, String username, String password) throws RemoteException;
    public List<String> getElectionLists(String title) throws RemoteException;
    public void newPerson(String name, int phone, String address, int id, Date expire, String password, String username, Faculty faculty, String department, String type) throws RemoteException;
    public void newPerson(String name, int phone, String address, int id, Date expire, String password, String username, String faculty, String department, String type) throws RemoteException;
    public void newElection(String title, String description, Date start, Date end, String type) throws RemoteException;
    public PersonInfo getPersonById(int id) throws RemoteException;
    public PersonInfo getPersonByUsername(String username) throws RemoteException;
    public List<ElectionInfo> getElections() throws RemoteException;
    public List<ElectionInfo> getOnGoingElections() throws RemoteException;
    public List<ElectionInfo> getEditableElections() throws RemoteException;
    public List<ElectionInfo> getOnGoingElections(int id, String type) throws RemoteException;
    public void changeTitle(String title, String newTitle) throws RemoteException;
    public void changeDescription(String title, String description) throws RemoteException;
    public void addListElection(String title, String list) throws RemoteException;
    public void removeListElection(String title, String list) throws RemoteException;
    public List<String> getLists() throws RemoteException;
    public void addList(String name) throws RemoteException;
    public void addMemberList(String listName, int id) throws RemoteException;
    public void removeMemberList(String listName, int id) throws RemoteException;
    public void vote(String election, String list) throws RemoteException;
    public List<Faculty> getFaculties() throws RemoteException;
    public void newFaculty(String name) throws RemoteException;
    public void newDepartment(String faculty, String department) throws RemoteException;

}