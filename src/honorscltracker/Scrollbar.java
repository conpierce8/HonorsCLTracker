/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package honorscltracker;

import java.util.HashMap;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Connor
 */
public class Scrollbar extends Group {
    private double availableSpace, topPad, bottomPad, minClipY, scrollStartY;
    private Node n;
    private final HashMap<String, Object> settings;
    
    public Scrollbar(final double availableSpace, final double topPad, 
            double bottomPad, final Node n, final double minClipY,
            HashMap<String, Object> settings) {
        this.availableSpace = availableSpace;
        this.topPad = topPad;
        this.bottomPad = bottomPad;
        this.n = n;
        this.minClipY = minClipY;
        this.settings = settings;
        
        init();
    }
    
    private void init() {    
        final double scrollbarWidth = (Double) settings.get("scrollbarWidth");
        final double height = n.getBoundsInLocal().getHeight() + topPad + bottomPad;
        double scrollSpace = availableSpace - scrollbarWidth;
        final double barHeight = Math.max(scrollbarWidth, scrollSpace*Math.min(1, availableSpace/height));
        
        Polygon scrollBarBG = new Polygon();
        scrollBarBG.getPoints().addAll(0.0,scrollbarWidth/2, 0.0, scrollSpace-scrollbarWidth/2,
                scrollbarWidth/2, scrollSpace, scrollbarWidth, scrollSpace-scrollbarWidth/2,
                scrollbarWidth, scrollbarWidth/2, scrollbarWidth/2, 0.0);
        scrollBarBG.setFill((Paint) settings.get("scrollbarBGPaint"));
        scrollBarBG.relocate(0, scrollbarWidth/2);
        getChildren().add(scrollBarBG);
        
        Polygon topBox = new Polygon();
        topBox.getPoints().addAll(0.0,0.0,0.0,scrollbarWidth-2, scrollbarWidth/2-1, scrollbarWidth/2-1,
                scrollbarWidth-2, scrollbarWidth-2, scrollbarWidth-2,0.0);
        topBox.setFill((Paint) settings.get("scrollbarFGPaint"));
        topBox.setStroke((Paint) settings.get("scrollbarFGStroke"));
        topBox.relocate(0 ,0);
        getChildren().add(topBox);
        
        Polygon bottomBox = new Polygon();
        bottomBox.getPoints().addAll(0.0,0.0,0.0,scrollbarWidth-2, scrollbarWidth-2, scrollbarWidth-2,
                scrollbarWidth-2,0.0, scrollbarWidth/2-1, scrollbarWidth/2-1);
        bottomBox.setFill((Paint) settings.get("scrollbarFGPaint"));
        bottomBox.setStroke((Paint) settings.get("scrollbarFGStroke"));
        bottomBox.relocate(0, availableSpace-scrollbarWidth-1);
        getChildren().add(bottomBox);
        
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
                scrollStartY = arg0.getSceneY() - bar.getLayoutY();
            }
        });
        
        final Rectangle clip = new Rectangle(0, 0, n.getBoundsInLocal().getWidth(), availableSpace-topPad-bottomPad);
        n.setClip(clip);
        bar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                double minY = scrollbarWidth/2;
                double maxY = availableSpace-scrollbarWidth/2;
                double newY = Math.max(minY, Math.min(arg0.getSceneY() - scrollStartY, maxY-barHeight));
                bar.setLayoutY(newY);
                double newClipY = ((newY-minY)/(maxY-minY))*height;
                clip.setLayoutY(newClipY);
                n.setLayoutY(minClipY+topPad-newClipY);
            }
        });
        getChildren().add(bar);
    }
    
    public void setNode(Node n) {
        this.n = n;
        getChildren().clear();
        init();
    }
    
}
