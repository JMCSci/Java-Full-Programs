/* Chat Application - Client side 
 * Program uses multithreading to handle server broadcasts of user messages
 */

package chatapp2;

import java.net.MulticastSocket;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.util.Scanner;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.UnknownHostException;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;

public class Client {
	static String serverIP = "";
	static String myIP = "";
	static byte [] packet = new byte [100]; 	
	static byte [] clientMessage = new byte [100];
//	static byte [] userName = new byte [25];
	
	public static void main(String[] args) throws Exception, InvocationTargetException {
		Scanner sc = new Scanner(System.in); 
		String message = "";
		String name = "";
		boolean keepGoing = true;
		// Packet to be sent (contains all information)
		// Empty byte array because this datagram packet is simply a request to the server for information. 
		// All the server needs to know to send a response--the address and port number to which reply
		// This is automatically part of the packet.
		
		System.out.print("Enter IP Address: ");
		serverIP = sc.nextLine();
		
		// Connect to server
		@SuppressWarnings("resource")
		Socket socket = new Socket(serverIP, 5000);
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());

		myIP = socket.getLocalSocketAddress().toString();
		out.writeUTF(myIP);
		
		System.out.print("Enter your username: ");
		name = sc.nextLine();
		out.writeUTF(name);
		
		// Separate thread for receiving messages from server
		Runnable runnable = new ClientRequest(serverIP);
		Thread thread = new Thread(runnable);
		thread.start();
		
		

		System.out.print("Write your message: ");
		while(keepGoing) {
			message = "";
			message = sc.nextLine();
			message = name + ": " + message;
			// Convert to bytes for transmission
			clientMessage = message.getBytes();
			out.write(clientMessage);
			out.flush();
	
		}

	}
	
}

// Create a separate thread for server requests
class ClientRequest implements Runnable {
	byte[] packet = new byte [120]; 
	String serverIP = null;
	
	ClientRequest(String serverIP) throws Exception, SocketException, UnknownHostException{
		this.serverIP = serverIP;
	}
	
	public void run() {	
			try {
				while(true) {
					MulticastSocket socket = new MulticastSocket(4446);
					InetAddress group = InetAddress.getByName("230.0.0.0");
					socket.joinGroup(group);
					
					// Package packet
					DatagramPacket clientPacket = new DatagramPacket(packet, packet.length);

					// Receive packet from server
					socket.receive(clientPacket);
					// Convert byte array into a String
					String serverText = new String(clientPacket.getData());
					// Display contents
					System.out.println(serverText);
					/* Close socket if user quits */
					clear();
					socket.close();
				
				}

			} catch(SocketException ex) {
			
			} catch(UnknownHostException ex) {
				ex.printStackTrace();
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		
	}
	
	public void clear() {
		for(int i = 0; i < packet.length; i++) {
			packet[i] = 0;
		}
	}
	
	
	
}
