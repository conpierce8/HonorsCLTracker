package honorscltracker.graphics;

import honorscltracker.CLActivity;
import honorscltracker.Handler;
import honorscltracker.Main;
import honorscltracker.Year;
import java.util.HashMap;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Defines the main screen.  This is the screen that the user sees immediately
 * after opening or creating a file.  It contains a table displaying a single
 * year's worth of comp learning activity, and includes buttons to move between
 * screens, and to move between years.
 * @author Connor Pierce
 */
public class MainScreen extends Group {
    private Text title; //displays the year currently in view
    private Year data; //data for the year currently in view
    private HashMap<String, Object> settings; //GUI settings
    private Group prevButton; //button to move to previous year
    private Group nextButton; //button to move to next year
    private Group homeButton; //button to show the homescreen
    private Group inputButton; //button to show the datascreen
    private Group saveButton; //button to save changes to file
    private Group table; //table displaying comp learning activity
    private Handler homescreenRequest; //handler called when homeButton is
                                       //clicked
    private Handler datascreenRequest;
    private Handler detailRequest; //handler called when user left-clicks on
                                   //table, wants to see details about a comp
                                   //learning activity
    private Handler prevYearRequest; //handler called when user clicks
                                     //prevButton
    private Handler nextYearRequest; //handler called when user clicks
                                     //nextButton
    private Handler saveRequest; //handler called when user clicks save button
    private Handler editRequest; //handler called when user right-clicks on
                                 //table, wants to edit a comp learning activity
    private double tableY; //y coordinate of the top of the table
    
    /**
     * Creates a new <code>MainScreen</code> and initializes GUI components.
     * Table and buttons are styled according to <code>settings</code>, and the
     * contains data stored in <code>data</code>.
     * @param settings GUI settings to style this screen with
     * @param data comp learning activities for one academic year
     */
    public MainScreen(HashMap<String, Object> settings, Year data) {
        this.settings = settings;
        this.data = data;
        table = new Group();
        title = new Text(this.data.getYearString());
        System.out.println("creating mainscreen, title text: "+title.getText());
        title.setFont((Font) this.settings.get("mainscreenLabelFont"));
        title.setFill((Paint) settings.get("mainscreenLabelPaint"));
        init();
    }
    
    /**
     * Changes the year for which this screen is displaying data.  Title and
     * table are updated to display the given <code>Year</code>.
     * @param data the new <code>Year</code> for which to display data
     */
    public void update(Year data) {
        this.data = data;
        update();
    }
    
    /**
     * Sets the handler to be called when the user requests to view the
     * homescreen (clicks the homeScreen button).
     * @param h the handler to be called when the users requests to view the
     * homescreen
     */
    public void setHomeScreenRequestHandler(Handler h) {
        homescreenRequest = h;
    }
    
    /**
     * Sets the handler to be called when the user requests to view the
     * datascreen (clicks the dataScreen button).
     * @param h the handler to be called when the users requests to view the
     * datascreen
     */
    public void setDataScreenRequestHandler(Handler h) {
        datascreenRequest = h;
    }
    
    /**
     * Sets the handler to be called when the user requests to edit a comp
     * learning activity (right clicks on a comp learning activity in the
     * table). When this handler is called, the activity to be edited is passed
     * to the <code>action</code> method.
     * @param h the handler to be called when the users requests to edit a comp
     * learning activity
     */
    public void setEditCLActivityRequestHandler(Handler h) {
        editRequest = h;
    }
    
    /**
     * Sets the handler to be called when the user requests to view details
     * about a comp learning activity (right clicks on a comp learning activity 
     * in the table). When this handler is called, the activity to be edited is 
     * passed to the <code>action</code> method.
     * @param h the handler to be called when the users requests to view details
     * about a comp learning activity
     */
    public void setDetailRequestHandler(Handler h) {
        detailRequest = h;
    }
    
