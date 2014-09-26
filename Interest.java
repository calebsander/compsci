/*
  Caleb Sander
  09/25/2014
  Lab 0 Part 3
*/

import java.util.Scanner;

class Interest {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Annual rate (%): "); //prompt for rate input
    double rate = scanner.nextDouble() / 100; //convert percent to decimal
    System.out.println("Maximum compounded annual rate: " + ((Math.pow(Math.E, rate) - 1) * 100) + "%"); //do e^r calculation and convert back to percent
    long input;
    System.out.print("Number of compounds per year: "); //prompt for compounds input
    while ((input = scanner.nextLong()) != 0) { //keep doing calculations until a value of 0 is inputted
      System.out.println("Compounded annual rate: " + ((Math.pow(rate / input + 1, input) - 1) * 100) + "%"); //do compounding calculation and convert back to percent
      System.out.print("Number of compounds per year: "); //prompt for compounds input
    }
  }
}