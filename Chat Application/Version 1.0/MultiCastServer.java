/* Chat Application - Server Side (Multiple connections) 
 * Program handles multiple client communication
 * Broadcasts to multiple users connected to the server
 * Program uses multithreading to listen and handle multiple clients
 */

package chatapp2;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.io.DataInputStream;
import java.util.Date;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.SocketException;

public class MultiCastServer {
	static String userName = null;
//	static byte [] name = new byte [25];
	
	public static void main(String[] args) throws Exception {
		Date date = new Date();
		// Holds data to be sent
	
		byte [] packet = new byte[100];
		System.out.println("Server started at: " + date);
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(5000);
	
		while(true) {
			Socket socket = serverSocket.accept();
			DataInputStream in = new DataInputStream(socket.getInputStream());
			System.out.println(in.readUTF() + " has connected!");
			userName = in.readUTF();
			Runnable runnable = new MultiCastServerThread(socket, userName);
			Thread thread = new Thread(runnable);
			thread.start();
		}
		
	}
	
}

// Class handles broadcasting to clients
class MultiCastServerThread implements Runnable {
	Socket clientSocket = null;
	byte [] clientMessage = new byte [100];
	String userName = null;

	
	MultiCastServerThread(Socket socket, String userName) throws Exception, IOException, SocketException {
		this.clientSocket = socket;
		this.userName = userName;
	}
	
	public void run() {
		while(true) {
			try {
				DataInputStream in = new DataInputStream(clientSocket.getInputStream());
				// Receive message as bytes 
				// Input into byte array
				in.read(clientMessage);
				// Convert byte array to a String
		//		String message = new String(clientMessage);					// USED FOR TESTING / JAVAFX
				// Create a new packet for transmission to clients and a new group
				// In IPv4, any address between 224.0.0.0 to 239.255.255.255 can be used as a multicast address.
				InetAddress group = InetAddress.getByName("230.0.0.0");
				// Transmit using multisocket at this port number -- NOT NECESSARRY -- WILL TRY LATER
				MulticastSocket socket = new MulticastSocket(4446);
				// Package information for transmission
				DatagramPacket packet = new DatagramPacket(clientMessage, clientMessage.length, 
						group, 4446);
				// Send to users
				socket.send(packet);
				socket.close();
				// Clear the byte array
				clear();
				
			} catch(UnknownHostException ex) {
				ex.printStackTrace();
			} catch(SocketException ex) {
				System.out.println(userName + " has left");
				break;
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	/* clear: Clears byte array */
	public void clear() {
		for(int i = 0; i < clientMessage.length; i++) {
			clientMessage[i] = 0;
		}
	}
	
	
	
}
