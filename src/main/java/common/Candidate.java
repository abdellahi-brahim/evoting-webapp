package common;

import java.io.Serializable;

public class Candidate implements Serializable{
    private static final long serialVersionUID = 1L;

    private Lista list;
    private int votes;

    public Candidate(Lista list){
        this.list = list;
        votes = 0;
    }

    public String getName(){
        return list.getName();
    }

    public void addVote(){
        votes++;
    }

    public int getVotes(){
        return votes;
    }

    public Lista getList(){
        return list;
    }
}
