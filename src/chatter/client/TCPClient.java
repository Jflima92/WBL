package chatter.client;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

class TCPClient {

	// Main Function
	public static void main(String argv[]) throws Exception {

		if (argv.length < 2) {
			System.out.println("USAGE:  java chatter.client.TCPClient <port> msg <message>");
			System.out.println("		java chatter.client.TCPClient <port> img <img_url>");
			System.exit(-1);
		}

		else {

			// get the port to send info to
			int port = Integer.parseInt(argv[0]);
			String action = argv[1];

			// Define the socket
			Socket clientSocket = new Socket("localhost", port);

			if (action.equals("msg")) {
				// Convert String message to bits
				DataOutputStream outToServer = new DataOutputStream(
						clientSocket.getOutputStream());
				// Write message to socket
				String message = "";
				for (int i = 2; i < argv.length; i++) {
					message += " " + argv[i];
				}
				outToServer.writeBytes(message);
			}
			else if(action.equals("img")) {
				
				// Get image's location
				String location = argv[2];
				
				// Get image
		        BufferedImage image = ImageIO.read(new File(location));
		        
		        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		        ImageIO.write(image, "jpg", byteArrayOutputStream);//TODO
		        
		        OutputStream outputStream = clientSocket.getOutputStream();

		        // Get image's size
		        byte[] imgSize = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
		        
		        outputStream.write(imgSize);
		        outputStream.write(byteArrayOutputStream.toByteArray());
		        outputStream.flush();
		        System.out.println("Flushed: " + System.currentTimeMillis());

		        Thread.sleep(120000);
		        System.out.println("Closing: " + System.currentTimeMillis());

			}

			// End connection
			clientSocket.close();
		}

	}

}
