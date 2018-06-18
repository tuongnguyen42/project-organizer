package application;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


/**
 * task model
 */
public class TaskModel implements Comparable<TaskModel> {
    private String name, description, dueDate, status;
    private LocalDate addDate;
    private Date dateForCompare;
    ArrayList<ModelListener> listeners = new ArrayList<>();

    public TaskModel(TaskModel task) {
        this.name = task.name;
        this.description = task.description;
        this.dueDate = task.dueDate;
        this.status = task.status;
        this.addDate = task.addDate;
        this.dateForCompare = task.dateForCompare;
    }

    public TaskModel(){
        this.name = "Task 1";
        this.description = "";
        this.dueDate = "0/0/0";
        this.status = "";
        this.addDate = LocalDate.now();
        this.dateForCompare = Calendar.getInstance().getTime();
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getDueDate() {
        return dueDate;
    }
    public String getStatus() {
        return status;
    }
    public LocalDate getAddDate(){ return addDate; }
    public Date getDateForCompare() { return dateForCompare; }
    public void setDateForCompare(Date date) {
        this.dateForCompare = date;
        updateAll();
    }
    public void setAddDate(LocalDate addDate) {
        this.addDate = addDate;
        updateAll();
    }

    public void setName(String name) {
        this.name = name;
        updateAll();
    }
    public void setDescription(String description) {
        this.description = description;
        updateAll();
    }
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
        updateAll();
    }
    public void setStatus(String status) {
        this.status = status;
        updateAll();
    }

    /**
     * Attach the UI class into the listeners
     * so that if task changed, updateAll will update task in all it's UI class
     * @param listener the UI class that passed in
     */
    public void attach(ModelListener listener) {
        listeners.add(listener);
    }

    /**
     * update all UI
     */
    public void updateAll() {
        Main.DIRTY = true;
        if(!listeners.isEmpty())
            listeners.get(0).update();
    }

    public int compareTo(TaskModel that) {
        // get 2 string arrays
        String[] thatIntegerString = that.getDueDate().split("-");
        String[] thisIntegerString = this.getDueDate().split("-");
//        sop("that: "+Arrays.toString(thatIntegerString));
//        sop("this: "+Arrays.toString(thisIntegerString));
        // convert them to int[]
        int[] thatIntArray = new int[thatIntegerString.length];
        int[] thisIntArray = new int[thisIntegerString.length];
        for(int i = 0; i < thatIntArray.length; i++)
            thatIntArray[i] = Integer.parseInt(thatIntegerString[i]);
        for(int i = 0; i < thisIntArray.length; i++)
            thisIntArray[i] = Integer.parseInt(thisIntegerString[i]);
//        sop("int: "+Arrays.toString(thatIntArray));
//        sop("int: "+Arrays.toString(thisIntArray));

        //year
        if(thisIntArray[0] == thatIntArray[0]) {
            // month
            if(thisIntArray[1] == thatIntArray[1]) {
                //day
                if(thisIntArray[2] == thatIntArray[2]) {
                    //by add date
                    if(addDate.compareTo(that.addDate) == 0) {
                        // by time
                        return dateForCompare.compareTo(that.dateForCompare);
                    } else {
                        return addDate.compareTo(that.addDate);
                    }
                } else {
                    return thisIntArray[2] - thatIntArray[2];
                }
            }
            else{
                return thisIntArray[1] - thatIntArray[1];
            }
        }
        else{
            return thisIntArray[0] - thatIntArray[0];
        }
    }
    @Override
    public boolean equals(Object ob) {
        if(!(ob instanceof TaskModel)) return false;
        TaskModel that = (TaskModel) ob;
        // same name, status, due date
        if(name.equals(that.name) && status.equals(that.status) && (this.compareTo(that) == 0))
            return true;
        return false;
    }
    @Override
    public int hashCode(){
        return Objects.hash(name, description, status, dueDate, addDate);
    }


    // debug use
    @Override
    public String toString() {
        StringBuffer sf = new StringBuffer();
        sf.append(this.name +" : " + this.status + " : " + this.dueDate);
        return sf.toString();
    }
}