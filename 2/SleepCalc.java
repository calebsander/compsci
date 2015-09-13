/*
  Caleb Sander
  09/23/2014
  Lab 0 Part 2
*/

import java.util.Scanner;

class SleepCalc {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Average hours per night: "); //prompt for hours input
    float hours = scanner.nextFloat();
    float yearlength = 8765.76F;
    System.out.println("Hours slept per year: " + (hours * 365.24F)); //multiply the daily number of hours slept by the number of days in a year to get hours slept per year
    System.out.println("Percentage of time spent sleeping: " + (hours / .24F) + "%"); //convert hours spent sleeping per day out of the total hours per day to percent
  }
}