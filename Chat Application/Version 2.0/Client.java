/* ChatApp class for Chat Application
 * Client side GUI and events
 */

package chatapp;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.UnknownHostException;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.Queue;
import java.util.ArrayDeque;
import java.net.SocketTimeoutException;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.StackPane;

public class Client extends Application { 
	static String serverIP = "";
	static String myIP = "";
	static byte [] packet = new byte [256]; 	
	static byte [] clientMessage = new byte [256];
	static String message = "";
	static boolean ready = false;
	static String name = "";
	static boolean connected = false;
	static Socket sock = null;
	static DataOutputStream dataout = null;			
	static DataInputStream datain = null;
	static String text = "";
	boolean running = false;
	static ClientRequest request = null;
	static Runnable runnable1 = null;
	static Thread thread1 = null;

	
	public static void main(String[] args) throws Exception, InvocationTargetException {
		Application.launch(args);
	
	}
	
	/* start: Create GUI and events */
	public void start(Stage primaryStage) throws Exception {
		/*
		 * 
		 * CONNECT TO SERVER WINDOW
		 * 
		 */
		
		StackPane root = new StackPane();
		
		HBox hbox = new HBox(10);
		hbox.setAlignment(Pos.BASELINE_CENTER);
		
		GridPane pane = new GridPane();
		pane.setVgap(5);
		pane.setHgap(10);

		TextField tf2 = new TextField();				// IP address
		TextField tf3 = new TextField();				// User name
		tf3.setPrefWidth(250);
		Label lb1 = new Label("IP Address");
		Label lb2 = new Label("Username");
		Button connect = new Button("Connect");
		
		pane.add(lb1, 0, 0);
		pane.add(tf2, 1, 0);
		pane.add(lb2, 0, 1);
		pane.add(tf3, 1, 1);
		pane.add(connect, 1, 2);
		GridPane.setHalignment(connect, HPos.CENTER);
		GridPane.setHalignment(lb1, HPos.LEFT);
		GridPane.setHalignment(lb2, HPos.LEFT);
		GridPane.setHalignment(tf2, HPos.LEFT);
		GridPane.setHalignment(tf3, HPos.LEFT);
		pane.setPadding(new Insets(20,50,10,25));
		
		root.getChildren().addAll(hbox, pane);

		/*
		 * 
		 * CHAT APP WINDOW
		 * 
		 */
		
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
		
		// Show "Connect to Server" window
		Stage connectStage = new Stage();
		Scene scene1 = new Scene(root, 430, 135, Color.BLACK);
		connectStage.setScene(scene1);
		connectStage.setTitle("Connect to Server");
		connectStage.setResizable(true);
		connectStage.show();
		
		// Show Chat window
		Scene scene2 = new Scene(bpane, 635, 300, Color.BLACK);
		primaryStage.setScene(scene2);
		primaryStage.setTitle("Chat Application");
		primaryStage.setResizable(false);
		
		
		/* 
		 * 
		 * EVENTS
		 * 
		 */
		
		// EVENT -- Connect to server  
		// Packet to be sent (contains all information)
		// Empty byte array because this datagram packet is simply a request to the server for information. 
		// All the server needs to know to send a response--the address and port number to which reply
		// This is automatically part of the packet.
		// Separate thread for receiving messages from server
		connect.setOnAction(e -> {
			hbox.getChildren().clear();
			try {
				serverIP = tf2.getText();
				name = tf3.getText();
				if(serverIP.isBlank()|| name.isBlank()) {
					/* SET TEXT IN SERVER WINDOW -- LABEL (RED) */
					Label lb4 = new Label("Please enter a valid IP Address and User Name");
					lb4.setTextFill(Color.RED);
					hbox.getChildren().add(lb4);
					
				} else {	
					hbox.getChildren().clear();
					// Socket will stop searching after 5 seconds
					int timeout = 5000;
					Socket socket = new Socket();
					InetSocketAddress inetSocketAddress = new InetSocketAddress(serverIP, 5000);
					socket.connect(inetSocketAddress, timeout);
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				
					sock = socket;
					dataout = out;
					myIP = socket.getLocalSocketAddress().toString();	
					dataout.writeUTF(myIP);
					dataout.writeUTF(name);	
					DataInputStream in = new DataInputStream(socket.getInputStream());
					datain = in;
					// Check if client socket has connected to server
					socketConnect(sock);
					
					if(connected == true) { 
						connectStage.hide();				// Hide Server window
						primaryStage.show();				// Show Chat window
					}
					
				}
			} catch (Exception e1) {
				/* Handles NoRouteToHostException */
				hbox.getChildren().clear();
				Label lb4 = new Label("Host cannot be reached");
				lb4.setTextFill(Color.RED);
				hbox.getChildren().add(lb4);
			} 
			
		});
		
		// EVENT -- Send a message
		send.setOnAction(e ->{
			message = tf.getText();
			tf.setText("");
			convertMessage();
			// Clear message from string
			message = "";						
				try {
					dataout.write(clientMessage);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		});
		
		// EVENT: Exit Chat window  
		primaryStage.setOnCloseRequest(e -> {
			connectStage.close();
			System.exit(-1);
		});
		
		// EVENT: Exit "Connect to Server" window
		connectStage.setOnCloseRequest(e -> {
			primaryStage.close();
			System.exit(-1);
		});
		
		/*
		 * 
		 *  SERVER THREAD
		 * 
		 */
				
		// Create ClientRequest object -- used to start a server request thread
		ClientRequest clientReq = new ClientRequest(serverIP, textarea, tf, sock);
		Runnable runnable = (Runnable)clientReq;
		Thread thread = new Thread(runnable);
		thread1 = thread;
		thread.start();
	}

	/* convertMessge: Converts String into a byte array for transmission */
	public static void convertMessage() {
		message = name + ": " + message;
		// Convert to bytes for transmission
		clientMessage = message.getBytes();
	}

	/* socketConnect: Check to see if client socket is connected to server */
	public static void socketConnect(Socket socket) {
		if(socket.isConnected()) {
			connected = true;
		} else {
			connected = false;
		}
	
	}

}

/* ClientRequest: Handles server requests */
class ClientRequest implements Runnable {
	byte[] packet = new byte [256]; 
	String serverIP = null;
	byte[] text = new byte [256]; 
	DataInputStream input = null;
	DataOutputStream request = null;
	TextArea textarea = null;
	TextField textField = null;
	Socket sock = null;
	
	ClientRequest(String serverIP, TextArea textarea, TextField textField, Socket sock) throws Exception, SocketTimeoutException, SocketException, UnknownHostException {
		this.serverIP = serverIP;
		this.textarea = textarea;
		this.sock = sock;
		this.textField = textField;
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
				textarea.appendText(serverText + "\n");
				/* Close socket if user quits */
				clear();
				socket.close();
			}

			} catch(SocketException ex) {
				ex.printStackTrace();
			} catch(UnknownHostException ex) {
				ex.printStackTrace();
			} catch(IOException ex) {
				/* Timeout exception if timeout is being used */
				textarea.appendText("\n*** You have exceeded the inactivity limit ***");
				textarea.appendText("\n*** Please exit the program and reconnect ***");
				textField.setText("");							// Clear textfield
				textField.setEditable(false);						// Make disable TextField and force user to quit program
				try {
					disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				} catch(NullPointerException e) {					
				}
			}
	}
	
	/* clear: Clears byte array */
	public void clear() {
		for(int i = 0; i < packet.length; i++) {
			packet[i] = 0;
		}
	
	}
		
	public void disconnect() throws IOException {
		sock.close();
	}
	
}


