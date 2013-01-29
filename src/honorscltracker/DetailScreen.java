/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package honorscltracker;

import java.util.HashMap;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;

/**
 *
 * @author Connor
 */
public class DetailScreen extends Group {
    private WebView view;
    private Handler mainscreenRequest;
    
    public DetailScreen(HashMap<String, Object> settings) {
        view = new WebView();
        view.setMaxSize((Double) settings.get("stageWidth")-20, (Double) settings.get("stageHeight")-35);
        view.setLayoutX(10);
        view.setLayoutY(25);
        getChildren().add(view);
        
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
                mainscreenRequest.action(null);
            }
        });
        getChildren().add(backButton);
    }
    
    public void setMainScreenRequestHandler(Handler h ) {
        mainscreenRequest = h;
    }
    
    public void setCLActivity(CLActivity c) {
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
        document += "<p class='default'>Date: "+Main.format.format(c.getDate().getTime())+"</p>";
        document += "<p class='default'>Hours: "+c.getHours()+"</p>";
        document += c.getDetails()+"</body></html>";
//        System.out.println(document);
        javafx.scene.web.WebEngine eng = view.getEngine();
        eng.loadContent(document);
    }
    
}
