/*
 Caleb Sander
 09/26/2014
 Lab 1
*/

import java.util.Scanner;

class LeapYear {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Date: "); //prompt for year input
		short year;
		while ((year = scanner.nextShort()) < 0) System.out.print("Please provide a positive date: "); //keep asking for a new date if it is not AD
		boolean leap = false; //assume not a leap year unless it is
		if ((year % 4) == 0) { //if a multiple of 4, it might be a leap year
			if ((year % 100) == 0) { //if also a multiple of 100, it probably isn't be a leap year
				if ((year % 400 == 0)) leap = true; //if also a multiple of 400, it is a leap year
			}
			else leap = true; //if not also a multiple of 100, it is a leap year
		}
		if (leap) System.out.println("Leap year? Yes"); //output whether or not it is a leap year
		else System.out.println("Leap year? No");
	}

}
