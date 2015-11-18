package chatter.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by jorgelima on 9/30/15.
 */
public class Server {

    private Selector selector = null;
    public ArrayList<SocketChannel> clients;
    private ServerSocketChannel serverSocket = null;
    private SocketChannel socket = null;
    private int port = 1818;
    private HashMap<SocketChannel, String> clientsNames;

    public Server(){
        System.out.println("Server online");
        clients = new ArrayList<>();
        clientsNames = new HashMap<>();
    }
    public Server(int port){
        this.port = port;
    }

    public void initializeServer() throws IOException {
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.configureBlocking(false);
        InetAddress ia = InetAddress.getByName("152.66.175.216"); // Currently running on localhost, change later
//        InetAddress ia = InetAddress.getLocalHost();
        InetSocketAddress isa = new InetSocketAddress(ia, port);
        serverSocket.socket().bind(isa);
        System.out.println("Initialized Server on " + ia.getHostName());
    }

    public String getIpAddress() throws IOException {
        URL myIP = new URL("https://api.ipify.org?format=text");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(myIP.openStream())
        );
        return in.readLine();
    }

    public void startServer() throws IOException{
        initializeServer();
        SelectionKey acceptKey = serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        Boolean broadcast = false;
        Boolean newClient = false;
        String rec = null;

        while (acceptKey.selector().select() > 0) {

            Set readyKeys = selector.selectedKeys();
            Iterator it = readyKeys.iterator();

            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();
                it.remove();
                try {
                    if (key.isAcceptable()) {
                        System.out.println("Acceptable");
                        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                        socket = ssc.accept();
                        socket.configureBlocking(false);
                        SelectionKey another = socket.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        clients.add(socket);

                    }
                    if (key.isReadable() & key.isValid()) {
                        String received = readMessage(key);

                        rec = received;

                        if (received.toString() == "noconn") {
                            System.out.println("Client disconnected: " + clientsNames.get(key.channel()));
                            broadcastMessage(clients, "Admin: User " + clientsNames.get(key.channel()) + " has disconnected.");
                            clientsNames.remove(key.channel());
                        } else if(received.toString().split("\\s+")[0].equals("joining")) {
                            clientsNames.put((SocketChannel) key.channel(), received.toString().split("\\s+")[1]);
                            broadcastMessage(clients, prepareAllUsers(clientsNames, clients));

                        } else
                        {
                            System.out.println("received: " + received.toString());
                            broadcast = true;
                        }

                    }
                    if (key.isWritable() & key.isValid()) {
                        //System.out.println("Writable");

                        if (broadcast) {

                            String client = clientsNames.get(key.channel());
                            String toSend = client + ": " + rec;
                            broadcastMessage(clients, toSend);

                            broadcast = false;
                        }

                    }
                } catch(CancelledKeyException e) {
                    key.channel().close();
                }
            }

        }
    }

    public String prepareAllUsers(HashMap<SocketChannel, String> socketToName, ArrayList<SocketChannel> sockets){

        String users = "users ";

        for (SocketChannel cli : sockets) {
            users = users + socketToName.get(cli) + " ";
        }
        return users;
    }

    public void broadcastMessage(ArrayList<SocketChannel> sockets, String message){

        for (SocketChannel cli : sockets) {

            try {
                System.out.println("Writing to clients address: " + cli.getRemoteAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }

            writeMessage(cli, message);
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


        if (ret.equals("quit") || ret.equals("shutdown")) {
            return;
        }
        try {

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

        } catch (ClosedChannelException cce) {
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String readMessage(SelectionKey key){
        int nBytes = 0;
        socket = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        String result = null;

        try{
            nBytes = socket.read(buffer);
            if(nBytes == -1){
                key.cancel();
                clients.remove(socket);
                return "noconn";
            }
            buffer.flip();
            Charset charset = Charset.forName("UTF-8");
            CharsetDecoder decoder = charset.newDecoder();
            CharBuffer charBuffer = decoder.decode(buffer);
            result = charBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}


