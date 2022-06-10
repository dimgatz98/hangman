package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class GameController implements Initializable {
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	private GameLogic gl;
	private ArrayList<Label> charLabels = new ArrayList<Label> ();
	private ArrayList<Integer> posFound = new ArrayList<Integer> ();
	private String path;
	private ArrayList<ArrayList<Pair<Float, Character> > > probs;
	private Label scrollPaneLabel = new Label ();
	private ArrayList<ArrayList<Character> > unavailableChars = new ArrayList<ArrayList<Character> > ();
	private String roundsData;
	
	@FXML
	private AnchorPane mainPane;
	
	@FXML	
	private TextField posField;
	
	@FXML	
	private TextField charField;
	
	@FXML	
	private Label nameLabel;
	
	@FXML
	private ScrollPane scrollPane;
	
	@FXML
	private Label errorLabel;
	
	@FXML
	private ImageView imageView;
	
	@FXML
	private Label guessRes;	
	
	@FXML
	private Button goButton;
	
	@FXML
	private Label topLabel;
	
	@FXML
	private Button solveButton;
	
	@FXML
	private MenuBar menuBar;
	
	@Override
    public void initialize(URL location, ResourceBundle resources) {
		System.out.println(getClass().getResource("/medialab").toString().split(":")[1] + "/path_to_dict.txt");
		File file = new File(getClass().getResource("/medialab").toString().split(":")[1] + "/path_to_dict.txt");
		try {
			Scanner scanner = new Scanner(file);
			this.path = scanner.nextLine();
			System.out.println("This is the path: " + path);
			scanner.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		mainPane.setStyle("-fx-background-color: #ffffff");
		menuBar.setStyle("-fx-background-color: #d1d1d1");
		menuBar.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0.11) ) ) );

		try {
			System.out.println("Pathhhhhh: " + path);
			gl = new GameLogic(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		setProbs(gl.getProbs() );
		for(Integer i = 0 ; i < gl.getWord().length() ; i++) {
			unavailableChars.add(new ArrayList<Character> () );
		}
		initializeScrollPane();
		topLabel.setText("Points: " + gl.getPoints().toString() + "		Words number: " + gl.getWordsNo().toString() + "\nCorrect guesses / total guesses: " + gl.getCorrectGuesses().toString() + " / " + gl.getTotalGuesses().toString() );
		drawHangman(-1, null);
		drawLines();
	}
	
	public void initializeScrollPane () {
		Integer i = 1;
		StringBuilder s = new StringBuilder ();
		for(ArrayList<Pair<Float, Character> > a: probs) {
			if(!posFound.contains(i - 1) ) {
				s.append(i.toString() + ":  ");
				for(Pair<Float, Character> p: a) {
					if(!unavailableChars.get(i - 1).contains(p.Y() ) )
						s.append(p.Y().toString() + "  ");
				}
				s.append("\n");
			}
			i++;
		}
		scrollPaneLabel.setText(s.toString() );
		scrollPane.setContent(scrollPaneLabel);
	}
	
	public void setProbs (ArrayList<ArrayList<Pair<Float, Character> > > probs) {
		this.probs = probs;
	}
	
	public void update() throws FileNotFoundException {
			if(charField.getText().length() > 1) {
				displayError("Character field text should be of length 1");
				return;
			}
			Character ch = Character.toUpperCase(charField.getText().charAt(0) );
			Integer pos = 0;
			if(!validateFields(ch, posField.getText() ) ) {
				displayError(null);
				return;
			}
			for(Integer i = 0 ; i < posField.getText().length() ; i++) {
				pos += ( (int) posField.getText().charAt(i) - (int) '0' ) * (int)Math.pow(10, i);
			}
			pos--;
			
			if(!validatePos(pos) ) {
				displayError("Position has to be a decimal from 1 to " + gl.getWord().length() + "\nfor which you haven't yet guessed the character right");
				return;
			}
			
			if(unavailableChars.get(pos).contains(ch) ) {
				displayError("This character has already been chosen");
				return;
			}
			
			Pair<Integer, Character> guess = new Pair<Integer, Character> (pos, ch);
			Integer isRightGuess = gl.update(guess);
			setProbs(gl.getProbs() );
			drawHangman(isRightGuess, guess);
			initializeScrollPane();
			topLabel.setText("Points: " + gl.getPoints().toString() + "		Words number: " + gl.getWordsNo().toString() + "\nCorrect guesses / total guesses: " + gl.getCorrectGuesses().toString() + " / " + gl.getTotalGuesses().toString() );
			int res = isGameOver();
			if(res == 0) {
				return;
			}
			if (res == 1) {
				try {
					writeRounds(true, gl.getPoints(), gl.getWord(), gl.getTotalGuesses() );
				} catch (IOException e) {
					e.printStackTrace();
				}
				guessRes.setTextFill(Color.GREEN);
				guessRes.setText("Congrats you won the game!");
				freezeGame();
			}
			else {
				guessRes.setTextFill(Color.RED);
				guessRes.setText("Unfortunately you lost");
				solve();
			}
	}
	
	public void writeRounds(boolean won, Integer points, String word, Integer totalGuesses) throws IOException {
		File file = new File(getClass().getResource("/medialab").toString().split(":")[1] + "/rounds.txt");
		String[] rounds = null;
		if(file.exists() ) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			rounds = reader.lines().toArray(String[]::new);	
			reader.close();
		}
		
		file.delete();
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		if(won)
			writer.write("1. Word: " + word + "  Tries: " + totalGuesses + "  Winner: Player\n");
		else
			writer.write("1. Word: " + word + "  Tries: " + totalGuesses + "  Winner: Computer\n");

		for(Integer i = 2 ; i < 6 ; i++) {
			if(rounds == null || i - 2 >= rounds.length)
				break;
			writer.write(i.toString() + rounds[i - 2].substring(1) + "\n");
		}
		writer.close();
	}
	
	public void start(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/GameScene.fxml") );
		root = loader.load();
		GameController gameController = loader.getController();
		gameController.nameLabel.setText(this.nameLabel.getText() );
		
//		System.out.println(event.getSource() );
		stage = (Stage) (mainPane).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void load() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoadPopUpScene.fxml") );
		root = loader.load();
//		LoadController loadController = loader.getController();
//		loadController.setScrollPane(roundsData);
		scene = new Scene(root);
		stage = new Stage();
		stage.setScene(scene);
		stage.show();
	}
	
	public void freezeGame() {
		posField.setDisable(true);
		charField.setDisable(true);
		goButton.setDisable(true);
		solveButton.setDisable(true);
	}
	
	public void solve() {
		try {
			writeRounds(false, gl.getPoints(), gl.getWord(), gl.getTotalGuesses() );
		} catch (IOException e) {
			e.printStackTrace();
		}
		String word = gl.getWord();
		for(Integer i = 0 ; i < word.length(); i++) {
			charLabels.get(i).setText(word.substring(i, i + 1) );
		}
		guessRes.setTextFill(Color.RED);
		guessRes.setText("Unfortunately you lost");
		freezeGame();
	}
	
	public void rounds() throws IOException {
		File file = new File(getClass().getResource("/medialab/").toString().split(":")[1] + "rounds.txt");
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String[] arr = reader.lines().toArray(String[]::new);
			StringBuilder s = new StringBuilder ();
			for(String w: arr) {
				s.append(w + "\n");
			}
			roundsData = s.toString();
			reader.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			roundsData = "No rounds played yet";
		}
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/PopUp.fxml") );
		root = loader.load();
		PopUpController popUpController = loader.getController();
		popUpController.setScrollPane(roundsData);
		scene = new Scene(root);
		stage = new Stage();
		stage.setScene(scene);
		stage.show();
	}

