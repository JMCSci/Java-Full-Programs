/* Project: Java Bank Management System v.1
 * Created by: Jason Moreau
 * Utilizes methods and constructors from Account, CheckingAccount, SavingsAccount and Transaction classes
 * The PIN number for new accounts is auto generated in consecutive order for ease of use
 * Initial balance for new accounts is $0
 * ATM prints "receipt" to text file and to the screen
 * ATM loop never ends; prompts a new user to create a new account or enter their "account" (ID)  
 * Program has been debugged and will not crash due to logic errors 
 */


package bank;

import java.util.Scanner;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.InputMismatchException;

public class Bank {
	public static void main(String[] args) throws Exception {
	/* Scanner "resource leak" message */
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		Account atm = new Account();
		Account date = new Account();
		Boolean condition = true;
		@SuppressWarnings("resource")
		/* prints a "receipt" for the customer */
		PrintStream receipt = new PrintStream("receipt.txt");	// relative file name, not absolute 
		int idCount = 0, option, tempID, PIN = 0, tempPIN;
		
	/* create accounts (ID array) */
	 ArrayList <Integer> bankAccountsID = new ArrayList<>();	
	
	/* Checking and Savings array (accounts) */
	 ArrayList <Double> checkingA = new ArrayList<>();
	 ArrayList <Double> savingsA = new ArrayList<>();
	 
	/* Set interest rate */
	 atm.setInterestRate(1.8);
	
	 while(true) {
		System.out.println("\n*** Welcome to Java Bank Management System ***\n");
		
		/* Create new account */ 
		System.out.println("1. Create a new account");
		System.out.println("2. Existing account holder");
		System.out.print("\nSelection: ");
		
		/* Selection must be a number */ 
		while(!input.hasNextInt()) {
			System.out.println("Selection must be a number. Please try again.\n");
			System.out.print("Selection: ");
			input.next();
		}
		
		option = input.nextInt();	
		
		/* Value must be number between 1 and 2 */
		while(option < 1 || option > 2) {
			try {
			System.out.println("Invalid selection.\n");
			System.out.print("Selection: ");
			option = input.nextInt();
			}
			catch(InputMismatchException ex) {
				System.out.println("Selection must be a number.\n");
				input.next();
			}
		}
		
		if(option == 1) {
			bankAccountsID.add(idCount);
			/* create checking and savings account values in ArrayList */
			checkingA.add(atm.getBalance());				
			savingsA.add(atm.getBalance());
			System.out.print("\nEnter your first name: ");
			atm.setFirstName(input.next());
			/* Automatically capitalize first letter in first name */ 
			atm.setFirstName(atm.capitalizeName(atm.getFirstName()));
			System.out.print("Enter your last name: ");
			atm.setLastName(input.next()); 
			/* Automatically capitalize first letter in last name */
			atm.setLastName(atm.capitalizeName(atm.getLastName()));	
			System.out.println("\nYour new ID number is: " + bankAccountsID.get(idCount)); 
			atm.setPIN(1234 + idCount);
			System.out.println("Your new PIN number is: " + atm.getPIN(idCount));
			idCount++;
			
			System.out.print("\nEnter an ID: ");
		/* Existing account holder 
		 * prompt user to enter ID number; if number does match "account #", asks user to enter correct number 
	 	 */ 
			
		} else if (option == 2) {
			// If no account on file, user must create one
			if(bankAccountsID.size() == 0) {
				System.out.println("No accounts on file. Please create a new account."); continue;
			}
			System.out.println("\nEnter an ID: ");
			}
		
		/* checks if user input contains letters or is a negative number */ 			
			atm.setId(checkNonNegativeID());
		/* uses instance variable in Account class as tempID Bank class */
			tempID = atm.getId();
				
		/* check if user "account" is in "system" (in ID array) */
		atm.setId(placeholderCheckID(tempID, bankAccountsID));
		
		/* user must enter PIN */
		System.out.println("\nEnter your 4-digit PIN: ");
		
		/* temporary store user entered PIN in Bank class */
		do {	
		try {
		PIN = input.nextInt();
		condition = false;
		}
		catch (InputMismatchException ex) {
			System.out.println("Your PIN cannot include letters. Please try again.\n");
			System.out.println("Enter your 4-digit PIN: ");
			input.next();
		}
		   
		} while(condition);
	
		/* check PIN; prompt user if incorrect 
		 * holds PIN from Account class (ArrayList) in temporary variable
		 */ 
		tempPIN = atm.getPIN(atm.getId());
		placeholderCheckPIN(PIN, tempPIN);
		tempPIN = 0; 									// not necessary -- removes PIN from memory (customer safety)
		PIN = 0;										// not necessary -- removes PIN from memory (customer safety)
		
		System.out.println("\n*** Select Account Type ***");
		System.out.println("1. Checking");
		System.out.println("2. Savings");
		System.out.println("3. Account Summary\n");
		System.out.print("Selection: ");
		
		/* User must enter a number */
		while(!input.hasNextInt()) {
			System.out.println("Selection must be a valid number.\n");
			System.out.print("Selection: ");  
				input.next();
			}
	
		option = input.nextInt();
		
		/* User must enter a number between 1 and 3 */
		while(option < 1 || option > 3) {
			try {
			System.out.println("Invalid selection.\n");
			System.out.print("Selection: ");
			option = input.nextInt();
			}
			catch(InputMismatchException ex) {
				System.out.println("Selection must be a valid number.\n");
				input.next();
			}
		}
		// ### CHECKING ###
		
		int selection = option;
		
		if(option == 1) {
			CheckingAccount checking = new CheckingAccount();	// checking account object
			Account account = new Account();					// account object
			account.setBalance(checkingA.get(atm.getId()));			// set balance in Account class
			System.out.println("\n*** Main Menu ***");
			System.out.println("1. Check balance");
			System.out.println("2. Withdraw");
			System.out.println("3. Deposit");
			System.out.println("4. Transfer");
			System.out.println("5. Exit");
			System.out.print("\nSelect an option: ");
			
			/* User must enter a number between 1 and 4 */
			while(!input.hasNextInt()) {
				System.out.println("Selection must be a valid number.\n");
				System.out.print("Selection: ");  
					input.next();
				}
		
			option = input.nextInt();
			
			/* User must enter a number between 1 and 4 */
			while(option < 1 || option > 5) {
				try {
				System.out.println("Invalid selection.\n");
				System.out.print("Selection: ");
				option = input.nextInt();
				}
				catch(InputMismatchException ex) {
					System.out.println("Selection must be a valid number.\n");
					input.next();
				}
				
			}
			switch(option) {
			/* get balance from Checking Array in Bank  */
			case 1: System.out.println("Checking balance: $" + checkingA.get(atm.getId())); continue;
			/* user input for withdrawal -- deducts from checking array element (Bank) 
			 * outputs transaction type and other details 
			 */
			case 2: System.out.println("Enter an amount to withdraw: ");
			/* User input must be nonnegative */
			/* withdrawInput variable holds user withdrawal amount */
			/* User input must be a number */
					double withdrawalInput = checkNonNegativeWithdrawal();
					checkingA.add(atm.getId(), account.withdraw(withdrawalInput,selection));
					System.out.println(checking.overDrawn(account.getBalance()));
					System.out.println(account.toString());
					receipt.println(account.toString());
					System.out.println("Transaction complete: " + date.dateCreated()); continue;
			/* user input for deposit -- adds amount to checking array element (Bank) 
			 * check user input to ensure number is positive
			 * depositInput variable holds user deposit amount 
			 * outputs transaction type and other details	
			 * prints transaction method toString in Account class (Transaction class)
			 */ 
			case 3: System.out.println("Enter an amount to deposit: ");
			/* User input must be a number */
					double depositInput = checkNonNegativeDeposit();
					checkingA.add(atm.getId(), account.deposit(depositInput, selection));
					System.out.println(account.toString());
					receipt.println(account.toString());
					System.out.println("Transaction complete: " + date.dateCreated()); continue;
					
			case 4: System.out.println("Enter amount to transfer to Savings account: ");
			/* User input must be a number */
					double transferInput = checkNonNegativeTransfer();
					// checks if transfer would cause a negative balance
					if(checkingA.get(atm.getId()) - transferInput < 0) { 
						System.out.println("\nInsufficent funds for transfer.\n");
						System.out.println("Transaction complete: " + date.dateCreated() + "\n"); break;
					} else {
					// temporary variable to hold savings account value
					double tempSavings = savingsA.get(atm.getId());
					// returns balance of checking account after transfer calculation
					checkingA.add(atm.getId(), account.transfer(transferInput, selection));
					// checks if checking account is overdrawn
					System.out.println(checking.overDrawn(account.getBalance()));
					// adds transfer amount to tempSavings variable
					tempSavings =  transferInput + tempSavings;
					// changes value of savings in ArrayList
					savingsA.add(atm.getId(), tempSavings);
					System.out.println(account.toString());
					receipt.println(account.toString());
					}
			case 5: System.out.println("\nGoodbye"); continue;
			default: System.out.println("Invalid selection."); continue;
			}	
		
		// ### SAVINGS ###	
			
		} else if (option == 2) {
			Account account = new Account();
			account.setBalance(savingsA.get(atm.getId()));
			System.out.println("\n*** Main Menu ***");
			System.out.println("1. Check balance");
			System.out.println("2. Withdraw");
			System.out.println("3. Deposit");
			System.out.println("4. Transfer");
			System.out.println("5. Exit");
			System.out.print("\nSelect an option: ");
	
			while(!input.hasNextInt()) {
				System.out.println("Selection must be a valid number.\n");
				System.out.print("Selection: ");  
					input.next();
				}
		
			option = input.nextInt();
			
			/* User must enter a number between 1 and 4 */
			while(option < 1 || option > 5) {
				try {
				System.out.println("Invalid selection.\n");
				System.out.print("Selection: ");
				option = input.nextInt();
				}
				catch(InputMismatchException ex) {
					System.out.println("Selection must be a valid number.\n");
					input.next();
				}
				
			}
			switch(option) {
			case 1: System.out.println("Savings balance: $" + savingsA.get(atm.getId()));
					receipt.println("Savings balance: $" + savingsA.get(atm.getId()));
					System.out.println(date.dateCreated()); continue;
			/* withdrawInput variable holds user withdrawal amount
			 * since savings accounts CANNOT be overdrawn, conditional statement checks before proceeding
			 * if withdrawal transaction would cause overdrawn account, transaction ends
			 */
			case 2: System.out.print("Enter an amount to withdraw: ");
			/* User input must be a number */
					double withdrawalInput = checkNonNegativeWithdrawal();
					if(savingsA.get(atm.getId()) - withdrawalInput < 0) { 
						System.out.println("\nInsufficent funds for withdrawal.\n");
						System.out.println("Transaction complete: " + date.dateCreated() + "\n"); break;
					} else {
					savingsA.add(atm.getId(), account.withdraw(withdrawalInput, selection));
					System.out.println(account.toString());
					receipt.println(account.toString());
					System.out.println("Transaction complete: " + date.dateCreated() + "\n"); continue;
					}
			/* asks user for deposit amount
			 * depositInput variable holds user deposit amount
			 * prints transaction method toString in Account class (Transaction class)
			 */
			case 3: System.out.println("Enter an amount to deposit: ");
			/* User input must be a number */
					double depositInput = checkNonNegativeWithdrawal();
					savingsA.add(atm.getId(), account.deposit(depositInput, selection));
					System.out.println(account.toString());
					receipt.println(account.toString());
					System.out.println("Transaction complete: " + date.dateCreated()+ "\n"); continue;
			case 4: System.out.println("Enter amount to transfer to Checking account: ");
			/* User input must be a number */
					double transferInput = checkNonNegativeTransfer();
					// checks if savings account is overdrawn
					if(savingsA.get(atm.getId()) - transferInput < 0) { 
						System.out.println("\nInsufficent funds for transfer.\n");
						System.out.println("Transaction complete: " + date.dateCreated() + "\n"); break;
					} else {
					// temporary variable to hold checking account value
					double tempChecking = checkingA.get(atm.getId());
					// returns balance of savings account after transfer calculation
					savingsA.add(atm.getId(), account.transfer(transferInput, selection));
					// adds transfer amount to tempSavings variable
					tempChecking =  transferInput + tempChecking;
					// changes value of savings in ArrayList
					checkingA.add(atm.getId(), tempChecking);
					System.out.println(account.toString());
					receipt.println(account.toString());
					System.out.println("Transaction complete: " + date.dateCreated()+ "\n"); continue;
					} 
			case 5: System.out.println("\nGoodbye\n"); continue;
			default:System.out.println("Invalid selection."); continue; 
		   }	
		  
		  }
		
		/* output account summary to screen and text file as a "receipt"  */
		else if(option == 3) {
			System.out.println("\n*** Summary for Account # " + atm.getId() + " ***");
			System.out.println("\nAccount holder name: " + atm.getFirstName() + " " + atm.getLastName());
			System.out.println("Checking balance: $" +  checkingA.get(atm.getId()));
			System.out.println("Savings balance: $" + savingsA.get(atm.getId()));
			System.out.println("Annual interest rate %" + atm.annualInterestRate());
			System.out.println("Monthly interest rate %" + atm.getMonthlyInterestRate());
			receipt.println("\n*** Summary for Account # " + atm.getId() + " ***");
			receipt.println("\nAccount holder name: " + atm.getFirstName() + " " + atm.getLastName());
			receipt.println("Checking balance: $" +  checkingA.get(atm.getId()));
			receipt.println("Savings balance: $" + savingsA.get(atm.getId()));
			receipt.println("Annual interest rate %" + atm.annualInterestRate());
			receipt.println("Monthly interest rate %" + atm.getMonthlyInterestRate());
		}
		
	  } 
	 
	}
	
