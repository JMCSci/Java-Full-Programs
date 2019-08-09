/* CheckingAccount extends Account class
 * Class models a checking account
 */

package bank;

public class CheckingAccount extends Account{
	public double chkBalance;
	
	/* Cannot used no-arg constructor since has private data fields
	 * Argument constructor must be invoked
	 * Super used to invoke superclass methods and constructors
	 * Default numbers 0 and 0 are used 
	 */
	
	public CheckingAccount(){
		super(0,0);
	}
	
// getter 
	public double getChecking() {
		return chkBalance;
	}

	
// setter 
	public void setChecking(double chkBalance) {
		this.chkBalance = chkBalance;
	}

// method determines if checking is overdrawn
	public String overDrawn(double balance) {
		if(balance < 0) {
			return "\n!!!!IMPORTANT!!!! \nAccount is overdrawn. Please deposit additional funds as soon as possible.\n" + 
					"Checking balance: -$" + Math.abs(balance);
		}
		return "";
	}


}
