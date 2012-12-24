package honorscltracker;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The main class for the Main application. The application exists in
 * three main states: home screen, main screen, and data adding screen. The
 * graphics of this application are fully customizable, with preferences for
 * each user stored in that user's file.
 * 
 * <h1>Home Screen</h1>
 * When the application is running in this state, no file is opened and no
 * workout data is in memory. The window displays a choice of options to the
 * user: create a new file or open an existing file. The background of the
 * window is defined by the preference <code>homescreenBGPaint</code> and the
 * outline is defined by the preference <code>homescreenBGStroke</code>
 * 
 * <h1>Main Screen</h1>
 * When the application is running in this state, a file is active, and data is
 * stored in memory. The window displays the user's workout data in a table, by
 * month. The application keeps track of the month and year being displayed, and
 * the window includes buttons to move forward and backward between months. The
 * background of the window is defined by the preference <code>mainscreenBGPaint
 * and the outline is defined by the preference <code>homescreenBGStroke</code>.
 * </code>.
 * 
 * <h1>Data Screen</h1>
 * When the application is running in this state, a file is active, and the user
 * is being asked to input data. The background of the window is defined by the
 * preference <code>datascreenBGPaint</code> and the outline is defined by the
 * preference <code>homescreenBGStroke</code>
 * @author Connor
 */
public class Main extends Application {
    //<editor-fold defaultstate="collapsed" desc=" Vars ">
    private HashMap<String, Object> settings = new HashMap<>();
    private java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("M/d/yyyy");
    
    private int currentYear;
    private YearList years = new YearList();
    
    private Group mainScreen, dataScreen, root;
    private Node table;
    private Text title;
    private VBox homeScreen;
    private String currState = "homescreen";
    private double tableY;
    
    private SimpleObjectProperty<Paint> windowBGPaint = new SimpleObjectProperty<>();
    private SimpleObjectProperty<Paint> windowBGStroke = new SimpleObjectProperty<>();
    private SimpleObjectProperty<Paint> windowButtonBG = new SimpleObjectProperty<>();
    private SimpleObjectProperty<Paint> windowButtonFG = new SimpleObjectProperty<>();

    private File file;
    
    private double dragOffsetX = 0, dragOffsetY = 0, scrollStartY, translateStartY;
    
    private final String[] recognizedPrefs = new String[]{"datascreenBGPaint", 
            "datascreenBGStroke", "datascreenButtonBGPaint", "datascreenButtonFGPaint", 
            "datascreenButtonOutlinePaint", "datascreenLabelFont", "datascreenLabelPaint", 
            "datascreenWindowButtonBGPaint", "datascreenWindowButtonFGPaint", "homescreenBGPaint", 
            "homescreenBGStroke", "homescreenTextFont", "homescreenTextPaint", 
            "homescreenWindowButtonBGPaint", "homescreenWindowButtonFGPaint", "mainscreenBGPaint", 
            "mainscreenBGStroke", "mainscreenButtonBGPaint", "mainscreenButtonFGPaint", 
            "mainscreenButtonOutlinePaint", "mainscreenLabelFont", "mainscreenLabelPaint", 
            "mainscreenWindowButtonBGPaint", "mainscreenWindowButtonFGPaint", "scrollbarBGPaint", 
            "scrollbarFGPaint", "scrollbarFGStroke", "scrollbarWidth", 
            "stageHeight", "stageWidth", "tableDataTextFont", 
            "tableDataTextPaint", "tableHeaderBGPaint", "tableHeaderTextFont", 
            "tableHeaderTextPaint", "tableRow1BGPaint", "tableRow2BGPaint", 
    };
    //</editor-fold>
    
    public static void main(String[] args) {
        //load last configuration
        launch(args);
    }
    
    public Main() {
        Arrays.sort(recognizedPrefs);
        
        GregorianCalendar c = new GregorianCalendar();
        currentYear = c.get(Calendar.YEAR);
    }
    
