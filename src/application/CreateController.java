package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class CreateController {
	@FXML
	private TextField dictIDField;
	
	@FXML
	private TextField libraryIDField;
	
	@FXML
	private Label errorLabel;
	
	@FXML
	private AnchorPane mainPane;
	
	public void create() throws JSONException, IOException {
		String dict_id = dictIDField.getText();
		String lib_id = libraryIDField.getText();
		if(lib_id == null || lib_id.length() == 0 || !lib_id.endsWith(".json")) {
			errorLabel.setText("LibraryID cant' be empty \nand has to end with .json");
			return;
		}
		
		if(dict_id.length() == 0 || dict_id == null) {
			errorLabel.setText("DictionaryID cant' be empty");
			return;	
		}
		
		Requests r = new Requests();
		int ret = r.makeRequest(lib_id, dict_id);
		File file = new File(System.getenv("HOME") + "/.hangman/path_to_dict.txt");
		file.delete();
		try{
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write(System.getenv("HOME") + "/.hangman/medialab/hangman_" + dict_id + ".txt");
			writer.flush();
			writer.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		if(ret == -1) {
			errorLabel.setText("Invalid LibraryID");
			file.delete();
			return;
		}
		
		else if(ret == 0) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Proceed?");
			alert.setContentText("A file with that DictionaryID already exists.\nOverwrite?");
			
			if(alert.showAndWait().get() == ButtonType.OK) {
				System.out.println("Starting the game");
				(new File(System.getenv("HOME") + "/.hangman/medialab/hangman_" + dict_id + ".txt") ).delete();
				create();
			}
			else {
				file.delete();
				errorLabel.setText("Pick another DictionaryID");
				return;
			}
		}
		( (Stage) mainPane.getScene().getWindow() ).close();
	}
}