//	public void load() {
//		FXMLLoader loader = new FXMLLoader(getClass().getResource("/PopUp.fxml") );
//		root = loader.load();
//		PopUpController popUpController = loader.getController();
//		popUpController.setScrollPane(roundsData);
//		scene = new Scene(root);
//		stage = new Stage();
//		stage.setScene(scene);
//		stage.show();
//	}
//	
	public void dictionary() throws IOException {
		StringBuilder dictData = new StringBuilder();
		List<String> words = gl.getWords();
		Integer size = words.size();
		Integer count1 = 0, count2 = 0 , count3 = 0;
		for(String w: words) {
			if(w.length() == 6)
				count1++;
			else if(w.length() >= 7 && w.length() <= 9)
				count2++;
			else
				count3++;
		}
		dictData.append("  6 letters words = " + count1 + " / " + size + "\n  7 to 9 letters words = " + count2 + " / " + size + 
				"\n  10 or more letters words = " + count3 + " / " + size);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/PopUp.fxml") );
		root = loader.load();
		PopUpController popUpController = loader.getController();
		popUpController.setScrollPane(dictData.toString() );
		scene = new Scene(root);
		stage = new Stage();
		stage.setScene(scene);
		stage.show();
	}

	
	public boolean validateFields(Character c, String pos) {
		if(c > 'Z' || c < 'A') {
			return false;
		}
		for(char i: pos.toCharArray() ) {
			if(i < 48 || i > 57) {
				return false;
			}
		}
		return true;
	}
	
	public boolean validatePos(Integer i) { 
		if(i < 0 || i >= gl.getWord().length() || posFound.contains(i) ) {
			return false;
		}
		return true;
	}
	
	public Integer isGameOver() {
		return gl.isGameOver();
	}
	
	public void drawLines() {
		for(Integer i = 0 ; i < gl.getWord().length() ; i++) {
			Line line = new Line(150 + i*25, 250, 150 + i*25 + 20, 250);
			charLabels.add(new Label() );
			charLabels.get(i).setTranslateX(155 + i*25);
			charLabels.get(i).setTranslateY(230);
			charLabels.get(i).setScaleX(2);
			charLabels.get(i).setScaleY(2);
			mainPane.getChildren().add(line);
			mainPane.getChildren().add(charLabels.get(i) );
		}
	}
	
	public void drawHangman(Integer isRightGuess, Pair<Integer, Character> guess) {
		if(isRightGuess == -1) {
			Integer wrongGuesses = gl.getWrongGuesses();
			if(wrongGuesses != 0) {
				unavailableChars.get(guess.X() ).add(guess.Y() );
				guessRes.setTextFill(Color.RED);
				guessRes.setText("		Wrong guess :/");
			}
			switch (wrongGuesses) {
		        case 0:
		        	Image img1 = new Image("/media/1.png");
		            imageView.setImage(img1);
		            break;
		        case 1:
		        	Image img2 = new Image("/media/2.png");
		            imageView.setImage(img2);
		            break;
		        case 2:
		        	Image img3 = new Image("/media/3.png");
		            imageView.setImage(img3);
		            break;
		        case 3:
		        	Image img4 = new Image("/media/4.png");
		            imageView.setImage(img4);
		            break;
		        case 4:
		        	Image img5 = new Image("/media/5.png");
		            imageView.setImage(img5);
		            break;
		        case 5:
		        	Image img6 = new Image("/media/6.png");
		            imageView.setImage(img6);
		            break;
		        case 6:
		        	Image img7 = new Image("/media/7.png");
		            imageView.setImage(img7);
		            break;
			}
		}
		else {
			posFound.add(guess.X() );
			guessRes.setTextFill(Color.GREEN);
			guessRes.setText("             Good job!");
			charLabels.get(guess.X() ).setText(guess.Y().toString() );
		}
		
		posField.clear();
		charField.clear();
		displayError("");
	}
	
	public void displayError(String text) {
		posField.clear();
		charField.clear();
		if(text != "")
			guessRes.setText("");
		if(text == null) {
			errorLabel.setText("Position has to be an integer \nfrom 1 to " + gl.getWord().length() +" and character field has to contain a character \nnot yet used");
			return;
		}
		errorLabel.setText(text);
	}
	
	public void displayName (String name) {
		nameLabel.setText("Hello " + name + "!");
	}
	
	public void setPath (String p) {
		this.path = p;
	}
	
	public void goToStartingScreen (ActionEvent e) throws IOException {
		root = FXMLLoader.load(getClass().getResource("/Main.fxml"));
		stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void create() throws IOException {	
		root = FXMLLoader.load(getClass().getResource("/CreatePopUpScene.fxml"));
		stage = new Stage();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void exit () {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Exit");
		alert.setContentText("Are you sure you want to exit?");
		
		if(alert.showAndWait().get() == ButtonType.OK) {
			( (Stage) mainPane.getScene().getWindow() ).close();
			System.out.println("You closed the game");
		}
	}
}
