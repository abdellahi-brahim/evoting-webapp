package common;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ElectionInfo implements Serializable{
    private String title;
    private String description;
    private String type;
    private Date start;
    private Date end;

    private List<String>lists;

    public ElectionInfo(String title, String description, String type, Date start, Date end){
        this.title = title;
        this.description = description;
        this.type = type;
        this.start = start;
        this.end = end;
    }

    public void addList(String list){
        lists.add(list);
    }

    public void removeList(String list){
        for(String l: lists){
            if(l.equals(list)){
                lists.remove(l);
                return;
            }
        }
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setStart(Date start){
        this.start = start;
    }

    public void setEnd(Date end){
        this.end = end;
    }

    public List<String>getLists(){
        return lists;
    }

    public String getType(){
        return type;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public Date getStart(){
        return start;
    }

    public Date getEnd(){
        return end;
    }
}

