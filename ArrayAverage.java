/* 1. Program reads file into array; total array elements are larger than values entered
 * 2. Copies array to another array
 * 3. Sorts second array
 * 4. Find average of array
 * 5. Conditional returns TRUE/FALSE if array average is greater to the number 35
 * 6. Outputs to file
 */

package practice2;

import java.util.Scanner;
import java.io.File;
import java.io.PrintStream;

public class practice2 {
	public static void main(String[] args) throws Exception {
		int [] array1 = new int [50];
		int [] array2 = new int [50];
		int elementsUsed, sum = 0, temp = 0;
		double average;
		readData(array1);
		copyArray(array1, array2);
		elementsUsed = searchArray(array1);
		sortArray(array2, temp, elementsUsed);
		average = findAverage(array2, sum, elementsUsed);
		printOutput(array1, array2, elementsUsed, average);
	}
	// Method reads data into array
		public static void readData(int array1[]) throws Exception {
			Scanner input = new Scanner(new File("/Users/jasonmoreau/Desktop/test.txt"));
			int i = 0;
			while(input.hasNext()) {
				array1[i] = input.nextInt();
				i++;
			}
			input.close();
		}
	// Method copies array1 to array2
		public static void copyArray(int array1[], int array2[]) {
			for(int i = 0; i < array1.length; i++)
			array2[i] = array1[i];
		}
	// Method searches array (in reverse) to find unfilled elements
	// Total will be used to find average
		public static int searchArray(int array1[]) {
			int counter = 0;
			for(int i = array1.length - 1; i >= 0 ; i--)
				if(array1[i] == 0 && array1[(array1.length - 1) - i] != 0)
					counter++;
			return counter;	
		}
	// Method sorts array2
		public static void sortArray(int array2[], int temp, int elementsUsed) {
			for(int i = 0; i < elementsUsed - 1; i++) {
				for(int j = 0; j < elementsUsed - 1; j++) {
					if(array2[j] > array2[j + 1]) {
						temp = array2[j];
						array2[j] = array2[j + 1];
						array2[j + 1] = temp;
					}
				}
			}
		}
	// Method finds average of array2 -- usedElements parameter is the total number elements used to find the average
		public static double findAverage(int array2[], int sum, int usedElements) {
			double avg;
			for(int i = 0; i < array2.length; i++) {
				 sum = array2[i] + sum;
			}
			avg = sum / usedElements;
			return avg;
		}
	// Method determines if array average is greater than 35 -- returns TRUE if it is
		public static Boolean greaterThan35(double average) {
			Boolean greater;
			greater = average > 35;
			return greater;
		}
	// Method prints output
		public static void printOutput(int array1[], int array2[], int elementsUsed, double average) throws Exception {
			PrintStream output = new PrintStream("/Users/jasonmoreau/Desktop/data.txt");
			output.println("Unsorted Array");
			for(int i = 0; i < elementsUsed; i++) {
				output.println(array1[i] + " " );
			}
			output.println("\nSorted Array");
			for(int j = 0; j < elementsUsed; j++) {
				output.println(array2[j] + " ");
			}
			output.println("\nIs the average of the array greater than 35?\n" + greaterThan35(average));
			output.close();
		}
}
