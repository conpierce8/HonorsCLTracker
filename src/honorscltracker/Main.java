package honorscltracker;

import honorscltracker.graphics.DataScreen;
import honorscltracker.graphics.DetailScreen;
import honorscltracker.graphics.HomeScreen;
import honorscltracker.graphics.MainScreen;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.*;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

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
    
    private HomeScreen homeScreen;
    private MainScreen mainScreen;
    private DataScreen dataScreen;
    private DetailScreen detailScreen;
    private FileChooser fileChooser;
    private Group alertBox; //TODO: add alerts
    private Group root;

    private File file;
    
    private Stack<UserAction> undo, redo;
    
    private boolean unsavedChanges = false;
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
        now();
    }
    
    /*
     * Method to update currentYear to the current year. Useful when opening new
     * files.
     */
    private void now() {
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

        primaryStage.setTitle("Comp Learning Tracker");
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        root = new Group(); //Contains everything to be displayed
        
        //<editor-fold defaultstate="collapsed" desc="Initialize all the screens">
        double width = (Double) settings.get("stageWidth");
        double height = (Double) settings.get("stageHeight");
        fileChooser = new FileChooser();
        mainScreen = new MainScreen(primaryStage, settings, new Year(currentYear));
        mainScreen.setLayoutX(width);
        mainScreen.setLayoutY(height);
        detailScreen = new DetailScreen(primaryStage, settings);
        detailScreen.setLayoutX(2*width);
        detailScreen.setLayoutY(height);
        dataScreen = new DataScreen(primaryStage, settings, years.getYearsList());
        dataScreen.setLayoutX(width);
        dataScreen.setLayoutY(0);
        homeScreen = new HomeScreen(primaryStage, settings);
        homeScreen.setLayoutX(0);
        homeScreen.setLayoutY(height);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Action handlers">
        //User is on the homescreen, clicks the New File button
        homeScreen.setOnNewFileRequestHandler(new Handler() {
            @Override
            public void action(Object data) {
                years = new YearList();
                now();
                updateMainScreen(currentYear);
                switchScreens("home", "main");
            }
        });
        
        //User is on the homescreen, clicks the Open File button or drags and
        //drops a file into the filename field
        homeScreen.setOnOpenFileRequestHandler(new Handler() {
            @Override
            public void action(Object data) {
                years = new YearList();
                try {
                    openExistingFile((String) data);
                } catch (ParseException ex) {
                    homeScreen.fileError(ex.getMessage());
                }
            }
        });
        
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
                if(unsavedChanges) {
                    //TODO: ask for confirmation to move to new screen
                } else {
                    switchScreens("mainscreen", "homescreen");
                }
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
        
        root.getChildren().addAll(homeScreen, mainScreen, dataScreen, detailScreen);
        root.setTranslateX(0);
        root.setTranslateY(-height);
//        root.setScaleX(1/3);

        Scene scene = new Scene(root, (Double) settings.get("stageWidth"), 
                (Double) settings.get("stageHeight"));
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
        double width = (Double) settings.get("stageWidth");
        double height = (Double) settings.get("stageHeight");
        Path path = new Path();
        MoveTo startPt = new MoveTo();
        LineTo endPt = new LineTo();
        switch(from) {
            case "mainscreen": 
                startPt.setX(0.5*width);
                startPt.setY(0);
                break;
            case "datascreen": 
                startPt.setX(0.5*width);
                startPt.setY(height);
                break;
            case "detailscreen":
                startPt.setX(-0.5*width);
                startPt.setY(0);
                break;
            case "homescreen": 
                startPt.setX(1.5*width);
                startPt.setY(0);
                break;
        }
        switch(to) {
            case "mainscreen": 
                endPt.setX(0.5*width);
                endPt.setY(0);
                break;
            case "datascreen": 
                endPt.setX(0.5*width);
                endPt.setY(height);
                break;
            case "detailscreen":
                endPt.setX(-0.5*width);
                endPt.setY(0);
                break;
            case "homescreen": 
                endPt.setX(1.5*width);
                endPt.setY(0);
                break;
        }
        path.getElements().add(startPt);
        path.getElements().add(endPt);
        PathTransition trans = new PathTransition(Duration.seconds(0.5),path,root);
        trans.play();
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
                        Contact contact = j.getContact();
                        line += "contactname="+contact.getName()+"\n";
                        line += "contactemail="+contact.getEmail()+"\n";
                        line += "contactphone="+contact.getPhone()+"\n";
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
            Contact contact = null;
            String details = null;
            while(s != null) {
                lineNum ++;
                if(s.equals("~Activity~") && a == null) {
                    parsingAct = true;
                    a = new CLActivity();
                    contact = new Contact();
                    details = "";
                } else if(s.startsWith("desc=") && a != null) {
                    a.setDesc(s.substring(s.indexOf('=')+1));
                    complete += 1;
                } else if(s.startsWith("date=") && a != null) {
                    String date = s.substring(s.indexOf('=')+1);
                    GregorianCalendar g = new GregorianCalendar();
                    g.setTime(format.parse(date));
                    a.setDate(g);
                    complete += 1<<1;
                } else if(s.startsWith("contactname=") && a != null) {
                    contact.setName(s.substring(s.indexOf('=')+1));
                    complete += 1<<2;
                } else if(s.startsWith("contactemail=") && a != null) {
                    contact.setEmail(s.substring(s.indexOf('=')+1));
                    complete += 1<<3;
                } else if(s.startsWith("contactphone=") && a != null) {
                    contact.setPhone(s.substring(s.indexOf('=')+1));
                    complete += 1<<4;
                } else if(s.startsWith("hours=") && a != null) {
                    a.setHours(Double.parseDouble(s.substring(s.indexOf('=')+1)));
                    complete += 1<<5;
                } else if(s.startsWith("year=") && a != null) {
                    a.setStartYr(Integer.parseInt(s.substring(s.indexOf('=')+1)));
                    complete += 1<<6;
                } else if(s.equals("~~Details~~") && a != null) {
                    parsingDetails = true;
                } else if(parsingDetails && a != null) {
                    if(!s.equals("~~/Details~~")) {
                        details += s + "\n";
                    } else {
                        parsingDetails = false;
                        a.setDetails(details);
                        complete += 1<<7;
                    }
                } else if((a != null) && s.equals("~/Activity~")) {
                    if(complete == (1<<8) - 1) {
                        a.setContact(contact);
                        years.addData(a);
                        complete = 0;
                        a = null;
                        contact = null;
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
