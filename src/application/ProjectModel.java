package application;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * model class of project
 * for serialization of the data
 */
public class ProjectModel {
    private String name;// project name
    private ArrayList<String> columns;// columns name
    private TreeSet<TaskModel> taskSet;// task that this project holds
    ArrayList<ModelListener> listeners = new ArrayList<>();

    public ProjectModel() {
        this.name = "Project 1";
        this.columns = new ArrayList<>();
        this.taskSet = new TreeSet<>();
    }

    public String getName() {
        return name;
    }
    public ArrayList<String> getColumns() {
        return columns;
    }
    public TreeSet<TaskModel> getTaskSet() {
        return taskSet;
    }
    public void setName(String name) {
        this.name = name;
        updateAll();
    }
    public void setTaskSet(TreeSet<TaskModel> taskSet) {
        this.taskSet = taskSet;
        updateAll();
    }
    public void setColumns(ArrayList<String> columns) {
        this.columns = columns;
        updateAll();
    }

    public void attach(ModelListener listener) {
        listeners.add(listener);
    }
    public void updateAll() {
        Main.DIRTY = true;
        if(!listeners.isEmpty())
            listeners.get(0).update();
    }

    /**
     * add the task into task set
     * @param task new task model
     */
    public void addTask(TaskModel task){
        this.taskSet.add(task);
        updateAll();
    }

    /**
     * remove the task from the task set
     * @param task the task being removed
     */
    public void removeTask(TaskModel task) {
        this.taskSet.remove(task);
        updateAll();
    }
    public void addColumn(String column) {
        this.columns.add(column);
        updateAll();
    }
    public void removeColumn(String column) {
        this.columns.remove(column);
        updateAll();
    }
}