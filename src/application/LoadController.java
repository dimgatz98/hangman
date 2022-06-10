package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoadController {
	@FXML
	private TextField dictIDField;
	
	@FXML
	private Label errorLabel;
	
	@FXML
	private AnchorPane mainPane;
	
	public void writeNewDictionaryID() throws IOException {
		File file = new File(System.getenv("HOME") + "/.hangman/path_to_dict.txt");
		file.delete();
		file.createNewFile();
		
		if(!(new File(System.getenv("HOME") + "/.hangman/medialab/hangman_" + dictIDField.getText() + ".txt") ).exists() ) {
			displayError("File does not exist");
		}
		else {
			FileWriter writer = new FileWriter(file);
			writer.write(System.getenv("HOME") + "/.hangman/medialab/hangman_" + dictIDField.getText() + ".txt");
			writer.close();
			( (Stage) mainPane.getScene().getWindow() ).close();
		}
	}
	
	public void displayError(String error) {
		errorLabel.setText(error);
	}
}
