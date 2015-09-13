/*
  Caleb Sander
  10/31/2014
  Grader3 (Add-on to in-class assignment)
*/

import java.util.Scanner;

class Grader3 {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.print("How many different courses? "); //prompt for input of number of classes
    int classes = scanner.nextInt();
    String[] classnames = new String[classes];
    for (int i = 0; i < classes; i++) { //store the different classnames in an array
      System.out.print("What is the name of one course? ");
      classnames[i] = scanner.next();
    }
    double[] finalgrades = new double[classes]; //store the final grade for each class in an array in order to calculate the GPA later
    for (int i = 0; i < classes; i++) {
      System.out.print("How many different types of " + classnames[i] + " assignments? "); //prompt for input of number of types of assignments
      int types = scanner.nextInt();
      String[] categories = new String[types]; //stores the names of the assignments
      for (int j = 0; j < types; j++) { //record the names of each of the types of assignments
        System.out.print("What is the name of one type of " + classnames[i] + " assignments? ");
        categories[j] = scanner.next();
      }
      double[][] data = new double[types][2]; //[][0] contains weighting data, [][1] contains average grades for each type of assignments
      for (int j = 0; j < types; j++) {
        System.out.print("How much are " + categories[j] + " assignments worth for " + classnames[i] + " (as a percent)? ");
        data[j][0] = scanner.nextDouble() / 100; //convert percentage to decimal
      }
      int assignments; //temporarily stores number of assignments for each category
      for (int j = 0; j < types; j++) {
        System.out.print("How many " + categories[j] + " assignments were there for " + classnames[i] + "? "); //prompt for number of assignments for the current type
        assignments = scanner.nextInt();
        double[] grades = new double[assignments]; //temporary array to hold the different assignment grades
        if (assignments == 0) data[j][1] = 0; //to avoid NaN errors when dividing by 0
        else { //if there were assignments, then continue as normal
          for (int k = 0; k < assignments; k++) { //get grades for as many assignments as were given
            System.out.print("What was one of the grades (as a percent)? ");
            grades[k] = scanner.nextDouble();
          }
          data[j][1] = average(grades); //average the grades for this type of assignments, then store it in the main array
        }
      }
      for (int j = 0; j < types; j++) System.out.println("Average " + categories[j] + " grade for " + classnames[i] + ": " + data[j][1] + "% (" + lettergrade(data[j][1]) + ")"); //for each type of assignments, print out the average grade, as calculated earlier
      finalgrades[i] = weightedaverage(data); //use the weighted average function to calculate the overall grade
      System.out.println("Final grade for " + classnames[i] + ": " + finalgrades[i] + "% (" + lettergrade(finalgrades[i]) + ")"); //print out the overall grade
    }
    System.out.println("GPA: " + lettergrade(average(finalgrades))); //calculate average of each subject's grade and then convert it to a letter grade
  }
  private static double average(double[] values) { //averages the values of an array
    double sum = 0;
    for (int i = 0; i < values.length; i++) sum += values[i]; //add up all the values
    return sum / values.length; //divide the sum by the number of values to get the arithmetic mean
  }
  private static double weightedaverage(double[][] data) { //takes a weighted average of values ([][0] has weightings, [][1] has values)
    double sum = 0;
    for (int i = 0; i < data.length; i++) sum += data[i][0] * data[i][1]; //add up the values at specific weightings
    return sum; //return the result
  }
  private static String lettergrade(double score) { //gives a letter grade from a numerical grade (0-100)
    if      (score >= 98) return "A+";
    else if (score >= 93) return "A";
    else if (score >= 90) return "A-";
    else if (score >= 87) return "B+";
    else if (score >= 83) return "B";
    else if (score >= 80) return "B-";
    else if (score >= 77) return "C+";
    else if (score >= 73) return "C";
    else if (score >= 70) return "C-";
    else if (score >= 60) return "D";
    else                  return "E"; //no "f"s at Commonwealth
  }
}
