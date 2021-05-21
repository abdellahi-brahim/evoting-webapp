package common;

import java.io.Serializable;
import java.util.Date;

public class CC implements Serializable{
    private static final long serialVersionUID = 1L;

    private int number;
    private Date validity;

    public CC(int number, Date validity){
        this.number = number;
        this.validity = validity;
    }
    public int getNumber(){
        return number;
    }
    public Date getValidity(){
        return validity;
    }
    public String toString(){
        return "Number: " + number + " Validity: " + validity;
    }
}