package honorscltracker.graphics;

import honorscltracker.Handler;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Connor
 */
public class HomeScreen extends Screen {
    private Handler newFileHandler;
    private Handler openFileHandler;
    private VBox content;
    private final FileChooser fileChooser;
    private TextField f;
    
    public HomeScreen(Stage primaryStage, HashMap<String, Object> settings) {
        super(primaryStage, settings, "home");
        fileChooser = new FileChooser();
        init(primaryStage, settings);
    }
    
    private void init(final Stage primaryStage, HashMap<String, Object> settings) {
        content = new VBox();
        content.setSpacing(20);
        content.setAlignment(Pos.CENTER);
        Text t = new Text("Welcome to the Honors Comp Learning tracker!\n"
                + "If you have an existing file, click Open File to open the"
                + " file\n If you have not used Comp Learning Tracker before, "
                + "click New.\n\nFor help, click the ? button in the upper "
                + "right of the window.");
        t.setTextAlignment(TextAlignment.CENTER);
        t.setFont((Font) settings.get("homescreenTextFont"));
        t.setFill((Paint) settings.get("homescreenTextPaint"));
        content.getChildren().add(t);
        
        Button chooseButton = new Button("Open File");
        chooseButton.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                File temp = fileChooser.showOpenDialog(primaryStage);
                if(temp != null) {
                    openFileHandler.action(temp.getAbsolutePath());
                }
            }
        });
        
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(20);
        Button b = new Button("Create new file");
        b.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                newFileHandler.action(null);
            }
        });
        buttonBox.getChildren().add(b);
        buttonBox.getChildren().add(chooseButton);
        content.getChildren().add(buttonBox);
        
        content.setPrefSize((Double) settings.get("stageWidth") - 20, (Double) settings.get("stageHeight") - 35);
        content.setLayoutX(10);
        content.setLayoutY(25);
        
        this.getChildren().add(content);
    }
    
    public void setOnNewFileRequestHandler(Handler h) {
        newFileHandler = h;
    }
    
    public void setOnOpenFileRequestHandler(Handler h) {
        openFileHandler = h;
    }
    
    public void fileError(String errMsg) {
        f.setText("Could not open file; "+errMsg);
        f.setAlignment(Pos.CENTER);
    }
    
}
