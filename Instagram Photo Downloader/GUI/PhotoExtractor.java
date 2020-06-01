/* Instagram Photo Downloader - GUI version
 * Program downloads photos from Instagram profiles
 * Reads and extracts images from HTML document and JSON
 * Utilizes multi-threading and various data structures
 * 
 * If all images are not saved try downloading at a lower resolution
 */

package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
// JAVAFX
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

public class PhotoExtractor extends Application {
	static CookieManager cm = new CookieManager();
	static ArrayDeque <String> htmlLinks = new ArrayDeque<>();
	static ArrayDeque <String> jsonLinks = new ArrayDeque<>();
	static ArrayDeque<String> queue = new ArrayDeque<>();
	static ArrayDeque<String> finalQ = new ArrayDeque<>();
	static boolean htmlNextPage = false;		// default is false
	static int counter = 1;
	static int num = 1;
	static int selection; 
	static String instaURL;
	static String c = "";
	static String tempLine = "";
	static String page;
	static String filename = "";
	static String containerValue = "";	
	static String jsFile = ""; 
	static String resolution;
	static String path = "";
	static String prevImage = ""; 
	static StringBuilder taText = new StringBuilder("");
	static BorderPane bp = new BorderPane();
	static TextArea ta = new TextArea();
	static Text text1 = new Text();
	static Image images;
	static ImageView imageView = new ImageView(images);
	static VBox vbox2 = new VBox(10);
	static ParseJSON parseJSON = new ParseJSON();
	static FirstRequest fr = new FirstRequest();
	static Runnable runnable1 = (Runnable) fr;
	static SubsequentRequest sr = new SubsequentRequest();
	static Runnable runnable2 = (Runnable) sr;
	
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	public void start(Stage primaryStage) throws Exception {
		StackPane root = new StackPane();
		BorderPane bp = new BorderPane();
		HBox hbox = new HBox();
		HBox hbox2 = new HBox(10);
		hbox2.setAlignment(Pos.CENTER);
			
		MenuBar menubar = new MenuBar();
		menubar.setPrefWidth(500);
		Menu menu1 = new Menu("File");
		Menu menu2 = new Menu("Help");
		
		MenuItem open = new MenuItem("Open");
		MenuItem exit = new MenuItem("Exit");
		MenuItem about = new MenuItem("About");
		open.setDisable(true);
		menubar.getMenus().addAll(menu1,menu2);
		menu1.getItems().addAll(open, exit);
		menu2.getItems().add(about);
		hbox.getChildren().add(menubar);
		root.getChildren().add(hbox);		// ROOT???
		
		// Enter Instagram profile URL
		ta.setFont(new Font(12));
		ta.setEditable(true);
		taText.append("Enter Instagram profile URL --> (ex: https://www.instagram.com/nike/?hl=en)");
		ta.setPromptText(taText.toString());
		ta.setPrefHeight(100);
		ta.setFocusTraversable(false);
		
		//  Resolution radio buttons
		RadioButton rb1 = new RadioButton("High");
		RadioButton rb2 = new RadioButton("Medium");
		ToggleGroup group = new ToggleGroup();
		rb1.setToggleGroup(group);
		rb2.setToggleGroup(group);
				
		// Text: Displays steps
		text1.setText("1. Enter the profile URL below");
		text1.setFill(Color.RED);
		
		imageView.setFitHeight(250);
		imageView.setFitWidth(250);
		
		vbox2.setAlignment(Pos.CENTER);
		
		bp.setTop(hbox);
		bp.setBottom(ta);
		bp.setCenter(text1);
		
		/*
		 * EVENT HANDLERS
		 */
			
		// Open folder
		open.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			File selectedDirectory = directoryChooser.showDialog(primaryStage);
			if(selectedDirectory != null) {
				filename = selectedDirectory.getAbsolutePath() + "/";
				text1.setText("3. Select image resolution");
				VBox vbox = new VBox(10);
				vbox.setAlignment(Pos.CENTER);
				hbox2.getChildren().addAll(rb1,rb2);
				vbox.getChildren().addAll(text1, hbox2);
				bp.setCenter(vbox);
				// Disable open in menu bar
				open.setDisable(true);
			}
		});
		
		// Radio buttons - High resolution
		rb1.setOnAction(e -> {
			resolution = "1080x1080";
			text1.setText("Preview");
			vbox2.getChildren().addAll(text1, imageView);
			ta.setFont(new Font(10));
			bp.setCenter(vbox2);
			ta.setPromptText("");
			// Initial GET request -- HTML files
			try {
				//Initial Request
				Thread thread1 = new Thread(fr);
				thread1.start();
			} catch (Exception e1) {
				taText.append("ERROR\n");
				ta.setText(taText.toString());
			}
		});
		
		// Radio buttons - Medium resolution
		rb2.setOnAction(e -> {
			resolution = "s640x640";
			text1.setText("Preview");
			vbox2.getChildren().addAll(text1, imageView);
			ta.setFont(new Font(10));
			bp.setCenter(vbox2);
			ta.setPromptText("");
			// Initial GET request -- HTML files
			try {
				//Initial Request
				Thread thread1 = new Thread(fr);
				thread1.start();
			} catch (Exception e1) {
				taText.append("ERROR\n");
				ta.setText(taText.toString());
			}
		});
		
		// Exit program
		exit.setOnAction(e -> {
			System.exit(-1);

		});
		
		// Enter profile URL
		ta.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) {
				instaURL = ta.getText();
				ta.setEditable(false);
				open.setDisable(false);
				text1.setText("2. Select folder to save images");
			}
		});
		
		// About stage
		about.setOnAction(e -> {
			BorderPane pane = new BorderPane();
			Stage stage = new Stage();
			Scene aboutScene = new Scene(pane,200,200);
			VBox aboutContainer = new VBox(10);
			Text programName = new Text("Instagram Photo Downloader");
			Text creator = new Text("Created by: Jason Moreau");
			Text programYear = new Text("(c) 2020");
			aboutContainer.setAlignment(Pos.CENTER);
			aboutContainer.getChildren().addAll(programName, creator, programYear);
			pane.setCenter(aboutContainer);
			stage.setResizable(false);
			stage.setScene(aboutScene);
			stage.show();
		});
		
		Scene scene = new Scene(bp, 500, 400);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setTitle("Instagram Photo Downloader");
		primaryStage.show();
	}
	
	// initialRequest: Extracts images from HTML document; retrieves required information to generate query URL for subsequent requests
	public static void initialRequest() throws Exception {
		URL	html = new URL(instaURL);
		URL jsPage = null;
		CookieHandler.setDefault(cm);
		HttpURLConnection conn = (HttpURLConnection) html.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(html.openStream()));
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Cookies", c);
		conn.addRequestProperty("User-Agent", "Mozilla/5.0");
		conn.getContent();
		// ## COOKIES ## 
		cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieStore cs = cm.getCookieStore();
		List<HttpCookie> cookieList = cs.getCookies(); 
		// add cookies
		for(HttpCookie cook: cookieList) {
			if(cook.toString().startsWith("ig")) {
				c += cook.getName() + "=" + cook.getValue();
				break;
			}
			c += cook.getName() + "=" + cook.getValue() + "; ";
		} 
		// Get profile ID, end cursor, and query hash to create URL for next page
		readFileHTML(html);
		// page variable contains html document
		parseURL();	
		readLinks();
		// Check if there is a next page after HTML document
		htmlNextPage();
		if(htmlNextPage == true) {
			parseJSON.setHasNextPage(htmlNextPage);
			// Start extraction to generate URL for next page (JSON)
			parseJSON.extractEndCursor(page);	
			parseJSON.extractProfileId(page);
			extractPageContainer();
			jsPage = new URL(jsFile);
			readJS(jsPage);
			parseJSON.extractQueryHash(page);
			// page variable now contains .js file
			taText.append("GENERATING URL...\n");
			ta.setText(taText.toString());
			parseJSON.generateURL();
			taText.append("URL GENERATED.\n");
			ta.setText(taText.toString());
			taText.append(parseJSON.getQueryURL() + "\n");
			ta.setText(taText.toString());
			counter++;
			taText.append("PAGE: ");
			ta.setText(taText.toString() + counter + "\n");
			reader.close();
		}	
	}

		// htmlNextPage: Checks if HTML document has a subsequent page 
	public static void htmlNextPage() {
		String findText = "\"has_next_page\":true";
		// If findText contents are in variable that mean there is a next page
		htmlNextPage = page.contains(findText);
	}
		
	// extractContainerPage: Parse HTML for .js file containing query hash -- found in HTML document
	public static void extractPageContainer() throws Exception {
		String delimiter1 = "\"/static/bundles/metro/ProfilePageContainer.js/";
		String delimiter2 = ".js\"";	
		String [] tokens1 = page.split(delimiter1);
		String [] tokens2 = tokens1[1].split(delimiter2);			
		containerValue = tokens2[0];
		jsFile = "https://instagram.com/static/bundles/metro/ProfilePageContainer.js/" + containerValue + ".js";
	}
			
	// readJS: Reads Javascript file (.js) to string variable
	public static void readJS(URL url) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line = "";
		page = "";
		// Looks for JPG and HTTP prefix and suffix and saves it file
		while ((line = reader.readLine()) != null) {
			page += line;
		}
		reader.close();
	}
				
	// getRequest: GET request for JSON file 
	public static void getRequest() throws Exception {
		instaURL = parseJSON.getQueryURL();
		URL queryURL = new URL(instaURL);
		BufferedReader reader = new BufferedReader(new InputStreamReader(queryURL.openStream()));
		HttpURLConnection conn2 = (HttpURLConnection) queryURL.openConnection();
		conn2.setRequestMethod("GET");
		conn2.setRequestProperty("Cookie", c);
		conn2.getContent();
		readStream(reader);
		readFileJSON(queryURL); 		// read JSON document 
		reader.close();
	}

	// readFileJSON: Reads JSON and parses for .jpg links; adds downloadable links to queue data structure 
	public static void readFileJSON(URL url) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line1 = "";
		String delimiter1 = "[{}]";
		String delimiter2 = "https:";
		String delimiter3 = "\"";
		page = "";
		// Reads JSON and adds it to string variable
		while ((line1 = reader.readLine()) != null) {
			page += line1;
		}
		    
		String [] tokens1 = page.split(delimiter1);
		// finds all .jpg links in JSON document and adds them to a queue
		for(int i = 0; i < tokens1.length; i++) {
			if(tokens1[i].contains("https") && tokens1[i].contains("jpg")) {
				if(tokens1[i].contains("src") && tokens1[i].contains("https") && tokens1[i].contains(resolution)) {
					queue.add(tokens1[i]);
				}
			}
		} 
					 
		int size = queue.size();
		int i = 0;
		// Loop through queue (as a stack) using delimiters to fix url
		// Iterator checks if link is already in data structure -- adds it to queue if it isnt
		while(i < size) {
			String [] tokens3 = queue.pop().split(delimiter2);
			String line = tokens3[1];
			String [] tokens4 = line.split(delimiter3);
			String link = "https:" + tokens4[0];
				if(jsonLinks.isEmpty()) {
					jsonLinks.add(link);
					i++;
					continue;
				}
			Iterator<String> iterator = jsonLinks.iterator();
			while(iterator.hasNext()) {
				String temp = iterator.next();
				if(link.contains(temp)) {
					break;
				} else {
					jsonLinks.push(link);
					break;
				}
			}
			i++;	
		}
		taText.append("\nTOTAL LINKS: " + Integer.toString(jsonLinks.size()) + "\n");
		ta.setText(taText.toString());
		reader.close();
	}

				
		// readFileHTML: Reads HTML and adds downloadable links to queue data structure
	public static void readFileHTML(URL url) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line = "";
		String delimiter1 = "[{}]";
		String delimiter2 = "url\":\"";
		String delimiter3 = "\"";
		page = "";
			
		// Reads HTML document -- adds it to string
		while ((line = reader.readLine()) != null) {
			page += line;
		}
		
		String [] tokens1 = page.split(delimiter1);
				
		// Finds all .jpg links in HTML document with 1080x1080 resolution and adds them to a queue
		for(int i = 0; i < tokens1.length; i++) {
			if(tokens1[i].contains("https") && tokens1[i].contains("jpg")) {
				if(selection == 1) {
					if(tokens1[i].contains("https") && tokens1[i].contains(resolution)) {
						queue.add(tokens1[i]);
					}
				} else {
					delimiter2 = "https:";
					delimiter3 = "\"";
					if(tokens1[i].contains("src") && tokens1[i].contains("https") && tokens1[i].contains(resolution)) {
						queue.add(tokens1[i]);
					}
				}
			}		 
		}
			
		int size = queue.size();
		int i = 0;
		// Loop through queue (as a stack) using delimiters to fix url
		// iterator checks if link is already in data structure -- adds it to queue if it isnt
		while(i < size) {
			String [] tokens3 = queue.pop().split(delimiter2);
			line = tokens3[1];
			String [] tokens4 = line.split(delimiter3);
			String link = tokens4[0];
			if(finalQ.isEmpty()) {
				finalQ.add(link);
				i++;
				continue;
			}
			Iterator<String> iterator = finalQ.iterator();
			while(iterator.hasNext()) {
				String temp = iterator.next();
				if(link.contains(temp)) {
					break;
				} else {
					finalQ.push(link);
					break;
				}
			}
			i++;	
		}
	}
		
	// parseURL: Adds removes Unicode value and replaces with it "&"; adds link to data structure -- FOR HTML DOCUMENT LINKS ONLY !!!!
	public static void parseURL() throws Exception {
		String delimeter4 = "[\"]";
		String unicode = "\\\\u0026";
		String link = "";
		while(!finalQ.isEmpty()) {
			String [] tokens = finalQ.pop().split(delimeter4);
			for(int j = 0; j < tokens.length; j++) {
				link = tokens[j];
				link = link.replaceAll(unicode, "&");
				if(selection == 1) {
					htmlLinks.push(link);
				} else {
					htmlLinks.push("http:" + link);
				}			
			}	
		}
		taText.append("\nTOTAL LINKS: " + Integer.toString(htmlLinks.size()) + "\n");
		ta.setText(taText.toString());		
	}	
			
	// readLinks: Reads links from array and outputs as a JPG file
	public static void readLinks() throws IOException, URISyntaxException {		
		taText.append("\n*** LINKS ***\n");
		ta.setText(taText.toString());
		// read links
		try {
			if(!htmlLinks.isEmpty()) {		// reads data structures -- HTML
				while(!htmlLinks.isEmpty()) {
					URL url = new URL(htmlLinks.peek());	
					taText.append("READ: " + htmlLinks.peek() + "\n");
					ta.setText(taText.toString());
					ta.end();
					htmlLinks.pop();
					InputStream istream = url.openStream();					
					FileOutputStream out = new FileOutputStream(new File(filename + Integer.toString(num) + ".jpg"));
					istream.transferTo(out);
					prevImage = "file:" + filename + Integer.toString(num) + ".jpg";
					images = new Image(prevImage); 
					imageView.setImage(images);
					num++;
					out.close();
				}
			} else {						// reads queue data structures -- JSON 
				while(!jsonLinks.isEmpty()) {
					URL url = new URL(jsonLinks.peek());
					taText.append("READ: " + jsonLinks.peek() + "\n");						
					ta.setText(taText.toString());
					ta.end();
					jsonLinks.pop();
					InputStream istream = url.openStream();
					FileOutputStream out = new FileOutputStream(new File(filename + Integer.toString(num) + ".jpg"));
					istream.transferTo(out);
					prevImage = "file:" + filename + Integer.toString(num) + ".jpg";
					images = new Image(prevImage); 
					imageView.setImage(images);
					num++;
					out.close();
				}
			}
		} catch (IOException ex) {
			taText.append("ERROR \n");
			ta.setText(taText.toString());
		}
		taText.append("\nPAGE COMPLETE\n");
		ta.setText(taText.toString());	
	}	
		
	// readStream: Reads open stream
	public static void readStream(BufferedReader stream) throws Exception {
		String line = "";
		tempLine = "";		// clear variable value
		while((line = stream.readLine()) != null) {
			tempLine += line;
		}
		stream.close();
	}
}

