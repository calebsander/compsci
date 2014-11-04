/*
  Caleb Sander
  11/04/2014
  Hangman (Lab 3)
*/

import java.util.Scanner;
import java.util.ArrayList;

class Hangman {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.print("What word would you like to use? ");
    String word = scanner.next();
    System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    char[] guessedword = new char[word.length()];
    for (int i = 0; i < word.length(); i++) guessedword[i] = '_';
    ArrayList guesses = new ArrayList();
    char guess;
    ArrayList<Integer> indices;
    byte incorrectguesses = 0;
    while (incorrectguesses < 7) {
      System.out.print("Guess a letter! ");
      guess = scanner.next().toCharArray()[0];
      indices = indicesOf(word, guess);
      if (indices.size() == 0) {
        incorrectguesses++;
        System.out.println("You guessed a letter that is not in the word, " + (7 - incorrectguesses) + " guesses remaining.");
      }
      else {
        for (int i = 0; i < indices.size(); i++) guessedword[indices.get(i).intValue()] = guess;
        System.out.println("You guessed a letter that is in the word: " + new String(guessedword));
      }
    }
  }
  
  public static ArrayList indicesOf(String word, char guess) {
    ArrayList<Integer> indices = new ArrayList<Integer>();
    for (int i = 0; i < word.length(); i++) {
      if (word.charAt(i) == guess) indices.add(i);
    }
    return indices;
  }
}