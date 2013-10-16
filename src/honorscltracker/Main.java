package honorscltracker;

import honorscltracker.graphics.DataScreen;
import honorscltracker.graphics.DetailScreen;
import honorscltracker.graphics.MainScreen;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The main class for the Main application. The application exists in
 * three main states: home screen, main screen, and data adding screen. The
 * graphics of this application are fully customizable, with preferences for
 * each user stored in that user's file.
 * 
 * <strong>Home Screen</strong>
 * When the application is running in this state, no file is opened and no
 * workout data is in memory. The window displays a choice of options to the
 * user: create a new file or open an existing file. The background of the
 * window is defined by the preference <code>homescreenBGPaint</code> and the
 * outline is defined by the preference <code>homescreenBGStroke</code>
 * 
 * <strong>Main Screen</strong>
 * When the application is running in this state, a file is active, and data is
 * stored in memory. The window displays the user's workout data in a table, by
 * month. The application keeps track of the month and year being displayed, and
 * the window includes buttons to move forward and backward between months. The
 * background of the window is defined by the preference <code>mainscreenBGPaint
 * and the outline is defined by the preference <code>homescreenBGStroke</code>.
 * </code>.
 * 
 * <strong>Data Screen</strong>
 * When the application is running in this state, a file is active, and the user
 * is being asked to input data. The background of the window is defined by the
 * preference <code>datascreenBGPaint</code> and the outline is defined by the
 * preference <code>homescreenBGStroke</code>
 * @author Connor
 */
public class Main extends Application {
    //<editor-fold defaultstate="collapsed" desc=" Vars ">
    private HashMap<String, Object> settings = new HashMap<>();
    public static SimpleDateFormat format = new SimpleDateFormat("M/d/yyyy");
    
    private int currentYear;
    private YearList years = new YearList();
    
    private MainScreen mainScreen;
    private DataScreen dataScreen;
    private DetailScreen detailScreen;
    private FileChooser fileChooser;
    private Group root;
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
    
    private Stack<UserAction> undo, redo;
    //</editor-fold>
    
    /**
     * The main method for the HonorsCLTracker program.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * Creates a new Main class. This class is the manager for all the screens
     * in the HonorsCLTracker program.
     */
    public Main() {
        GregorianCalendar c = new GregorianCalendar();
        if(c.get(Calendar.MONTH) < 5) {
            currentYear = c.get(Calendar.YEAR)-1;
        } else {
            currentYear = c.get(Calendar.YEAR);
        }
    }
    
