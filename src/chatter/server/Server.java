package chatter.server;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by jorgelima on 9/30/15.
 */
public class Server {

    private Selector selector = null;
    private ServerSocketChannel serverSocket = null;
    private SocketChannel socket = null;
    private int port = 1818;

    public Server(){
        System.out.println("Server online");
    }
    public Server(int port){
        this.port = port;
    }

    public void initializeServer() throws IOException {
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.configureBlocking(false);
        InetAddress ia = InetAddress.getLocalHost(); // Currently running on localhost, change later
        InetSocketAddress isa = new InetSocketAddress(ia, port);
        serverSocket.socket().bind(isa);
        System.out.println("Initialized Server on " + ia);
    }

    public void startServer() throws IOException{
        initializeServer();
        SelectionKey acceptKey = serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        while (acceptKey.selector().select() > 0) {

            Set readyKeys = selector.selectedKeys();
            Iterator it = readyKeys.iterator();

            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();
                it.remove();

                if (key.isAcceptable()) {
                    System.out.println("Acceptable");
                    ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                    socket = (SocketChannel) ssc.accept();
                    socket.configureBlocking(false);
                    SelectionKey another = socket.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                }
                if (key.isReadable()) {
                    System.out.println("Readable");

                }
                if (key.isWritable()) {
                    System.out.println("Writable");
                    String msg = "TESTE";
                    writeMessage(socket, msg);

                }
            }
        }
    }

    public static void main(String args[]) {
        Server server = new Server();
        try {
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    public void writeMessage(SocketChannel socket, String ret) {
        System.out.println("Inside the loop");

        if (ret.equals("quit") || ret.equals("shutdown")) {
            return;
        }
       // File file = new File(ret);
        try {

           // RandomAccessFile rdm = new RandomAccessFile(file, "r");
           // FileChannel fc = rdm.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put(ret.getBytes());
            buffer.flip();

            Charset set = Charset.forName("UTF-8");
            CharsetDecoder dec = set.newDecoder();
            CharBuffer charBuf = dec.decode(buffer);
            System.out.println(charBuf.toString());
            buffer = ByteBuffer.wrap((charBuf.toString()).getBytes());
            int nBytes = socket.write(buffer);
            System.out.println("nBytes = " + nBytes);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


