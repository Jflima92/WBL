package chatter.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by jorgelima on 9/30/15.
 */
public class Client {

    private int port;
    private String address;
    public SocketChannel clientSocket;
    private InetSocketAddress isa;

    public Client(int port, String address){
        this.address = address;
        this.port = port;

    }

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


    public void shutdown(){

        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}


