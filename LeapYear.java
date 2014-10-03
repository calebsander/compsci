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
  while ((year = scanner.nextShort()) < 0) System.out.print("Please provide a positive date: "); //make sure date is AD
  boolean leap = false; //assume not a leap year by 
  if ((year % 4) == 0) { //if year is a multiple of 4
   if ((year % 100) == 0) { //if it is a multiple of 100, it is not a leap year
    if ((year % 400 == 0)) leap = true; //unless it is also a multiple of 400, in which case it is a leap year
   }
   else leap = true; //if it is not a multiple of 100, it is definitely a leap year
  }
  if (leap) System.out.println("Leap year? Yes"); //print out whether or not it is a leap year, from prior calculations
  else System.out.println("Leap year? No");
 }
}