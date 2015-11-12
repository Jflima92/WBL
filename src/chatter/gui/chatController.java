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
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.RunnableFuture;

import chatter.client.Client;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

    @FXML
    private VBox onlineMembers;


    private int port = 1818;
    private Client client;
    public boolean active = true;
    private String userName = null;


    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert sendMessageButton != null : "fx:id=\"sendMessageButton\" was not injected: check your FXML file 'chatInterface'.";


        client = new Client(port, "192.168.1.229");

        client.connectToServer();
        System.out.println(port);
        userName = getRandomUserName();
        String join = "joining " + userName;
        client.writeMessage(join);
        addNewUser(userName, true);
        receiveMessage();

        // initialize your logic here: all @FXML variables will have been injected

        textBox.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                String msg = textBox.getText();
                System.out.println(msg);
                client.writeMessage(msg);
                textBox.clear();
            }
        });

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

    public void addNewUser(String name, Boolean own){
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                Text t;
                t = new Text(name);
                if(own)
                    t.setFill(Color.RED);
                else
                    t.setFill(Color.BLACK);
                onlineMembers.getChildren().add(t);
            }
        });
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
                            System.out.printf("tamanho: " + result.split("\\s+").length);
                            if (result.split("\\s+")[0].equals("users")) {
                                for(int i = 1; i < result.split("\\s+").length; i++) {
                                    String name = result.split("\\s+")[i];
                                    if (!name.equals(userName))
                                        addNewUser(name, false);
                                }
                            }

                            else{

                                Date date = new Date();
                                SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
                                String prep = "[" + df.format(date) + "] " + result;

                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Text t;
                                        t = new Text(prep);
                                        if(result.split("\\s+")[0].equals(userName+":"))
                                            t.setFill(Color.RED);
                                        else
                                            t.setFill(Color.WHITE);
                                        genVBox.getChildren().add(t);
                                    }
                                });
                            }
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
        textBox.clear();
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

    public String getRandomUserName(){
        String s;
        s = "Anon";
        String ID = UUID.randomUUID().toString().replaceAll("[\\s\\-()]", "");
        return s+ID.substring(3, 10);
    }

}
