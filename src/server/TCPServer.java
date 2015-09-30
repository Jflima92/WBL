package server;

import java.io.*;
import java.net.*;

class TCPServer {

	public static void main(String argv[]) throws Exception {
		if (argv.length != 1) {
			System.out.println("USAGE: java TCPServer <port> ");
			System.exit(-1);
		}

		// Initialize server
		int port = Integer.parseInt(argv[0]);
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("Running server on port " + port);

		// Initialize clientRequest string
		String clientRequest = "";

		while (!clientRequest.equals("QUIT")) {
			Socket connectionSocket = serverSocket.accept();
			BufferedReader inFromClient = new BufferedReader(
					new InputStreamReader(connectionSocket.getInputStream()));
			clientRequest = inFromClient.readLine();
			System.out.println("Received: " + clientRequest);

		}
		serverSocket.close();
	}

}
