/* Personal Project -- Study Timer
 * Program allows user to keep track of course study sessions 
 * Study times (and dates) are recorded to a file when the user clicks the "Stop" button
 * Saved filed is named "Study Sessions"
 * 
 * I created this program because MacOS doesn't include a stopwatch as part of its operating system
 * I use this to keep track of my study sessions
 */

package stopwatch;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

public class Stopwatch extends Application {
	long milliseconds;
	long currentSecond;
	long currentMinute;
	long seconds;
	long currentHour;
	long pos = 0;
	String second = "";
	String minute = "";
	String hour = "";
	String course = "";
	boolean t = false;
	boolean f = true;
	boolean load = false;
	boolean clockStop = false;
	byte [] array = new byte [10];
	
	public void start(Stage primaryStage) throws Exception {
		Date date = new Date();
		RandomAccessFile inout = new RandomAccessFile("Study Sessions", "rw");
		RandomAccessFile lp = new RandomAccessFile("fp", "rw");
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream di = new DataOutputStream(bout);
		StackPane root = new StackPane();
		BorderPane pane = new BorderPane();
		BorderPane lapPane = new BorderPane();
		
		// If file does exist, set file pointer to last known position
		// If file does not exist, set file position to 0
		try {
			pos = lp.readLong();
		} catch(EOFException ex) {
			pos = 0;
		}

		HBox hbox = new HBox(20);
		hbox.setAlignment(Pos.CENTER);
		hbox.setPadding(new Insets(140,0,0,0));
		
		Text time = new Text("00:00:00");
		time.setFont(new Font(65));
		BorderPane.setMargin(time, new Insets(0,0,50,0));
		
		// Stopwatch "lap"
		Text lap = new Text();
		lap.setFill(Color.RED);
		lap.setFont(new Font(13));
		
		// HBox holds user "Stop" message 
		HBox hbox2 = new HBox();
		hbox2.setAlignment(Pos.CENTER);
		String m = "Make sure to click \"Stop\" to record your session";
		Text message = new Text(m);
		message.setFill(Color.RED);
		hbox2.getChildren().add(message);
		
		// Button objects
		Button start = new Button("Start");
		Button pause = new Button("Pause");
		Button stop = new Button("Stop");
		Button reset = new Button("Reset");
		start.setPrefWidth(75);
		pause.setPrefWidth(75);
		stop.setPrefWidth(75);
		reset.setPrefWidth(75);
		
		// Add nodes and panes to StackPane
		hbox.getChildren().addAll(start, pause, stop, reset);
		pane.setCenter(time);
		root.getChildren().addAll(lapPane, pane, lap, hbox);
		
		// Pane, HBox, VBox, Text, Button for new Scene -- Course stage
		BorderPane b = new BorderPane();
		
		// HBox holds Textfield and Submit button
		HBox hbox3 = new HBox(10);
		hbox3.setAlignment(Pos.CENTER);
		
		// Vbox holds Text, HBox, and Radio Buttons
		VBox vbox = new VBox(20);
		vbox.setAlignment(Pos.CENTER);
		
		Text enterCourse = new Text("Enter course name ");
		enterCourse.setFill(Color.BLACK);
		
		// Submit course
		Button submit = new Button("Submit");
		submit.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, null, null)));
		
		// Course textfield
		TextField tf = new TextField();
		
		// Group Radio Buttons
		ToggleGroup group = new ToggleGroup();
		
		// Radio Buttons -- Light and Dark Mode
		RadioButton lightMode = new RadioButton("Light mode");
		RadioButton darkMode = new RadioButton("Dark mode");
		lightMode.setTextFill(Color.BLACK);
		darkMode.setTextFill(Color.BLACK);
		lightMode.setToggleGroup(group);
		darkMode.setToggleGroup(group);
		group.selectToggle(lightMode);
		
		// Add nodes and panes to BorderPane
		hbox3.getChildren().addAll(tf, submit);
		vbox.getChildren().addAll(enterCourse, hbox3, lightMode, darkMode);
		b.setCenter(vbox);		

		// Create a new Scene so that user can input Course name
		Scene scene2 = new Scene(b, 375, 300);
		Stage firstStage = new Stage();
		firstStage.setScene(scene2);
		firstStage.setTitle("Study Timer");
		firstStage.setResizable(false);
		firstStage.show();
		
		// EVENT -- Used for Timeline
		EventHandler <ActionEvent> event = new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e) {
				if(t == false) {
					milliseconds = System.currentTimeMillis();
					currentSecond += 1;
						if(currentSecond == 60) {
							currentSecond = 0;
							currentMinute += 1;
							if(currentMinute == 60) {
								currentHour += 1;
								currentMinute = 0;
								if(currentHour == 25) {
									currentHour = 0;
								}
							}
							
						}
					hour = String.format("%02d", currentHour);
					minute = String.format("%02d", currentMinute);
					second = String.format("%02d", currentSecond);
					
					time.setText(hour + ":" + minute + ":" + second);
					t = true;
				} else {
					milliseconds = System.currentTimeMillis();
					currentSecond += 1;
						if(currentSecond == 60) {
							currentSecond = 0;
							currentMinute += 1;
							if(currentMinute == 60) {
								currentHour += 1;
								currentMinute = 0;
								if(currentHour == 25) {
									currentHour = 0;
								}
							}
							
						}
					}
					hour = String.format("%02d", currentHour);
					minute = String.format("%02d", currentMinute);
					second = String.format("%02d", currentSecond);
					
					time.setText(hour + ":" + minute + ":" + second);
					t = false;
				}
		};
		
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), event));
		timeline.setCycleCount(Timeline.INDEFINITE);
		
		// COURSE EVENT
		submit.setOnAction(e -> {
			course = tf.getText();
			load = true;
			if(load == true) { 
				firstStage.hide();
				lapPane.setTop(hbox2);
				Scene scene = new Scene(root, 375, 300);
				primaryStage.setScene(scene);
				primaryStage.setResizable(false);
				primaryStage.setTitle("Study Timer");
				primaryStage.show();		
			}
		});
		
		// EVENT -- Light Mode
		lightMode.setOnAction(e -> {
			enterCourse.setFill(Color.BLACK);
			b.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
			root.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
			time.setFill(Color.BLACK);
			lightMode.setTextFill(Color.BLACK);
			darkMode.setTextFill(Color.BLACK);
			// Change background color of buttons (dark mode)
			submit.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, null, null)));
			start.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, null, null)));
			pause.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, null, null)));
			stop.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, null, null)));
			reset.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, null, null)));
			
		});
		
		// EVENT - Dark Mode
		darkMode.setOnAction(e -> {
			enterCourse.setFill(Color.WHITE);
			b.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
			root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
			time.setFill(Color.ORANGE);
			lightMode.setTextFill(Color.WHITE);
			darkMode.setTextFill(Color.WHITE);
			// Change background color of buttons (dark mode)
			submit.setBackground(new Background(new BackgroundFill(Color.DARKGREY, null, null)));
			start.setBackground(new Background(new BackgroundFill(Color.DARKGREY, null, null)));
			pause.setBackground(new Background(new BackgroundFill(Color.DARKGREY, null, null)));
			stop.setBackground(new Background(new BackgroundFill(Color.DARKGREY, null, null)));
			reset.setBackground(new Background(new BackgroundFill(Color.DARKGREY, null, null)));
		});
	
		// BUTTON EVENTS
		start.setOnAction(e -> {
			if(timeline.getStatus() == Status.PAUSED) {
				pause.setText("Pause");
			}
			message.setText("");
			timeline.play();
			clockStop = false;
			
		});	
		
		pause.setOnAction(e -> {
			if(currentSecond > 0 && clockStop == false) {
				message.setText("Paused");
				pause.setText("Resume");
				timeline.pause();
				clockStop = true;
			} else {
				timeline.getStatus();
				if (clockStop == true && timeline.getStatus() == Status.PAUSED) {
					message.setText("");
					pause.setText("Pause");
					timeline.play();
					clockStop = false;
				}
			}
		});
		
		stop.setOnAction(e -> {
			message.setText("");
			timeline.stop();
			clockStop = true;
			if(currentSecond > 0) {
				String sessionTime = "Study time: " + hour + ":" + minute + ":" + second;
				lap.setText(sessionTime);
				lapPane.setBottom(lap);	
				
				sessionTime = date.toString() + "\n" + course + " - " + hour  + ":" + minute + ":" + second + "\n" +
						"----------------------------------------\n";
				array = sessionTime.getBytes();
				try {
					inout.seek(pos);
					inout.write(array); 
					// Get file pointer position
					pos = inout.getFilePointer();
					lp.seek(0);
					lp.writeLong(pos);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			if(timeline.getStatus() == Status.STOPPED) {
				pause.setText("Pause");
			}
		});
		
		reset.setOnAction(e -> {
			if(timeline.getStatus() == Status.PAUSED) {
				pause.setText("Pause");
				message.setText("");
			}
			currentSecond = 0;
			currentMinute = 0;
			currentHour = 0;
			time.setText("00:00:00");
			timeline.stop();
		});
		
		// Saves time to file if user closes window using exit button
		primaryStage.setOnCloseRequest(e -> {
			
			if(currentSecond > 0 && timeline.getStatus() != Status.STOPPED) {
				message.setText("");
				timeline.stop();
				clockStop = true;
				String sessionTime = "Study time: " + hour + ":" + minute + ":" + second;
				sessionTime = date.toString() + "\n" + course + " - " + hour  + ":" + minute + ":" + second + "\n" +
						"----------------------------------------\n";
				array = sessionTime.getBytes();
				try {
					inout.seek(pos);
					inout.write(array); 
					// Get file pointer position
					pos = inout.getFilePointer();
					lp.seek(0);
					lp.writeLong(pos);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			} 
			if(timeline.getStatus() == Status.STOPPED) {
				pause.setText("Pause");
			}
		});

	}
	
	public static void main(String[] args) {
		Application.launch(args);	
	}

}
