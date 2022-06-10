package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GameLogic {
	// private String path;
	private Integer correctGuesses;
	private Integer wrongGuesses;
	private Integer points;
	private String word;
	private List<String> words;
	private ArrayList<ArrayList<Pair<Float, Character> > > probs;
	
	public GameLogic (String path) throws IOException {
		try {
			// this.path = path;
			File file = new File(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			words = Arrays.asList(reader.lines().toArray(String[]::new) );
			reader.close();
			word = chooseWord(path);
			probs = computeProbs();
			correctGuesses = 0;
			wrongGuesses = 0;
			points = 0;
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getWords() {
		return this.words;
	}
	
	public Integer getTotalGuesses() {
		return this.wrongGuesses + this.correctGuesses;
	}
	
	public Integer getPoints() {
		return this.points;
	}
	
	public Integer getWordsNo() {
		return this.words.size();
	}
	
	public String getWord() {
		return this.word;
	}
	
	public Integer getCorrectGuesses() {
		return this.correctGuesses;
	}
	
	public Integer getWrongGuesses() {
		return this.wrongGuesses;
	}
	
	public String chooseWord (String path) {
		Random rand = new Random();
		int index = rand.nextInt(words.size() );
		// System.out.println("This is the index: " + index + "  / This is the random word: " + words.get(index) );
		return words.get(index);
	}
	
	public ArrayList<ArrayList<Pair<Float, Character> > > computeProbs() {
		ArrayList<ArrayList<Pair<Float, Character> > > probs = new ArrayList<ArrayList<Pair<Float, Character> > > ();
		Integer count = words.size();
		
		ArrayList<Pair<Float, Character> > temp;
		Character c;
		for(Integer j = 0 ; j < word.length() ; j++) {
			c = 'A';
			temp = new ArrayList<Pair<Float, Character> > ();
			for(Integer i = 0 ; i < 26 ; i++) {
				temp.add(new Pair<Float, Character> (0f,  c) );
				c++;
			}
			for(String w: words) {
				if(w.length() <= j) {
					continue;
				}
				temp.get(w.charAt(j) - 'A').setX(temp.get(w.charAt(j) - 'A').X() + (1f / count) ); 
			}
			Collections.sort(temp, new Compare() );
			probs.add(new ArrayList<Pair<Float, Character> > (temp) );
			temp.clear();
		}
		
		removeZeros(probs);
		
		return probs;
	}
	
	// every time the player guesses a letter update the game
	public int update (Pair<Integer, Character> guess) {
		// correct guess 
		if(word.charAt(guess.X() ) == guess.Y() ) {
			Float prob = getProb(probs.get(guess.X() ), guess.Y() );
			if(prob >= 0.6f) {
				points += 5;
			}
			else if(prob >= 0.4f) {
				points += 10;
			}
			else if(prob >= 0.25f) {
				points += 15;
			}
			else {
				points += 30;
			}
			
			deleteWordsWithoutChar(guess.Y(), guess.X() );
			probs = computeProbs();
			correctGuesses++;
			return 1;
		}
		
		// wrong guess
		wrongGuesses++;
		deleteWordsWithChar(guess.Y(), guess.X() );
		probs = computeProbs();
		if(points - 15 >= 0)
			points -= 15;
		return -1;
	}
	
	public ArrayList<ArrayList<Pair<Float, Character> > > getProbs() { 
		return this.probs;
	}

	// delete words without character c at position ind
	public void deleteWordsWithoutChar(Character c, Integer ind) {
		List<Integer> toRemove = new ArrayList<Integer> ();
		for(Integer i = 0 ; i < words.size() ; i++) {
			if(words.get(i).length() <= ind)
				continue;
			
			if(words.get(i).charAt(ind) != c) {
				toRemove.add(i);
			}
		}
		Collections.reverse(toRemove);
		for(Integer i: toRemove) {
			words.remove(i);
		}
	}
	
	// delete words with character c at position ind
	public void deleteWordsWithChar(Character c, Integer ind) {
		List<Integer> toRemove = new ArrayList<Integer> ();
		for(Integer i = 0 ; i < words.size() ; i++) {
			if(words.get(i).length() <= ind)
				continue;
			
			if(words.get(i).charAt(ind) == c) {
				toRemove.add(i);
			}
		}
		Collections.reverse(toRemove);
		for(Integer i: toRemove) {
			words.remove(i);
		}
	}
	
	// every time the player guesses a letter check whether the game is over
	public int isGameOver() {
		// won
		if(correctGuesses == word.length() )
			return 1;
		
		// lost
		if(wrongGuesses == 6)
			return -1;
		
		// game continues
		return 0;
	}
	
	public static Float getProb(ArrayList<Pair<Float, Character> > a, Character b) {
		for(Pair<Float, Character> p: a) {
			if(p.Y() == b) {
				return p.X();
			}
		}
		return -1f;
	}
	
	public static void removeZeros(ArrayList<ArrayList<Pair<Float, Character> > > probs) {
		// remove letters with no occurrences in dictionary
		ArrayList<Pair<Float, Character> > temp;
		for(ArrayList<Pair<Float, Character> > a: probs) {
			temp = new ArrayList<Pair<Float, Character> > ();
			for(Pair<Float, Character> p: a) {
				if(p.X() == 0f) {
					continue;
				}
				temp.add(p);
			}
			a.clear();
			a.addAll(temp);
		}
	}
	
	public static class Compare implements Comparator<Pair<Float, Character> > {
	    @Override
		public int compare(Pair<Float, Character> a, Pair<Float, Character> b) {
	        return b.X().compareTo(a.X() );
	    }
	}

}
