package interceptors;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Map;

import common.ServerI;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class ServerConnectionInterceptor implements Interceptor{
    @Override
    public void destroy(){}

    @Override
    public void init(){}

    @Override
    public String intercept(ActionInvocation ai) throws Exception {
        Map<String, Object>session = ai.getInvocationContext().getSession();

        //Initialize RMI connection
        if(!session.containsKey("Connection")){
            try{
                ServerI server = (ServerI)Naming.lookup("rmi://127.0.0.1:6666/vote");
                session.put("Connection", server);
            }
            catch(Exception e){
                return "failure";
            }
        }
        else{
            ServerI server = (ServerI)session.get("Connection");    
            try{
                server.isConnected();
            }
            catch(RemoteException e){
                return "failure";
            }
        }

        return ai.invoke();
    }
}
