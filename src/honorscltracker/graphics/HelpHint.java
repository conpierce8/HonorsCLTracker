package honorscltracker.graphics;

import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 *
 * @author Connor
 */
public class HelpHint extends Group {
    private double width, height;
    
    public HelpHint(Paint fg, Paint bg, Paint outline, String text) {
        Text t = new Text(text);
        t.setTextOrigin(VPos.BOTTOM);
        width = t.getBoundsInParent().getWidth() + 10;
        height = t.getBoundsInParent().getHeight() + 10;
        Rectangle r = new Rectangle(0,0,width, height);
        r.setFill(bg);
        r.setStroke(outline);
        this.getChildren().add(r);
        t.setFill(fg);
        t.setStroke(fg);
        t.setLayoutX(5);
        t.setLayoutY(height-5);
        this.getChildren().add(t);
        this.setMouseTransparent(true);
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }
    
}
