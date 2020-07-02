/* ParseJSON class
 * 
 * Parses HTML and JSON for end cursor
 * Parses JSON for query hash
 * Parses JSON for profile id number
 * Uses this information to generate URL for next JSON file
 */

package extractor;

public class ParseJSON {
	private String json;					// holds JSON
	private String end_cursor;	   			// holds end cursor string
	private String queryHash;				// holds end query hash
	private String profileId;				// holds id number
	private final String QUERYHASH = "44efc15d3c13342d02df0b5a9fa3d33f";	// hard coded
	private boolean hasNextPage = false; 	// default is false
	private String jsonURL = "https://www.instagram.com/graphql/query/?query_hash=";
	private String afterQueryHash = "&variables=%7B%22id%22%3A%22";
	private String afterProfileId = "%22%2C%22first%22%3A12%2C%22after%22%3A%22";
	private String afterEndCursor = "%3D%3D%22%7D";
	private String queryURL;
	
	ParseJSON() {
		
	}
	
	//extractQueryHash: Extracts next page hash from json -- needs to run length of program for each JSON
	void extractEndCursor(String file) throws Exception {	
		String delimiter = "\"end_cursor\":\"";
		
		json = file;
		
		String [] tokens = json.split(delimiter);

		end_cursor = tokens[1].substring(0,118);
	}
	
	// checkNextPage: Extracts boolean from json -- needs to run each time json is received
	void checkNextPage() throws Exception {
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
	void extractProfileId(String html) throws Exception {
		String delimiter1 = "\"profilePage_";
		String delimiter2 = "\"";
		
		String [] tokens1 = html.split(delimiter1);
		
		String [] tokens2 = tokens1[1].split(delimiter2);

		profileId = tokens2[0];
	}
	
	// extractQueryHash: Extracts query id/hash from .js file -- only run once (at beginning from js file)
	void extractQueryHash(String javascript) throws Exception {
		String delimiter1 = "pagination},queryId:\"";
		String delimiter2 = "\"";
			
		String [] tokens1 = javascript.split(delimiter1);
		
		String [] tokens2 = tokens1[1].split(delimiter2);
		
		queryHash = tokens2[0];	
				
	}
	
	// constructURL: Constructs url for next query -- last thing to be done
	void generateURL() { 
		queryURL = jsonURL + queryHash + afterQueryHash + profileId + afterProfileId + 
				end_cursor + afterEndCursor;
	}
	
	/* GETTERS */ 
	
	String getJSON() {
		return json;
	}
		
	String getEndCursor() {
		return end_cursor;
	}
		
	String getQueryHash() {
		return queryHash;
	}
		
	String getProfileId() {
		return profileId;
	}	
	
	// getQueryURL: Getter for generated URL
	String getQueryURL() {
		return queryURL;
	}
	
	// getNextPage: Returns boolean value of hasNextPage variable 
	boolean getNextPageValue() {
		return hasNextPage;
	}
	
	/* SETTERS */
	
	void setJSON(String value) {
		json = value;
	}
		
	void setEndCursor(String value) {
		end_cursor = value;
	}
		
	void setProfileId(String value) {
		profileId = value;
	}	
	
	// setQueryHash: Setter for query hash
	void setQueryHash(String containerValue) {
		queryHash = containerValue;
	}
	
	// getQueryURL: Getter for generated URL
	void setQueryURL(String value) {
		queryURL = value;
	}
	
	void setNextPage(boolean newValue) {
		hasNextPage = newValue;
	}
	
	// setHasNextPage: Setter for hasNextPage -- only used once in initialRequest method
	void setHasNextPage(boolean htmlNextPage) {
		if(htmlNextPage == true) {
			hasNextPage = true;
		}
	}
	
	

}
