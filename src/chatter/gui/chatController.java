package chatter.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import chatter.client.Client;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;


/**
 * Created by jorgelima on 9/30/15.
 */
public class chatController implements Initializable {


    @FXML //  fx:id="myButton"
    private Button sendMessageButton; // Value injected by FXMLLoader
    @FXML
    private TextField textBox;


    private int port = 1818;
    private Client client;


    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert sendMessageButton != null : "fx:id=\"sendMessageButton\" was not injected: check your FXML file 'chatInterface'.";


        client = new Client(port, "127.0.1.1");
        client.connectToServer();
        System.out.println(port);
        client.receiveMessage();


        /*stage.setOnCloseRequest(new EventHandler<WindowEvent>() {  //TODO close all connections on window close
            @Override
            public void handle(WindowEvent event) {
                System.out.println("Exiting");
                client.shutdown();
                Platform.exit();
            }
        });*/

        // initialize your logic here: all @FXML variables will have been injected
        sendMessageButton.setOnAction(this::handleButtonAction);

    }

    private void handleButtonAction(ActionEvent event) {
        // Button was clicked, do something...
        System.out.println("Button Action\n");
        String msg = textBox.getText();
        System.out.println(msg);
        client.writeMessage(msg);
    }

}
