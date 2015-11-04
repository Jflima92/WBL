package chatter.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Created by jorgelima on 9/30/15.
 */
public class Client {

    private int port;
    private String address;
    public SocketChannel clientSocket;
    private InetSocketAddress isa;
    public receivingThread receiving = null;


    public Client(int port, String address){
        this.address = address;
        this.port = port;

    }

    private Selector initSelector() throws IOException {
        // Create a new selector
        return SelectorProvider.provider().openSelector();
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

       /* try {
            clientSocket.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    public void writeMessage(String msg){

        ByteBuffer bytebuf = ByteBuffer.allocate(1024);
        int nBytes = 0;

        try{
            System.out.println("Message to be broadcasted is: " + msg);
            bytebuf = ByteBuffer.wrap(msg.getBytes());
            nBytes = clientSocket.write(bytebuf);
            System.out.println("Sent " + nBytes + " to server");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage(){
        receiving = new receivingThread("Receiving Thread", clientSocket);
        receiving.start();
    }

    public void shutdown(){

        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public class receivingThread extends Thread {
        public SocketChannel socket =  null;
        public boolean active = true;

        public receivingThread(String rec, SocketChannel sc){
            super(rec);
            this.socket = sc;
        }

        public void run(){
            int nBytes = 0;

            ByteBuffer buffer = ByteBuffer.allocate(2048);
            try{
                while(active){
                    while((nBytes  = clientSocket.read(buffer)) > 0){
                        buffer.flip();
                        Charset charset = Charset.forName("us-ascii");
                        CharsetDecoder decoder = charset.newDecoder();
                        CharBuffer charBuffer = decoder.decode(buffer);
                        String result = charBuffer.toString();
                        System.out.println("recebi aqui:" + result);
                        buffer.flip();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


