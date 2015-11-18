package chatter.gui;

import chatter.client.Client;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.SimpleDateFormat;
import java.util.*;


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
    private ScrollPane tabScroll;
    @FXML
    private VBox onlineMembers;

    @FXML
    private MenuItem changeNickname;

    private TextInputDialog dialog = new TextInputDialog("itsamemario");
    private int port = 1818;
    private Client client;
    public boolean active = true;
    private String userName = null;
    private ArrayList<String> userList;


    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert sendMessageButton != null : "fx:id=\"sendMessageButton\" was not injected: check your FXML file 'chatInterface'.";


//        client = new Client(port, "152.66.175.216");
        try {
            client = new Client(port, InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        client.connectToServer();
        userName = getRandomUserName();
        userList = new ArrayList<>();
        String join = "joining " + userName;
        client.writeMessage(join);
        userList.add(userName);
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

        genTab.setOnSelectionChanged(arg0 -> tabScroll.setVvalue(1.0));

        sendMessageButton.setOnAction(this::handleButtonAction);

        changeNickname.setOnAction(this::handleChangeNickname);

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
        Platform.runLater(() -> {
            Text t;
            t = new Text(name);
            if(own)
                t.setFill(Color.RED);
            else
                t.setFill(Color.BLACK);
            onlineMembers.getChildren().add(t);
        });
    }

    public void removeUser(String name){

        System.out.println(name);
        userList.remove(name);

        Platform.runLater(() -> {
            onlineMembers.getChildren().remove(name);
            updateUserList();
        });
    }

    public void updateUserList(){
        onlineMembers.getChildren().clear();
        addNewUser(userName, true);
        for(int i = 0; i < userList.size(); i++){
            String name1 = userList.get(i);
            if(!userName.equals(userList.get(i)))
                addNewUser(name1, false);
        }
    }

    public void addTextToTab(String text, Boolean admin){
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
        String prep = "[" + df.format(date) + "] " + text;

        Platform.runLater(() -> {
            Text t;
            t = new Text(prep);
            if(text.split("\\s+")[0].equals(userName+":"))
                t.setFill(Color.RED);
            else if(admin)
                t.setFill(Color.DARKORANGE);
            else
                t.setFill(Color.WHITE);
            genVBox.getChildren().add(t);
            tabScroll.setVvalue(1.0);
        });
    }

    public void receiveMessage(){
        Task rec = new Task<Void>() {
            @Override
            public Void call() throws IOException {
                int nBytes = 0;


                ByteBuffer buffer = ByteBuffer.allocate(2048);
                try {
                    while (active) {
                        while ((nBytes = client.clientSocket.read(buffer)) > 0) {
                            buffer.flip();
                            Charset charset = Charset.forName("UTF-8");
                            CharsetDecoder decoder = charset.newDecoder();
                            CharBuffer charBuffer = decoder.decode(buffer);
                            String result = charBuffer.toString();
                            System.out.println("recebi aqui:" + result);

                            if (result.split("\\s+")[0].equals("users")) {
                                for(int i = 1; i < result.split("\\s+").length; i++) {
                                    String name = result.split("\\s+")[i];
                                    if(!userList.contains(name))
                                        if (!name.equals(userName)) {
                                            userList.add(name);
                                            addNewUser(name, false);
                                            addTextToTab("Admin: User " + name + " joined the room.", true);
                                        }
                                }
                            }
                            else if(result.split("\\s+")[0].equals("Admin:") && result.split("\\s+")[4].equals("disconnected.")){

                                addTextToTab(result, true);
                                removeUser(result.split("\\s+")[2]);
                            }
                            else if(result.split("\\s+")[0].equals("Admin:") && result.split("\\s+")[3].equals("changed")){

                                addTextToTab(result, true);

                                String previous = result.split("\\s+")[2];

                                if (previous.equals(userName)) { //TODO with errors
                                    userName = result.split("\\s+")[7];
                                    removeUser(previous);
                                    System.out.println("userlist: " + userList);
                                }

                                else {
                                    userList.add(result.split("\\s+")[7]);
                                    removeUser(previous);

                                }
                            }

                            else{
                                addTextToTab(result, false);
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
        String msg = textBox.getText();
        System.out.println(msg);
        client.writeMessage(msg);
        textBox.clear();
    }

    private void handleChangeNickname(ActionEvent event) {

        final String[] newNick = {null};

        dialog.setTitle("Chatter");
        dialog.setHeaderText("Change your Nickname");
        dialog.setContentText("Please enter your new nick:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> newNick[0] = name);
        System.out.println(newNick[0]);
        String join = "changing " + newNick[0];
        client.writeMessage(join);

    }

    public String getRandomUserName(){
        String s;
        s = "Anon";
        String ID = UUID.randomUUID().toString().replaceAll("[\\s\\-()]", "");
        return s+ID.substring(3, 10);
    }

}
