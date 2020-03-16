/* Personal Project -- Study Session Stopwatch
 * I created a stopwatch for my MacBook because MacOS doesn't include one out of the box
 * I use this program to monitor my study sessions
 */

package stopwatch;

import javafx.application.Application;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;


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
	
	public void start(Stage primaryStage) {
		StackPane root = new StackPane();
		BorderPane pane = new BorderPane();
		
	
		HBox hbox = new HBox(30);
		hbox.setAlignment(Pos.CENTER);
		hbox.setPadding(new Insets(140,0,0,0));
		
		Text text = new Text("00:00:00");
		text.setFont(new Font(40));
		text.setFill(Color.BLUE);
		
		Button start = new Button("Start");
		Button stop = new Button("Stop");
		Button reset = new Button("Reset");
		
		hbox.getChildren().addAll(start, stop, reset);
		pane.setCenter(text);
		root.getChildren().addAll(pane, hbox);
		
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
