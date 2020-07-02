/* Progress Bar class
 * Displays a progress bar in the console
 */

package extractor;

public class ProgressBar {
	
	ProgressBar() {
		
	}
	
	public void display(int len, int count) throws Exception {	
		StringBuilder s = new StringBuilder("[                                                  ]");
			if(count <= len * 0.25) {
				for(int i = 1; i < 12; i++) {
					s = s.replace(i, i + 1, "\u2588");
				}
				System.out.print("Progress " + s + " 25% \r");
			} else if(count >= len * 0.25 && count <= len * 0.50) {
				for(int i = 1; i < 25; i++) {
					s = s.replace(i, i + 1, "\u2588");
				}
				System.out.print("Progress " + s + " 50% \r");
			} else if(count >= len * 0.50 && count <= len * 0.75) {
				for(int i = 1; i < 37; i++) {
					s = s.replace(i, i + 1, "\u2588");
				}
				System.out.print("Progress " + s + " 75% \r");
			} else {
				for(int i = 1; i < s.length() - 1; i++) {
					s = s.replace(i, i + 1, "\u2588");
				}
				System.out.print("Progress " + s + " 100% \r");
			}
	}

}
