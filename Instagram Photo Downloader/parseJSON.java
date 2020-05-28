/* ParseJSON class
 * 
 * Parses HTML and JSON for end cursor
 * Parses JSON for query hash
 * Parses JSON for profile id number
 * Uses this information to generate URL for next JSON file
 */

package extractor;

import java.io.File;
import java.util.Scanner;

public class ParseJSON {
	String json;					// holds JSON
	String html;					// holds HTML
	String end_cursor;	   			// holds end cursor string
	String queryHash;				// holds end query hash
	String profileId;				// holds id number
	final String QUERYHASH = "44efc15d3c13342d02df0b5a9fa3d33f";	// hard coded
	boolean hasNextPage = true; 	// default is true
	String jsonURL = "https://www.instagram.com/graphql/query/?query_hash=";
	String afterQueryHash = "&variables=%7B%22id%22%3A%22";
	String afterProfileId = "%22%2C%22first%22%3A12%2C%22after%22%3A%22";
	String afterEndCursor = "%3D%3D%22%7D";
	String queryURL;
	
	ParseJSON() {
		
	}
	
	//extractQueryHash: Extracts next page hash from json -- needs to run length of program for each JSON
	public void extractEndCursor(String file) throws Exception {	
		String delimiter = "\"end_cursor\":\"";
		
		json = file;
		
		String [] tokens = json.split(delimiter);

		end_cursor = tokens[1].substring(0,118);
	}
	
	// checkNextPage: Extracts boolean from json -- needs to run each time json is received
	public void checkNextPage() throws Exception {
		// No need to get json again b/c it is saved in a variable
		String temp;
		String delimiter1 = "\"has_next_page\":";
		String delimiter2 = ",";
		char trueOrFalse;
		
		String [] tokens1 = json.split(delimiter1);
		String [] tokens2 = tokens1[1].split(delimiter2);
		
		temp = tokens2[0];
		
		trueOrFalse = temp.charAt(0);
		if(trueOrFalse == 't') {
			hasNextPage = true;
		} else {
			hasNextPage = false;
		}
		
	}
	
	// extractProfileId: Extracts id number from HTML -- only run once (at beginning from HTML document)
	public void extractProfileId(String html) throws Exception {
		String delimiter1 = "\"profilePage_";
		String delimiter2 = "\"";
		
		String [] tokens1 = html.split(delimiter1);
		
		String [] tokens2 = tokens1[1].split(delimiter2);

		profileId = tokens2[0];
	}
	
	// extractQueryHash: Extracts query id/hash from .js file -- only run once (at beginning from js file)
	public void extractQueryHash(String javascript) throws Exception {
		//file: c764fdbac4ca.js
		File file = new File("jsfile");
		Scanner sc = new Scanner(file);
		String js = "";
		String delimiter1 = "o?void0:o.pagination},queryId:\"";
		String delimiter2 = "\"";
		
		while(sc.hasNext()) {
			js += sc.next();
		}
		
		String [] tokens1 = js.split(delimiter1);
		
		String [] tokens2 = tokens1[1].split(delimiter2);
		
		queryHash = tokens2[0];	
		sc.close();
	}
	
	// constructURL: Constructs url for next query -- last thing to be done
	public void generateURL() { 
		queryURL = jsonURL + queryHash + afterQueryHash + profileId + afterProfileId + 
				end_cursor + afterEndCursor;
	}
	
	// getQueryURL: Getter for generated URL
	public String getQueryURL() {
		return queryURL;
	}
	
	// setQueryHash: Setter for query hash
	public void setQueryHash(String containerValue) {
		queryHash = containerValue;
	}

}
