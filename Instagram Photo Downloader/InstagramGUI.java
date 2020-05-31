/* Instagram Photo Downloader GUI
 * 
 * 
 * WORK-IN-PROGRESS
 * 
 */

package instagramgui;

import javafx.application.Application;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;
import java.io.File;

public class InstagramGUI extends Application {
	String path = "";
	String profileURL = "";
	String resolution = "";
	
	
	public void start(Stage primaryStage) {
		StackPane root = new StackPane();
		BorderPane bp = new BorderPane();
		HBox hbox = new HBox();
		HBox hbox2 = new HBox();
			
		MenuBar menubar = new MenuBar();
		menubar.setPrefWidth(500);
		Menu menu1 = new Menu("File");
		Menu menu2 = new Menu("Help");
		
		MenuItem open = new MenuItem("Open");
		MenuItem exit = new MenuItem("Exit");
		menubar.getMenus().addAll(menu1,menu2);
		menu1.getItems().addAll(open, exit);
		hbox.getChildren().add(menubar);
		root.getChildren().add(hbox);		// ROOT???
		
		// STEP 1: Enter Instagram profile URL
		TextArea ta = new TextArea();
		ta.setEditable(true);
		ta.setPromptText("https://www.instagram.com/nike/?hl=en");
		ta.setPrefHeight(100);
		ta.setFocusTraversable(false);
		
		// STEP 3: Resolution radio buttons
		RadioButton rb1 = new RadioButton("High");
		RadioButton rb2 = new RadioButton("Medium");
		ToggleGroup group = new ToggleGroup();
		rb1.setToggleGroup(group);
		rb2.setToggleGroup(group);
				
		// Text: Displays steps
		Text text1 = new Text("1. Enter the profile URL below");
		text1.setFill(Color.RED);
		
		// FINAL: This will cycle through each image (preview)
		Image images = new Image("file:/Users/jasonmoreau/Desktop/Photos/m.jpg");
		ImageView imageView = new ImageView(images);
		imageView.setFitHeight(250);
		imageView.setFitWidth(250);
		
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
			// filechooser.showOpenDialog(primaryStage);
			directoryChooser.showDialog(primaryStage);
			if(selectedDirectory != null) {
				path = selectedDirectory.getAbsolutePath();
				System.out.println(path);
				text1.setText("3. Select image resolution");
				VBox vbox = new VBox(10);
				HBox hbox3 = new HBox(10);
				vbox.setAlignment(Pos.CENTER);
				hbox3.setAlignment(Pos.CENTER);
				hbox3.getChildren().addAll(rb1,rb2);
				vbox.getChildren().addAll(text1, hbox3);
				bp.setCenter(vbox);
			} 
		});
		
		// Radio buttons - High resolution
		rb1.setOnAction(e -> {
			resolution = "1080x1080";
			System.out.println(resolution);
			// Disable open in menu bar
			open.setDisable(true);
			bp.setCenter(imageView);
		});
		
		// Radio buttons - Medium resolution
		rb2.setOnAction(e -> {
			resolution = "s640x640";
			System.out.println(resolution);
			// Disable open in menu bar
			open.setDisable(true);
			bp.setCenter(imageView);
		});
		
		// Exit program
		exit.setOnAction(e -> {
			System.exit(-1);
		});
		
		// Enter profile URL
		ta.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) {
				profileURL = ta.getText();
				System.out.println(profileURL);
				ta.setEditable(false);
				text1.setText("2. Select folder to save images");
			}
		});

		Scene scene = new Scene(bp, 500, 400);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setTitle("Instagram Photo Downloader");
		primaryStage.show();	
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}


}