    @Override
    public void start(final Stage primaryStage) {
        defaultSettings();
        updateWindowGraphics();

        primaryStage.setTitle("FX RunningBuddy");
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        root = new Group(); //Contains everything to be displayed
        root.getChildren().add(getWindow(primaryStage)); //window graphics
        
        getHomeScreen();
        root.getChildren().add(homeScreen);

        Scene scene = new Scene(root, (Double) settings.get("stageWidth"), (Double) settings.get("stageHeight"));
        scene.setFill(new Color(0,0,0,0));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void createNewFile(String fileName) {
        file = new File(fileName);
        defaultSettings();
        getMainScreen();
        mainScreen.setLayoutY(25);
        root.getChildren().remove(homeScreen);
        root.getChildren().add(mainScreen);
        currState = "mainscreen";
        updateWindowGraphics();
    } 
    
    private void defaultSettings() {
        settings.put("homescreenBGPaint", Color.BLACK.brighter().brighter());
        settings.put("homescreenBGStroke", Color.BLACK);
        settings.put("homescreenTextPaint", Color.WHITE);
        settings.put("homescreenTextFont", new Font("Arial", 16));
        settings.put("mainscreenBGPaint", Color.BLACK.brighter().brighter());
        settings.put("mainscreenBGStroke", Color.BLACK);
        settings.put("datascreenBGPaint", Color.BLACK.brighter().brighter());
        settings.put("datascreenBGStroke", Color.BLACK);
        settings.put("mainscreenWindowButtonFGPaint", Color.WHITE);
        settings.put("homescreenWindowButtonFGPaint", Color.WHITE);
        settings.put("datascreenWindowButtonFGPaint", Color.WHITE);
        settings.put("mainscreenWindowButtonBGPaint", new Color(0,0,0,0));
        settings.put("homescreenWindowButtonBGPaint", new Color(0,0,0,0));
        settings.put("datascreenWindowButtonBGPaint", new Color(0,0,0,0));
        settings.put("datascreenLabelFont", new Font("Arial",16));
        settings.put("datascreenLabelPaint", Color.WHITESMOKE);
        settings.put("mainscreenLabelPaint", Color.LIME);
        settings.put("mainscreenLabelFont", new Font("Arial", 30));
        settings.put("tableDataTextPaint", Color.WHITESMOKE);
        settings.put("tableDataTextFont", new Font("Arial", 12));
        settings.put("tableHeaderTextPaint", Color.LIME);
        settings.put("tableHeaderTextFont", new Font("Comic Sans MS", 16));
        settings.put("mainscreenButtonFGPaint", Color.LIME);
        settings.put("mainscreenButtonBGPaint", new Color(0,0,0,0));
        settings.put("mainscreenButtonOutlinePaint", Color.LIME);
        settings.put("datascreenButtonFGPaint", Color.LIME);
        settings.put("datascreenButtonBGPaint", new Color(0,0,0,0));
        settings.put("datascreenButtonOutlinePaint", Color.LIME);
        settings.put("tableRow1BGPaint", Color.GRAY);
        settings.put("tableRow2BGPaint", new Color(1,1,1,0));
        settings.put("tableHeaderBGPaint", Color.BLACK);
        settings.put("stageWidth", 700.0);
        settings.put("stageHeight", 450.0);
        settings.put("scrollbarWidth", 10.0);
        settings.put("scrollbarFGPaint", Color.BLACK);
        settings.put("scrollbarFGStroke", Color.LIGHTGRAY);
        settings.put("scrollbarBGPaint", Color.BLUEVIOLET);
    }
    
    private void getDataInputScreen() {
        VBox v = new VBox();
        v.setSpacing(10);
        
        final ComboBox yearCombo = new ComboBox();
        yearCombo.setItems(javafx.collections.FXCollections.observableArrayList(years.getYearsList()));
        HBox hb1 = new HBox(); hb1.getChildren().add(new Label("Year:"));
        hb1.getChildren().add(yearCombo);
        v.getChildren().add(hb1);
        
        final TextField dateField = new TextField();
        dateField.setPromptText("Enter date (MM/DD/YYYY)");
        HBox hb2 = new HBox(); hb2.getChildren().add(new Label("Date:"));
        hb2.getChildren().add(dateField);
        v.getChildren().add(hb2);
        
        final TextField contactNameField = new TextField();
        contactNameField.setPromptText("Enter contact name:");
        final TextField contactEmailField = new TextField();
        contactEmailField.setPromptText("Contact email:");
        final TextField contactPhoneField = new TextField();
        contactEmailField.setPromptText("Contact phone:");
        HBox hb3 = new HBox(); hb3.getChildren().add(new Label("Contact:"));
        VBox contact = new VBox();
        contact.getChildren().add(contactNameField);
        contact.getChildren().add(contactEmailField);
        contact.getChildren().add(contactPhoneField);
        hb3.getChildren().add(contact);
        v.getChildren().add(hb3);
        
        Button addJog = new Button("Add Jog");
        addJog.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                GregorianCalendar d = new GregorianCalendar();
                double distanceVal = -1;
                double timeVal = -1;
                boolean goForIt = true;
                try{
                    d.setTimeInMillis(format.parse(dateField.getText()).getTime());
                } catch(ParseException ex) { 
                    dateField.setText("");
                    goForIt = false;
                }
                try {
                    distanceVal = Double.parseDouble(contactNameField.getText());
                } catch(NumberFormatException ex) {
                    contactNameField.setText("");
                }
                try {
                    timeVal = Integer.parseInt(contactEmailField.getText());
                } catch(NumberFormatException ex) {
                    contactEmailField.setText("");
                }
                CLActivity j = null;
                if(distanceVal < 0 && timeVal > 0) {
                    j = new Jog(60*timeVal,d);
                } else if(distanceVal > 0 && timeVal < 0) {
                    j = new Jog(distanceVal, Unit.MILE, d);
                } else if(distanceVal > 0 && timeVal > 0) {
                    j = new Jog(distanceVal, Unit.MILE, timeVal*60, d);
                } else {
                    j = new Jog(d);
                }
                if(goForIt) {
                    //go for it
                    years.addData(j);
                }
            }
        });
        v.getChildren().add(addJog);
        
        Separator sep = new Separator();
        v.getChildren().add(sep);
        
        
        
        HBox exerciseData = new HBox();
        exerciseData.setSpacing(5);
