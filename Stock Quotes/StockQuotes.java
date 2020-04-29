/* StockQuotes 
 * Program retrieves current stock prices
 * Obtains data from Yahoo Finance
 * Scrapes HTML and returns current quote
 */

package stock;

import java.net.URL;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

public class StockQuotes {
	public static void main(String[] args) throws Exception {
		Date date = new Date();
		Scanner sc = new Scanner(System.in);
		String symbol = "";
		double price;
		System.out.print("Enter stock symbol: ");
		symbol = sc.next();
	
		String link = "https://query1.finance.yahoo.com/v8/finance/chart/" + symbol + 
				"?region=US&lang=en-US&includePrePost=false&interval=2m&range=1d&corsDomain="
				+ "finance.yahoo.com&.tsrc=finance";
		URL url = new URL(link);
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		String inputLine;
		while((inputLine = in.readLine()) != null) {
			if(inputLine.contains("regularMarketPrice")) {
				break;
			}
		}
		in.close();
		sc.close();
	
		price = parse(inputLine);
		
		System.out.println(date);
		System.out.println("Current stock price: " + price);
	}
	
	public static double parse(String inputLine) {
		String delim = ",";
		String [] tokens = inputLine.split(delim);
		String tempString = null;
		
		for(int i = 0; i < tokens.length; i++) {
			if(tokens[i].contains("regularMarketPrice")) {
				 tempString = tokens[i];
			}
		}
		
		tempString = tempString.substring(21, tempString.length());
 
		return Double.parseDouble(tempString);
	}

}
