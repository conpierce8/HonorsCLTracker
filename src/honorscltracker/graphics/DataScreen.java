/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package honorscltracker.graphics;

import honorscltracker.CLActivity;
import honorscltracker.Contact;
import honorscltracker.Handler;
import honorscltracker.Main;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TreeSet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

/**
 *
 * @author Connor
 */
public class DataScreen extends Screen {
    private CLActivity owner;
    private HashMap<String, Object> settings;
    private String labelStyle = "-fx-text-fill:white;-fx-font-weight:bold;";
    
    private Handler newActivityHandler, updateActivityHandler, mainRequestedHandler;
    
    private ComboBox yearCombo;
    private TextField dateField, contactNameField, contactEmailField,
            contactPhoneField, shortDescField, hoursField;
    private Button addActivity;
    private Text message;
    private HTMLEditor detailsField;
    private final ScrollPane scrollpane;
    
    /**
     * Creates a new DataScreen class, which is a GUI for entering complementary
     * learning activities into this program.
     * @param prefs the GUI preferences
     * @param years years for which the user's has previous comp learning 
     * activities.  Used for initializing 'year' combo box. Should be obtained
     * from <code>honorscltracker.YearList.getYearList()</code>
     */
    public DataScreen(Stage primaryStage, HashMap<String, Object> prefs, TreeSet<String> years) {
        super(primaryStage, prefs, "data");
        this.scrollpane = new ScrollPane();
        // By default, we're not editing anything an activity; we're creating
        // a new one (i.e. 'owner' is null)
        owner = null;
        settings = prefs;
        init(years);
    }
    