//        exerciseData.setAlignment(Pos.CENTER);
        final TextField sets = new TextField();
        sets.setPromptText("Sets");
        exerciseData.getChildren().add(sets);
        Text of = new Text("of");
        of.setFill((Paint) settings.get("datascreenLabelPaint"));
        exerciseData.getChildren().add(of);
        final TextField reps = new TextField();
        reps.setPromptText("Reps");
        exerciseData.getChildren().add(reps);
        v.getChildren().add(exerciseData);
        
        Button addExer = new Button("Add Exercise");
        addExer.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                GregorianCalendar d = new GregorianCalendar();
                int s = 0;
                int r = 0;
                boolean goForIt = true;
                try{
                    d.setTimeInMillis(format.parse(dateField.getText()).getTime());
                } catch(ParseException ex) { 
                    dateField.setText(""); 
                    goForIt = false;
                }
                try {
                    s = Integer.parseInt(sets.getText());
                } catch(NumberFormatException ex) {
                    sets.setText("");
                    goForIt = false;
                }
                try {
                    r = Integer.parseInt(reps.getText());
                } catch(NumberFormatException ex) {
                    reps.setText("");
                    goForIt = false;
                }
                if(goForIt) {
                    //go for it
                    Exercise e = new Exercise(yearCombo.getValue().toString(),s,r,d);
                    years.addData(e);
                }
            }
        });
        v.getChildren().add(addExer);
        
        Separator sep2 = new Separator();
        
        v.getChildren().add(sep2);
        
        Text t3 = new Text("New Exercise");
        t3.setFont((Font) settings.get("datascreenLabelFont"));
        t3.setFill((Paint) settings.get("datascreenLabelPaint"));
        v.getChildren().add(t3);
        
        final TextField newExer = new TextField();
        newExer.setPromptText("Exercise name");
        v.getChildren().add(newExer);
        
        Button newExerButton = new Button("Add new exercise");
        newExerButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                String name = newExer.getText();
                if(name != null && !name.equals("")) {
                    years.addExerciseName(name);
                    yearCombo.setItems(javafx.collections.FXCollections.observableArrayList(years.getYearsList()));
                }
            }
        });
        v.getChildren().add(newExerButton);
        ScrollPane scroll = new ScrollPane();
        scroll.setStyle("-fx-background-color:none;");
        scroll.setPrefSize((Double) settings.get("stageWidth")-20, (Double) settings.get("stageHeight")-35);
        scroll.setContent(v);
        
        Group backButton = new Group();
        Rectangle r = new Rectangle(0,0,30,7);
        r.setFill((Paint) settings.get("datascreenButtonBGPaint"));
        r.setStroke((Paint) settings.get("datascreenButtonOutlinePaint"));
        backButton.getChildren().add(r);
        Polygon p = new Polygon();
        p.getPoints().addAll(15.0, 2.0, 13.0, 5.0, 17.0, 5.0);
        p.setFill((Paint) settings.get("datascreenButtonFGPaint"));
        backButton.getChildren().add(p);
        backButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                root.getChildren().remove(dataScreen);
                updateDisplay();
                root.getChildren().add(mainScreen);
                currState = "mainscreen";
                updateWindowGraphics();
            }
        });
        backButton.setLayoutX((Double) settings.get("stageWidth") - 145);
        backButton.setLayoutY((Double) settings.get("stageHeight") - 34);
        
        dataScreen = new Group();
        dataScreen.getChildren().add(scroll);
        dataScreen.getChildren().add(backButton);
        //TODO: data screen
    }
    
    private void getHomeScreen() {
        homeScreen = new VBox();
        homeScreen.setSpacing(20);
        homeScreen.setAlignment(Pos.CENTER);
        Text t = new Text("Welcome to RunningBuddy!\nPlease enter file name and choose an action:");
        t.setTextAlignment(TextAlignment.CENTER);
        t.setFont((Font) settings.get("homescreenTextFont"));
        t.setFill((Paint) settings.get("homescreenTextPaint"));
        homeScreen.getChildren().add(t);
        
        final TextField f = new TextField("Enter file name here");
        f.setAlignment(Pos.BASELINE_CENTER);
        homeScreen.getChildren().add(f);
        
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(20);
        Button b = new Button("Create new file");
        b.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                createNewFile(f.getText());
            }
        });
        buttonBox.getChildren().add(b);
        
        Button b2 = new Button("Open existing file");
        b2.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                String file = f.getText();
                try {
                    openExistingFile(file);
                } catch (ParseException ex) {
                    f.setText("Could not read file: "+file);
                }
            }
        });
        buttonBox.getChildren().add(b2);
        homeScreen.getChildren().add(buttonBox);
        
        homeScreen.setPrefSize((Double) settings.get("stageWidth") - 20, (Double) settings.get("stageHeight") - 35);
        homeScreen.setLayoutX(10);
        homeScreen.setLayoutY(25);
    }
    
    private void getMainScreen() {
        mainScreen = new Group();
        
        title = getTitle();
        title.setLayoutX(((Double) settings.get("stageWidth")-title.getBoundsInParent().getWidth())/2);
        tableY = title.getBoundsInParent().getHeight();
        title.setLayoutY(tableY - 10);
        mainScreen.getChildren().add(title);
        
        table = getTable();
        
        Group prevButton = new Group();
        Rectangle prevButtonBG = new Rectangle(50,tableY-10);
        prevButtonBG.setArcHeight(5); prevButtonBG.setArcWidth(5);
        prevButtonBG.setFill((Paint) settings.get("mainscreenButtonBGPaint"));
        prevButtonBG.setStroke((Paint) settings.get("mainscreenButtonOutlinePaint"));
        prevButtonBG.setStrokeWidth(2);
        prevButton.getChildren().add(prevButtonBG);
        Polygon prevButtonFG = new Polygon(35,(tableY-20)/2,15,(tableY-10)/2,35,tableY/2);
        prevButtonFG.setFill((Paint) settings.get("mainscreenButtonFGPaint"));
        prevButton.getChildren().add(prevButtonFG);
        prevButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                currentYear--;
                updateDisplay();
            }
        });
        prevButton.setLayoutX(10); prevButton.setLayoutY(0);
        mainScreen.getChildren().add(prevButton);
        
        Group nextButton = new Group();
        Rectangle nextButtonBG = new Rectangle(50,tableY-10);
        nextButtonBG.setArcHeight(5); nextButtonBG.setArcWidth(5);
        nextButtonBG.setFill((Paint) settings.get("mainscreenButtonBGPaint"));
        nextButtonBG.setStroke((Paint) settings.get("mainscreenButtonOutlinePaint"));
        nextButtonBG.setStrokeWidth(2);
        nextButton.getChildren().add(nextButtonBG);
        Polygon nextButtonFG = new Polygon(15,(tableY-20)/2,35,(tableY-10)/2,15,tableY/2);
        nextButtonFG.setFill((Paint) settings.get("mainscreenButtonFGPaint"));
        nextButton.getChildren().add(nextButtonFG);
        nextButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                currentYear++;
                updateDisplay();
            }
        });
        nextButton.setLayoutX((Double) settings.get("stageWidth")-60);
        nextButton.setLayoutY(0); 
        mainScreen.getChildren().add(nextButton);
        
        Group inputButton = new Group();
        Rectangle inputButtonBg = new Rectangle(30, 7);
        inputButtonBg.setStroke((Paint) settings.get("mainscreenButtonOutlinePaint"));
        inputButtonBg.setFill((Paint) settings.get("mainscreenButtonBGPaint"));
        inputButton.getChildren().add(inputButtonBg);
        Polygon inputButtonFg = new Polygon();
        inputButtonFg.getPoints().addAll(0.0,0.0,4.0,0.0,2.0,4.5);
        inputButtonFg.setFill((Paint) settings.get("mainscreenButtonFGPaint"));
        inputButtonFg.setLayoutX(13); inputButtonFg.setLayoutY(1.5);
        inputButton.getChildren().add(inputButtonFg);
        inputButton.setLayoutX((Double) settings.get("stageWidth")-145);
        inputButton.setLayoutY(-7.5);
        inputButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                getDataInputScreen();
                dataScreen.setLayoutX(10);
                dataScreen.setLayoutY(25);
                root.getChildren().remove(mainScreen);
                root.getChildren().add(dataScreen);
                currState = "datascreen";
                updateWindowGraphics();
            }
        });
        mainScreen.getChildren().add(inputButton);
        
        Group saveButton = new Group();
        Rectangle saveButtonBg = new Rectangle(30, 7);
        saveButtonBg.setStroke((Paint) settings.get("mainscreenButtonOutlinePaint"));
        saveButtonBg.setFill((Paint) settings.get("mainscreenButtonBGPaint"));
        saveButton.getChildren().add(saveButtonBg);
        Rectangle saveButtonFg = new Rectangle(0,0,4,4);
        saveButtonFg.setFill((Paint) settings.get("mainscreenButtonFGPaint"));
        saveButtonFg.setLayoutX(13); saveButtonFg.setLayoutY(1.5);
        saveButton.getChildren().add(saveButtonFg);
        saveButton.setLayoutX((Double) settings.get("stageWidth")-185);
        saveButton.setLayoutY(-7.5);
        saveButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                writeToFile(file);
            }
        });
        mainScreen.getChildren().add(saveButton);
        
        Group homeButton = new Group();
        Rectangle homeButtonBg = new Rectangle(6, 30);
        homeButtonBg.setStroke((Paint) settings.get("mainscreenButtonOutlinePaint"));
        homeButtonBg.setFill((Paint) settings.get("mainscreenButtonBGPaint"));
        homeButton.getChildren().add(homeButtonBg);
        Polygon homeButtonFg = new Polygon();
        homeButtonFg.getPoints().addAll(0.0,2.0,3.0,0.0,3.0,4.0);
        homeButtonFg.setFill((Paint) settings.get("mainscreenButtonFGPaint"));
        homeButtonFg.setLayoutX(1.5); homeButtonFg.setLayoutY(13);
        homeButton.getChildren().add(homeButtonFg);
        homeButton.setLayoutX(2.5);
        homeButton.setLayoutY(((Double) settings.get("stageHeight") - 35)/2-15);
        homeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
