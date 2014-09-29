/*
	Caleb Sander
	09/29/2014
	Homework 2
*/

import java.util.Scanner;

class Economy {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Annual growth rate (%): ");
		double growth = scanner.nextDouble();
		System.out.print("Inflation (%): ");
		double inflation = scanner.nextDouble();
		if (growth < 1) {
			if (inflation < 3) System.out.println("Increase welfare spending, reduce personal taxes, and decrease discount rate.");
			else System.out.println("Reduce business taxes.");
		}
		else if (growth > 4) {
			if (inflation < 1) System.out.println("Increase personal and business taxes, and decrease discount rate");
			else if (inflation > 3) System.out.println("Increase discount rate.");
			else System.out.println("No change in economic policy");
		}
		else System.out.println("No change in economic policy");
	}
}
