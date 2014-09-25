/*
  Caleb Sander
  09/25/2014
  Lab 0 Part 3
*/

import java.util.Scanner;

class Interest {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Annual rate (%): ");
    double rate = scanner.nextDouble() / 100;
    System.out.println("Maximum rate: " + ((Math.pow(Math.E, rate) - 1) * 100) + "%");
    long input = 1;
    System.out.print("Number of compounds per year: ");
    while ((input = scanner.nextLong()) != 0) {
      System.out.println("Rate: " + ((Math.pow(rate / input + 1, input) - 1) * 100) + "%");
      System.out.print("Number of compounds per year: ");
    }
  }
}