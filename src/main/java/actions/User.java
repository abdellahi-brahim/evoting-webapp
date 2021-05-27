package actions;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionSupport;

import org.apache.struts2.interceptor.SessionAware;

import common.ElectionInfo;
import common.PersonInfo;
import common.ServerI;

public class User extends ActionSupport implements SessionAware{
    private static final String FAILURE = "failure";

    private transient Map<String, Object>session;

    private List<ElectionInfo> data;
    private List<String>elections;
    private List<String>lists;

    private String selectedElection;
    private String selectedList;

    @Override
    public String execute(){
        ServerI server = (ServerI)session.get("Connection");
        PersonInfo p = (PersonInfo)session.get("Profile"); 

        try{
            data = server.getOnGoingElections(p.getId().getNumber(), p.getType());
            elections = new ArrayList<>();
            for(ElectionInfo election: data){
                elections.add(election.getTitle());
            }
            return SUCCESS;
        }
        catch(RemoteException e){
            return FAILURE;
        }
    }

    public String electionSelected(){
        if(selectedElection == null || selectedElection.length() == 0){
            addFieldError("fieldName", "Please select one election!");
            return INPUT;
        }        

        return SUCCESS;
    }

    public String lists(){
        execute();

        for(ElectionInfo election: data){
            if(selectedElection.equals(election.getTitle())){
                lists = election.getLists();
                return SUCCESS;
            }
        }

        return LOGIN;
    }

    public String vote(){
        ServerI server = (ServerI)session.get("Connection");
        PersonInfo profile = (PersonInfo)session.get("Profile");

        try{
            server.vote(profile.getId().getNumber(), "webStation", selectedElection, selectedList);
            return SUCCESS;
        }
        catch(RemoteException e){
            return FAILURE;
        }
    }

    public List<String>getLists(){
        return lists;
    }

    public List<String>getElections(){
        return elections;
    }

    public String getSelectedElection(){
        return selectedElection;
    }

    public String getSelectedList(){
        return selectedList;
    }

    public void setSelectedElection(String selectedElection){
        this.selectedElection = selectedElection;
    }

    public void setSelectedList(String selectedList){
        this.selectedList = selectedList;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
