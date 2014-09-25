/*
  Caleb Sander
  09/23/2014
  Lab 0 Part 1
*/

import java.util.Scanner;

class WindChill {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Temperature: ");
    float temp = scanner.nextFloat();
    temp = temp * 1.8F + 32F;
    
    System.out.print("Windspeed: ");
    float speed = scanner.nextFloat();
    speed = speed * 0.621371F * 60F; //0.621371 from a Google search
    
    System.out.println("Windchill: " + (35.74F + .6215F * temp - 35.75F * Math.pow(speed, .16F) + .4275F * temp * Math.pow(speed, .16F)));
  }
}