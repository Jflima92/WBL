package chatter.gui;

import com.sun.javafx.tk.PlatformImage;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.RunnableFuture;

import chatter.client.Client;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;


/**
 * Created by jorgelima on 9/30/15.
 */
public class chatController implements Initializable {

    @FXML
    private Stage mainStage;
    @FXML //  fx:id="myButton"
    private Button sendMessageButton; // Value injected by FXMLLoader
    @FXML
    private TextField textBox;

    @FXML
    private VBox genVBox;

    @FXML
    private Tab genTab;


    private int port = 1818;
    private Client client;
    public boolean active = true;


    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert sendMessageButton != null : "fx:id=\"sendMessageButton\" was not injected: check your FXML file 'chatInterface'.";

        client = new Client(port, "127.0.1.1");
        client.connectToServer();
        System.out.println(port);
        receiveMessage();

        // initialize your logic here: all @FXML variables will have been injected
        sendMessageButton.setOnAction(this::handleButtonAction);

    }

    public void setStage(Stage stage)
    {
        this.mainStage = stage;
    }

    public void disconnect() {
        this.active = false;
        client.shutdown();
    }

    public void receiveMessage(){
        //receiving = new receivingThread(client.clientSocket);
        //Platform.runLater(receiving);
        Task rec = new Task<Void>() {
            @Override
            public Void call() throws IOException {
                int nBytes = 0;


                ByteBuffer buffer = ByteBuffer.allocate(2048);
                try {
                    while (active) {
                        while ((nBytes = client.clientSocket.read(buffer)) > 0) {
                            buffer.flip();
                            Charset charset = Charset.forName("us-ascii");
                            CharsetDecoder decoder = charset.newDecoder();
                            CharBuffer charBuffer = decoder.decode(buffer);
                            String result = charBuffer.toString();
                            System.out.println("recebi aqui:" + result);
                            Date date = new Date();
                            SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
                            String prep = df.format(date) + ": " + result;

                            Platform.runLater(new Runnable(){
                                @Override
                                public void run() {
                                    genVBox.getChildren().add(new Text(prep));
                                }
                            });
                            
                            buffer.flip();
                        }
                        buffer.clear();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        Thread th = new Thread(rec);
        th.setDaemon(true);

        th.start();
    }

    private void handleButtonAction(ActionEvent event) {
        // Button was clicked, do something...
        System.out.println("Button Action\n");
        String msg = textBox.getText();
        System.out.println(msg);
        client.writeMessage(msg);
    }



    public class receivingThread implements Runnable {
        public SocketChannel socket =  null;
        public boolean active = true;

        public receivingThread(SocketChannel sc){

            this.socket = sc;
        }

        public void run(){
            int nBytes = 0;

            ByteBuffer buffer = ByteBuffer.allocate(2048);
            try{
                while(active){
                    while ((nBytes  = client.clientSocket.read(buffer)) > 0){
                        buffer.flip();
                        Charset charset = Charset.forName("us-ascii");
                        CharsetDecoder decoder = charset.newDecoder();
                        CharBuffer charBuffer = decoder.decode(buffer);
                        String result = charBuffer.toString();
                        System.out.println("fodasse");
                        System.out.println("recebi aqui:" + result);


                        buffer.flip();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void printToPane(String msg, String user){
        Text t = new Text();
        t.setFont(new Font(20));
        t.setWrappingWidth(200);
        t.setTextAlignment(TextAlignment.JUSTIFY);
        t.setText(msg);
    }

}
