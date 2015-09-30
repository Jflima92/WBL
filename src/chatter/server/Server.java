package chatter.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
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
}
