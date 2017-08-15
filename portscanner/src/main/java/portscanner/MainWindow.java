package portscanner;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MainWindow extends Application {
	
	private static final int DEFAULT_WIDTH = 480;
	private static final int DEFAULT_HEIGHT = 320;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setTitle("PortSniffer");

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		
		
		TextField textField = new TextField();
		grid.add(textField, 0, 0);
		
		Button btn = new Button("Submit");
		grid.add(btn, 0, 1);
		
		Label label = new Label();
		grid.add(label, 0, 2);
		
		btn.setOnAction(new EventHandler<ActionEvent>() {
			/* TODO
			 * Add an ip checker
			 */
			@Override
			public void handle(ActionEvent event) {
				String address = textField.getText();
				Sniffer sniffer = new Sniffer(address);
				label.setText(new String());
				
				List<Integer> ports = null;
				
				try {
					ports = sniffer.sniff();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				for (Integer port : ports) {
					label.setText(label.getText() + port.intValue() + "\n");
				}
			}
		});
	
		
		//root.getChildren().add(label);
		primaryStage.setScene(new Scene(grid, DEFAULT_WIDTH, DEFAULT_HEIGHT));
		primaryStage.show();
	}
}