    /**
     * Sets the handler to be called when the user requests to view the previous
     * year (clicks on the left arrow button).  The <code>Year</code> currently
     * in view is passed to the <code>action</code> method.
     * @param h the handler to be called when the user requests to view the
     * previous year
     */
    public void setPrevYearRequestHandler(Handler h) {
        prevYearRequest = h;
    }
    
    /**
     * Sets the handler to be called when the user requests to view the next
     * year (clicks on the right arrow button). The <code>Year</code> currently
     * in view is passed to the <code>action</code> method.
     * @param h the handler to be called when the user requests to view the next
     * year
     */
    public void setNextYearRequestHandler(Handler h) {
        nextYearRequest = h;
    }
    
    /**
     * Sets the handler to be called when the user clicks the save button.
     * <code>null</code> is passed to the <code>action</code> method.
     * @param h the handler to be called when the user clicks the save button
     */
    public void setSaveRequestHandler(Handler h) {
        saveRequest = h;
    }
    
    /*
     * Finds the dimenions of the title Text object, and lays it out to be
     * centered horizontally and aligned 10 pixels above the table.
     */
    private void layoutTitle() {
        double x = ((Double) settings.get("stageWidth")-title.getBoundsInParent().getWidth())/2;
        title.setLayoutX(x);
        tableY = title.getBoundsInLocal().getHeight();
        double y = tableY - 10;
        title.setLayoutY(y);
        System.out.println("title layout: "+x+","+y);
    }
    
