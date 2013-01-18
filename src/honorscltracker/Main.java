package honorscltracker;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.web.HTMLEditor;
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
    public static SimpleDateFormat format = new SimpleDateFormat("M/d/yyyy");
    
    private int currentYear;
    private YearList years = new YearList();
    
    private MainScreen mainScreen;
    private DataScreen dataScreen;
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
    //</editor-fold>
    
    public static void main(String[] args) {
        //load last configuration
        launch(args);
    }
    private Group detailScreen;
    
    public Main() {
        GregorianCalendar c = new GregorianCalendar();
        if(c.get(Calendar.MONTH) > 5) {
            currentYear = c.get(Calendar.YEAR)-1;
        } else {
            currentYear = c.get(Calendar.YEAR);
        }
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
        mainScreen = new MainScreen(settings, new Year(currentYear));
        root.getChildren().add(homeScreen);

        Scene scene = new Scene(root, (Double) settings.get("stageWidth"), (Double) settings.get("stageHeight"));
        scene.setFill(new Color(0,0,0,0));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void createNewFile(String fileName) {
        file = new File(fileName);
        defaultSettings();
        if(mainScreen == null) {
            mainScreen = new MainScreen(settings, new Year(currentYear));
        }
        mainScreen.setLayoutY(25);
        root.getChildren().remove(homeScreen);
        root.getChildren().add(mainScreen);
        currState = "mainscreen";
        updateWindowGraphics();
    } 
    
    private void defaultSettings() {
        settings.put("homescreenBGPaint", Color.BLACK);
        settings.put("homescreenBGStroke", new Color(1,.5,0,.8));
        settings.put("homescreenTextPaint", Color.LIGHTGRAY);
        settings.put("homescreenTextFont", new Font("Arial", 16));
        settings.put("mainscreenBGPaint", new Color(.506, .243, .118, .8));
        settings.put("mainscreenBGStroke", Color.BLACK);
        settings.put("datascreenBGPaint", new Color(.1, .1, .1, .8));
        settings.put("datascreenBGStroke", Color.TRANSPARENT);
        settings.put("detailscreenBGPaint", Color.ORANGE);
        settings.put("detailscreenBGStroke", Color.BLACK);
        settings.put("mainscreenWindowButtonFGPaint", new Color(1,.5,0,1));
        settings.put("homescreenWindowButtonFGPaint", new Color(1,.5,0,1));
        settings.put("datascreenWindowButtonFGPaint", new Color(1,.5,0,1));
        settings.put("detailscreenWindowButtonFGPaint", Color.BLACK);
        settings.put("mainscreenWindowButtonBGPaint", Color.TRANSPARENT);
        settings.put("homescreenWindowButtonBGPaint", Color.TRANSPARENT);
        settings.put("datascreenWindowButtonBGPaint", Color.TRANSPARENT);
        settings.put("detailscreenWindowButtonBGPaint", Color.LIGHTGRAY);
        settings.put("mainscreenLabelPaint", Color.WHITE);
        settings.put("mainscreenLabelFont", new Font("Arial", 30));
        settings.put("tableDataTextPaint", Color.BLACK);
        settings.put("tableDataTextFont", new Font("Arial", 12));
        settings.put("tableHeaderTextPaint", Color.WHITE);
        settings.put("tableHeaderTextFont", new Font("Comic Sans MS", 16));
        settings.put("mainscreenButtonFGPaint", Color.WHITE);
        settings.put("mainscreenButtonBGPaint", Color.TRANSPARENT);
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
        settings.put("scrollbarBGPaint", new Color(1, 1, 1, .4));
    }
    
    private void getHomeScreen() {
        homeScreen = new VBox();
        homeScreen.setSpacing(20);
        homeScreen.setAlignment(Pos.CENTER);
        Text t = new Text("Welcome to the Honors\n"
                + "Comp Learning tracker!\n"
                + "Please enter file name and choose an action:");
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
        if(years.indexOf(currentYear) >= 0) {
            mainScreen.updateTable(years.get(years.indexOf(currentYear)));
        } else {
            mainScreen.updateTable(new Year(currentYear));
        }
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
        
        final Object[][] data = new Object[4][yr.getSize()];
        int row = 0;
        Group dataRows = new Group();
        Text asdf = new Text("T");
        asdf.setFont((Font) settings.get("tableDataTextFont"));
        double rowHeight = asdf.getBoundsInParent().getHeight() + 4;
        for(String s : yr.getAllDescs()) {
            int count = 0;
            for(CLActivity c : yr.getCLActivities(s)) {
                if(count == 0) {
                    data[0][row] = s;
                }
                data[1][row] = format.format(c.getDate().getTime());
                data[2][row] = c.getContact().getName();
                data[3][row] = c.getHours();
                dataRows.getChildren().add(getTableRowRect(row,rowHeight, c));
                count ++;
                row ++;
            }
        }
        
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
        
        if(row > 0) {
            c1Head.setLayoutX(10);
            c1Head.setLayoutY(headerRowHeight / 2);
            tempTable.getChildren().add(c1Head);
            c2Head.setLayoutX(c1Width+10);
            c2Head.setLayoutY(headerRowHeight / 2);
            tempTable.getChildren().add(c2Head);
            c3Head.setLayoutX(c1Width+c2Width+10);
            c3Head.setLayoutY(headerRowHeight / 2);
            tempTable.getChildren().add(c3Head);
            c4Head.setLayoutX(c1Width+c2Width+c3Width+10);
            c4Head.setLayoutY(headerRowHeight / 2);
            tempTable.getChildren().add(c4Head);
        }
        
        c1.setLayoutX(10);
        c2.setLayoutX(c1Width+10);
        c3.setLayoutX(c1Width+c2Width+10);
        c4.setLayoutX(c1Width+c2Width+c3Width+10);
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
            Text rowData = new Text((o==null)?"":o.toString());
            rowData.setTextOrigin(VPos.CENTER);
            rowData.setFont((Font) settings.get("tableDataTextFont"));
            rowData.setFill((Paint) settings.get("tableDataTextPaint"));
            rowData.setLayoutY(y+rowHeight/2);
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
    
    private Rectangle getTableRowRect(int row, double dataRowHeight, final CLActivity c) {
        Rectangle r = new Rectangle();
        r.setWidth((Double) settings.get("stageWidth") - 20 - (Double) settings.get("scrollbarWidth"));
        r.setHeight(dataRowHeight);
        r.setArcHeight(dataRowHeight/2);
        r.setArcWidth(dataRowHeight/2);
        r.setFill( (row%2==0)
                ?(Paint) settings.get("tableRow1BGPaint")
                :(Paint) settings.get("tableRow2BGPaint") );
        r.setLayoutX(0); r.setLayoutY(row*dataRowHeight);
        r.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                currState = "detailscreen";
                getDetailScreen(c);
                root.getChildren().remove(mainScreen);
                root.getChildren().add(detailScreen);
                updateWindowGraphics();
            }
        });
        return r;
    }
    
    private void getDetailScreen(CLActivity c) {
        detailScreen = new Group();
        String document = "<html><head><style>"
                + "h1.organization{font:18pt bold;font-family:sans-serif;}";
        document += "span.contact-name{font:14pt italic;font-family:sans-serif;}";
        document += "span.label{font:10pt;font-family:sans-serif;";
        document += "span.contact-phone{font:11pt font-family:sans-serif;}";
        document += "span.contact-email{font:11pt; font-family: monospace;}";
        document += "p.default{font:10pt;}table.noborders{border:0px;}";
        document += "td.spaced{padding:10px;height:25px;}";
        document += "</style></head>";
        document += "<body><h1 class='organization'>"+c.getDesc()+"</h1>";
        document += "<table class='noborders'>";
        document += "<tr><td class='spaced'><span class='label'>Contact Name: </span></td>"
                + "<td class='spaced'><span class='contact-name'>"+c.getContact().getName()+"</span></td></tr>";
        document += "<tr><td class='spaced'><span class='label'>Email: </span></td>"
                + "<td class='spaced'><span class='contact-email'>"+c.getContact().getEmail()+"</span></td></tr>";
        document += "<tr><td class='spaced'><span class='label'>Phone: </span></td>"
                + "<td class='spaced'><span class='contact-phone'>"+c.getContact().getPhone()+"</span></td></tr>";
        document += "</table>";
        document += "<p class='default'>Date: "+format.format(c.getDate().getTime())+"</p>";
        document += "<p class='default'>Hours: "+c.getHours()+"</p>";
        document += c.getDetails()+"</body></html>";
        System.out.println(document);
        javafx.scene.web.WebView view = new javafx.scene.web.WebView();
        view.setMaxSize((Double) settings.get("stageWidth")-20, (Double) settings.get("stageHeight")-35);
        view.setLayoutX(10);
        view.setLayoutY(25);
        javafx.scene.web.WebEngine eng = view.getEngine();
        eng.loadContent(document);
        detailScreen.getChildren().add(view);
        
        Group backButton = new Group();
        Rectangle backButtonBg = new Rectangle(6, 30);
        backButtonBg.setStroke((Paint) settings.get("detailscreenButtonOutlinePaint"));
        backButtonBg.setFill((Paint) settings.get("detailscreenButtonBGPaint"));
        backButton.getChildren().add(backButtonBg);
        Polygon homeButtonFg = new Polygon();
        homeButtonFg.getPoints().addAll(0.0,2.0,3.0,0.0,3.0,4.0);
        homeButtonFg.setFill((Paint) settings.get("detailscreenButtonFGPaint"));
        homeButtonFg.setLayoutX(1.5); homeButtonFg.setLayoutY(13);
        backButton.getChildren().add(homeButtonFg);
        backButton.setLayoutX(2.5);
        backButton.setLayoutY(((Double) settings.get("stageHeight") - 35)/2-15);
        backButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                root.getChildren().remove(detailScreen);
                root.getChildren().add(mainScreen);
                currState = "mainscreen";
                updateWindowGraphics();
            }
        });
        detailScreen.getChildren().add(backButton);
    }
    
    private void writeToFile (File f) {
        try(java.io.PrintWriter p = new java.io.PrintWriter(f)) {
            for(Year y : years) {
                for(String d: y.getAllDescs()) {
                    for(CLActivity j : y.getCLActivities(d)) {
                        String line = "~Activity~\n";
                        line += "desc="+d+"\n";
                        line += "date="+format.format(j.getDate().getTime())+"\n";
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
                } else if(s.equals("~~Details~~") && parsingAct) {
                    parsingDetails = true;
                } else if(parsingDetails) {
                    if(!s.equals("~~/Details~~")) {
                        details += s + "\n";
                    } else {
                        parsingDetails = false;
                        a.setDetails(details);
                        complete += 1<<6;
                    }
                } else if(parsingAct && s.equals("~/Activity~")) {
                    if(complete == (1<<7) - 1) {
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
