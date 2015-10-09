package chatter.gui; /**
 * Created by jorgelima on 9/30/15.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class chatInterface extends Application {

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Chatter");
        Parent root = FXMLLoader.load(getClass().getResource("chatInterface.fxml"));
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()  //
        {
            public void handle(WindowEvent e){              //TODO fix issue of disconnecting socket on window close
                System.out.println("test");
                try {
                    Platform.exit();
                    System.exit(0);
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });


    }
}
