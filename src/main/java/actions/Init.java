package actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class Init extends ActionSupport implements SessionAware{
    private transient Map<String,Object> session;

    @Override
    public void setSession(Map<String,Object> session){
        this.session = session;
    }

    @Override
    public String execute() throws Exception{
        return SUCCESS;
    }
}