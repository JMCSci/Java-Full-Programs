/* Progress Bar class
 * Displays a progress bar in the console
 */

package extractor;

public class ProgressBar {
	
	ProgressBar() {
		
	}
	
	public void displayBar(int len, int count) throws Exception {	
		StringBuilder s = new StringBuilder("[                                                  ]");
		while(count < 50) {
			if(count < len * 0.33) {
				for(int i = 1; i < 17; i++) {
					s = s.replace(i, i + 1, "=");
				}
				System.out.print(s + " 33% \r");
			} else if(count > len * 0.33 && count < len * 0.66) {
				for(int i = 1; i < 35; i++) {
					s = s.replace(i, i + 1, "=");
				}
				System.out.print(s + " 66% \r");
			} else if(count > len * 0.66 && count < len * 0.99) {
				for(int i = 1; i < 49; i++) {
					s = s.replace(i, i + 1, "=");
				}
				System.out.print(s + " 99% \r");
				
			} else {
				for(int i = 1; i < s.length() - 1; i++) {
					s = s.replace(i, i + 1, "=");
				}
				System.out.print(s + " 100% \r");
				break;
			}
			count++;
		}
	}

}
