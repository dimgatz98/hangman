package application;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SceneController {
	private Stage stage;
	private Scene scene;
	private Parent root;
	@FXML
	private AnchorPane scenePane;
	
	public void switchToLoginScene (ActionEvent e) throws IOException { 
		root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
		stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void switchToAboutScene (ActionEvent e) throws IOException { 
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/About.fxml") );
		root = loader.load();
		LoginController loginController = loader.getController();
		loginController.displayLabel("Hangman game variation\nfor 7th semester's NTUA Medialab project.\nOwner: Dimitrios Gkatziouras 03116145.");
		stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void exit (ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Exit");
		alert.setContentText("Are you sure you want to exit?");
		
		if(alert.showAndWait().get() == ButtonType.OK) {
			stage = (Stage) scenePane.getScene().getWindow();
			System.out.println("You closed the game");
			stage.close();
		}
	}
}
