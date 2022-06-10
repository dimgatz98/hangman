package application;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginController {

	private Stage stage;
	private Scene scene;
	private Parent root;
	
	@FXML
	private TextField nameTextField;
	@FXML
	private TextField libraryID;
	@FXML
	private TextField dictionaryID;
	@FXML
	private Label errorLabel;
	@FXML
	private AnchorPane scenePane;
	@FXML
	private Label aboutLabel;
	
	public void displayLabel (String text) {
		this.aboutLabel.setText(text);
	}
	
	public void switchToGameScene (ActionEvent event) throws IOException, JSONException { 
		String username = nameTextField.getText();
		String dict_id = dictionaryID.getText();
		String lib_id = libraryID.getText();
		if(username.length() >= 20 || username.length() == 0 || username == null) {
			errorLabel.setText("      Username can't be empty \nor have more than 20 characters");
			return;
		}
		
		if(lib_id == null || lib_id.length() == 0 || !lib_id.endsWith(".json")) {
			errorLabel.setText("        LibraryID cant' be empty \n        and has to end with .json");
			return;
		}
		
		if(dict_id.length() == 0 || dict_id == null) {
			errorLabel.setText("       DictionaryID cant' be empty");
			return;	
		}
		
		Requests r = new Requests();
		int ret = r.makeRequest(lib_id, dict_id);
		File file = new File(getClass().getResource("/medialab").toString().split(":")[1] + "/path_to_dict.txt");
		file.delete();
		try{
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write(getClass().getResource("/medialab/").toString().split(":")[1] + "hangman_" + dict_id + ".txt");
			writer.flush();
			writer.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		if(ret == -1) {
			errorLabel.setText("             Invalid LibraryID");
			file.delete();
			return;
		}
		
		else if(ret == -2) {
			errorLabel.setText("Read timeout. Server currently unreachable.");
			file.delete();
			return;
		}
		
		else if(ret == 0) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Proceed?");
			alert.setContentText("A file with that DictionaryID already exists.\nContinue with this file?");
			
			if(alert.showAndWait().get() == ButtonType.OK) {
				System.out.println("Starting the game");
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/GameScene.fxml"));
				root = loader.load();
				GameController gameController = loader.getController();
				gameController.displayName(username);
				
				stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				scene = new Scene(root);
				stage.setScene(scene);
				stage.show();
			}
			else {
				file.delete();
				errorLabel.setText("      Pick another DictionaryID");
			}
		}
		else {
			System.out.println("Starting the game");
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/GameScene.fxml") );
			root = loader.load();
			GameController gameController = loader.getController();
			gameController.displayName(username);
			
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		}
		
	}

	
	public void goToStartingScreen (ActionEvent e) throws IOException {
		root = FXMLLoader.load(getClass().getResource("/Main.fxml"));
		stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}