    /*
     * Initializes and lays out the data input elements.
     */
    private void init(TreeSet<String> years) {
        Group content = new Group();
        
        VBox v = new VBox();
        v.setMaxWidth((Double) settings.get("stageWidth")-35);
        v.setSpacing(10);
        v.setPadding(new Insets(0,10,0,10));
        
        yearCombo = new ComboBox();
        yearCombo.setItems(javafx.collections.FXCollections.observableArrayList(years));
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
        
        dateField = new TextField();
        dateField.setPromptText("Enter date (M/D/Y)");
        HBox hb2 = new HBox();
        Label l2 = new Label("Date:");
        l2.setStyle(labelStyle);
        hb2.getChildren().add(l2);
        hb2.getChildren().add(dateField);
        hb2.setAlignment(Pos.CENTER_LEFT);
        hb2.setSpacing(5);
        v.getChildren().add(hb2);
        
        contactNameField = new TextField();
        contactNameField.setPromptText("Enter contact name:");
        contactEmailField = new TextField();
        contactEmailField.setPromptText("Contact email:");
        contactPhoneField = new TextField();
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
        
        shortDescField = new TextField();
        shortDescField.setPromptText("Organization, e.g.");
        HBox hb4 = new HBox();
        Label l4 = new Label("Enter a short description:");
        l4.setStyle(labelStyle);
        hb4.getChildren().add(l4);
        hb4.getChildren().add(shortDescField);
        hb4.setAlignment(Pos.CENTER_LEFT);
        hb4.setSpacing(5);
        v.getChildren().add(hb4);
        
        hoursField = new TextField();
        hoursField.setPromptText("Hours");
        HBox hb5 = new HBox();
        Label l5 = new Label("Hours:");
        l5.setStyle(labelStyle);
        hb5.getChildren().add(l5);
        hb5.getChildren().add(hoursField);
        hb5.setAlignment(Pos.CENTER_LEFT);
        hb5.setSpacing(5);
        v.getChildren().add(hb5);
        
        detailsField = new HTMLEditor();
        detailsField.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                t.consume();
            }
        });
        detailsField.setPrefHeight(200);
        v.getChildren().add(detailsField);
        
        message = new Text("");
        message.setFont(new javafx.scene.text.Font(16));
        v.getChildren().add(message);
        
        addActivity = new Button("Add Comp Learning Event");
        addActivity.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                buttonClicked();
            }
        });
        v.getChildren().add(addActivity);
        
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
        backButton.setLayoutY((Double) settings.get("stageHeight") - 34.5);
        
        content.getChildren().add(scrollpane);
        content.getChildren().add(backButton);
        content.setLayoutX(10);
        content.setLayoutY(25);
        
        this.getChildren().add(content);
    }
    
    /**
     * Updates the academic year combo box to display the list of all academic
     * years contained in <code>years</code>.
     * @param years the collection of academic years ("####-##") to be displayed
     * in the years combo box
     */
    public void updateYearCombo(TreeSet<String> years) {
        yearCombo.setItems(javafx.collections.FXCollections.observableArrayList(years));
    }
    
    /*
     * The function that is called when the submit button is clicked.
     */
    private void buttonClicked() {
        ArrayList<String> missingFields = new ArrayList<>();
        GregorianCalendar d = new GregorianCalendar();
        String contactName = contactNameField.getText();
        if(contactName == null) {
            contactName = "";
        }
        String contactEmail = contactEmailField.getText();
        if(contactEmail == null) {
            contactEmail = "";
        }
        String contactPhone = contactPhoneField.getText();
        if(contactPhone == null) {
            contactPhone = "";
        }
        String year = yearCombo.getValue().toString();
        if(year == null) {
            missingFields.add("year");
        }
        String shortDesc = shortDescField.getText();
        if(shortDesc == null) {
            shortDesc = "";
        }
        String details = detailsField.getHtmlText();
        details = details.substring(details.indexOf("<body"));
        details = details.substring(details.indexOf('>')+1, details.indexOf("</body"));
        double hours;
        int startYr;
        try {
            hours = Double.parseDouble(hoursField.getText());
        } catch(NumberFormatException ex) {
            hours = 0;
        }
        try{
            startYr = Integer.parseInt(year.substring(0, year.indexOf('-')));
        } catch(NumberFormatException ex) {
            startYr = -1;
            missingFields.add("academic year");
        }
        try {
            d.setTime(Main.format.parse(dateField.getText()));
        } catch(ParseException ex) {
            missingFields.add("date");
        }
        if(missingFields.size() > 0) {
            String error = "The following fields are required:";
            for(String s: missingFields) {
                error += " "+s+",";
            }
            error = error.substring(0, error.length()-1);
            message.setFill(Color.RED);
            message.setText(error);
            return;
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
        String action = (owner == null)?"created":"updated";
        message.setText("Activity "+action+" successfully");
        message.setFill(Color.GREEN);
    }
    
    /**
     * Sets the handler to be called when a new comp learning activity is
     * created.  This occurs when user clicks the "Add Comp Learning Event"
     * button, all required fields are filled, and the <code>DataScreen</code>
     * was was opened in 'new activity' mode (as opposed to 'edit activity'
     * mode).  The handler's <code>action(Object)</code> method will be called
     * and the newly created activity will be passed to the <code>data</code>
     * parameter.
     * @param h The handler to be called when a new activity is created
     */
    public void setNewActivityHandler(Handler h) {
        newActivityHandler = h;
    }
    
    /**
     * Sets the handler to be called when a comp learning activity is edited.
     * This occurs when user clicks the "Add Comp Learning Event"
     * button, all required fields are filled, and the <code>DataScreen</code>
     * was was opened in 'edit activity' mode.  The handler's 
     * <code>action(Object)</code> method will be called and the edited
     * activity will be passed to the <code>data</code> parameter.  The
     * <code>CLActivity</code> that is passed is a separate object from the
     * original.
     * @param h The handler to be called when the activity is updated
     */
    public void setUpdateActivityHandler(Handler h) {
        updateActivityHandler = h;
    }
    
    /**
     * Set the <code>Handler</code> to be called when the user clicks the
     * 'return to main screen' button.  The handler's <code>action</code>
     * method is called and <code>null</code> is passed as the <code>data</code>
     * parameter.
     * @param h the <code>Handler</code> to be called when the 'return to main
     * screen' button is clicked
     */
    public void setMainScreenRequestHandler(Handler h) {
        mainRequestedHandler = h;
    }
    
    /**
     * Sets the <code>CLActivity</code> that is to be the 'owner' of this
     * data screen.  When the owner is set, the input elements on the data
     * screen are updated to reflect the data in the <code>CLActivity</codee>.
     * When the owner is set, any changes to the data are considered edits to 
     * the owning <code>CLActivity</code>, and the <code>Handler</code> set by
     * <code>setUpdateActivityHandler</code> will be called when the user clicks
     * the submit button.
     * 
     * To clear the owner of this data screen, pass <code>null</code> to this
     * function.  The owning <code>CLActivity</code> will be cleared, as well as
     * the content of the data input fields.  When the user clicks the submit
     * button, a new <code>CLActivity</code> will be created, and the
     * <code>Handler</code> set by the <code>setNewActivityHandler</code> will
     * be called.
     * @param owner the <code>CLActivity<code> that is to become the 'owner' of\
     * this data screen, or <code>null</code> to clear the owner
     * @see setUpdateActivtyHandler
     * @see setNewActivityHandler
     */
    public void setOwner(CLActivity owner) {
        if(owner != null) {
            yearCombo.setValue(owner.getYearString());
            dateField.setText(Main.format.format(owner.getDate().getTime()));
            contactNameField.setText(owner.getContact().getName());
            contactEmailField.setText(owner.getContact().getEmail());
            contactPhoneField.setText(owner.getContact().getPhone());
            shortDescField.setText(owner.getDesc());
            hoursField.setText(owner.getHours()+"");
            detailsField.setHtmlText("<html><head></head><body>"+owner.getDetails()+
                    "</body></html>");
            addActivity.setText("Update Comp Learning Activity");
        } else {
            yearCombo.setValue("");
            dateField.setText("");
            contactNameField.setText("");
            contactEmailField.setText("");
            contactPhoneField.setText("");
            shortDescField.setText("");
            hoursField.setText("");
            detailsField.setHtmlText("<html><head></head><body></body></html>");
            addActivity.setText("Add Comp Learning Activity");
        }
        message.setText("");
        scrollpane.setVvalue(scrollpane.getVmin());
        this.owner = owner;
    }
    
}
