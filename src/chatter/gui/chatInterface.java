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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("chatInterface.fxml"));
        Parent root = (Parent)loader.load();
        chatController controller = (chatController)loader.getController();

        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

        controller.setStage(this.primaryStage);

        primaryStage.setOnCloseRequest(e -> {              //TODO fix issue of disconnecting socket on window close
            try {
                controller.disconnect();
                Platform.exit();
                System.exit(0);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
        });


    }
}
