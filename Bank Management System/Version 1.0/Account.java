/* Account class models a bank account */ 

package bank;

import java.util.Date;
import java.util.ArrayList;

// blueprint
public class Account {
	private int id;						// default (0)
	private double balance;				// default (0)
	private double annualInterestRate;	// stores current interest rate (default 0)
	private Date dateCreated;			// stores the date when account was created
	private String firstName;			// stores the first name of the customer
	private String lastName;			// stores the last name of the customer
	private ArrayList <Object> transactions = new ArrayList<>(); 
	private ArrayList <Integer> PIN = new ArrayList<>();
	
// constructor -- default account 
	Account() {
		id = 0;
		balance = 0;
		annualInterestRate = 0;
		firstName = " ";
		lastName = " ";
		dateCreated = null;
	}
	
// second constructor -- specified id and initial balance 
	public Account(int id, double balance) {
		this.id = id;
		this.balance = balance;
	}
	
// third constructor -- specified name, id, and balance
	public Account(String firstName, String lastName, int id, double balance) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.id = id;
		this.balance = balance;
	}
	
// set PIN number to account
	public void setPIN(int newValue) {
		PIN.add(newValue);
	}
	
// get PIN number from account
	public int getPIN(int index) {
		return PIN.get(index);
		
		
	}
// accessor (getter) method
	public int getId() {
		return id;
	}
// id mutator (setter) method	
	public void setId(int newValue) {
		id = newValue;
	}
// balance accessor (getter) method	
	public double getBalance() {
		return balance;
	}
// balance mutator (setter) method	
	public void setBalance(double newValue) {
		balance = newValue;
	}
	
// first name mutator (setter) method
	public void setFirstName(String newValue) {
		firstName = newValue;
	}
	
// last name setter method
	public void setLastName(String newValue) {
		lastName = newValue;
	}
	
// annualInterestRate (getter) method	
	public double annualInterestRate() {
		return annualInterestRate;
	}
// annualInterestRate mutator (setter) method
	public void setInterestRate(double newValue) {
		annualInterestRate = newValue;
	}
// Date accessor (getter)
	public Date dateCreated() {
		Date date = new Date();
		dateCreated = date;
		return dateCreated;
	}
// First name accessor (getter) method
	public String getFirstName() {
		return firstName;
	}
	
// Last name getter method
	public String getLastName() {
		return lastName;
	}
	
// method returns monthly interest rate
	public double getMonthlyInterestRate() {
		return annualInterestRate / 12;
	}
// method returns monthly interest
	public double getMonthlyInterest(double monthlyInterestRate) {
		return balance * monthlyInterestRate;
	}
	
// balance object 
	void balance(double balance) {
		Transaction newBalance = new Transaction();	
	}
	
	
// method checks first letter in name and capitalizes it
	public String capitalizeName(String name) {
		char temp;
		String temp2;
		temp = name.charAt(0);
		temp2 = Character.toString(temp);
		temp2 = temp2.toUpperCase();
		name = temp2 + name.substring(1, name.length());
		return name;
	}
	
// method withdraws specified amount from account 
// Transaction object uses balance variable from argument constructor
	double withdraw(double withdrawalAmount, int selection) {
		// ## CHECKING ## // 
		if(selection == 1) {
		Transaction newWithdrawal = new Transaction('W', withdrawalAmount, balance, "Withdrawal from Checking"); 
		// add newWithdrawl object to ArrayList
		transactions.add(newWithdrawal);
		balance = balance - newWithdrawal.getAmount();
		newWithdrawal.getAmount();
		return balance;
		} else if(selection == 2) {
			Transaction newWithdrawal = new Transaction('W', withdrawalAmount, balance, "Withdrawal from Savings"); 
			// add newWithdrawl object to ArrayList
			transactions.add(newWithdrawal);
			balance = balance - newWithdrawal.getAmount();
			newWithdrawal.getAmount();
			return balance;
		}
		return balance;
		
	}
// deposits specified amount into account	 
// Transaction object uses balance variable from argument constructor
	double deposit(double depositAmount, int selection) {
		// ## CHECKING ## //
		if(selection == 1) {
		Transaction newDeposit = new Transaction('D', depositAmount, balance, "Deposit to Checking");
		// add newDeposit object to ArrayList
		transactions.add(newDeposit);
		balance = balance + newDeposit.getAmount();
		return balance;
		// ## SAVINGS ## //
		} else if (selection == 2) {
			Transaction newDeposit = new Transaction('D', depositAmount, balance, "Deposit to Savings");
			// add newDeposit object to ArrayList
			transactions.add(newDeposit);
			balance = balance + newDeposit.getAmount();
			return balance;
		}
		return balance;
	}
	
	// transfer specified amount between accounts
	// Transaction object uses balance variable from argument constructor
		double transfer(double transferAmount, int selection) {
			// ## CHECKING ## //
			if(selection == 1) {
			Transaction newTransfer = new Transaction('T', transferAmount, balance, "Checking transfer to Savings");
			// add newTransfer object to ArrayList
			transactions.add(newTransfer); 
			// update checking and savings account value with transfer amount -- ArrayList transaction
			balance = balance - newTransfer.getAmount();
			return balance;
			// ## SAVINGS ## //
			} else if (selection == 2) {
				Transaction newTransfer = new Transaction('T', transferAmount, balance, "Savings transfer to Checking");
				// add newTransfer object to ArrayList
				transactions.add(newTransfer); 
				// update checking and savings account value with transfer amount -- ArrayList transaction
				balance = balance - newTransfer.getAmount();
				return balance;
			}
			return balance;
		}
	
// override toString method with custom method
// prints Account details, calls overridden toString method in Transaction class
	@Override
		public String toString() {
		String x = transactions.toString();
		// removes brackets and comma from printed string (ArrayList)
		return x.replace("[" , " ").replace("]", " ").replace(",", " ");
			
		
		
	}


	
}
