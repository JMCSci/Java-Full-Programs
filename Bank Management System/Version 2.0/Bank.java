/* Project: Java Bank Management System v.2
 * Created by: Jason Moreau
 * Full database integration with MySQL
 * Maximum number of accounts (and PIN) is 10000; can be increased if necessary via MySQL
 * Program allows customer to check account balance, account summary, make deposits, withdrawals and transfers
 * Utilizes methods and constructors from Account, BankSQL, CheckingAccount, SavingsAccount and Transaction classes
 * PIN number is auto generated for new accounts
 * Initial balance for new accounts is $0
 * ATM prints "receipt" to text file (receipt.txt) and to the screen
 * ATM loop never ends; prompts a new user to create a new account or enter their "account ID" (ID)  
 * Program has been debugged and will not crash due to logic errors 
 */

package bank;

import java.io.File;
import java.util.InputMismatchException;
import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;

public class Bank {
	public static void main(String[] args) throws Exception {
	/* Scanner "resource leak" message */
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		@SuppressWarnings("resource")
		Scanner readFile = new Scanner(new File("lastId.txt"));
		Account atm = new Account();
		Account date = new Account();
		BankSQL database = new BankSQL();
		Boolean condition = true;
		Random rand = new Random();
		@SuppressWarnings("resource")
		/* prints a "receipt" for the customer */
		PrintStream receipt = new PrintStream("receipt.txt");	// relative file name, not absolute
		int idCount = 0, selection, tempID, PIN = 0, tempPIN;
		
	/* Set interest rate */
	 atm.setInterestRate(1.8);
	
	 while(true) {
		 /* Method displays program banner "Java Bank Management System" */
		programTitle();
		
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
		
		selection = input.nextInt();	
		
		/* Value must be number between 1 and 2 */
		while(selection < 1 || selection > 2) {
			try {
			System.out.println("Invalid selection.\n");
			System.out.print("Selection: ");
			selection = input.nextInt();
			}
			catch(InputMismatchException ex) {
				System.out.println("Selection must be a number.\n");
				input.next();
			}
		}
		
		/* ### CREATE NEW ACCOUNT IN DATABASE ###  */ 
		
		if(selection == 1) {
			/* Read last ID created from file 
			 * New account will use next consecutive id number
			 * Saves number to idCount variable		
			 */
			while(readFile.hasNext()) {
				idCount = readFile.nextInt(); 
				idCount++;
			}
			
			/* Create lastId.txt file */
			@SuppressWarnings("resource")
			PrintStream lastId = new PrintStream("lastId.txt");
			
			/* DATABASE: createAccountRow creates new row in Account table with prefilled values */
			database.createAccountRow(idCount);
			/* Prints last ID created to a file */
			lastId.print(idCount);			
			/* DATABASE: add last ID created to id_count column in database */
			database.insertIdCount(idCount);
			/* DATABASE: UPDATE database column chkBalance using Account constructor (default is 0) */
			database.insertChkBalance(idCount, atm.getBalance());
			/* DATABASE: UPDATE database column savBalance using Account constructor (default is 0) */
			database.insertSavBalance(idCount, atm.getBalance());
			System.out.print("\nEnter your first name: ");
			atm.setFirstName(input.next());
			/* Automatically capitalize first letter in first name */ 
			atm.setFirstName(atm.capitalizeName(atm.getFirstName()));
			/* DATABASE: INSERT firstName to database */
			database.insertFirstName(atm.getFirstName(), idCount);
			System.out.print("Enter your last name: ");
			atm.setLastName(input.next()); 
			/* Automatically capitalize first letter in last name */
			atm.setLastName(atm.capitalizeName(atm.getLastName()));	
			/* DATABASE: INSERT lastName in database */
			database.insertLasttName(atm.getLastName(), idCount);
			System.out.println("\nYour new ID number is: " + database.getId(idCount)); 
			/* Saves randomly generated number to tempPIN variable */
			tempPIN = (rand.nextInt(9999 - 1000 + 1) + 1000);
			/* DATABASE: INSERT newly created PIN in PIN table */
			database.insertPIN(idCount, tempPIN);
			System.out.println("Your new PIN number is: " + database.existingPIN(idCount));
			/* increases value of idCount */
			idCount++;
			/* saves idCount to file (lastId.txt) */
			lastId.println();
			database.idCountBackup(idCount);
	
		/* ### READ DATABASE FOR EXISTING ACCOUNT HOLDER ### 
		 * Prompt user to enter ID number
		 * If number does match number, asks user to enter correct number 
	 	 */ 
			
		} else if (selection == 2) {
		System.out.print("\nEnter an ID: ");
		
		/* Checks if user input contains letters or is a negative number */ 			
			atm.setId(checkNonNegativeID());
		/* Uses instance variable in Account class as tempID Bank class */
			tempID = atm.getId();
		/* Checks if user input for ID matches an ID in database
		 * SIDENOTE: If database contains no values (empty database), user given message and program restarts 
		 * CONTROL FLOW: pulls last name in row, saves it to temp variable
		 * Compares name in temp variable to name in row 
		 * If it matches, program continues, if not, user given "ID does not exist message" */
			try {
			String temp = database.getLastName(atm.getId());
			if(database.getId(atm.getId()) >= 0 && temp.equals(database.getLastName(atm.getId())));
			}
			catch(NullPointerException ex) {
				System.out.println("ID does not exist."); 
				continue;
			}
				
		/* DATABASE: Queries database for account, sets ID in Account class */
			atm.setId(database.existingAccounts(tempID));
		
		/* User must enter PIN */
		System.out.println("\nEnter your 4-digit PIN: ");
		
		/* Temporary storage of user entered PIN in Bank class 
		 * Used to check if input is not a number 
		 * Catches exception and asks user to retry
		 */
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
	
		/* DATABASE: Queries database for PIN and saves it to tempPIN variable
		 * Method checks user entered PIN to database PIN
		 */ 
		tempPIN = database.existingPIN(atm.getId());	// store generated PIN from database into variable
		checkPIN(PIN, tempPIN);				// compare user entered PIN and generated PIN
		//tempPIN = 0; 									// not necessary -- removes PIN from memory (customer safety)
		//PIN = 0;										// not necessary -- removes PIN from memory (customer safety)
		
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
	
		selection = input.nextInt();
		
		/* User must enter a number between 1 and 3 */
		while(selection < 1 || selection > 3) {
			try {
			System.out.println("Invalid selection.\n");
			System.out.print("Selection: ");
			selection = input.nextInt();
			}
			catch(InputMismatchException ex) {
				System.out.println("Selection must be a valid number.\n");
				input.next();
			}
		}
		// ### CHECKING ### 
		
		/* Option variable used for transfer method in Account class
		 * Duplicates selection value so that method can determine with transfer is for checking or savings
		 */
		int option = selection;
		
		if(selection == 1) {
			CheckingAccount checking = new CheckingAccount();				// checking account object
			Account account = new Account();								// account object
			account.setBalance(database.getChkBalance(atm.getId()));		// set balance in Account class
			System.out.println("\n*** Main Menu ***");
			System.out.println("1. Check balance");
			System.out.println("2. Withdraw");
			System.out.println("3. Deposit");
			System.out.println("4. Transfer");
			System.out.println("5. Exit");
			System.out.print("\nSelect an selection: ");
			
			/* User must enter a number between 1 and 5 */
			while(!input.hasNextInt()) {
				System.out.println("Selection must be a valid number.\n");
				System.out.print("Selection: ");  
					input.next();
				}
		
			selection = input.nextInt();
			
			/* User must enter a number between 1 and 5 */
			while(selection < 1 || selection > 5) {
				try {
				System.out.println("Invalid selection.\n");
				System.out.print("Selection: ");
				selection = input.nextInt();
				}
				catch(InputMismatchException ex) {
					System.out.println("Selection must be a valid number.\n");
					input.next();
				}
				
			}
			switch(selection) {
			/* DATABSE: Query balance from database */
			case 1: System.out.println("Checking balance: $" + database.getChkBalance(atm.getId())); continue;
			/* Method checkNonNegativeWithdrawal asks for user input for withdrawal amount (stored in withdrawlInput)
			 * DATABASE: calculates withdrawal using Withdraw method in Account class 
			 * Sends updated balance to database 
			 * Prints transaction using toString method in Account class (Transaction class)
			 * Outputs transaction type and other details to screen and text file (receipt.txt) 
			 */
			case 2: System.out.println("Enter an amount to withdraw: ");
			/* User input must be nonnegative */
			/* withdrawInput variable holds user withdrawal amount */
			/* User input must be a number */
					char W = 'W';
					double withdrawalInput = checkNonNegativeWithdrawal();
					database.insertChkBalance(atm.getId(), account.withdraw(withdrawalInput, W, option));
					System.out.println(checking.overDrawn(account.getBalance()));
					System.out.println(account.toString());
					receipt.println(account.toString());
					database.getDescription(atm.getId(), W, account.toString());
					System.out.println("Transaction complete: " + date.dateCreated()); continue;
			/* Method checkNonNegativeDeposit asks for user input for deposit amount (stored in depositInput)
			 * DATABASE: calculates deposit using Deposit method in Account class
			 * Sends updated balance to database	
			 * Prints transaction using toString method in Account class (Transaction class)
			 * Outputs transaction type and other details to screen and text file (receipt.txt)
			 */ 
			case 3: System.out.println("Enter an amount to deposit: ");
					char D = 'D';
					double depositInput = checkNonNegativeDeposit();
					database.insertChkBalance(atm.getId(), account.deposit(depositInput, D, option));
					System.out.println(account.toString());
					receipt.println(account.toString());
					database.getDescription(atm.getId(), D, account.toString());
					System.out.println("Transaction complete: " + date.dateCreated()); continue;
			/* Transfer money between accounts, Checking to Savings
			 * checkNonNegativeTransfer asks for user input for transfer amount (stored in transferInput)
			 * DATABASE: queries database for savings balance (stored in tempSavings)
			 * DATABASE: Calculates transfer using Transfer method in Account class, stores value chkBalance
			 * tempSavings variable adds transfer amount and previous savings balance together
			 * DATABASE: Savings balance is updated in database using tempSavings variable
			 * Outputs transaction type and other details to screen and text file (receipt.txt)
			 */
			case 4: System.out.println("Enter amount to transfer to Savings account: ");
			/* User input must be a number */
					char T = 'T';
					double transferInput = checkNonNegativeTransfer();
					// checks if transfer would cause a negative balance
					if(database.getChkBalance(atm.getId()) - transferInput < 0) { 
						System.out.println("\nInsufficent funds for transfer.\n");
						System.out.println("Transaction complete: " + date.dateCreated() + "\n"); break;
					} else {
					double tempSavings = database.getSavBalance(atm.getId());
					database.insertChkBalance(atm.getId(), account.transfer(transferInput, T, option));
					// checks if checking account is overdrawn
					System.out.println(checking.overDrawn(account.getBalance()));
					tempSavings =  transferInput + tempSavings;
					database.insertSavBalance(atm.getId(), tempSavings);
					System.out.println(account.toString());
					receipt.println(account.toString());
					database.getDescription(atm.getId(), T, account.toString());
					}
			case 5: System.out.println("\nGoodbye"); continue;
			default: System.out.println("Invalid selection."); continue;
			}	
		
		// ### SAVINGS ###	
			
		} else if (selection == 2) {
			Account account = new Account();
			account.setBalance(database.getSavBalance(atm.getId()));
			System.out.println("\n*** Main Menu ***");
			System.out.println("1. Check balance");
			System.out.println("2. Withdraw");
			System.out.println("3. Deposit");
			System.out.println("4. Transfer");
			System.out.println("5. Exit");
			System.out.print("\nSelect an selection: ");
	
			while(!input.hasNextInt()) {
				System.out.println("Selection must be a valid number.\n");
				System.out.print("Selection: ");  
					input.next();
				}
		
			selection = input.nextInt();
			
			/* User must enter a number between 1 and 5 */
			while(selection < 1 || selection > 5) {
				try {
				System.out.println("Invalid selection.\n");
				System.out.print("Selection: ");
				selection = input.nextInt();
				}
				catch(InputMismatchException ex) {
					System.out.println("Selection must be a valid number.\n");
					input.next();
				}
				
			}
			switch(selection) {
			/* DATABASE: Query balance from database */
			case 1: System.out.println("Savings balance: $" + database.getSavBalance(atm.getId()));
					receipt.println("Savings balance: $" + database.getSavBalance(atm.getId()));
					System.out.println(date.dateCreated()); continue;
			/* Method checkNonNegativeWithdrawal asks for user input for withdrawal amount (stored in withdrawlInput)
			 * CONTROL FLOW: Savings accounts CANNOT be overdrawn, checks before proceeding
			 * Queries savings balance in database, subtracts it from withdrawal 
			 * If withdrawal transaction would cause overdrawn account, transaction ends, otherwise it continues
			 * DATABASE: calculates withdrawal using Withdrawal method in Account class
			 * Prints transaction using toString method in Account class (Transaction class)
			 * Outputs transaction type and other details to screen and text file (receipt.txt)
			 */
			case 2: System.out.print("Enter an amount to withdraw: ");
					char W = 'W';
					double withdrawalInput = checkNonNegativeWithdrawal();
					if(database.getSavBalance(atm.getId()) - withdrawalInput < 0) { 
						System.out.println("\nInsufficent funds for withdrawal.\n");
						System.out.println("Transaction complete: " + date.dateCreated() + "\n"); break;
					} else {
					database.insertSavBalance(atm.getId(), account.withdraw(withdrawalInput, W, option));
					System.out.println(account.toString());
					receipt.println(account.toString());
					database.getDescription(atm.getId(), W, account.toString());
					System.out.println("Transaction complete: " + date.dateCreated() + "\n"); continue;
					}
			/* Method checkNonNegativeDeposit asks for user input for deposit amount (stored in depositInput)
			 * DATABASE: calculates deposit using Deposit method in Account class
			 * Sends updated balance to database	
			 * Prints transaction using toString method in Account class (Transaction class)
			 * Outputs transaction type and other details to screen and text file (receipt.txt)
			 */
			case 3: System.out.println("Enter an amount to deposit: ");
			/* User input must be a number */
					char D = 'D';
					double depositInput = checkNonNegativeDeposit();
					database.insertSavBalance(atm.getId(), account.deposit(depositInput, D, option));
					System.out.println(account.toString());
					receipt.println(account.toString());
					database.getDescription(atm.getId(), D, account.toString());
					System.out.println("Transaction complete: " + date.dateCreated()+ "\n"); continue;
			/* Transfer money between accounts, Savings to Checking
			 * checkNonNegativeTransfer asks for user input for transfer amount (stored in transferInput)
			 * CONTROL FLOW: Savings accounts CANNOT be overdrawn, checks before proceeding
			 * Queries savings balance in database, subtracts it from transferInput
			 * If transfer would cause an overdrawn account, transaction ends, otherwise it continues
			 * DATABASE: queries database for checking balance (stored in tempSavings)
			 * DATABASE: Calculates transfer using Transfer method in Account class, stores value savBalance
			 * tempChecking variable adds transfer amount and previous checking balance together
			 * DATABASE: Checking balance is updated in database using tempChecking variable
			 * Outputs transaction type and other details to screen and text file (receipt.txt)
			 */				
			case 4: System.out.println("Enter amount to transfer to Checking account: ");
			/* User input must be a number */
					char T = 'T';
					double transferInput = checkNonNegativeTransfer();
					// checks if savings account is overdrawn
					if(database.getSavBalance(atm.getId()) - transferInput < 0) { 
						System.out.println("\nInsufficent funds for transfer.\n");
						System.out.println("Transaction complete: " + date.dateCreated() + "\n"); break;
					} else {
					double tempChecking = database.getChkBalance(atm.getId());
					database.insertSavBalance(atm.getId(), account.transfer(transferInput, T, option));
					tempChecking =  transferInput + tempChecking;
					database.insertChkBalance(atm.getId(), tempChecking);
					System.out.println(account.toString());
					receipt.println(account.toString());
					database.getDescription(atm.getId(), T, account.toString());
					System.out.println("Transaction complete: " + date.dateCreated()+ "\n"); continue;
					} 		
			case 5: System.out.println("\nGoodbye\n"); continue;
			default:System.out.println("Invalid selection."); continue; 
		   }	
		  
		  }
		
		/* output account summary to screen and text file (receipt.txt)  */
		else if(selection == 3) {
			System.out.println("\n*** Summary for Account # " + atm.getId() + " ***");
			System.out.println("\nAccount holder name: " + database.getFirstName(atm.getId()) + " " + database.getLastName(atm.getId()));
			System.out.println("Checking balance: $" +  database.getChkBalance(atm.getId()));
			System.out.println("Savings balance: $" + database.getSavBalance(atm.getId()));
			System.out.println("Annual interest rate %" + atm.annualInterestRate());
			System.out.println("Monthly interest rate %" + atm.getMonthlyInterestRate());
			receipt.println("\n*** Summary for Account # " + atm.getId() + " ***");
			receipt.println("\nAccount holder name: " + database.getFirstName(atm.getId()) + " " + database.getLastName(atm.getId()));
			receipt.println("Checking balance: $" +  database.getChkBalance(atm.getId()));
			receipt.println("Savings balance: $" + database.getSavBalance(atm.getId()));
			receipt.println("Annual interest rate %" + atm.annualInterestRate());
			receipt.println("Monthly interest rate %" + atm.getMonthlyInterestRate());
		}
		
	  } 
	 }
	 
	}	
	
	/* method programTitle displays program banner */
	public static void programTitle() {
		System.out.println("\n+-----------------------------------------------+");
		System.out.println("|          Java Bank Management System          |");
		System.out.println("+-----------------------------------------------+\n");
	}
	
	/* method checkPIN checks if PIN number matches PIN number stored in ArrayList */
	public static void checkPIN(int PIN, int tempPIN) {
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
	
	/* method checkNonNegativeWithdrawal makes sure withdrawal amount is nonnegative and doesn't contain letters */
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
	
	/* method checkNonNegativeDeposit makes sure deposit amount is nonnegative and doesn't contain letters */
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
	
	/* method checkNonNegativeTransfer makes sure deposit amount is nonnegative and doesn't contain letters */
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
	
	/* method checkNonNegativeID makes sure ID is nonnegative and doesn't contain letters */
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
		
			
}
