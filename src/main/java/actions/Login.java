package actions;

import java.rmi.RemoteException;
import java.util.Map;
import com.opensymphony.xwork2.ActionSupport;

import org.apache.struts2.interceptor.SessionAware;

import common.PersonInfo;
import common.ServerI;

public class Login extends ActionSupport implements SessionAware{
    /***
     * User information
    */
    private String username;
    private String password;
    private String id;
    private int idValue;

    private static final String FAILURE = "failure";

    private transient Map<String, Object> session;

    @Override
    public String execute(){
        return SUCCESS;
    }

    public String logout(){
        session.remove("Profile");
        session.remove("loggedin");
        return SUCCESS;
    }
    
    public String userLogin(){
        ServerI server = (ServerI)session.get("Connection");

        try{
            if(server.login(idValue, username, password)){
                PersonInfo profile = server.getPersonById(idValue);
                
                session.put("Profile", profile);
                session.put("loggedin", true);

                return SUCCESS;
            }

            System.out.println(server.getPersonById(idValue));
        }
        catch(RemoteException e){
            return FAILURE;
        }
        return LOGIN;
    }

    public String adminLogin(){
        if(idValue == 14163568 && username.equals("admin") && password.equals("admin")){
            session.put("loggedin", true);
            return SUCCESS;
        }
        return LOGIN;
    }

    @Override
    public void validate(){
        if(username.length() == 0){
            addFieldError("username", "Username field is required");
        }

        if(password.length() == 0){
            addFieldError("password", "Password field is required");
        }

        if(id.length() == 0){
            addFieldError("id", "ID field is required");
        }

        try{
            idValue = Integer.parseInt(id);
            if(id.length() != 8){
                addFieldError("id", "ID has 8 characters");
            }
        }
        catch(NumberFormatException e){
            addFieldError("id", "ID is numeric");
        }
    }

    @Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

    public void setPassword(String password){
        this.password = password;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getPassword(){
        return password;
    }

    public String getUsername(){
        return username;
    }

    public int getId(){
        return idValue;
    }
}