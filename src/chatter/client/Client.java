package chatter.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by jorgelima on 9/30/15.
 */
public class Client {

    private int port;
    private String address;
    private SocketChannel clientSocket;
    private InetSocketAddress isa;


    public Client(int port, String address){
        this.address = address;
        this.port = port;
    }

    public void connectToServer(){

        try{
            clientSocket = SocketChannel.open();
            isa = new InetSocketAddress(address,port);
            clientSocket.connect(isa);
            clientSocket.configureBlocking(false);
            System.out.println("Connected to the Server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