	/* method makes sure number withdrawal amount is nonnegative and doesn't contain letters */
	public static double checkNonNegativeWithdrawal() {
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		Boolean condition = true;
		double temp = 0;
		while(condition == true) {
		try {
			temp = input.nextDouble();
			if(temp < 0) {
				System.out.println("Withdrawal amount must be a nonnegative number.");
				System.out.print("\nEnter amount to withdraw: ");
				continue;
			}
			condition = false;
		}
			catch(InputMismatchException ex) {
				System.out.println("Withdrawal amount cannot contain letters.");
				System.out.print("\nEnter amount to withdraw: ");
				input.next();
			}
		}
		return temp;
	}
	
	/* method makes sure number deposit amount is nonnegative and doesn't contain letters */
	public static double checkNonNegativeDeposit() {
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		Boolean condition = true;
		double temp = 0;
		while(condition == true) {
		try {
			temp = input.nextDouble();
			if(temp < 0) {
				System.out.println("Deposit amount must be a nonnegative number.");
				System.out.print("\nEnter amount to deposit: ");
				continue;
			}
			condition = false;
		}
			catch(InputMismatchException ex) {
				System.out.println("Deposit amount cannot contain letters.");
				System.out.print("\nEnter amount to deposit: ");
				input.next();
			}
		}
		return temp;
	}
	
	
	/* method makes sure number deposit amount is nonnegative and doesn't contain letters */
	public static double checkNonNegativeTransfer() {
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		Boolean condition = true;
		double temp = 0;
		while(condition == true) {
		try {
			temp = input.nextDouble();
			if(temp < 0) {
				System.out.println("Transfer amount must be a nonnegative number.");
				System.out.print("\nEnter amount to transfer: ");
				continue;
			}
			condition = false;
		}
			catch(InputMismatchException ex) {
				System.out.println("Transfer amount cannot contain letters.");
				System.out.print("\nEnter amount to transfer: ");
				input.next();
			}
		}
		return temp;
	}
	
