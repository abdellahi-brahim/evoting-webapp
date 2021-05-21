package actions;

import java.util.Map;

import com.opensymphony.xwork2.ActionSupport;

import org.apache.struts2.interceptor.SessionAware;

public class LoginAction extends ActionSupport implements SessionAware{
    private String username;
    private String password;
    private Map<String, Object> session;

    @Override
    public String execute() {
        return SUCCESS;
    }

    @Override
    public void validate(){
        if(username.length() == 0){
            addFieldError("username", "Username field is required");
        }

        if(password.length() == 0){
            addFieldError("password", "Password fie");
        }
    }

    @Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
    
    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }
}