// Thread for first GET request
class FirstRequest extends PhotoExtractor implements Runnable {
	
	FirstRequest() {
		
	}
	
	public void run() {
		try {
			initialRequest();
			Thread.sleep(2000);
			//Subsequent Request
		} catch (Exception e) {
			taText.append("ERROR\n");
			ta.setText(taText.toString());
		}
		Thread thread2 = new Thread(sr);
		thread2.start();
	}
	
}

// Thread for all subsequent GET requests
class SubsequentRequest extends PhotoExtractor implements Runnable {
	
	SubsequentRequest() {
		
	}
	 
	public void run() {
		while(parseJSON.getNextPageValue() == true) {
			try {
				getRequest();
				readLinks(); 										
				parseJSON.extractEndCursor(tempLine);
				parseJSON.checkNextPage();
				taText.append("GENERATING URL...\n");
				ta.setText(taText.toString());
				parseJSON.generateURL();
				taText.append(parseJSON.getQueryURL() + "\n");
				ta.setText(taText.toString());
				taText.append("URL GENERATED.\n");
				ta.setText(taText.toString());			
				counter++;
				taText.append("PAGE: ");
				ta.setText(taText.toString() + Integer.toString(counter) + "\n");
				Thread.sleep(2000);
			} catch (Exception e) {
				taText.append("ERROR\n");
				ta.setText(taText.toString());
			}
		}
	}
}