    @Override
    public void start(final Stage primaryStage) {
        defaultSettings();

        primaryStage.setTitle("FX RunningBuddy");
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        root = new Group(); //Contains everything to be displayed
        root.getChildren().add(getWindow(primaryStage)); //window graphics
        
        //<editor-fold defaultstate="collapsed" desc="Initialize all the screens">
        fileChooser = new FileChooser();
        getHomeScreen(primaryStage);
        mainScreen = new MainScreen(settings, new Year(currentYear));
        mainScreen.setLayoutY(25);
        detailScreen = new DetailScreen(settings);
//        detailScreen.setLayoutY(15);
        dataScreen = new DataScreen(settings, years.getYearsList());
        dataScreen.setLayoutY(25);
        dataScreen.setLayoutX(10);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Implement action handlers">
        //user is on mainScreen, wants the dataScreen to be shown (i.e. clicks
        //on the button to show the dataScreen
        mainScreen.setDataScreenRequestHandler(new Handler() {
            @Override
            public void action(Object data) {
                dataScreen.setOwner(null);
                switchScreens("mainscreen", "datascreen");
            }
        });
        
        //user is on the mainScreen, wants detailScreen for a particular
        //activity (clicks on an activity in the table)
        mainScreen.setDetailRequestHandler(new Handler() {
            @Override
            public void action(Object data) {
                detailScreen.setCLActivity((CLActivity) data);
                switchScreens("mainscreen", "detailscreen");
            }
        });
        
        //user is on the mainScreen, wants dataScreen to edit an activity (right
        //clicks on activity in the table)
        mainScreen.setEditCLActivityRequestHandler(new Handler() {
            @Override
            public void action(Object data) {
                dataScreen.setOwner((CLActivity) data);
                switchScreens("mainscreen", "datascreen");
            }
        });
        
        //user is on the mainScreen, wants to see the homeScreen (clicks on the
        //homeScreen button)
        mainScreen.setHomeScreenRequestHandler(new Handler() {
            @Override
            public void action(Object data) {
                switchScreens("mainscreen", "homescreen");
            }
        });
        
        //user clicks the save button -- write to file
        mainScreen.setSaveRequestHandler(new Handler() {
            @Override
            public void action(Object data) {
                if(file == null) {
                    file = fileChooser.showSaveDialog(primaryStage);
                }
                writeToFile(file);
            }
        });
        
        //user is viewing year n, clicks the right arrow button to advance to
        //year n+1 
        mainScreen.setNextYearRequestHandler(new Handler() {
            @Override
            public void action(Object data) {
                updateMainScreen(((Year) data).getStartYear() + 1);
            }
        });
        
        //user is on mainScreen viewing year n, clicks the left arrow button
        //to back up to year n-1
        mainScreen.setPrevYearRequestHandler(new Handler() {
            @Override
            public void action(Object data) {
                updateMainScreen( ((Year) data).getStartYear() - 1);
            }
        });
        
        //user is on dataScreen, clicks button to return to mainScreen
        dataScreen.setMainScreenRequestHandler(new Handler() {
            @Override
            public void action(Object data) {
                switchScreens("datascreen", "mainscreen");
            }
        });
        
        //user clicks the submit button on the datascreen and no owner is set
        //for the datascren
        dataScreen.setNewActivityHandler(new Handler() {
            @Override
            public void action(Object data) {
                CLActivity c = (CLActivity) data;
                years.addData(c);
                undo.push(UserAction.addition(c));
                redo.clear();
            }
        });
        
        //user clicks the submit button on the datascreen while the owner is set
        dataScreen.setUpdateActivityHandler(new Handler() {
            @Override
            public void action(Object data) {
                mainScreen.update();
            }
        });
        
        //user is viewing details for a CLActivity and clicks the button to
        //return to the mainscreen
        detailScreen.setMainScreenRequestHandler(new Handler() {
            @Override
            public void action(Object data) {
                switchScreens("detailscreen", "mainscreen");
            }
        });
        //</editor-fold>
        
        root.getChildren().add(homeScreen);

        Scene scene = new Scene(root, (Double) settings.get("stageWidth"), (Double) settings.get("stageHeight"));
        scene.setFill(new Color(0,0,0,0));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /*
     * convenience method to simplify code in action handlers.  When mainScreen
     * requests that the current year in the display be incremented or
     * decremented, it passes the Year that it is currently displaying.  From
     * that we extract the numerical value of the year, add or subtract 1, and
     * call this method to find the Year object for the requested year
     */
    private void updateMainScreen(int newYear) {
        if(years.indexOf(newYear) != -1) {
            mainScreen.update(years.get(years.indexOf(newYear)));
        } else {
            mainScreen.update(new Year(newYear));
        }
    }
    
    /*
     * removes the current screen from the Scene, adds the requested screen, and
     * updates the window graphics
     */
    private void switchScreens(String from, String to) {
        switch(from) {
            case "mainscreen": root.getChildren().remove(mainScreen); break;
            case "datascreen": root.getChildren().remove(dataScreen); break;
            case "detailscreen": root.getChildren().remove(detailScreen); break;
            case "homescreen": root.getChildren().remove(homeScreen); break;
        }
        switch(to) {
            case "mainscreen": root.getChildren().add(mainScreen); break;
            case "datascreen": root.getChildren().add(dataScreen); break;
            case "detailscreen": root.getChildren().add(detailScreen); break;
            case "homescreen": root.getChildren().add(homeScreen); break;
        }
        currState = to;
        windowBGPaint.setValue((Paint) settings.get(currState+"BGPaint"));
        windowBGStroke.setValue((Paint) settings.get(currState+"BGStroke"));
        windowButtonBG.setValue((Paint) settings.get(currState+"WindowButtonBGPaint"));
        windowButtonFG.setValue((Paint) settings.get(currState+"WindowButtonFGPaint"));
    }
    
    /* 
     * initializes the GUI settings.  These are not changed anywhere else, so if
     * you want to edit the background colors/borders/font colors/etc., this is
     * the place to do it
     */
    private void defaultSettings() {
        settings.put("homescreenBGPaint", Color.BLACK);
        settings.put("homescreenBGStroke", new Color(1,.5,0,.8));
        settings.put("homescreenTextPaint", Color.LIGHTGRAY);
        settings.put("homescreenTextFont", new Font("Arial", 16));
//        settings.put("mainscreenBGPaint", new Color(.506, .243, .118, .8));
        settings.put("mainscreenBGPaint", new Color(0,0,0,.8));
        settings.put("mainscreenBGStroke", Color.BLACK);
        settings.put("datascreenBGPaint", new Color(.1, .1, .1, .8));
        settings.put("datascreenBGStroke", Color.TRANSPARENT);
        settings.put("detailscreenBGPaint", Color.BLACK);
        settings.put("detailscreenBGStroke", Color.DARKORANGE);
        settings.put("mainscreenWindowButtonFGPaint", new Color(1,.5,0,1));
        settings.put("homescreenWindowButtonFGPaint", new Color(1,.5,0,1));
        settings.put("datascreenWindowButtonFGPaint", new Color(1,.5,0,1));
        settings.put("detailscreenWindowButtonFGPaint", Color.WHITE);
        settings.put("mainscreenWindowButtonBGPaint", Color.TRANSPARENT);
        settings.put("homescreenWindowButtonBGPaint", Color.TRANSPARENT);
        settings.put("datascreenWindowButtonBGPaint", Color.TRANSPARENT);
        settings.put("detailscreenWindowButtonBGPaint", Color.TRANSPARENT);
        settings.put("mainscreenLabelPaint", Color.WHITE);
        settings.put("mainscreenLabelFont", new Font("Arial", 30));
        settings.put("tableDataTextPaint", Color.BLACK);
        settings.put("tableDataTextFont", new Font("Arial", 14));
        settings.put("tableHeaderTextPaint", Color.WHITE);
        settings.put("tableHeaderTextFont", new Font("Comic Sans MS", 16));
        settings.put("mainscreenButtonFGPaint", Color.WHITE);
        settings.put("mainscreenButtonBGPaint", new Color(.4,.4,.4,1));
        settings.put("mainscreenButtonOutlinePaint", Color.WHITE);
        settings.put("datascreenButtonFGPaint", Color.WHITE);
        settings.put("datascreenButtonBGPaint", Color.TRANSPARENT);
        settings.put("datascreenButtonOutlinePaint", Color.WHITE);
        settings.put("detailscreenButtonFGPaint", Color.BLACK);
        settings.put("detailscreenButtonBGPaint", Color.WHITE);
        settings.put("detailscreenButtonOutlinePaint", Color.BLACK);
        settings.put("tableRow1BGPaint", new Color(1, 0.5, 0, 1));
        settings.put("tableRow2BGPaint", new Color(1, .639, .288, 1));
        settings.put("tableHeaderBGPaint", Color.TRANSPARENT);
        settings.put("stageWidth", 700.0);
        settings.put("stageHeight", 450.0);
        settings.put("scrollbarWidth", 10.0);
        settings.put("scrollbarFGPaint", new Color(1, .5, 0, 1));
        settings.put("scrollbarFGStroke", Color.BLACK);
        settings.put("scrollbarBGPaint", new Color(1, 1, 1, 0.15));
        windowBGPaint.setValue((Paint) settings.get("homescreenBGPaint"));
        windowBGStroke.setValue((Paint) settings.get("homescreenBGStroke"));
        windowButtonBG.setValue((Paint) settings.get("homescreenWindowButtonBGPaint"));
        windowButtonFG.setValue((Paint) settings.get("homescreenWindowButtonFGPaint"));
    }
    
    /*
     * initializes the homeScreen (file open/create screen)
     */
    private void getHomeScreen(final Stage primaryStage) {
        homeScreen = new VBox();
        homeScreen.setSpacing(20);
        homeScreen.setAlignment(Pos.CENTER);
        Text t = new Text("Welcome to the Honors Comp Learning tracker!\n"
                + "If you have an existing file, enter its name or click Choose,"
                + " then click Open.\n If you have not used Comp Learning"
                + " Tracker before, click New.");
        t.setTextAlignment(TextAlignment.CENTER);
        t.setFont((Font) settings.get("homescreenTextFont"));
        t.setFill((Paint) settings.get("homescreenTextPaint"));
        homeScreen.getChildren().add(t);
        
        HBox fileBox = new HBox();
        fileBox.setAlignment(Pos.CENTER);
        fileBox.setSpacing(20);
        final TextField f = new TextField("Enter file name here");
        f.setAlignment(Pos.BASELINE_CENTER);
        f.setPrefColumnCount(50);
        f.setOnDragOver(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.LINK);
            }
        });
        f.setOnDragDropped(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                if(event.getDragboard().hasFiles()) {
                    List<File> files = event.getDragboard().getFiles();
                    if(files.size() > 1) {
                        f.setText("Please select only one file!");
                        f.setAlignment(Pos.CENTER);
                        event.consume();
                        return;
                    }
                    try {
                        openExistingFile(files.get(0).getAbsolutePath());
                    } catch(ParseException ex) {
                        f.setText("Could not open file; "+ex.getMessage());
                        f.setAlignment(Pos.CENTER);
                    } finally {
                        event.consume();
                    }
                }
            }
        });
        fileBox.getChildren().add(f);
        Button chooseButton = new Button("Choose...");
        chooseButton.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                File temp = fileChooser.showOpenDialog(primaryStage);
                if(temp != null) {
                    f.setText(temp.getAbsolutePath());
                }
            }
        });
        fileBox.getChildren().add(chooseButton);
        homeScreen.getChildren().add(fileBox);
        
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(20);
        Button b = new Button("Create new file");
        b.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                if(mainScreen == null) {
                    mainScreen = new MainScreen(settings, new Year(currentYear));
                }
                mainScreen.setLayoutY(25);
                switchScreens("homescreen", "mainscreen");
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
                    f.setText("Could not read file: "+file+"; "+ex.getMessage());
                    f.setAlignment(Pos.CENTER);
                }
            }
        });
        buttonBox.getChildren().add(b2);
        homeScreen.getChildren().add(buttonBox);
        
        homeScreen.setPrefSize((Double) settings.get("stageWidth") - 20, (Double) settings.get("stageHeight") - 35);
        homeScreen.setLayoutX(10);
        homeScreen.setLayoutY(25);
    }
    
    /*
     * Initializes the window background, window border, and window buttons.
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
        Rectangle box = new Rectangle(11,11);
        box.fillProperty().bind(windowButtonBG);
        closeButton.getChildren().add(box);
        Line slash1 = new Line(1,1,10,10);
        slash1.strokeProperty().bind(windowButtonFG);
        closeButton.getChildren().add(slash1);
        Line slash2 = new Line(1,10,10,1);
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
        Rectangle box2 = new Rectangle(11,11);
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
                primaryStage.setIconified(true);
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
    
    /*
     * Opens a new file and initializes the mainScreen to show the newly opened
     * data.  If there are problems with the file's contents, the ParseException
     * thrown by loadFromFile is allowed to pass
     */
    private void openExistingFile(String fileName) throws ParseException {
        file = new File(fileName);
        loadFromFile(file);
        if(years.indexOf(currentYear) >= 0) {
            mainScreen.update(years.get(years.indexOf(currentYear)));
        } else {
            mainScreen.update(new Year(currentYear));
        }
        switchScreens("homescreen", "mainscreen");
    }

    /*
     * Writes the data to the given file
     */
    private void writeToFile (File f) {
        try(java.io.PrintWriter p = new java.io.PrintWriter(f)) {
            for(Year y : years) {
                for(String d: y.getAllDescs()) {
                    for(CLActivity j : y.getCLActivities(d)) {
                        String line = "~Activity~\n";
                        line += "desc="+d+"\n";
                        line += "date="+format.format(j.getDate().getTime())+"\n";
                        line += "year="+j.getStartYr()+"\n";
                        Contact c = j.getContact();
                        line += "contactname="+c.getName()+"\n";
                        line += "contactemail="+c.getEmail()+"\n";
                        line += "contactphone="+c.getPhone()+"\n";
                        line += "hours="+j.getHours()+"\n";
                        line += "~~Details~~\n";
                        line += j.getDetails()+"\n~~/Details~~\n~/Activity~";
                        p.println(line);
                        p.println();
                    }
                }
            }
        } catch(IOException ex) {
            
        }
    }
    
    /*
     * Parses the given file.  Is fairly robust--usually can tell you where it
     * encountered an error, if it does. This is stored in the ParseException
     * that is thrown.
     */
    private void loadFromFile(File file) throws ParseException {
        int lineNum = 0;
        try {
            java.io.BufferedReader b = new java.io.BufferedReader(new java.io.FileReader(file));
            String s = b.readLine();
            CLActivity a = null;
            boolean parsingAct = false, parsingDetails = false;
            int complete = 0;
            Contact c = null;
            String details = null;
            while(s != null) {
                lineNum ++;
                if(s.equals("~Activity~") && !parsingAct) {
                    parsingAct = true;
                    a = new CLActivity();
                    c = new Contact();
                    details = "";
                } else if(s.startsWith("desc=") && parsingAct) {
                    a.setDesc(s.substring(s.indexOf('=')+1));
                    complete += 1;
                } else if(s.startsWith("date=") && parsingAct) {
                    String date = s.substring(s.indexOf('=')+1);
                    GregorianCalendar g = new GregorianCalendar();
                    g.setTime(format.parse(date));
                    a.setDate(g);
                    complete += 1<<1;
                } else if(s.startsWith("contactname=") && parsingAct) {
                    c.setName(s.substring(s.indexOf('=')+1));
                    complete += 1<<2;
                } else if(s.startsWith("contactemail=") && parsingAct) {
                    c.setEmail(s.substring(s.indexOf('=')+1));
                    complete += 1<<3;
                } else if(s.startsWith("contactphone=") && parsingAct) {
                    c.setPhone(s.substring(s.indexOf('=')+1));
                    complete += 1<<4;
                } else if(s.startsWith("hours=") && parsingAct) {
                    a.setHours(Double.parseDouble(s.substring(s.indexOf('=')+1)));
                    complete += 1<<5;
                } else if(s.startsWith("year=") && parsingAct) {
                    a.setStartYr(Integer.parseInt(s.substring(s.indexOf('=')+1)));
                    complete += 1<<6;
                } else if(s.equals("~~Details~~") && parsingAct) {
                    parsingDetails = true;
                } else if(parsingDetails) {
                    if(!s.equals("~~/Details~~")) {
                        details += s + "\n";
                    } else {
                        parsingDetails = false;
                        a.setDetails(details);
                        complete += 1<<7;
                    }
                } else if(parsingAct && s.equals("~/Activity~")) {
                    if(complete == (1<<8) - 1) {
                        a.setContact(c);
                        years.addData(a);
                        parsingAct = false;
                        complete = 0;
                        a = null;
                        c = null;
                    } else {
                        throw new ParseException("Incomplete CL activity at line "+lineNum,0);
                    }
                } else if(s.equals("")){
                    
                } else {
                    throw new ParseException("Invalid syntax at line "+lineNum, 0);
                }
                s = b.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
}
