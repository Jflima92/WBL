package chatter.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by jorgelima on 9/30/15.
 */
public class Client {

    // Server port
    private int port;

    //Server Address
    private String address;

    //SocketChannel used for the connection
    public SocketChannel clientSocket;

    // Variable that will contain the information needed for connection
    private InetSocketAddress isa;


    // Client Constructor
    public Client(int port, String address){
        this.address = address;
        this.port = port;

    }

    // Function that connects the client to the given Server
    public void connectToServer(){
        int result = 0;

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

    // Function that sends a given message to the Server
    public void writeMessage(String msg){

        ByteBuffer bytebuf = ByteBuffer.allocate(1024);
        int nBytes = 0;

        try{
            bytebuf = ByteBuffer.wrap(msg.getBytes());
            nBytes = clientSocket.write(bytebuf);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Function that shuts down and disconnects the Client from the Server
    public void shutdown(){

        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}


