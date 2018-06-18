package application;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.Optional;

/**
 * This class modifies the functionality of main screen
 */
public class MainScreen implements ModelListener {
    private Stage mainScreenStage;// main screen has it's own stage
    private BorderPane boardLayout;// main screen uses a border panel
    private TaskBoardModel currentBoard;// the current project showing
    private ProjectView currentProjectView;
    private Button newProjectBtn, editBtn, saveBtn, loadBtn, logoutBtn;
    private ChoiceBox<String> projectPicker;
    private HBox top;

    public MainScreen(Stage stage) throws Exception {
        mainScreenStage = stage;
        boardLayout = new BorderPane();
        top = new HBox(12);
        boardLayout.setStyle("-fx-background-color: #FFFFFF");
        boardLayout.setMinSize(Main.WINDOWHIGHT, Main.WINDOWWIDTH);
//        BorderPane.clearConstraints(boardLayout);
        currentBoard = new TaskBoardModel();
        currentBoard.attach(this);
        setMainScene();

        stage.setScene(new Scene(boardLayout, Main.WINDOWWIDTH, Main.WINDOWHIGHT));
        stage.show();
        stage.setOnCloseRequest(e -> {
            if(Main.DIRTY) {
                promptSave();
                e.consume();
            }
            else {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void load(File file) throws IOException {
        XMLDecoder decoder = new XMLDecoder(new BufferedInputStream( new FileInputStream(file.getPath())));
        this.currentBoard = (TaskBoardModel) decoder.readObject();
        decoder.close();
        currentBoard.attach(this);
        currentProjectView = new ProjectView(this.mainScreenStage, this.currentBoard, this.currentBoard.getCurrentProjectModel());
        currentProjectView.update();
        update();

        editBtn.setDisable(false);
        saveBtn.setDisable(false);

        this.mainScreenStage.show();
    }
    public void save(File file) throws IOException {
        XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream( new FileOutputStream(file)));
        encoder.writeObject(currentBoard);
        encoder.close();
        Main.DIRTY = false;
    }

    public void setMainScene(){
    	
        HBox pickerBox = new HBox(8);
        pickerBox.setId("picker_box");
        pickerBox.setAlignment(Pos.CENTER);
        Label pickerLb = new Label("Projects");
        this.projectPicker = new ChoiceBox<>();
        pickerBox.getChildren().setAll(pickerLb, projectPicker);
        top.getChildren().add(pickerBox);
        boardLayout.setTop(top);

    	
        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(10);
        shadow.setOffsetY(3);
        this.newProjectBtn = new Button("New Project");
        this.editBtn = new Button("Edit");
        editBtn.setDisable(true);
        this.saveBtn = new Button("Save");
        saveBtn.setDisable(true);
        this.loadBtn = new Button("Load...");
        this.logoutBtn = new Button("Logout");
        top.getChildren().addAll(
                newProjectBtn, editBtn, saveBtn, loadBtn, logoutBtn);
        top.setPadding(new Insets(12));
        // UI



        // new project
        newProjectBtn.setOnAction(e -> {
            currentProjectView = new ProjectView(mainScreenStage, currentBoard, new ProjectModel());
            currentProjectView.createProject();
            currentBoard.setCurrentProjectModel(currentProjectView.getProject());
        });
        // edit
        editBtn.setOnAction(e -> {
            currentProjectView.editProject();
        });
        saveBtn.setOnAction(e -> {
            try{
                FileChooser chooser = new FileChooser();
                FileChooser.ExtensionFilter extensionFilter =
                        new FileChooser.ExtensionFilter("XML file (*.xml)", "*.xml");
                chooser.getExtensionFilters().add(extensionFilter);

                File file = chooser.showSaveDialog(this.mainScreenStage);
                if(file != null)
                    save(file);
            }catch (IOException ex) {
                ex.printStackTrace();
            }

        });

        loadBtn.setOnAction(e -> {
            try{
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Open TaskBoard");
                FileChooser.ExtensionFilter extensionFilter =
                        new FileChooser.ExtensionFilter("XML file (*.xml)", "*.xml");
                chooser.getExtensionFilters().add(extensionFilter);

                File file = chooser.showOpenDialog(this.mainScreenStage);
                if(file != null)
                    load(file);
            }catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        logoutBtn.setOnAction(e -> {
            if(Main.DIRTY) {
                if(needSave()) return;
            }
            try{
                Main.DIRTY = false;
                Main.toLogin();
                this.mainScreenStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//
        });

        projectPicker.setOnAction(e -> {
            int p = 0;
            for(int i = 0; i < currentBoard.getProjectList().size(); i++) {
                if(currentBoard.getProjectList().get(i).getName().equals(projectPicker.getValue()))
                    p = i;
            }
            currentBoard.setCurrentProjectModel(currentBoard.getProjectList().get(p));
            currentProjectView = new ProjectView(mainScreenStage, currentBoard, currentBoard.getProjectList().get(p));
            currentProjectView.update();
        });

    }

    public void update() {
        Main.DIRTY = true;
        ScrollPane sp = new ScrollPane();
        sp.setMinHeight(Main.WINDOWHIGHT);

        boardLayout.setCenter(sp);

        sp.setContent(currentProjectView.getProjectView());

        projectPicker.getItems().setAll(currentBoard.getProjectsName());

        if(currentBoard.getProjectList().size() != 0
                && currentBoard.getCurrentProjectModel().getColumns().size() > 0) {
            editBtn.setDisable(false);
            saveBtn.setDisable(false);
        }
        else {
            editBtn.setDisable(true);
            saveBtn.setDisable(true);
        }
    }

    private void promptSave() {
        Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION);
        saveAlert.setTitle("Save Warning");
        saveAlert.setHeaderText(null);
        saveAlert.setContentText("This project has been changed, click \"save\" to save these progress.");
        ButtonType saveTp = new ButtonType("Save");
        ButtonType exitTp = new ButtonType("Exit");
        ButtonType okTp = new ButtonType("OK");
        saveAlert.getButtonTypes().setAll(saveTp,exitTp, okTp);
        Optional<ButtonType> op = saveAlert.showAndWait();
        if(op.get() == saveTp)
            saveBtn.fire();
        else if(op.get() == exitTp) {
            Platform.exit();
            System.exit(0);
        }
    }

    private boolean needSave() {
        Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION);
        saveAlert.setTitle("Save Warning");
        saveAlert.setHeaderText(null);
        saveAlert.setContentText("To save latest changes, click \"save\" to save these progress.");
            ButtonType saveTp = new ButtonType("SAVE");
            ButtonType continueTp = new ButtonType("CONTINUE");
            saveAlert.getButtonTypes().setAll(saveTp, continueTp);
            Optional<ButtonType> op = saveAlert.showAndWait();
            if(op.get() == saveTp) {
                saveBtn.fire();
                return true;
            }
            return false;
    }

}
