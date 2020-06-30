/* Instagram Photo Downloader - Command Line version
 * Program downloads photos from Instagram profiles
 * Reads and extracts images from HTML document
 * Extracts relevent information from HTML and JS files which is then used to create URL next page (JSON) 
 * Reads and extracts from JSON 
 * 
 * If all images are not saved try downloading at a lower resolution
 */

package extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class PhotoExtractor {
	static ArrayDeque <String> htmlLinks = new ArrayDeque<>();
	static ArrayDeque <String> jsonLinks = new ArrayDeque<>();
	static ArrayDeque<String> queue = new ArrayDeque<>();
	static ArrayDeque<String> finalQ = new ArrayDeque<>();
	static boolean htmlNextPage = false;		// default is false
	static int counter = 1;
	static int num = 1;
	static int selection; 
	static CookieManager cm = new CookieManager();
	static String instaURL;
	static String c = "";
	static String tempLine = "";
	static String page;
	static String filename = "";
	static String containerValue = "";	
	static String jsFile = ""; 
	static String resolution;
	static String path = "";

	public static void main(String[] args) throws Exception {
		ParseJSON parseJSON = new ParseJSON();
		Dots dots = new Dots();
		Scanner sc = new Scanner(System.in);
		programTitle();
		getProfile(sc);
		getPath(sc);
		checkPath(sc);
		getFilename(sc);
		resolutionSize(sc);
		sc.close();					// Close scanner
		initialRequest(parseJSON, dots);		// Initial GET request -- HTML files
		while(parseJSON.getNextPageValue() == true) {	// Subsequent GET requests -- JSON files
			getRequest(parseJSON);
			readLinks(); 										
			parseJSON.extractEndCursor(tempLine);
			parseJSON.checkNextPage();
			System.out.print("GENERATING URL...");	
			parseJSON.generateURL();
			dots.displayDots();
			System.out.print("URL GENERATED.\n");			
			Thread.sleep(2000);
			counter++;
			System.out.println("PAGE: " + counter);
		}
	}
	
	public static void programTitle() {
		System.out.println("*-----------------------------------------*");
		System.out.println("|                                         |");
		System.out.println("|       Instagram Photo Downloader        |");
		System.out.println("|                                         |");
		System.out.println("*-----------------------------------------*");
	}
	
	public static void getProfile(Scanner sc) {
		System.out.print("\nEnter IG URL: ");
		instaURL = sc.nextLine();
	}
	
	public static void getPath(Scanner sc) {
		System.out.print("Enter the complete file path you wish to save images in: ");
		path = sc.nextLine();
	}
	
	public static void getFilename(Scanner sc) {
		System.out.print("Enter the file name you wish to use: ");
		filename = sc.nextLine();
	}
	
	// checkFilePath: Checks if the file path exists
	public static void checkPath(Scanner sc) {
		String os = System.getProperty("os.name");		// gets operating system name for use with path
		boolean pathTest = true; 
		while(pathTest) {
			File filePath = new File(path);
			if(filePath.exists()) { 					// check if it ends with a forward slash
				if(os.contains("Mac")) {	    		// UNIX
					if(!path.endsWith("/")) {			// check if path ends with a forward slash
						path = path + "/";
					} 
				} else if(os.contains("Windows")) {		// Windows
					if(!path.endsWith("\\")) {   		// check if it ends with a backslash
						path = path + "\\";
					}
				}
				pathTest = false;
			} else {
				System.out.print("Path does not exist, try again: ");
				path = sc.nextLine();
			}
		}
	}
	
	// resolutionSize: Allows user to select image resolution size
	public static void resolutionSize(Scanner sc) {
		while(selection != 1 || selection != 2) {
			try {
				System.out.println("\nChoose image resolution size:");
				System.out.println("-----------------------------\n");
				System.out.println("1 - High");
				System.out.println("2 - Medium\n");
				System.out.print("Selection: ");
				selection = sc.nextInt();
				if(selection == 1) {
					resolution = "1080x1080";
					break;
				} else if(selection == 2) {
					resolution = "s640x640";
					break;
				}
				System.out.println("Please enter a valid selection.");
				continue;
			} catch(InputMismatchException ex) {
				System.out.println("Please enter a valid selection.");
			}
			sc.next();
		}	
	}
	
	// initialRequest: Extracts images from HTML document; retrieves required information to generate query URL for subsequent requests
	public static void initialRequest(ParseJSON parseJSON, Dots dots) throws Exception {
		URL	html = new URL(instaURL);
		URL jsPage = null;
		CookieHandler.setDefault(cm);
		HttpURLConnection conn = (HttpURLConnection) html.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(html.openStream()));
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Cookies", c);
		conn.addRequestProperty("User-Agent", "Mozilla/5.0");
		conn.getContent();
		// ## COOKIES ## 
		cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieStore cs = cm.getCookieStore();
		List<HttpCookie> cookieList = cs.getCookies(); 
		// add cookies
		for(HttpCookie cook: cookieList) {
			if(cook.toString().startsWith("ig")) {
				c += cook.getName() + "=" + cook.getValue();
				break;
			}
			c += cook.getName() + "=" + cook.getValue() + "; ";

		} 
		// Get profile ID, end cursor, and query hash to create URL for next page
		readFileHTML(html);
		// page variable contains html document
		parseURL();	
		readLinks();
		// Check if there is a next page after HTML document
		htmlNextPage();
		if(htmlNextPage == true) {
			parseJSON.setHasNextPage(htmlNextPage);
			// Start extraction to generate URL for next page (JSON)
			parseJSON.extractEndCursor(page);	
			parseJSON.extractProfileId(page);
			extractPageContainer(parseJSON);
			jsPage = new URL(jsFile);
			readJS(jsPage);
			parseJSON.extractQueryHash(page);
			// page variable now contains .js file
			System.out.print("GENERATING URL...");
			dots.displayDots();
			parseJSON.generateURL();
			System.out.print("URL GENERATED.\n");
			System.out.println(parseJSON.getQueryURL());
			counter++;
			System.out.println("PAGE: " + counter);
			reader.close();
			Thread.sleep(2000);
		}
		
	}
	
	// htmlNextPage: Checks if HTML document has a subsequent page 
	public static void htmlNextPage() {
		String findText = "\"has_next_page\":true";
		// If findText contents are in variable that mean there is a next page
		htmlNextPage = page.contains(findText);
	}
	
	// extractContainerPage: Parse HTML for .js file containing query hash -- found in HTML document
	public static void extractPageContainer(ParseJSON parseJSON) throws Exception {
		String delimiter1 = "\"/static/bundles/metro/ProfilePageContainer.js/";
		String delimiter2 = ".js\"";
			
		String [] tokens1 = page.split(delimiter1);
		String [] tokens2 = tokens1[1].split(delimiter2);
			
		containerValue = tokens2[0];
	
		jsFile = "https://instagram.com/static/bundles/metro/ProfilePageContainer.js/" + containerValue + ".js";
	}
		
	// readJS: Reads Javascript file (.js) to string variable
	public static void readJS(URL url) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line = "";
		page = "";
		// Looks for JPG and HTTP prefix and suffix and saves it file
		while ((line = reader.readLine()) != null) {
			page += line;
		}
		reader.close();
	}
			
	// getRequest: GET request for JSON file 
	public static void getRequest(ParseJSON parseJSON) throws Exception {
		instaURL = parseJSON.getQueryURL();
		URL queryURL = new URL(instaURL);
		BufferedReader reader = new BufferedReader(new InputStreamReader(queryURL.openStream()));
		HttpURLConnection conn2 = (HttpURLConnection) queryURL.openConnection();
		conn2.setRequestMethod("GET");
		conn2.setRequestProperty("Cookie", c);
		conn2.getContent();
		readStream(reader);
		readFileJSON(queryURL); 		// read JSON document 
		reader.close();
	}

	// readFileJSON: Reads JSON and parses for .jpg links; adds downloadable links to queue data structure 
	public static void readFileJSON(URL url) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line1 = "";
		String delimiter1 = "[{}]";
		String delimiter2 = "https:";
		String delimiter3 = "\"";
		page = "";
		// Reads JSON and adds it to string variable
		while ((line1 = reader.readLine()) != null) {
			page += line1;
		}
			    
		String [] tokens1 = page.split(delimiter1);
		 // finds all .jpg links in JSON document and adds them to a queue
		for(int i = 0; i < tokens1.length; i++) {
			if(tokens1[i].contains("https") && tokens1[i].contains("jpg")) {
				if(tokens1[i].contains("src") && tokens1[i].contains("https") && tokens1[i].contains(resolution)) {
					queue.add(tokens1[i]);
				}
			}
		} 
				 
		int size = queue.size();
		int i = 0;
		// Loop through queue (as a stack) using delimiters to fix url
		// Iterator checks if link is already in data structure -- adds it to queue if it isnt
		while(i < size) {
			String [] tokens3 = queue.pop().split(delimiter2);
			String line = tokens3[1];
			String [] tokens4 = line.split(delimiter3);
			String link = "https:" + tokens4[0];
				if(jsonLinks.isEmpty()) {
					jsonLinks.add(link);
					i++;
					continue;
				}
			Iterator<String> iterator = jsonLinks.iterator();
			while(iterator.hasNext()) {
				String temp = iterator.next();
				if(link.contains(temp)) {
					break;
				} else {
					jsonLinks.push(link);
					break;
				}
			}
			i++;	
		}
		System.out.println("TOTAL LINKS: " + jsonLinks.size());
		reader.close();
	}
			
	// readFileHTML: Reads HTML and adds downloadable links to queue data structure
	public static void readFileHTML(URL url) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line = "";
		String delimiter1 = "[{}]";
		String delimiter2 = "url\":\"";
		String delimiter3 = "\"";
		page = "";
		
		// Reads HTML document -- adds it to string
		while ((line = reader.readLine()) != null) {
			page += line;
		}
		
		String [] tokens1 = page.split(delimiter1);
			
		// Finds all .jpg links in HTML document with 1080x1080 resolution and adds them to a queue
		for(int i = 0; i < tokens1.length; i++) {
			if(tokens1[i].contains("https") && tokens1[i].contains("jpg")) {
				if(selection == 1) {
					if(tokens1[i].contains("https") && tokens1[i].contains(resolution)) {
						queue.add(tokens1[i]);
					}
				} else {
					delimiter2 = "https:";
					delimiter3 = "\"";
					if(tokens1[i].contains("src") && tokens1[i].contains("https") && tokens1[i].contains(resolution)) {
						queue.add(tokens1[i]);
					}
				}
			}		 
		}
		
		int size = queue.size();
		int i = 0;
		// Loop through queue (as a stack) using delimiters to fix url
		// iterator checks if link is already in data structure -- adds it to queue if it isnt
		while(i < size) {
			String [] tokens3 = queue.pop().split(delimiter2);
			line = tokens3[1];
			String [] tokens4 = line.split(delimiter3);
			String link = tokens4[0];
			if(finalQ.isEmpty()) {
				finalQ.add(link);
				i++;
				continue;
			}
			Iterator<String> iterator = finalQ.iterator();
			while(iterator.hasNext()) {
				String temp = iterator.next();
				if(link.contains(temp)) {
					break;
				} else {
					finalQ.push(link);
					break;
				}
			}
			i++;	
		}
	}
	
	// parseURL: Adds removes Unicode value and replaces with it "&"; adds link to data structure -- FOR HTML DOCUMENT LINKS ONLY !!!!
	public static void parseURL() throws Exception {
		String delimeter4 = "[\"]";
		String unicode = "\\\\u0026";
		String link = "";
		while(!finalQ.isEmpty()) {
			String [] tokens = finalQ.pop().split(delimeter4);
			for(int j = 0; j < tokens.length; j++) {
				link = tokens[j];
				link = link.replaceAll(unicode, "&");
				if(selection == 1) {
					htmlLinks.push(link);
				} else {
					htmlLinks.push("http:" + link);
				}
				
			}	
		}
		System.out.println("TOTAL LINKS: " + htmlLinks.size());			
	}	
		
	// readLinks: Reads links from array and outputs as a JPG file
	public static void readLinks() throws IOException, URISyntaxException {			
		System.out.println("\n*** LINKS ***\n");
		// read links
		try {
			if(!htmlLinks.isEmpty()) {		// reads data structures -- HTML
				while(!htmlLinks.isEmpty()) {
					URL url = new URL(htmlLinks.peek());	
					System.out.println("READ: " + htmlLinks.pop());
					InputStream istream = url.openStream();					
					FileOutputStream out = new FileOutputStream(new File(path + Integer.toString(num) + ".jpg"));
					istream.transferTo(out);
					num++;
					out.close();
				}
			} else {					 
				while(!jsonLinks.isEmpty()) {
					URL url = new URL(jsonLinks.peek());	
					System.out.println("READ: " + jsonLinks.pop());
					InputStream istream = url.openStream();
					FileOutputStream out = new FileOutputStream(new File(path + Integer.toString(num) + ".jpg"));
					istream.transferTo(out);
					num++;
					out.close();
				}
			}
		} catch (IOException ex) {
			System.out.println("ERROR");
			ex.printStackTrace();
		}
		System.out.println("\nPAGE COMPLETE\n");	
	}	
	
	// readStream: Reads open stream
	public static void readStream(BufferedReader stream) throws Exception {
		String line = "";
		tempLine = "";		// clear variable value
		while((line = stream.readLine()) != null) {
			tempLine += line;
		}
		stream.close();
	}
		
}