	/* method makes sure ID is nonnegative and doesn't contain letters */
	public static int checkNonNegativeID() {
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		Boolean condition = true;
		int temp = 0;
		while(condition == true) {
		try {
			temp = input.nextInt();
			if(temp < 0) {
				System.out.println("ID must be a nonnegative number.");
				System.out.print("\nEnter an ID: ");
				continue;
			}
			condition = false;
		}
			catch(InputMismatchException ex) {
				System.out.println("ID cannot contain letters ");
				System.out.print("\nEnter an ID: ");
				input.next();
			}
		}
		return temp;
	}
	
	/* method checks if user "account" is in "system" (in ID array) 
	 * returns correct ID to main
	 */
	public static int placeholderCheckID(int tempID, ArrayList <Integer> bankAccountsID) {
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		/* checks if tempID is larger than ArrayList */
		if(tempID >= bankAccountsID.size())
		/* if tempID is larger than ArrayList or is less than 0, loop is initiated */
		while(tempID >= bankAccountsID.size() || tempID < 0) {
			System.out.println("\nID is incorrect. Please enter correct number.\n"); 
			System.out.print("Enter an ID: ");
			/* Input cannot contain letters */
			while(input.hasNextInt() == false) {
				try {
					tempID = input.nextInt();
				}
				catch(InputMismatchException ex) {
					System.out.println("\nID cannot contain letters.\n");
					System.out.print("Enter an ID: ");
					input.next();
				}
			}
			tempID = input.nextInt();
		  }
			return tempID;
	}
	
	/* method checks if PIN number matches PIN number stored in ArrayList */
	public static void placeholderCheckPIN(int PIN, int tempPIN) {
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		while(PIN != tempPIN) {
			System.out.println("PIN is incorrect. Please try again.\n");
			try {
			System.out.println("Enter your 4-digit PIN: ");
			PIN = input.nextInt();
			}
			catch(InputMismatchException ex) {
				System.out.println("Your PIN cannot include characters.\n");
				input.next();
			}
		}
			
	}
			
}
