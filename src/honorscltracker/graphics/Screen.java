package honorscltracker.graphics;

import java.util.HashMap;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
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
    private double dragOffsetX = 0;
    private double dragOffsetY = 0;
    
    
    public Screen(final Stage primaryStage, HashMap<String, Object> settings, String screenType) {
        switch(screenType) {
            case "main": case "detail": case "home": case "data": break;
            default: throw new IllegalArgumentException("Invalid screen type: "+screenType);
        }
        windowBGPaint = (Paint) settings.get(screenType+"screenBGPaint");
        windowBGStroke = (Paint) settings.get(screenType+"screenBGStroke");
        windowButtonBG = (Paint) settings.get(screenType+"screenWindowButtonBGPaint");
        windowButtonFG = (Paint) settings.get(screenType+"screenWindowButtonFGPaint");
        
        //create the window background
        Polygon windowBackground = new Polygon();
        windowBackground.setFill(windowBGPaint);
        windowBackground.setStroke(windowBGStroke);
        double w = (Double) settings.get("stageWidth");
        double h = (Double) settings.get("stageHeight");
        windowBackground.getPoints().addAll(0d,15d,0d,h,w,h,w,15d,w-15,0d,w-65,0d,w-80,15d);
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
        getChildren().add(dragArea);
        
    }
}
