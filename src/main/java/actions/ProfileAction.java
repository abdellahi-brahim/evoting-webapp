package actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import javax.servlet.http.HttpSession;

public class ProfileAction extends ActionSupport{

    @Override
    public String execute(){  
        HttpSession session = ServletActionContext.getRequest().getSession(false);  
        if(session == null || session.getAttribute("login") == null){  
            return "login";  
        }  
        else{  
            return "success";  
        }  
    } 
}