//                getDataInputScreen();
//                dataScreen.setLayoutX(10);
//                dataScreen.setLayoutY(25);
                root.getChildren().remove(mainScreen);
                root.getChildren().add(homeScreen);
                file = null;
                years.clear();
                currState = "homescreen";
                updateWindowGraphics();
            }
        });
        mainScreen.getChildren().add(homeButton);
        
        table.setLayoutY(tableY); table.setLayoutX(10);
        mainScreen.getChildren().add(table);
    }
    
    private Text getTitle() {
        Text titleText=new Text(currentYear+"-"+((currentYear+1))%100);
        titleText.setFont((Font) settings.get("mainscreenLabelFont"));
        titleText.setFill((Paint) settings.get("mainscreenLabelPaint"));
        return titleText;
    }
    
    /**
     * Initializes the window background, window border, and window buttons.
     * @param primaryStage the stage this window will be displayed in
     * @return a decorated window
     */
    private Group getWindow(final Stage primaryStage) {
        Group wdw = new Group(); //the node that will be returned
        
        //create the window background
        Polygon windowBackground = new Polygon();
        windowBackground.fillProperty().bind(windowBGPaint);
        windowBackground.strokeProperty().bind(windowBGStroke);
        double w = (Double) settings.get("stageWidth");
        double h = (Double) settings.get("stageHeight");
        windowBackground.getPoints().addAll(0d,15d,0d,h,w,h,w,15d,w-15,0d,w-65,0d,w-80,15d);
        windowBackground.setStrokeWidth(1.5);
        wdw.getChildren().add(windowBackground);
        
        //create close button
        Group closeButton = new Group();
        Rectangle box = new Rectangle(10,10);
        box.fillProperty().bind(windowButtonBG);
        closeButton.getChildren().add(box);
        Line slash1 = new Line(0,0,10,10);
        slash1.strokeProperty().bind(windowButtonFG);
        closeButton.getChildren().add(slash1);
        Line slash2 = new Line(0,10,10,0);
        slash2.strokeProperty().bind(windowButtonFG);
        closeButton.getChildren().add(slash2);
        closeButton.setLayoutX(w-30);
        closeButton.setLayoutY(5);
        closeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
//                if(file != null) {
//                    writeToFile(file);
//                }
                primaryStage.close();
            }
        });
        wdw.getChildren().add(closeButton);
        
        //create minimize button
        Group minimizeButton = new Group();
        Rectangle box2 = new Rectangle(10,10);
        box2.fillProperty().bind(windowButtonBG);
        minimizeButton.getChildren().add(box2);
        Line line = new Line(0,10,10,10);
        line.strokeProperty().bind(windowButtonFG);
        minimizeButton.getChildren().add(line);
        minimizeButton.setLayoutX(w-45);
        minimizeButton.setLayoutY(5);
        minimizeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
