package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Date;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.SocketException;

public class Server {
	static String userName = null;
	static byte [] userConnected = new byte [256];
	
	public static void main(String[] args) throws Exception {
		Date date = new Date();
		System.out.println("Server started at: " + date);
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(5000);
	
		while(true) {
			Socket socket = serverSocket.accept();
			DataInputStream in = new DataInputStream(socket.getInputStream());
			System.out.println(in.readUTF() + " has connected!");
			userName = in.readUTF();
			String connect = userName + " has connected!";
			userConnected = connect.getBytes();
			Runnable runnable = new MultiCastServerThread(socket, userName, userConnected);
			Thread thread = new Thread(runnable);
			thread.start();
		}
		
	}
	
}

class MultiCastServerThread implements Runnable {
	Socket clientSocket = null;
	byte [] clientMessage = new byte [256];
	String userName = null;
	String userLeft = null;

	
	MultiCastServerThread(Socket socket, String userName, byte [] userConnected) throws Exception, IOException, SocketException {
		this.clientSocket = socket;
		this.userName = userName;
		this.userLeft = userName + " has left the session";
		this.clientMessage = userConnected;
	}
	
	public void run() {
		while(true) {
			try {
				DataInputStream in = new DataInputStream(clientSocket.getInputStream());
				DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
				
				// Convert byte array to a String
				// Create a new packet for transmission to clients and a new group
				// In IPv4, any address between 224.0.0.0 to 239.255.255.255 can be used as a multicast address.
				InetAddress group = InetAddress.getByName("230.0.0.0");
				// Transmit using multisocket at this port number -- NOT NECESSARRY -- WILL TRY LATER
				MulticastSocket socket = new MulticastSocket(4446);
		 
				// Package information for transmission
				DatagramPacket packet = new DatagramPacket(clientMessage, clientMessage.length, 
						group, 4446);
				
				out.write(clientMessage);	// Packages new connection made
				socket.send(packet);		// Broadcasts it
				clear();					// Clears byte array for next transmission
				
				// Receive message as bytes 
				// Input into byte array
				in.read(clientMessage);
				// Send to users
				socket.send(packet);
				// Attempts to output to connected user 
				// If user has left the session, SocketException is thrown
				out.write(clientMessage);	
				clear();
			} catch(UnknownHostException ex) {
				ex.printStackTrace();
			} catch(SocketException ex) {
				try {
					clientMessage = userLeft.getBytes();
					InetAddress group = InetAddress.getByName("230.0.0.0");
					MulticastSocket socket = new MulticastSocket(4446);
					DatagramPacket packet = new DatagramPacket(clientMessage, clientMessage.length, 
							group, 4446);
	//			System.out.println(userName + " has left the session");
					socket.send(packet);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
