/* Dots class
 * 
 * Display dots as URL is being generated
 * 2 second timer
 * 
 */

package extractor;

public class Dots {
	
	Dots() {
		
	}
	
	// displayDots: Appends period punctuation as elapsed time visual aid
	public void displayDots () throws InterruptedException {
		StringBuilder d = new StringBuilder();
		// set seconds (2 seconds)
		long initialTime = System.currentTimeMillis() / 1000 + 2;
		long current = System.currentTimeMillis() / 1000;
		// Stop thread
		Thread.sleep(1000);	
		while(current < initialTime) {
			current = System.currentTimeMillis() / 1000;
			if(current %  2 == 0) {
				d.delete(0, 3);
				d.append(".");
			} 
			System.out.print(d);
			Thread.sleep(1000);
		}
	
	}

}


