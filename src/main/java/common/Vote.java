package common;

import java.util.Date;
import java.io.Serializable;

public class Vote implements Serializable{
    private static final long serialVersionUID = 1L;

    private String station;
    private Election election;
    private Date date;
    
    public Vote(String station, Date date, Election election){
        this.station = station;
        this.date = date;
        this.election = election;
    }

    public Election getElection(){
        return election;
    }

    public Date getDate(){
        return date;
    }

    public String getStation(){
        return station;
    }
}