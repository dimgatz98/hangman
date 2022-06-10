package application;

import java.util.*;
import java.io.*;
import org.json.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import application.Exceptions.*;

public class Requests {
	private static HttpURLConnection conn;
	
	public int makeRequest(String lib_id, String dictionary_id) throws JSONException, IOException {
		final String BASE_URL = "https://openlibrary.org/works/";
    	final String full_url = BASE_URL + lib_id;
    	Set<String> words = new HashSet<String>();
    	
    	System.out.println("LibraryID: " + lib_id + " / DictionaryID: " + dictionary_id);
    	
    	File dir = new File(getClass().getResource("").toString().split(":")[1] + "../medialab");
		System.out.println(getClass().getResource("").toString().split(":")[1] + "../medialab");
    	if(!dir.exists() ) {
    		dir.mkdirs();
    	}
    	final String path = getClass().getResource("/medialab").toString().split(":")[1] + "/hangman_" + dictionary_id + ".txt";
    	File file = new File(path);
		System.out.println(path);
    	if(file.exists() ) {
    		System.out.println("File already exists");
    		return 0;
    	}
    	
    	file.createNewFile();
		
		BufferedReader reader;
		String line;
		StringBuilder responseContent = new StringBuilder();
		try{
			URL url = new URL(full_url);
			conn = (HttpURLConnection) url.openConnection();
			
			// Request setup
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(7000);// 5000 milliseconds = 5 seconds
			conn.setReadTimeout(7000);
			
			// Test if the response from the server is successful
			int status = conn.getResponseCode();
			
			if (status >= 300) {
				reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				while ((line = reader.readLine()) != null) {
					responseContent.append(line);
				}
				reader.close();
			}
			else {
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((line = reader.readLine()) != null) {
					responseContent.append(line);
				}
				reader.close();
			}
			String[] words_arr = parse(responseContent.toString() );
			if(words_arr == null) {
				file.delete();
				return -1;
			}
			words = create_set(words_arr);
			System.out.println("Those are the words:\n" + words);
			if(words == null) {
				file.delete();
				return -1;
			}

			int ret = write_words_to_file(words, path);
			if(ret == -1) {
				file.delete();
				System.out.println("Something went wrong while writing the words");
				return -1;
			}
		}
		catch (SocketTimeoutException e) {
			file.delete();
			e.printStackTrace();
			return -2;
		}
		catch (MalformedURLException e) {
			file.delete();
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			file.delete();
			e.printStackTrace();
			return -1;
		}finally {
			conn.disconnect();
		}
		return words.size();
			
	}

	public static int write_words_to_file(Set<String> words, String path) {
		File dir = new File(path);
		if (!dir.exists()){
		    dir.mkdirs();
		}
		try {
			int count = 0;
	    	FileWriter myWriter = new FileWriter(path);
		    
			for(String word: words) {
				myWriter.write(word + "\n");
				count++;
			}
			myWriter.close();
			if(count == 0)
				return -1;
			
			return count;
		}
		catch (IOException e) {
			e.printStackTrace();
			return -1;
	    }
	}

	public static String[] parse(String responseBody) {
		try{
			String data = new JSONObject(responseBody).getJSONObject("description").getString("value");			
			String[] words = data.replaceAll("[^a-zA-Z ]", "").toUpperCase().split("\\s+");
			return words;
		}
		catch (JSONException e){
			e.printStackTrace();
			return null;
		}
	}

	public static Set<String> create_set(String[] words) {
		try {
			Set<String> set = new HashSet<String> ();
			double count = 0;
			for(String word: words){
				if(word.length() >= 6){
					count++;
					set.add(word);
				}
			}
	
			double count_with_nine = 0;
			for(String word: set) {
				if(word.length() < 6){
					throw new InvalidRangeException("All words' lengths should be at least 6");
				}
				if(word.length() >= 9)
					count_with_nine++;
			}
	
			if(count_with_nine / count < 0.2) {
				throw new UnbalancedException("At least 20% of the words should be of length 9 or higher");
			} 
	
			if(count < 20) {
				throw new UndersizeException("Not enough words in description");
			}
	
			return set;
		}
		catch (UndersizeException e) {
			e.printStackTrace();
			return null;
		}
		catch (InvalidRangeException e) {
			e.printStackTrace();
			return null;
		}
		catch (UnbalancedException e) {
			e.printStackTrace();
			return null;
		}
	}

}