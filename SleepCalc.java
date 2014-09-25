/*
  Caleb Sander
  09/23/2014
  Lab 0 Part 2
*/

import java.util.Scanner;

class SleepCalc {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Average hours per night: ");
    float hours = scanner.nextFloat();
    float yearlength = 8765.76F; //24 * 365.24
    System.out.println("Hours slept per year: " + (hours * 365.24F));
    System.out.println("Percentage of time spent sleeping: " + (hours / .24F) + "%");
  }
}