    /*
     * Initializes and lays out the components of the main screen: title, table,
     * and buttons.
     */
    private void init() {
        layoutTitle();
        this.getChildren().add(title);
        
        getTable();
        
        prevButton = new Group();
        Rectangle prevButtonBG = new Rectangle(50,tableY-10);
        prevButtonBG.setArcHeight(5);
        prevButtonBG.setArcWidth(5);
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
                prevYearRequest.action(data);
            }
        });
        prevButton.setLayoutX(10); prevButton.setLayoutY(0);
        this.getChildren().add(prevButton);
        
        nextButton = new Group();
        Rectangle nextButtonBG = new Rectangle(50,tableY-10);
        nextButtonBG.setArcHeight(5);
        nextButtonBG.setArcWidth(5);
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
                nextYearRequest.action(data);
            }
        });
        nextButton.setLayoutX((Double) settings.get("stageWidth")-60);
        nextButton.setLayoutY(0); 
        this.getChildren().add(nextButton);
        
        inputButton = new Group();
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
                datascreenRequest.action(null);
            }
        });
        this.getChildren().add(inputButton);
        
        saveButton = new Group();
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
                saveRequest.action(null);
            }
        });
        this.getChildren().add(saveButton);
        
        homeButton = new Group();
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
                homescreenRequest.action(null);
            }
        });
        this.getChildren().add(homeButton);
        
        table.setLayoutY(tableY);
        table.setLayoutX(10);
        this.getChildren().add(table);
    }
    
    private void getTable() {
        final Object[][] tableData = new Object[4][data.getSize()+1];
        int row = 0;
        Group dataRows = new Group();
        Text asdf = new Text("T");
        asdf.setFont((Font) settings.get("tableDataTextFont"));
        double rowHeight = asdf.getBoundsInParent().getHeight() + 4;
        double totalHours = 0;
        for(String s : data.getAllDescs()) {
            int count = 0;
            for(CLActivity c : data.getCLActivities(s)) {
                if(count == 0) {
                    tableData[0][row] = s;
                }
                tableData[1][row] = Main.format.format(c.getDate().getTime());
                tableData[2][row] = c.getContact().getName();
                tableData[3][row] = c.getHours();
                totalHours += c.getHours();
                dataRows.getChildren().add(getTableRowRect(row,rowHeight, c));
                count ++;
                row ++;
            }
        }
        dataRows.getChildren().add(getTableRowRect(row, rowHeight, null));
        tableData[2][tableData[2].length-1] = "TOTAL";
        tableData[3][tableData[3].length-1] = totalHours;
        
        final double stageWidth = (Double) settings.get("stageWidth");
        
        Group c2 = getColumn(tableData[1], rowHeight, 180);
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
        table.getChildren().add(headerBG);
        
        Group c3 = getColumn(tableData[2], rowHeight, 180);
        Text c3Head = new Text("Contact");
        c3Head.setTextOrigin(VPos.CENTER); 
        c3Head.setFont((Font) settings.get("tableHeaderTextFont"));
        c3Head.setFill((Paint) settings.get("tableHeaderTextPaint"));
        double c3Width = Math.max(c3.getBoundsInParent().getWidth(),
                c3Head.getBoundsInParent().getWidth())+10;
        
        Group c4 = getColumn(tableData[3], rowHeight, 180);
        Text c4Head = new Text("Hours");
        c4Head.setTextOrigin(VPos.CENTER); 
        c4Head.setFont((Font) settings.get("tableHeaderTextFont"));
        c4Head.setFill((Paint) settings.get("tableHeaderTextPaint"));
        double c4Width = Math.max(c4.getBoundsInParent().getWidth(),
                c4Head.getBoundsInParent().getWidth())+10;
        
        Group c1 = getColumn(tableData[0], rowHeight, stageWidth-20-c2Width-c3Width-c4Width);
        Text c1Head = new Text("Desc");
        c1Head.setFont((Font) settings.get("tableHeaderTextFont"));
        c1Head.setFill((Paint) settings.get("tableHeaderTextPaint"));
        double c1Width = Math.max(c1.getBoundsInParent().getWidth(),
                c1Head.getBoundsInParent().getWidth())+10;
        c1Head.setTextOrigin(VPos.CENTER);
        
        if(row > 0) {
            c1Head.setLayoutX(10);
            c1Head.setLayoutY(headerRowHeight / 2);
            table.getChildren().add(c1Head);
            c2Head.setLayoutX(c1Width+10);
            c2Head.setLayoutY(headerRowHeight / 2);
            table.getChildren().add(c2Head);
            c3Head.setLayoutX(c1Width+c2Width+10);
            c3Head.setLayoutY(headerRowHeight / 2);
            table.getChildren().add(c3Head);
            c4Head.setLayoutX(c1Width+c2Width+c3Width+10);
            c4Head.setLayoutY(headerRowHeight / 2);
            table.getChildren().add(c4Head);
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
        table.getChildren().add(dataRows);
        
        final double availableSpace = ((Double) settings.get("stageHeight")) - tableY - headerRowHeight - 35;
        Group scrollBar = new Scrollbar(availableSpace, 5, 0, dataRows,headerRowHeight, settings);
        scrollBar.setLayoutX(stageWidth-20-((Double)settings.get("scrollbarWidth")));
        scrollBar.setLayoutY(headerRowHeight);
        table.getChildren().add(scrollBar);
        //TODO: table scroll bar
    }
    
    /*
     * Lays out one column of text in the table, given the text to be displayed
     * there as an Object[], the height of each row, and the maximum allowed
     * width for the column.  Text will be truncated and an ellipsis ... added
     * to it.
     */
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
    
    /*
     * Creates the background rectangle for the row-th row of the table.  This
     * row is formatted according to the settings contained in the GUI
     * settings.
     */
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
        if(c != null) {
            r.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    if(event.getButton() == MouseButton.PRIMARY) {
                        detailRequest.action(c);
                    } else if(event.getButton() == MouseButton.SECONDARY) {
                        editRequest.action(c);
                    }
                }
            });
        }
        return r;
    }

    /**
     * Updates the main screen - title and table are re-initialized.  Should be
     * called after <code>CLActivity</code>s have been added or modified and the
     * table needs to be updated to reflect those changes.
     */
    public void update() {
//        System.out.println("setting data to "+data.getYearString());
        title.setText(this.data.getYearString());
        layoutTitle();
        table.getChildren().clear();
        getTable();
    }
    
}