//                System.out.println("Minimizing");
                primaryStage.toBack();
            }
        });
        wdw.getChildren().add(minimizeButton);
        
        //create drag button
        Group dragArea = new Group();
        Rectangle box3 = new Rectangle(12,12);
        box3.fillProperty().bind(windowButtonBG);
        dragArea.getChildren().add(box3);
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 6 - (i%2); j++) {
                Rectangle dot = new Rectangle(1,1);
                dot.setX(j*2+(i%2));
                dot.setY(i*2);
                dot.fillProperty().bind(windowButtonFG);
                dragArea.getChildren().add(dot);
            }
        }
        dragArea.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                dragOffsetX = arg0.getScreenX() - primaryStage.getX();
                dragOffsetY = arg0.getScreenY() - primaryStage.getY();
            }
        });
        dragArea.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                primaryStage.setX(arg0.getScreenX() - dragOffsetX);
                primaryStage.setY(arg0.getScreenY() - dragOffsetY);
            }
        });
        dragArea.setLayoutX(w - 61);
        dragArea.setLayoutY(4);
        wdw.getChildren().add(dragArea);
        
        return wdw;
    }
    
    private void openExistingFile(String fileName) throws ParseException {
        file = new File(fileName);
        defaultSettings();
        loadFromFile(file);
        getMainScreen();
        mainScreen.setLayoutY(25);
        root.getChildren().remove(homeScreen);
        root.getChildren().add(mainScreen);
        currState = "mainscreen";
        updateWindowGraphics();
    }
    
    private String getYearString(int year) {
        String s = year+"-";
        s += (year + 1) % 100;
        return s;
    }
    
    private void updateDisplay() {
        mainScreen.getChildren().remove(title);
        title = new Text(getYearString(currentYear));
        title.setFont((Font) settings.get("mainscreenLabelFont"));
        title.setFill((Paint) settings.get("mainscreenLabelPaint"));
        title.setLayoutX(((Double) settings.get("stageWidth")-title.getBoundsInParent().getWidth())/2);
        title.setLayoutY(tableY - 10);
        mainScreen.getChildren().add(title);
        
        mainScreen.getChildren().remove(table);
        table = getTable();
        table.setLayoutY(tableY); table.setLayoutX(10);
        mainScreen.getChildren().add(table);
    }
    
    private void updateWindowGraphics() {
        windowBGPaint.setValue((Paint) settings.get(currState+"BGPaint"));
        windowBGStroke.setValue((Paint) settings.get(currState+"BGStroke"));
        windowButtonBG.setValue((Paint) settings.get(currState+"WindowButtonBGPaint"));
        windowButtonFG.setValue((Paint) settings.get(currState+"WindowButtonFGPaint"));
    }
    
    //<editor-fold defaultstate="collapsed" desc=" Get Table ">
    private Node getTable() {
        Group tempTable = new Group();
        
        Year yr = new Year(currentYear);
        int yearIdx = years.indexOf(yr);
        if(yearIdx >= 0) {
            yr = years.get(yearIdx);
        }
        Object[][] data = new Object[4][yr.getSize()];
        int row = 0;
        for(String s : yr.getAllDescs()) {
            int count = 0;
            for(CLActivity c : yr.getCLActivities(s)) {
                if(count == 0) {
                    data[0][row] = s;
                }
                data[1][row] = format.format(c.getDate().getTime());
                data[2][row] = c.getContact().getName();
                data[3][row] = c.getHours();
                count ++;
                row ++;
            }
        }
        
        Text asdf = new Text("T");
        asdf.setFont((Font) settings.get("tableDataTextFont"));
        double rowHeight = asdf.getBoundsInParent().getHeight() + 4;
        
        final double stageWidth = (Double) settings.get("stageWidth");
        
        Group c2 = getColumn(data[1], rowHeight, -1);
        Text c2Head = new Text("Date");
        c2Head.setTextOrigin(VPos.CENTER); 
        c2Head.setFont((Font) settings.get("tableHeaderTextFont"));
        c2Head.setFill((Paint) settings.get("tableHeaderTextPaint"));
        double c2Width = Math.max(c2.getBoundsInParent().getWidth(),
                c2Head.getBoundsInParent().getWidth())+10;
        
        final double headerRowHeight = c2Head.getBoundsInParent().getHeight()+10;
        Rectangle headerBG = new Rectangle(0, 0, stageWidth-20, headerRowHeight);
        headerBG.setArcHeight(headerRowHeight/2);
        headerBG.setArcWidth(headerRowHeight/2);
        headerBG.setFill((Paint) settings.get("tableHeaderBGPaint"));
        tempTable.getChildren().add(headerBG);
        
        Group c3 = getColumn(data[2], rowHeight, -1);
        Text c3Head = new Text("Contact");
        c3Head.setTextOrigin(VPos.CENTER); 
        c3Head.setFont((Font) settings.get("tableHeaderTextFont"));
        c3Head.setFill((Paint) settings.get("tableHeaderTextPaint"));
        double c3Width = Math.max(c3.getBoundsInParent().getWidth(),
                c3Head.getBoundsInParent().getWidth())+10;
        
        Group c4 = getColumn(data[3], rowHeight, -1);
        Text c4Head = new Text("Hours");
        c4Head.setTextOrigin(VPos.CENTER); 
        c4Head.setFont((Font) settings.get("tableHeaderTextFont"));
        c4Head.setFill((Paint) settings.get("tableHeaderTextPaint"));
        double c4Width = Math.max(c4.getBoundsInParent().getWidth(),
                c4Head.getBoundsInParent().getWidth())+10;
        
        Group c1 = getColumn(data[0], rowHeight, stageWidth-20-c2Width-c3Width-c4Width);
        Text c1Head = new Text("Desc");
        c1Head.setFont((Font) settings.get("tableHeaderTextFont"));
        c1Head.setFill((Paint) settings.get("tableHeaderTextPaint"));
        double c1Width = Math.max(c1.getBoundsInParent().getWidth(),
                c1Head.getBoundsInParent().getWidth())+10;
        c1Head.setTextOrigin(VPos.CENTER);
        
        c1Head.setLayoutX(5); c1Head.setLayoutY(headerRowHeight / 2);
        tempTable.getChildren().add(c1Head);
        c2Head.setLayoutX(c1Width+5); c2Head.setLayoutY(headerRowHeight / 2);
        tempTable.getChildren().add(c2Head);
        c3Head.setLayoutX(c1Width+c2Width+5); c3Head.setLayoutY(headerRowHeight / 2);
        tempTable.getChildren().add(c3Head);
        c4Head.setLayoutX(c1Width+c2Width+c3Width+5); c4Head.setLayoutY(headerRowHeight / 2);
        tempTable.getChildren().add(c4Head);
        
        double y = 0;
        Group dataRows = new Group();
        for(int i = 0; i < yr.getSize(); i++) {
            dataRows.getChildren().add(getTableRowRect(i, y, rowHeight));
        }
        
        c1.setLayoutX(5);
        c2.setLayoutX(c1Width+5);
        c3.setLayoutX(c1Width+c2Width+5);
        c4.setLayoutX(c1Width+c2Width+c3Width+5);
        dataRows.getChildren().add(c1);
        dataRows.getChildren().add(c2);
        dataRows.getChildren().add(c3);
        dataRows.getChildren().add(c4);
        dataRows.setLayoutY(headerRowHeight+5);
        tempTable.getChildren().add(dataRows);
        
        final double availableSpace = ((Double) settings.get("stageHeight")) - tableY - headerRowHeight - 35;
        Group scrollBar = getScrollbar(availableSpace, 5, 0, dataRows,headerRowHeight);
        scrollBar.setLayoutX(stageWidth-20-((Double)settings.get("scrollbarWidth")));
        scrollBar.setLayoutY(headerRowHeight);
        tempTable.getChildren().add(scrollBar);
        //TODO: table scroll bar
        return tempTable;
    }
    
    private Group getColumn(Object[] data, double rowHeight, double maxWidth) {
        //TODO: no header -- put that in getTable so that it is unaffected by scrollbar
        Group col = new Group();
        
        double y = 0;
        for(Object o : data) {
            Text rowData = new Text(o.toString());
            rowData.setTextOrigin(VPos.CENTER);
            rowData.setFont((Font) settings.get("tableDataTextFont"));
            rowData.setFill((Paint) settings.get("tableDataTextPaint"));
            rowData.setLayoutY(y+rowHeight/2);
            rowData.setLayoutX(5);
            double dataWidth = rowData.getBoundsInParent().getWidth();
            if(maxWidth > 0 && dataWidth > maxWidth - 4) {
                double percent = (maxWidth < 4) ? 0 : ((maxWidth - 4) / dataWidth);
                String newData = o.toString();
                newData = newData.substring(0, (int) (newData.length()*percent) - 4);
                newData += "...";
                rowData.setText(newData);
            }
            col.getChildren().add(rowData);
            y += rowHeight;
        }
        return col;
    }
    
    private Group getScrollbar(final double availableSpace, final double topPad, 
            double bottomPad, final Node n, final double minClipY) {
        Group group = new Group();
        
        final double scrollbarWidth = (Double) settings.get("scrollbarWidth");
//        System.out.println("stageHeight:"+(Double) settings.get("stageHeight"));
//        System.out.println("avail: "+availableSpace);
        final double height = n.getBoundsInLocal().getHeight() + topPad + bottomPad;
//        System.out.println("Available:"+availableSpace+", dataHeight="+height);
        double scrollSpace = availableSpace - scrollbarWidth;
        final double barHeight = Math.max(scrollbarWidth, scrollSpace*Math.min(1, availableSpace/height));
        
        Polygon scrollBarBG = new Polygon();
        scrollBarBG.getPoints().addAll(0.0,scrollbarWidth/2, 0.0, scrollSpace-scrollbarWidth/2,
                scrollbarWidth/2, scrollSpace, scrollbarWidth, scrollSpace-scrollbarWidth/2,
                scrollbarWidth, scrollbarWidth/2, scrollbarWidth/2, 0.0);
        scrollBarBG.setFill((Paint) settings.get("scrollbarBGPaint"));
        scrollBarBG.relocate(0, scrollbarWidth/2);
        group.getChildren().add(scrollBarBG);
        
        Polygon topBox = new Polygon();
        topBox.getPoints().addAll(0.0,0.0,0.0,scrollbarWidth-2, scrollbarWidth/2-1, scrollbarWidth/2-1,
                scrollbarWidth-2, scrollbarWidth-2, scrollbarWidth-2,0.0);
        topBox.setFill((Paint) settings.get("scrollbarFGPaint"));
        topBox.setStroke((Paint) settings.get("scrollbarFGStroke"));
        topBox.relocate(0 ,0);
        group.getChildren().add(topBox);
        
        Polygon bottomBox = new Polygon();
        bottomBox.getPoints().addAll(0.0,0.0,0.0,scrollbarWidth-2, scrollbarWidth-2, scrollbarWidth-2,
                scrollbarWidth-2,0.0, scrollbarWidth/2-1, scrollbarWidth/2-1);
        bottomBox.setFill((Paint) settings.get("scrollbarFGPaint"));
        bottomBox.setStroke((Paint) settings.get("scrollbarFGStroke"));
        bottomBox.relocate(0, availableSpace-scrollbarWidth-1);
        group.getChildren().add(bottomBox);
        
        final Polygon bar = new Polygon();
        bar.getPoints().addAll(0.0,scrollbarWidth/2-1, 0.0, barHeight-scrollbarWidth/2,
                scrollbarWidth/2-1, barHeight-1, scrollbarWidth-2, barHeight-scrollbarWidth/2,
                scrollbarWidth-2, scrollbarWidth/2-1, scrollbarWidth/2-1, 0.0);
        bar.setFill((Paint) settings.get("scrollbarFGPaint"));
        bar.setStroke((Paint) settings.get("scrollbarFGStroke"));
        bar.relocate(0, scrollbarWidth/2-1);
        
        bar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
//                System.out.println("Bar is at: "+bar.getLayoutY());
                scrollStartY = arg0.getSceneY() - bar.getLayoutY();
//                System.out.println("translateStartY="+translateStartY);
            }
        });
        
        final Rectangle clip = new Rectangle(0, 0, n.getBoundsInLocal().getWidth(), availableSpace-topPad-bottomPad);
