/* ChatApp class for Chat Application
 * Starts GUI
 */

package chatapp;

import javafx.scene.layout.GridPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.Scanner;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.UnknownHostException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

public class Client extends Application { 
	static String serverIP = "";
	static String myIP = "";
	static byte [] packet = new byte [256]; 	
	static byte [] clientMessage = new byte [256];
	static String message = "";
	static boolean ready = false;
	static String name = "";
	static boolean connected = false;
	static String textBroadcast = "";
	static Queue<String> queue = new ArrayDeque<String>();
	static Socket sock = null;
	static DataOutputStream dataout = null;			
	static DataInputStream datain = null;
	static String text = "";
	boolean running = false;
	static ClientRequest request = null;
	static Runnable runnable1 = null;
	static Thread thread1 = null;
	static Message msg = new Message();

	
	public static void main(String[] args) throws Exception, InvocationTargetException {
		Application.launch(args);
	
	}
	
//Start GUI
	public void start(Stage primaryStage) throws Exception {
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);
		TextField tf2 = new TextField();
		TextField tf3 = new TextField();
		Label lb1 = new Label("IP Address");
		Label lb2 = new Label("Username");
		Button connect = new Button("Connect");
		
		pane.add(lb1, 0, 0);
		pane.add(tf2, 1, 0);
		pane.add(lb2, 0, 1);
		pane.add(tf3, 1, 1);
		pane.add(connect, 1, 2);
		
		BorderPane bpane = new BorderPane();
		
		TextArea textarea = new TextArea();
		textarea.setEditable(false);
		textarea.setPrefWidth(616.999);
		textarea.setPrefHeight(400);
		textarea.setWrapText(true);

		HBox hbox1 = new HBox(10);
		hbox1.setAlignment(Pos.BASELINE_CENTER);
		BorderPane.setMargin(hbox1, new Insets(20,30,20,0));
		
		Label label1 = new Label("Type here: ");
		TextField tf = new TextField();
		tf.setPrefWidth(300);
		Button send = new Button("SEND");
		
		hbox1.getChildren().addAll(label1, tf, send);
		bpane.setCenter(textarea);
		bpane.setBottom(hbox1);
		bpane.setStyle("-fx-background-color:gray;");
		
		byte [] textMsg = new byte [256];
		
		Stage stage2 = new Stage();
		Scene scene2 = new Scene(pane, 250, 100, Color.BLACK);
		stage2.setScene(scene2);
		stage2.setTitle("Connect to Server");
		stage2.setResizable(true);
		stage2.setX(550);
		stage2.setY(80);
		stage2.show();
		
		// Show Chat window
		Scene scene1 = new Scene(bpane, 635, 300, Color.BLACK);
		primaryStage.setScene(scene1);
		primaryStage.setTitle("Chat Application");
		primaryStage.setResizable(false);
		
		/* Second stage */
		
		// EVENT -- Connect to server  
		connect.setOnAction(e ->{
			try {
				serverIP = tf2.getText();
				name = tf3.getText();
				if(!serverIP.isEmpty() && !name.isEmpty())
					textarea.setText("");
				Socket socket = new Socket(serverIP, 5000);
				socket.setSoTimeout(10000);
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				sock = socket;
				dataout = out;
				myIP = socket.getLocalSocketAddress().toString();	
				
				dataout.writeUTF(myIP);
				
				dataout.writeUTF(name);	
				
				DataInputStream in = new DataInputStream(socket.getInputStream());
				datain = in;
				
				// check socket connection
				socketConnect(sock);
				
				if(connected == true) { 
					stage2.hide();				// Close Server window
					primaryStage.show();		// Show Chat window
				}
			
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		});
		
		ClientRequest clientReq = new ClientRequest(serverIP, textBroadcast, textarea, sock);
		
		Runnable runnable = (Runnable)clientReq;
		Thread thread = new Thread(runnable);
		thread1 = thread;
		thread.start();

		// EVENT -- Send message
		send.setOnAction(e ->{
			message = tf.getText();
			tf.setText("");
			convertMessage();
			message = "";			// Clear it
			try {
				dataout.write(clientMessage);

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}
	
public static void connect() throws Exception {
	// Packet to be sent (contains all information)
	// Empty byte array because this datagram packet is simply a request to the server for information. 
	// All the server needs to know to send a response--the address and port number to which reply
	// This is automatically part of the packet.
	// Separate thread for receiving messages from server
}

public static void convertMessage() {
	message = name + ": " + message;
	// Convert to bytes for transmission
	clientMessage = message.getBytes();
}

public static void socketConnect(Socket socket) {
	if(socket.isConnected()) {
		connected = true;
	} else {
		connected = false;
	}
	
}

}

//Create a separate thread for server requests
class ClientRequest implements Runnable {
	byte[] packet = new byte [256]; 
	String serverIP = null;
	String textBroadcast = "";
	Message msg = null;
	byte[] text = new byte [256]; 
	DataInputStream input = null;
	DataOutputStream request = null;
	TextArea textarea = null;
	Socket sock = null;
	
	ClientRequest(String serverIP, String broadcast, TextArea textarea, Socket sock) throws Exception, SocketException, UnknownHostException {
		this.serverIP = serverIP;
		textBroadcast = broadcast;
		this.textarea = textarea;
		this.sock = sock;
	}
	
	public void run() {	
		try {
		while(true) {
				MulticastSocket socket = new MulticastSocket(4446);
				InetAddress group = InetAddress.getByName("230.0.0.0");
				socket.joinGroup(group);
				socket.setSoTimeout(30000);		// time out after 30 seconds
				// Package packet
				DatagramPacket clientPacket = new DatagramPacket(packet, packet.length);
				// Receive packet from server
				socket.receive(clientPacket);
				// Convert byte array into a String
				String serverText = new String(clientPacket.getData());
				/* WOULD IT BE MORE EFFICIENT TO USE A DATA STRUCTURE? */
				textBroadcast += serverText + "\n";
				textarea.setText(textBroadcast);
				/* Close socket if user quits */
				clear();
				socket.close();
			}

			} catch(SocketException ex) {
				ex.printStackTrace();
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
	
	public String getText() {
		return textBroadcast;
	}
	
	public String getTextMessage() {
		return msg.getMessage();
	}
	
	public void disconnect(Socket sock) throws IOException {
		sock.close();
	}
	
}



class Message {
	ArrayList<String> allMessages = new ArrayList<>();
	static String message = "";
	
	Message(String message) {
		allMessages.add(message);
	}
	
	Message() {
		
	}

	// Add all messages to list -- used in TextArea display
		public void addMessages(String messages) {
			allMessages.add(messages);
			System.out.println(allMessages.get(0));
		}
	
		public void addMessage(String text) {
			message = text;
		}
		
		public String getMessage() {
			return message;
		}
		
		public String getMessages() {
			return allMessages.get(0);
		}
	
}

