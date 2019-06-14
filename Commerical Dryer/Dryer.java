/* Object Oriented Programming
 * Simulates a commercial dryer machine
 */

package dryer;

public class Dryer {
	private int status; 			// on/off (default: OFF = 0)
	private double cardValue;		// money remaining electronic card (default: $5.00)
	private double startDryer; 		// pay $2.00 to start cycle
	private int temperature;		// low = 0, medium = 1, high = 2 
	private double addTime;			// add additional time to cycle (10 minutes = 1.00);
	private long startTime;			// current time (start)
	private long endTime;    		// current time (end); 
	private long elapsed;			// elapsed time
	
	
// no-arg constructor
	Dryer() {
		status = 0;
		cardValue = 5.00;
		startDryer = 2.00; 
		temperature = 0;
		addTime = 1.00;
		startTime = 0;
		endTime = 0;
	}
	
// argument constructor
	Dryer(int status, double cardValue, double startDryer,  int temperature, double addTime) {
		this.status = status;
		this.cardValue = cardValue;
		this.startDryer = startDryer;
		this.temperature = temperature;
		this.addTime = addTime; 
	}
	
// getters
	public int getStatus() {
		return status;
	}
	
	public double getCardValue() {
		return cardValue;
	}
	
	public double getStartDryer() {
		return startDryer;
	}
	
	public int getTemperature() {
		return temperature;
	}
	
	public double getAddTime() {
		return addTime;
	}
	
	public long getTime() {
		return elapsed - startTime();
	}
	
// setters
	public void setStatus(int newValue) {
			status = newValue;
	}
		
	public void setcardValue(double newValue) {
			cardValue = newValue;
	}
	
	public void getStart(double newValue) {
		startDryer = newValue;
	}
		
	public void setTemperature(int newValue) {
		temperature = newValue;
	}
	
	public void setAddTime(double newValue) {
		addTime = newValue;
	}

		
// method startCycle -- start dryer cycle 
	public double startCycle() {
		status = 1;
		cardValue = cardValue - startDryer;
		startTime();
		return cardValue;
	}
	
// method calculates system time; sets startTime to current system time
	public long startTime() {
		long milliseconds =  System.currentTimeMillis();
		startTime = milliseconds / 1000;
		return startTime;
	}
	
// method endTime calculates system time; sets endTime to system time plus 30 seconds
// also sets elapsed equal to endTime; used in checkAddTime method
	public long endTime() {
		long milliseconds =  System.currentTimeMillis();
		endTime = (milliseconds / 1000) + 30;
		elapsed = endTime;
		return endTime;
	}
	
// method checked time -- used to save value to elapsed variable if time added in addCycleTime method
	public long checkAddTime() { 
		return elapsed;
	}
	
// method dryerStatus -- turn dryer ON or OFF
	public int dryerStatus() {
		if(getTime() > 0) {
			status = 1;
			return status;
		} else if (getTime() <= 0) {
			status = 0;
			return status;
		}
		return status;
	}
	
// method dryer temperature
	public void dryerTemperature(int selectTemp) {
		switch(selectTemp) {
		case 1: temperature = 1;
				System.out.println("LOW selected"); break;
		case 2: temperature = 2; 
				System.out.println("MEDIUM selected"); break;
		case 3: temperature = 3; 
				System.out.println("HIGH selected"); break;
		default: System.out.println("\n*** Invalid Response *** "); 
		}
	}

// method add time to cycle and increase by 5 seconds   
	public void addCycleTime() {
		if(cardValue >= 1) {
		cardValue = cardValue - 1;
		elapsed = elapsed + 5;
		
		} else {
			System.out.println("\n*** Insufficient funds ***");
		}
	}
	
// method cycle end -- check if cycle has ended 
	public Boolean checkCycle() {
		if(startTime() < elapsed && status == 1) {
			System.out.println("\nCycle is incomplete.\n"
					+ "Time remaining " + getTime());
			return true;
			
		} else if (startTime() == elapsed && status == 1 || startTime() >= elapsed && status == 0) {
			System.out.println("\nEnd of cycle"); 
			return false;
		} 
		return false;
	}

}
