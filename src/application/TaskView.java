package application;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;


/**
 * Task view class
 */
public class TaskView implements ModelListener {
    private TaskModel task;// the task model this view class rely on
    private GridPane taskForm;// the layout where the task got displayed
    private ProjectModel project;// the project where it has been aggregated
    private Stage stage;// the stage it's on

    public TaskView(Stage stage, ProjectModel project, TaskModel task) {
        this.task = task;
        this.project = project;
        taskForm = showTask(new GridPane(), task);
        this.stage = stage;
        task.attach(this);
    }

    public TaskModel getTask() {
        return task;
    }
    public GridPane getTaskForm() { return taskForm; }
    public Stage getStage() { return stage; }
    public ProjectModel getProject() { return project; }
    public void setProject(ProjectModel project) { this.project = project; }
    public void setStage(Stage stage) { this.stage = stage; }
    public void setTaskForm(GridPane taskForm) { this.taskForm = taskForm; }
    public void setTask(TaskModel task) {
        this.task = task;
    }

    /**
     * How task view would update
     */
    public void update() {
        this.taskForm = showTask(taskForm, task);
        project.updateAll();
    }
    /**
     * create pop-up to get task information from users
     */
    public void createTask() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("New Task");
        // =============Start make screen=============

        GridPane createLayout = new GridPane();


        VBox textBox = new VBox();
        Text newTaskText = new Text("New\nTask:");
        textBox.getChildren().addAll(newTaskText);
        textBox.setAlignment(Pos.CENTER);
        textBox.setPadding(new Insets(12));

        // create grid panel that holds everything
        VBox layout = new VBox(30);
        layout.setPadding(new Insets(12));

        // each line will be in a HBox
        // name
        HBox name = new HBox(8);
        Text nameText = new Text("Task name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter task name");
        name.getChildren().addAll(nameText, nameField);
        // status
        HBox statusBox = new HBox(8);
        Text statusText = new Text("Pick Status:");

        ChoiceBox<String> status = new ChoiceBox<>();
        status.getItems().addAll(project.getColumns());
        status.setValue(task.getStatus());
        statusBox.getChildren().addAll(statusText, status);

        // due date
        HBox dueDate = new HBox(8);
        Text ddText  = new Text("Due Date");

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());

        dueDate.getChildren().addAll(ddText, datePicker);


        // description
        VBox description = new VBox(8);
        Text desText = new Text("Task Description:");
        TextArea desArea = new TextArea();
        desArea.setMaxSize(300, 200);
        desArea.setPromptText("Enter task description");
        description.getChildren().addAll(desText, desArea);

        // create and Cancel button
        HBox createAndCancel = new HBox(8);
        Button createBtn = new Button("Create");
        Button cancelBtn = new Button("Cancel");
        createAndCancel.getChildren().addAll(createBtn, cancelBtn);

        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(10);
        shadow.setOffsetY(3);

        Text errorText = new Text("");
        errorText.setFill(Color.RED);

        layout.getChildren().addAll(name, statusBox, dueDate, description, createAndCancel, errorText);
        createLayout.add(layout, 1,0);
        // =============End make screen=============

        createBtn.setOnAction(e -> {
            if(nameField.getText().equals("")) {
                errorText.setText("Invalid input: task name cannot be empty.");
                return;
            }
            task.setName(nameField.getText());
            task.setDescription(desArea.getText());
            task.setDueDate(datePicker.getValue().toString());
            task.setStatus(status.getValue());
            // add the changed task back in
            project.addTask(task);
            stage.close();
        });
        cancelBtn.setOnAction(e -> {
            stage.close();
        });
        stage.setScene(new Scene(createLayout));
        stage.show();
    }

    /**
     * return a solution show to show task
     * @param taskReport the layout that being modified
     * @return a solution of showing it
     */
    public GridPane showTask(GridPane taskReport, TaskModel task) {


        VBox reportBox = new VBox(8);
 

        reportBox.setPadding(new Insets(12));
        Text name = new Text(task.getName());
        name.setFill(Color.BLACK);
        name.setFont(Font.font("verdana", FontWeight.BOLD, 10));
        Text description = new Text("Description: \n" + task.getDescription());
        description.setFont(Font.font("verdana",FontWeight.MEDIUM, 10));
        description.setFill(Color.BLACK);
        Text dueDate = new Text("Due Date: " + task.getDueDate());
        dueDate.setFont(Font.font("verdana", FontWeight.BOLD, 8));
        dueDate.setFill(Color.RED);
        reportBox.getChildren().addAll(name, description, dueDate);
        taskReport.add(reportBox, 0,0);
        taskReport.setOnMouseClicked((e -> editTask()));
        // UI part
        taskReport.setStyle("-fx-background-color: #C1E2F2");
        taskReport.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            taskReport.setStyle("-fx-background-color: #FFFFFF");
        });
        taskReport.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            taskReport.setStyle("-fx-background-color: #C1E2F2");
        });

        // when drag on task report
        return taskReport;
    }

    public void editTask() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(task.getName());

        // =============Start make screen=============

        GridPane editLayout = new GridPane();



        Text newTaskText = new Text("New\nTask:");
        // create grid panel that holds everything
        VBox layout = new VBox(30);
        layout.setPadding(new Insets(12));
        // each line will be in a HBox
        // name
        HBox name = new HBox(8);
        Text nameText = new Text("Task name:");

        TextField nameField = new TextField(task.getName());
        name.getChildren().addAll(nameText, nameField);
        // status
        HBox statusBox = new HBox(8);
        Text statusText = new Text("Pick Status:");

        ChoiceBox<String> status = new ChoiceBox<>();
        status.getItems().addAll(project.getColumns());
        status.setValue(task.getStatus());
        statusBox.getChildren().addAll(statusText, status);

        // due date
        HBox dueDate = new HBox(8);
        Text ddText  = new Text("Due Date");
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(task.getAddDate());
        dueDate.getChildren().addAll(ddText, datePicker);


        // description
        VBox description = new VBox(8);
        Text desText = new Text("Task Description:");
        TextArea desArea = new TextArea(task.getDescription());
        desArea.setMaxSize(300, 200);
        description.getChildren().addAll(desText, desArea);

        // create and Cancel button
        HBox doneAndCancel = new HBox(8);
        Button doneBtn = new Button("Done");
        Button cancelBtn = new Button("Cancel");
        doneAndCancel.getChildren().addAll(doneBtn, cancelBtn);



        Text errorText = new Text("");
        errorText.setFill(Color.RED);

        layout.getChildren().addAll(name, statusBox, dueDate, description, doneAndCancel, errorText);
        editLayout.add(layout, 1,0);
        // =============End make screen=============

        doneBtn.setOnAction(e -> {
            project.removeTask(task);
            task.setName(nameField.getText());
            task.setDescription(desArea.getText());
            task.setStatus(status.getValue());
            task.setDueDate(datePicker.getValue().toString());
            project.addTask(task);
            stage.close();
        });
        cancelBtn.setOnAction(e -> {
            stage.close();
        });
        stage.setScene(new Scene(editLayout));
        stage.show();
    }

    public static void sop(Object x){ System.out.println(x);}

    public static void main(String[] args){
    }

}