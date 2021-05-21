package common;

import java.io.Serializable;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.UUID;

public class Election implements Serializable, Comparable<Election>{
    private static final long serialVersionUID = 1L;

    private UUID id;
    private ElectionInfo info;
    private List<Candidate>candidates;
    private List<Person>voters;

    public Election(String title, String description, Date start, Date end, String type){
        //Generate unique id for election
        id = UUID.randomUUID();
        //Initialize other instances
        candidates = new CopyOnWriteArrayList<>();
        voters = new CopyOnWriteArrayList<>();
        info = new ElectionInfo(title, description,type, start, end);
    }

    private boolean canEdit(){
        Date now = new Date();
        return now.before(info.getEnd());
    }

    public void setTitle(String title){
        if(canEdit())
            info.setTitle(title);
    }

    public void setDescription(String description){
        if(canEdit())
            info.setDescription(description);
    }

    public void setStart(Date start){
        if(canEdit())
            info.setStart(start);
    }

    public void setEnd(Date end){
        if(canEdit())
            info.setEnd(end);
    }

    public void addVoter(Person person){
        if(canEdit())
            voters.add(person);
    }

    public void addList(Lista list){
        if(canEdit())
            candidates.add(new Candidate(list));
    }

    public String getType(){
        return info.getType();
    }

    public String getTitle(){
        return info.getTitle();
    }

    public String getDescription(){
        return info.getDescription();
    }

    public Date getStart(){
        return info.getStart();
    }

    public Date getEnd(){
        return info.getEnd();
    }

    public UUID getId(){
        return id; 
    }

    public List<Candidate> getCandidates(){
        return candidates;
    }

    public List<String> getLists(){
        return info.getLists();
    }

    public Lista getWinner(){
        Lista winner = null;
        Date now = new Date();

        if(now.before(info.getEnd()))
            return winner;

        int max = 0;

        for(Candidate candidate: candidates){
            if(candidate.getVotes() > max){
                max = candidate.getVotes();
                winner = candidate.getList();
            }
        }

        return winner;
    }

    public ElectionInfo getInfo(){
        return info;
    }

    @Override
    public int compareTo(Election election) {
        if (getEnd() == null || election.getEnd() == null)
            return 0;
        return getEnd().compareTo(election.getEnd());
    }
}