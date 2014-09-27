/*
	Caleb Sander
	09/26/2014
	Lab 1
*/

import java.util.Scanner;

class LeapYear {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Date: ");
		short year;
		while ((year = scanner.nextShort()) < 0) System.out.print("Please provide a positive date: ");
		boolean leap = false;
		if ((year % 4) == 0) {
			if ((year % 100) == 0) {
				if ((year % 400 == 0)) leap = true;
			}
			else leap = true;
		}
		if (leap) System.out.println("Leap year? Yes");
		else System.out.println("Leap year? No");
	}
}