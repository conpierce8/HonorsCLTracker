package honorscltracker.graphics;

import honorscltracker.Handler;
import java.util.HashMap;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Abstract class that provides a base for all the different screens that 
 * HonorsCLTracker displays.  Includes the features common to all screens,
 * namely the background <code>Polygon</code> and window buttons.
 * @author Connor
 */
public class Screen extends Group {
    private final Paint windowBGPaint;
    private final Paint windowBGStroke;
    private final Paint windowButtonBG;
    private final Paint windowButtonFG;
    private final Paint helpButtonEnabledBG;
    private final Circle helpButtonBG;
    protected boolean helpEnabled = false;
    private double dragOffsetX = 0;
    private double dragOffsetY = 0;
    private boolean dragging;
    private Handler toggleHelpEnabledHandler;
    
    
    public Screen(final Stage primaryStage, HashMap<String, Object> settings, String screenType) {
        switch(screenType) {
            case "main": case "detail": case "home": case "data": break;
            default: throw new IllegalArgumentException("Invalid screen type: "+screenType);
        }
        windowBGPaint = (Paint) settings.get(screenType+"screenBGPaint");
        windowBGStroke = (Paint) settings.get(screenType+"screenBGStroke");
        windowButtonBG = (Paint) settings.get(screenType+"screenWindowButtonBGPaint");
        windowButtonFG = (Paint) settings.get(screenType+"screenWindowButtonFGPaint");
        helpButtonEnabledBG = (Paint) settings.get(screenType+"screenHelpButtonEnabledBGPaint");
        //create the window background
        Polygon windowBackground = new Polygon();
        windowBackground.setFill(windowBGPaint);
        windowBackground.setStroke(windowBGStroke);
        double w = (Double) settings.get("stageWidth");
        double h = (Double) settings.get("stageHeight");
        windowBackground.getPoints().addAll(0d,15d,0d,h,w,h,w,15d,w-15,0d,w-80,0d,w-95,15d);
        windowBackground.setStrokeWidth(1.5);
        getChildren().add(windowBackground);
        
        //create close button
        Group closeButton = new Group();
        Rectangle box = new Rectangle(11,11);
        box.setFill(windowButtonBG);
        closeButton.getChildren().add(box);
        Line slash1 = new Line(1,1,10,10);
        slash1.setStroke(windowButtonFG);
        closeButton.getChildren().add(slash1);
        Line slash2 = new Line(1,10,10,1);
        slash2.setStroke(windowButtonFG);
        closeButton.getChildren().add(slash2);
        closeButton.setLayoutX(w-30);
        closeButton.setLayoutY(5);
        final HelpHint hhCloseButton = new HelpHint(windowBGStroke, windowBGPaint, windowBGStroke, "Click to close.");
        hhCloseButton.setLayoutX(w-15-hhCloseButton.getBoundsInParent().getWidth());
        hhCloseButton.setLayoutY(20);
        closeButton.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if(helpEnabled)
                    getChildren().add(hhCloseButton);
            }
        });
        closeButton.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if(helpEnabled)
                    getChildren().remove(hhCloseButton);
            }
        });
        closeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                primaryStage.close();
            }
        });
        getChildren().add(closeButton);
        
        //create minimize button
        Group minimizeButton = new Group();
        Rectangle box2 = new Rectangle(11,11);
        box2.setFill(windowButtonBG);
        minimizeButton.getChildren().add(box2);
        Line line = new Line(0,10,10,10);
        line.setStroke(windowButtonFG);
        minimizeButton.getChildren().add(line);
        minimizeButton.setLayoutX(w-45);
        minimizeButton.setLayoutY(5);
        final HelpHint hhMinButton = new HelpHint(windowBGStroke, windowBGPaint, windowBGStroke, "Click to minimize.");
        hhMinButton.setLayoutX(w-30-hhMinButton.getBoundsInParent().getWidth());
        hhMinButton.setLayoutY(20);
        minimizeButton.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if(helpEnabled)
                    getChildren().add(hhMinButton);
            }
        });
        minimizeButton.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if(helpEnabled)
                    getChildren().remove(hhMinButton);
            }
        });
        minimizeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                primaryStage.setIconified(true);
            }
        });
        getChildren().add(minimizeButton);
        
        //create drag button
        Group dragArea = new Group();
        Rectangle box3 = new Rectangle(12,12);
        box3.setFill(windowButtonBG);
        dragArea.getChildren().add(box3);
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 6 - (i%2); j++) {
                Rectangle dot = new Rectangle(1,1);
                dot.setX(j*2+(i%2));
                dot.setY(i*2);
                dot.setFill(windowButtonFG);
                dragArea.getChildren().add(dot);
            }
        }
        final HelpHint hhDrag = new HelpHint(windowBGStroke, windowBGPaint, windowBGStroke, "Click and drag to move the window.");
        hhDrag.setLayoutX(w-45-hhDrag.getBoundsInParent().getWidth());
        hhDrag.setLayoutY(20);
        dragArea.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if(helpEnabled && !dragging)
                    getChildren().add(hhDrag);
            }
        });
        dragArea.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if(helpEnabled && !dragging)
                    getChildren().remove(hhDrag);
            }
        });
        dragArea.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                dragOffsetX = arg0.getScreenX() - primaryStage.getX();
                dragOffsetY = arg0.getScreenY() - primaryStage.getY();
                dragging = true;
            }
        });
        dragArea.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                primaryStage.setX(arg0.getScreenX() - dragOffsetX);
                primaryStage.setY(arg0.getScreenY() - dragOffsetY);
            }
        });
        dragArea.setOnMouseReleased(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                dragging = false;
            }
        });
        //TODO: tooltips for all buttons, tables, etc. -- display and hide
        dragArea.setLayoutX(w - 61);
        dragArea.setLayoutY(4);
        getChildren().add(dragArea);
        
        Group helpButton = new Group();
        helpButtonBG = new javafx.scene.shape.Circle(6);
        helpButtonBG.setFill(windowButtonBG);
        helpButtonBG.setStroke(windowButtonFG);
        helpButtonBG.setLayoutX(6);
        helpButtonBG.setLayoutY(6);
        helpButton.getChildren().add(helpButtonBG);
        Text helpButtonFG = new Text("?");
        helpButtonFG.setFill(windowButtonFG);
        helpButtonFG.setStroke(windowButtonFG);
        helpButtonFG.setLayoutX(4);
        helpButtonFG.setLayoutY(9);
        helpButtonFG.setFont(new javafx.scene.text.Font(10));
        helpButton.getChildren().add(helpButtonFG);
        helpButton.setLayoutX(w-79);
        helpButton.setLayoutY(4);
        final HelpHint hhHelpButton = new HelpHint(windowBGStroke, windowBGPaint, windowBGStroke, "Help hints enabled. Click to disable.");
        hhHelpButton.setLayoutX(w-60-hhHelpButton.getBoundsInParent().getWidth());
        hhHelpButton.setLayoutY(20);
        helpButton.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if(helpEnabled)
                    getChildren().add(hhHelpButton);
            }
        });
        helpButton.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                getChildren().remove(hhHelpButton);
            }
        });
        helpButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if(toggleHelpEnabledHandler != null) {
                    toggleHelpEnabledHandler.action(null);
                }
                if(helpEnabled) {
                    getChildren().add(hhHelpButton);
                } else {
                    getChildren().remove(hhHelpButton);
                }
            }
        });
        getChildren().add(helpButton);
    }
    
    /**
     * Sets whether this screen should display help hints or not.
     * @param helpEnabled whether this screen should display help hints
     */
    public void setHelpEnabled(boolean helpEnabled) {
        this.helpEnabled = helpEnabled;
        helpButtonBG.setFill(this.helpEnabled ? helpButtonEnabledBG : windowButtonBG);
    }
    
    /**
     * Sets the handler to be called when the user clicks the help button, to
     * toggle help hints on/off. <code>null</code> is passed as a parameter to
     * the handler's <code>action</code> method when the <code>action</code>
     * method is called.
     * @param h the handler to be called when the user clicks the help button
     */
    public void setToggleHelpEnabledRequestHandler(Handler h) {
        toggleHelpEnabledHandler = h;
    }
}
