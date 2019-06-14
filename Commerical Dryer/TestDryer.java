/* Test Dryer class */

package dryer;

import java.util.Scanner;

public class DryerTest { 
	public static void main(String[] args) { 
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		Dryer dryer = new Dryer();
		String start, choice;
		
		// check card value 
		System.out.printf("Card balance: $%.2f\n", dryer.getCardValue());  
		System.out.println("Cycle time is $2.00 for 30 seconds. \n");
		
		// prompt user whether or not to start cycle -- YES or NO
		System.out.println("Start dryer cycle? Type YES or NO: ");
		start = input.next();
		start = start.toUpperCase();
		
		// starts cycle; gets current system time
		// creates start PLUS 30 seconds
		switch(start) {
		case "YES" : dryer.startCycle(); 	
					 System.out.printf("Balance: $%.2f\n", dryer.getCardValue());
					 dryer.endTime();				
				    	 System.out.print("\nSelect dryer temperature: \nLOW (1), MEDIUM (2), HIGH (3): ");
					 dryer.dryerTemperature(input.nextInt()); 	 break;
		case "NO"  : System.out.println("*** END OF PROGRAM ***");  	   	 break;	
		default    : System.out.println("Invalid response.");
					 System.out.println("\n*** END OF PROGRAM ***"); break;	
		}
		// start loop if dryer is ON (equals 1) 
		while(dryer.dryerStatus() == 1) {
			
		// loop to check if cycle is complete
		while(dryer.checkCycle() == true && dryer.getStatus() == 1) {	
				
		// display card balance
			System.out.printf("Balance: $%.2f\n", dryer.getCardValue());
				
		// add time to cycle
			System.out.println("\nWould you like to add an additional 5 seconds for 1.00?");
			System.out.print("Type YES or NO: ");
			choice = input.next();
			choice = choice.toUpperCase();
		
			switch(choice) {
				case "YES" : dryer.addCycleTime(); break;
				case "NO"  : dryer.checkAddTime(); break;
				default    : System.out.println("\n\n*** Invalid response ***");
				}
				
		// change dryer status based on time (ON or OFF)
			dryer.dryerStatus();
		}
		
	 }	
							
   }

}
