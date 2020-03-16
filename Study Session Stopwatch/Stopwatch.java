/* Personal Project -- Study Session Stopwatch
 * I created a stopwatch for my MacBook because MacOS doesn't include one out of the box
 * I use this to keep track of my study sessions
 */

package stopwatch;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Stopwatch extends Application {
	long milliseconds;
	long currentSecond;
	long currentMinute;
	long seconds;
	long currentHour;
	String second = "";
	String minute = "";
	String hour = "";
	boolean t = false;
	boolean f = true;
	byte [] array = new byte [30];
	byte [] lstPos = new byte [30];
	
	long pos = 0;
	
	public void start(Stage primaryStage) throws Exception {
		RandomAccessFile inout = new RandomAccessFile("session", "rw");
		RandomAccessFile lp = new RandomAccessFile("last", "rw");
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

		HBox hbox = new HBox(30);
		hbox.setAlignment(Pos.CENTER);
		hbox.setPadding(new Insets(140,0,0,0));
		
		Text text = new Text("00:00:00");
		text.setFont(new Font(65));
		text.setFill(Color.BLUE);
		BorderPane.setMargin(text, new Insets(0,0,50,0));
		
		Text lap = new Text();
		lap.setFill(Color.RED);
		lap.setFont(new Font(13));
	
		Button start = new Button("Start");
		Button stop = new Button("Stop");
		Button reset = new Button("Reset");
		
		hbox.getChildren().addAll(start, stop, reset);
		pane.setCenter(text);
		root.getChildren().addAll(lapPane, pane, lap, hbox);
		
		// EVENT
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
					
					text.setText(hour + ":" + minute + ":" + second);
					
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
					
					text.setText(hour + ":" + minute + ":" + second);
					t = false;
				}
		};
		
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), event));
		timeline.setCycleCount(Timeline.INDEFINITE);
		
		// BUTTON EVENTS
		start.setOnAction(e -> {
			timeline.play();
		});	
		
		stop.setOnAction(e -> {
			timeline.stop();
			if(currentSecond > 0) {
				String lapTime = "Study time: " + hour + ":" + minute + ":" + second;
				lap.setText(lapTime);
				lapPane.setBottom(lap);	
				lapTime += "\n";
				array = lapTime.getBytes();
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
		});
		
		reset.setOnAction(e -> {
			currentSecond = 0;
			currentMinute = 0;
			currentHour = 0;
			text.setText("00:00:00");
			timeline.stop();
		});
		
		Scene scene = new Scene(root, 300, 300, Color.BLACK);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setTitle("Study Session Stopwatch");
		primaryStage.show();		
	}
	
	public static void main(String[] args) {
		Application.launch(args);
		
	}

}