//        clip.setLayoutY(minClipY+topPad);
        n.setClip(clip);
        bar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                double minY = scrollbarWidth/2;
                double maxY = availableSpace-scrollbarWidth/2;
                double newY = Math.max(minY, Math.min(arg0.getSceneY() - scrollStartY, maxY-barHeight));
//                System.out.println("min:"+minY+", max:"+maxY+", val:"+newY);
                bar.setLayoutY(newY);
                double newClipY = ((newY-minY)/(maxY-minY))*height;
                clip.setLayoutY(/*minClipY+topPad+*/newClipY);
//                System.out.println("\tclip="+dataRows.getClip().getLayoutY());
                n.setLayoutY(minClipY+topPad-newClipY);
            }
        });
        group.getChildren().add(bar);
        return group;
    }
    
    private Rectangle getTableRowRect(int row, double y, double dataRowHeight) {
        Rectangle r = new Rectangle();
        r.setWidth((Double) settings.get("stageWidth") - 20 - (Double) settings.get("scrollbarWidth"));
        r.setHeight(dataRowHeight);
        r.setArcHeight(dataRowHeight/2);
        r.setArcWidth(dataRowHeight/2);
        r.setFill( (row%2==0)
                ?(Paint) settings.get("tableRow1BGPaint")
                :(Paint) settings.get("tableRow2BGPaint") );
        r.setLayoutX(0); r.setLayoutY(y);
        return r;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc=" IO Functions ">
    /**
     * Converts a double value in the range [0.0, 1.0] into a hex color: the
     * double is first converted to an integer in the range [0, 255], then the
     * hex representation of that integer is calculated and returned.
     * @param d in the range [0.0, 1.0]; treated as an RGBA color component
     * (i.e. an integer in the range [0, 255]
     * @return two-character hex string that represents the integer conversion
     * of <code>d</code>
     */
    private String getHexFromDouble(double d) {
        if(d > 1 || d < 0) {
            throw new IllegalArgumentException("Value must be between 0 and 1");
        } else {
            String hex = "";
            int val = (int) (d * 255);
            switch(val / 16) {
                case 10: hex += "A"; break;
                case 11: hex += "B"; break;
                case 12: hex += "C"; break;
                case 13: hex += "D"; break;
                case 14: hex += "E"; break;
                case 15: hex += "F"; break;
                default: hex += (val / 16); break;
            }
            switch(val % 16) {
                case 10: hex += "A"; break;
                case 11: hex += "B"; break;
                case 12: hex += "C"; break;
                case 13: hex += "D"; break;
                case 14: hex += "E"; break;
                case 15: hex += "F"; break;
                default: hex += (val % 16); break;
            }
            return hex;
        }
    }
    
    /**
     * Parses a two-character string containing a hex number to get the
     * corresponding integer. All characters after the first two are ignored.
     * @param s the string to be parsed
     * @return the integer value of the hex string contained in s
     */
    private int getIntFromHex(String s) {
        int val = 0;
        switch(s.substring(0, 1).toLowerCase()) {
            case "a": case "A": val += 160; break;
            case "b": case "B": val += 176; break;
            case "c": case "C": val += 192; break;
            case "d": case "D": val += 208; break;
            case "e": case "E": val += 224; break;
            case "f": case "F": val += 240; break;
            default: val += 16 * Integer.parseInt(s.substring(0, 1)); break;
        }
        switch(s.substring(1, 2).toLowerCase()) {
            case "a": case "A": val += 10; break;
            case "b": case "B": val += 11; break;
            case "c": case "C": val += 12; break;
            case "d": case "D": val += 13; break;
            case "e": case "E": val += 14; break;
            case "f": case "F": val += 15; break;
            default: val += Integer.parseInt(s.substring(1, 2)); break;
        }
        return val;
    }
    
    //<editor-fold defaultstate="collapsed" desc=" Output ">
    /**
     * Method intended for use inside encodePaint(Paint). Returns a String
     * representation of <code>c</code> in the form #RRGGBBOO where RR is the
     * hex value of the <code>Color</code>'s red component, GG is the hex for
     * the green component, BB for the blue component, and OO is the hex for the
     * opacity component.
     */
    private String encodeColor(Color c) {
        String enc = "#";
        enc += getHexFromDouble(c.getRed());
        enc += getHexFromDouble(c.getGreen());
        enc += getHexFromDouble(c.getBlue());
        enc += getHexFromDouble(c.getOpacity());
        return enc;
    }
    
    private String encodeFont(Font f) {
        return "f{"+f.getName()+","+f.getSize()+"}";
    }
    
    private String encodePaint(Paint p) {
        String enc = "";
        if(p instanceof Color) {
            Color c = (Color) p;
            enc += encodeColor(c);
        } else if(p instanceof LinearGradient) {
            LinearGradient lg = (LinearGradient) p;
            enc += "lg";
            enc += "{"+lg.getStartX()+","+lg.getStartY()+","+lg.getEndX()+","
                    +lg.getEndY()+"}";
            for(Stop s: lg.getStops()) {
                enc += "["+s.getOffset()+","+encodeColor(s.getColor())+"]";
            }
        } else if(p instanceof RadialGradient) {
            RadialGradient rg = (RadialGradient) p;
            enc = "rg{";
            enc += rg.getFocusAngle()+","+rg.getFocusDistance()+"}";
            for(Stop s: rg.getStops()) {
                enc += "["+s.getOffset()+","+encodeColor(s.getColor())+"]";
            }
        }
        return enc;
    }
    
    private void writeToFile (File f) {
        try(java.io.PrintWriter p = new java.io.PrintWriter(f)) {
            p.println("--Prefs--");
            for(String s: settings.keySet()) {
                Object val = settings.get(s);
                String line = s+":";
//                System.out.println(val.getClass().getName());
                switch(val.getClass().getName()){
                    case "javafx.scene.paint.Color": line += encodeColor((Color) val); break;
                    case "javafx.scene.paint.RadialGradient":
                    case "javafx.scene.paint.LinearGradient":
                        line += encodePaint((Paint) val); break;
                    case "javafx.scene.text.Font":
                        line += encodeFont((Font) val); break;
                    case "java.lang.Integer":
                    case "java.lang.Double":
                        line += val; break;
                }
                p.println(line);
            }
            p.println("--/Prefs--");
            
            p.println("--Data--");
            for(Year m : years) {
                for(Date d: m.getAllDates()) {
                    for(Jog j : m.getJogs(d)) {
                        String line = "j"+d.month+"/"+d.day+"/"+d.year+";{";
                        line += j.isDistanceKnown()
                                ?j.getDist()
                                :"n/a";
                        line += ","+j.getDistUnits().toString()+",";
                        line += (j.isTimed() ? j.getTime() : "n/a") + "}";
                        p.println(line);
                    }
                    for(Exercise e: m.getExercises(d)) {
                        String line = "e"+d.month+"/"+d.day+"/"+d.year+";{";
                        line += e.getName()+","+e.getSets()+","+e.getReps()+"}";
                        p.println(line);
                    }
                }
            }
            p.println("--/Data--");
        } catch(IOException ex) {
            
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Input ">
    private Color decodeColor(String s) throws ParseException {
        if(!s.startsWith("#") || s.length() != 9) {
            throw new ParseException("Decoding color; expected #",1);
        } else {
            try {
                double r = getIntFromHex(s.substring(1, 3)) / 255.0;
                double g = getIntFromHex(s.substring(3, 5)) / 255.0;
                double b = getIntFromHex(s.substring(5, 7)) / 255.0;
                double a = getIntFromHex(s.substring(7)) / 255.0;
                return new Color(r, g, b, a);
            } catch(NumberFormatException ex) {
                Pattern p = Pattern.compile("^[0-9AaBbCcDdEeFf]");
                Matcher m = p.matcher(s);
                m.find();
                throw new ParseException("Illegal character in color string",m.start());
            }
        }
    }

    private Exercise decodeExercise(String s) throws ParseException {
        int idx = 0;
        try {
            GregorianCalendar g = new GregorianCalendar();
            java.util.Date d = format.parse(s.substring(1, s.indexOf(';')));
            g.setTime(d);
            idx = s.indexOf('{')+1;
            String temp = s.substring(idx, idx = s.indexOf(',',idx)); //PRESERVE THIS temp
            String name = temp;
            temp = s.substring(idx+1, idx = s.indexOf(',', idx+1));
            int sets = Integer.parseInt(temp);
            temp = s.substring(idx+1, idx = s.indexOf('}'));
            int reps = Integer.parseInt(temp);
            return new Exercise(name, sets, reps, g);
        } catch(ParseException ex) {
            throw new ParseException("Illegal date format in exercise", 1);
        } catch(NumberFormatException ex) {
            throw new ParseException("Unexpected type in exercise, expected int",idx);
        }
    }
    
    private Font decodeFont(String string) throws ParseException {
        try{
            String name = string.substring(string.indexOf('{')+1, string.indexOf(','));
            double size = Double.parseDouble(string.substring(string.indexOf(',')+1, string.indexOf('}')));
            return new Font(name, size);
        } catch (NumberFormatException ex) {
            throw new ParseException("Illegal double value", string.indexOf(','));
        }
    }

    private Paint decodeGradient(String s) throws ParseException {
        boolean isLG = false;
        if(s.startsWith("lg")) {
            isLG = true;
        }
        int idx = 3;
        double x1, y1, x2 = 0, y2 = 0;
        try{
            String temp = s.substring(idx, idx = s.indexOf(',',idx+1));
            x1 = Double.parseDouble(temp);
            if(isLG) {
                temp = s.substring(idx+1, idx = s.indexOf(',',idx+1));
                y1 = Double.parseDouble(temp);
                x2 = Double.parseDouble(s.substring(idx+1, idx = s.indexOf(',',idx+1)));
                y2 = Double.parseDouble(s.substring(idx+1, idx = s.indexOf('}',idx+1)));
            } else {
                temp = s.substring(idx+1, idx = s.indexOf('}',idx+1));
                y1 = Double.parseDouble(temp);
            }
        } catch(NumberFormatException ex) {
            throw new ParseException("Illegal value, expected type double", idx);
        }
        ArrayList<Stop> stops = new ArrayList<>();
        if(!s.substring(idx+1).startsWith("[")) {
            throw new ParseException("Error creating radial gradient: expected gradient stops",idx);
        } else {
            try {
                while ( s.indexOf('[', idx) > -1 ) {
                    String temp = s.substring(s.indexOf('[',idx)+1, idx = s.indexOf(',', idx));
                    double d = Double.parseDouble(temp);
                    temp = s.substring(s.indexOf(',', idx)+1, idx = s.indexOf(']', idx));
                    Color c = decodeColor(temp);
                    stops.add(new Stop(d, c));
                }
            } catch (NumberFormatException ex) {
                throw new ParseException("Attemping to parse "+(isLG?"LG":"RG")+" stops, encountered invalid double value", idx);
            } catch (ParseException | StringIndexOutOfBoundsException ex) {
                throw new ParseException("Attempting to parse "+(isLG?"LG":"RG")+" stops, encountered invalid color value", idx);
            }
        }
        if(isLG) {
            return new LinearGradient(x1, y1, x2, y2, true, CycleMethod.NO_CYCLE, stops);
        } else {
            return new RadialGradient(x1, y1, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, stops);
        }
    }

    private Jog decodeJog(String s) throws ParseException {
        int idx = 0;
        try {
            GregorianCalendar g = new GregorianCalendar();
            java.util.Date d = format.parse(s.substring(1, s.indexOf(';')));
            g.setTime(d);
            idx = s.indexOf('{')+1;
            String temp = s.substring(idx, idx = s.indexOf(',',idx)); //PRESERVE THIS temp
            boolean hasdist = ! temp.toLowerCase().equals("n/a");
            double dist = 0;
            if(hasdist) {
                dist = Double.parseDouble(temp);
            }
            temp = s.substring(idx+1, idx = s.indexOf(',', idx+1));
            Unit units = Unit.valueOf(temp);
            temp = s.substring(idx+1, idx = s.indexOf('}'));
            boolean hastime = ! temp.toLowerCase().equals("n/a");
            double time = 0;
            if(hastime) {
                time = Double.parseDouble(temp);
            }
            if(hasdist && hastime) {
                return new Jog(dist, units, time, g);
            } else if(hasdist) {
                return new Jog(dist, units, g);
            } else if(hastime) {
                return new Jog(time, g);
            } else {
                return new Jog(g);
            }
        } catch(ParseException ex) {
            throw new ParseException("Illegal date format in jog", 1);
        } catch(NumberFormatException ex) {
            throw new ParseException("Unexpected type, expected int or double",idx);
        }
    }
    
    private String getEnv(int mode) {
        switch(mode) {
            case 1: return "Prefs";
            case 2: return "Data";
            default: return "(Unknown Environment)";
        }
    }
    
    private void loadFromFile(File file) throws ParseException {
        int line = 0;
        try {
            java.io.BufferedReader b = new java.io.BufferedReader(new java.io.FileReader(file));
            final int prefsMode = 1, dataMode = 2;
            int mode = 0;
            String s = b.readLine();
            while(s != null) {
                line ++;
                switch (s) {
                    case "--Prefs--":
                    case "--Data--":
                        if(mode != 0) {
                            throw new ParseException("Unexpected "+s+" while inside an environment."
                                    +" Expected --/"+getEnv(mode)+"-- first.", 0);
                        } else {
                            mode = (s.contains("P"))?prefsMode:dataMode;
                        }
                        break;
                    case "--/Prefs--":
                        if(mode == prefsMode) {
                            mode = 0;
                        } else {
                            throw new ParseException("Unexpected "+s+" while outside"+
                                    "Prefs environment", 0);
                        }
                        break;
                    case "--/Data--":
                        if(mode == dataMode) {
                            mode = 0;
                        } else {
                            throw new ParseException("Unexpected "+s+" while outside"+
                                    "Data environment", 0);
                        }
                        break;
                    default:
                        if(mode == prefsMode) {
                            String[] prop = s.replaceAll("\\s", "").split(":");
                            prop[1] = prop[1].trim();
                            if(Arrays.binarySearch(recognizedPrefs, prop[0]) < 0) {
                                //TODO: log a warning--unrecognized pref
//                                throw new ParseException("Unrecognized preference: "+prop[0],1);
                            } else if(prop.length > 2) {
                                throw new ParseException("Found extra ':'", s.indexOf(':', s.indexOf(':')));
                            } else {
                                /*
                                 * Here's how we will try to figure out the
                                 * value type:
                                 * 1. check for a recognized prefix (explicit
                                 *    type declaration)
                                 *   a. rg -RadialGradient
                                 *   b. lg -LinearGradient
                                 *   c. f  -Font
                                 *   d. #  -Color
                                 * 2. see if it's a double
                                 * 3. any future types that get added
                                 */
                                if(prop[1].startsWith("rg") || prop[1].startsWith("lg")){
                                    if(prop[0].endsWith("Paint") || prop[0].endsWith("Stroke"))
                                        settings.put(prop[0], decodeGradient(prop[1]));
                                    else
                                        ; //warning
                                    break; //exit this massively complicated switch statement
                                } else if(prop[1].startsWith("#")) {
                                    if(prop[0].endsWith("Paint") || prop[0].endsWith("Stroke"))
                                        settings.put(prop[0], decodeColor(prop[1]));
                                    else
                                        ; //TODO: log a warning
                                    break; //exit this massively complicated switch statement
                                } else if(prop[1].startsWith("f")) {
                                    if(prop[0].endsWith("Font"))
                                        settings.put(prop[0], decodeFont(prop[1]));
                                    else; //warning
                                    break; //exit this massively complicated switch statement
                                }
                                try{ 
                                        double val = Double.parseDouble(prop[1]);
                                        settings.put(prop[0], val);
                                        break;
                                } catch(NumberFormatException e) {
                                    throw new ParseException("Value '"+prop[1]+"' for property '"+prop[0]+"' does not correspond to any known types", 0);
                                }
                            }
                        } else if(mode == dataMode) {
                            s = s.replaceAll("\\s", "");
                            if(s.startsWith("j")) {
                                years.addData(decodeJog(s));
                            } else if(s.startsWith("e")) {
                                years.addData(decodeExercise(s));
                            }
                        } else {
//                            System.out.println("Unrecognized line: '"+s+"', treating as comment");
                        }
                }
                s = b.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            throw new ParseException("Error in line "+line+":"+ex.getMessage(), ex.getErrorOffset());
        }
    }
    //</editor-fold>
    
    //</editor-fold>
    
}
