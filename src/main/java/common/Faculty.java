package common;

import java.util.List;
import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

public class Faculty implements Serializable{
    private static final long serialVersionUID = 1L;

    private String name;
    private List<String>departments;

    public Faculty(String name){
        this.name = name;
        departments = new CopyOnWriteArrayList<>();
    }
    /** 
     * @return String
     */
    public String getName(){
        return this.name;
    }
    /** 
     * @param department
     */
    public void addDepartment(String department){
        this.departments.add(department);
    }
    /** 
     * @return CopyOnWriteArrayList<String>
     */
    public List<String> getDepartments(){
        return this.departments;
    }
    /** 
     * @return String
     */
    public String toString(){
        StringBuilder d = new StringBuilder("name");

        for(String department: departments){
            d.append("%n" + department);
        }

        return String.format("%s%s", this.name, departments);
    }
}