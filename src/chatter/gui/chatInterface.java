package chatter.gui; /**
 * Created by jorgelima on 9/30/15.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class chatInterface extends Application {

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Chatter");
        this.primaryStage.setResizable(false);
        this.primaryStage.sizeToScene();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("chatInterface.fxml"));
        Parent root = loader.load();
        chatController controller = loader.getController();

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
