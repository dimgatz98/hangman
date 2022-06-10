package application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

public class PopUpController {
	@FXML
	private ScrollPane scrollPane;
	
	public void setScrollPane(String text) {
		scrollPane.setContent(new Label(text) );
	}
}
