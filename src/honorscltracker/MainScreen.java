/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package honorscltracker;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Connor
 */
public class MainScreen extends Group {
    private Text title;
    private Year data;
    private HashMap<String, Object> settings;
    private Group prevButton, nextButton, homeButton, inputButton, saveButton;
    private Group table;
    private Handler homescreenRequest, datascreenRequest, detailRequest,
            prevYearRequest, nextYearRequest, saveRequest;
    private double tableY, scrollStartY;
    
    public MainScreen(HashMap<String, Object> settings, String title) {
        this.settings = settings;
        this.title = new Text(title);
        this.title.setFont((Font) this.settings.get("mainscreenLabelFont"));
        init();
    }
    
    public void setTitle(String title) {
        this.title.setText(title);
    }
    
    public void updateTable(Year data) {
        
    }
    
    public void addHomeScreenRequestHandler(Handler h) {
        homescreenRequest = h;
    }
    
    public void addDataScreenRequestHandler(Handler h) {
        datascreenRequest = h;
    }
    
    public void addDetailRequestHandler(Handler h) {
        detailRequest = h;
    }
    
    public void addPrevYearRequestHandler(Handler h) {
        
    }
    
    private void init() {
        title.setLayoutX(((Double) settings.get("stageWidth")-title.getBoundsInParent().getWidth())/2);
        tableY = title.getBoundsInParent().getHeight();
        title.setLayoutY(tableY - 10);
        this.getChildren().add(title);
        
        getTable();
        
        prevButton = new Group();
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
                prevYearRequest.action(null);
            }
        });
        prevButton.setLayoutX(10); prevButton.setLayoutY(0);
        this.getChildren().add(prevButton);
        
        nextButton = new Group();
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
                nextYearRequest.action(null);
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
        
        table.setLayoutY(tableY); table.setLayoutX(10);
        this.getChildren().add(table);
    }
    
    private void getTable() {
        table = new Group();
        
        final Object[][] tableData = new Object[4][data.getSize()];
        int row = 0;
        Group dataRows = new Group();
        Text asdf = new Text("T");
        asdf.setFont((Font) settings.get("tableDataTextFont"));
        double rowHeight = asdf.getBoundsInParent().getHeight() + 4;
        for(String s : data.getAllDescs()) {
            int count = 0;
            for(CLActivity c : data.getCLActivities(s)) {
                if(count == 0) {
                    tableData[0][row] = s;
                }
                tableData[1][row] = Main.format.format(c.getDate().getTime());
                tableData[2][row] = c.getContact().getName();
                tableData[3][row] = c.getHours();
                dataRows.getChildren().add(getTableRowRect(row,rowHeight, c));
                count ++;
                row ++;
            }
        }
        
        final double stageWidth = (Double) settings.get("stageWidth");
        
        Group c2 = getColumn(tableData[1], rowHeight, -1);
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
        
        Group c3 = getColumn(tableData[2], rowHeight, -1);
        Text c3Head = new Text("Contact");
        c3Head.setTextOrigin(VPos.CENTER); 
        c3Head.setFont((Font) settings.get("tableHeaderTextFont"));
        c3Head.setFill((Paint) settings.get("tableHeaderTextPaint"));
        double c3Width = Math.max(c3.getBoundsInParent().getWidth(),
                c3Head.getBoundsInParent().getWidth())+10;
        
        Group c4 = getColumn(tableData[3], rowHeight, -1);
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
        Group scrollBar = getScrollbar(availableSpace, 5, 0, dataRows,headerRowHeight);
        scrollBar.setLayoutX(stageWidth-20-((Double)settings.get("scrollbarWidth")));
        scrollBar.setLayoutY(headerRowHeight);
        table.getChildren().add(scrollBar);
        //TODO: table scroll bar
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
                detailRequest.action(c);
            }
        });
        return r;
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
    
}
