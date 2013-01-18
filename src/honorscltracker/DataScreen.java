/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package honorscltracker;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.HTMLEditor;

/**
 *
 * @author Connor
 */
public class DataScreen extends Group {
    private CLActivity owner;
    private HashMap<String, Object> settings;
    private String labelStyle = "-fx-text-fill:white;";
    
    private Handler newActivityHandler, updateActivityHandler, mainRequestedHandler;
    
    public DataScreen(HashMap<String, Object> prefs, YearList years) {
        // By default, we're not editing anything an activity; we're creating
        // a new one (i.e. 'owner' is null)
        owner = null;
        settings = prefs;
        init(years);
    }
    
    private void init(YearList years) {
        VBox v = new VBox();
        v.setMaxWidth((Double) settings.get("stageWidth")-35);
        v.setSpacing(10);
        
        final ComboBox yearCombo = new ComboBox();
        yearCombo.setItems(javafx.collections.FXCollections.observableArrayList(years.getYearsList()));
        yearCombo.setEditable(true);
        yearCombo.setPrefHeight(25);
        yearCombo.setPrefWidth(100);
        HBox hb1 = new HBox(); 
        hb1.setAlignment(Pos.CENTER_LEFT);
        hb1.setSpacing(5);
        hb1.setFillHeight(false);
        Label l1 = new Label("Year:");
        l1.setStyle(labelStyle);
        hb1.getChildren().add(l1);
        hb1.getChildren().add(yearCombo);
        v.getChildren().add(hb1);
        
        final TextField dateField = new TextField();
        dateField.setPromptText("Enter date (M/D/Y)");
        HBox hb2 = new HBox();
        Label l2 = new Label("Date:");
        l2.setStyle(labelStyle);
        hb2.getChildren().add(l2);
        hb2.getChildren().add(dateField);
        hb2.setAlignment(Pos.CENTER_LEFT);
        hb2.setSpacing(5);
        v.getChildren().add(hb2);
        
        final TextField contactNameField = new TextField();
        contactNameField.setPromptText("Enter contact name:");
        final TextField contactEmailField = new TextField();
        contactEmailField.setPromptText("Contact email:");
        final TextField contactPhoneField = new TextField();
        contactPhoneField.setPromptText("Contact phone:");
        HBox hb3 = new HBox();
        Label l3 = new Label("Contact:");
        l3.setStyle(labelStyle);
        hb3.getChildren().add(l3);
        VBox contact = new VBox();
        contact.setSpacing(3);
        contact.getChildren().add(contactNameField);
        contact.getChildren().add(contactEmailField);
        contact.getChildren().add(contactPhoneField);
        hb3.getChildren().add(contact);
        hb3.setAlignment(Pos.CENTER_LEFT);
        hb3.setSpacing(5);
        v.getChildren().add(hb3);
        
        final TextField shortDescField = new TextField();
        shortDescField.setPromptText("Organization, e.g.");
        HBox hb4 = new HBox();
        Label l4 = new Label("Enter a short description:");
        l4.setStyle(labelStyle);
        hb4.getChildren().add(l4);
        hb4.getChildren().add(shortDescField);
        hb4.setAlignment(Pos.CENTER_LEFT);
        hb4.setSpacing(5);
        v.getChildren().add(hb4);
        
        final TextField hoursField = new TextField();
        hoursField.setPromptText("Hours");
        HBox hb5 = new HBox();
        Label l5 = new Label("Hours:");
        l5.setStyle(labelStyle);
        hb5.getChildren().add(l5);
        hb5.getChildren().add(hoursField);
        hb5.setAlignment(Pos.CENTER_LEFT);
        hb5.setSpacing(5);
        v.getChildren().add(hb5);
        
        final HTMLEditor detailsField = new HTMLEditor();
        detailsField.setPrefHeight(200);
        v.getChildren().add(detailsField);
        
        Button addActivity = new Button("Add Comp Learning Event");
        addActivity.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                GregorianCalendar d = new GregorianCalendar();
                String contactName = contactNameField.getText();
                String contactEmail = contactEmailField.getText();
                String contactPhone = contactPhoneField.getText();
                String year = yearCombo.getValue().toString();
                String shortDesc = shortDescField.getText();
                String details = detailsField.getHtmlText();
                details = details.substring(details.indexOf("<body"));
                details = details.substring(details.indexOf('>')+1, details.indexOf("</body"));
                double hours = 0;
                int startYr = -1;
                try {
                    hours = Double.parseDouble(hoursField.getText());
                } catch(NumberFormatException ex) {
                    hours = 0;
                }
                try{
                    startYr = Integer.parseInt(year.substring(0, 4));
                } catch(NumberFormatException ex) {
                    startYr = -1;
                }
                CLActivity j = (owner == null)? new CLActivity() : owner;
                Contact c = new Contact();
                c.setEmail(contactEmail);
                c.setName(contactName);
                c.setPhone(contactPhone);
                j.setContact(c);
                j.setDate(d);
                j.setDesc(shortDesc);
                j.setDetails(details);
                j.setHours(hours);
                j.setStartYr(startYr);
                if(startYr > 0) {
                    if(owner == null) {
                        newActivityHandler.action(j);
                    } else {
                        updateActivityHandler.action(j);
                    }
                }
            }
        });
        v.getChildren().add(addActivity);
        
        ScrollPane scrollpane = new ScrollPane();
        scrollpane.setStyle("-fx-background-color: rgb(0,0,0,0);");
        scrollpane.setPrefSize((Double) settings.get("stageWidth")-20,(Double) settings.get("stageHeight")-40);
        scrollpane.setContent(v);
        
        Group backButton = new Group();
        Rectangle r = new Rectangle(0,0,30,7);
        r.setFill((Paint) settings.get("datascreenButtonBGPaint"));
        r.setStroke((Paint) settings.get("datascreenButtonOutlinePaint"));
        backButton.getChildren().add(r);
        Polygon p = new Polygon();
        p.getPoints().addAll(15.0, 2.0, 13.0, 5.0, 17.0, 5.0);
        p.setFill((Paint) settings.get("datascreenButtonFGPaint"));
        backButton.getChildren().add(p);
        backButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                owner = null;
                mainRequestedHandler.action(null);
            }
        });
        backButton.setLayoutX((Double) settings.get("stageWidth") - 145);
        backButton.setLayoutY((Double) settings.get("stageHeight") - 34);
        
        getChildren().add(scrollpane);
        getChildren().add(backButton);
        //TODO: data screen
    }
    
    public void setNewActivityHandler(Handler h) {
        newActivityHandler = h;
    }
    
    public void setUpdateActivityHandler(Handler h) {
        updateActivityHandler = h;
    }
    
    public void setMainScreenRequestHandler(Handler h) {
        mainRequestedHandler = h;
    }
    
}
