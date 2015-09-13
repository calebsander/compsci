/*
  Caleb Sander
  11/04/2014
  Hangman (Lab 3)
*/

import java.util.Scanner;
import java.util.ArrayList;

class Hangman {
  public static void main(String[] args) {
    clearScreen();
    Scanner scanner = new Scanner(System.in);
    System.out.print("What word would you like to use? "); //prompt for word input
    String word = scanner.next(); //stores original word
    clearScreen(); //so the word can't be seen by the opponent
    char[] guessedword = new char[word.length()]; //stores which characters in the word have been guessed so far
    for (int i = 0; i < word.length(); i++) guessedword[i] = '_'; //fill guessed "string" with underscores (to show that no characters have yet been guessed)
    ArrayList<Character> guesses = new ArrayList<Character>(); //stores past guesses
    char guess; //temporarily stores current guess
    ArrayList<Integer> indices; //temporarily stores the result of indicesOf()
    byte guessesremaining = 7; //arbitrarily choose to have 7 incorrect guesses before losing
    boolean guessed = false; //stores whether or not the word has bene correctly guessed
    while (guessesremaining > 0 && !guessed) { //keep guessing until getting the word or running out of guesses
      System.out.print("Guess a letter! "); //prompt for letter input
      guess = scanner.next().toCharArray()[0]; //get single character from input
      clearScreen();
      if (guesses.indexOf(guess) == -1) { //if letter hasn't yet been guessed
        guesses.add(guess); //record that character has been guessed
        indices = indicesOf(word, guess); //find occurences of the letter in the word
        if (indices.size() == 0) { //if the guess is not in the word
          guessesremaining--; //count down remaining guesses
          System.out.println("You guessed a letter that is not in the word.");
          printHangman(guessesremaining); //print the hanging scene
        }
        else { //if the guess is in the word
          for (int i = 0; i < indices.size(); i++) guessedword[indices.get(i).intValue()] = guess; //replace underscores with guessed character, where they should be
          System.out.println("You guessed a letter that is in the word: " + new String(guessedword));
          guessed = new String(guessedword).equals(word); //calculate whether word has been guessed (only necessary if a new letter was revealed)
        }
        String lettersstring = ""; //a space-separated list of all the old guesses to tell the user what they can guess
        for (int i = 0; i < guesses.size(); i++) { //iterate through each of the old guesses
          char[] oldguess = {guesses.get(i)}; //because characters can't be converted to strings
          lettersstring += new String(oldguess) + " "; //add the guess and a space character to the string
        }
        System.out.println(lettersstring); //print the list of previously guessed letters
      }
      else System.out.println("Already guessed \"" + guess + "\". Please guess something else."); //if the letter has been guessed, then ask for another
    }
    if (guessed) System.out.println("You WON!"); //if the word was correctly guessed, display so
    else System.out.println("You LOST!"); //if the word wasn't guessed, display so
    System.out.println("The word was: " + word); //regardless, display the word
  }

  public static ArrayList<Integer> indicesOf(String word, char guess) { //returns an array of undetermined length of integer indices for a character in the word
    ArrayList<Integer> indices = new ArrayList<Integer>();
    for (int i = 0; i < word.length(); i++) { //iterate over the word, character-by-character
      if (word.charAt(i) == guess) indices.add(i); //keep a list of the occurences
    }
    return indices;
  }

  public static void printHangman(byte guessesremaining) { //prints ASCII art corresponding to the number of turns remaining
    if (guessesremaining == 1) System.out.println("1 guess remaining:"); //don't pluralize "guess" if only one remains
    else System.out.println(guessesremaining + " guesses remaining:"); //otherwise, just print out how many guesses remain
    switch (guessesremaining) { //print a different "image" for each number of remaining guesses
      case 0: //remove the box
        System.out.println("________ ");
        System.out.println("|      | ");
        System.out.println("|      O ");
        System.out.println("|     -|-");
        System.out.println("|     / \\");
        System.out.println("|        ");
        System.out.println("---------");
        break;
      case 1: //draw left leg
        System.out.println("________ ");
        System.out.println("|      | ");
        System.out.println("|      O ");
        System.out.println("|     -|-");
        System.out.println("|     / \\");
        System.out.println("|     T-T");
        System.out.println("---------");
        break;
      case 2: //draw right leg
        System.out.println("________ ");
        System.out.println("|      | ");
        System.out.println("|      O ");
        System.out.println("|     -|-");
        System.out.println("|     /  ");
        System.out.println("|     T-T");
        System.out.println("---------");
        break;
      case 3: //draw arms
        System.out.println("________ ");
        System.out.println("|      | ");
        System.out.println("|      O ");
        System.out.println("|     -|-");
        System.out.println("|        ");
        System.out.println("|     T-T");
        System.out.println("---------");
        break;
      case 4: //draw torso
        System.out.println("________ ");
        System.out.println("|      | ");
        System.out.println("|      O ");
        System.out.println("|      | ");
        System.out.println("|        ");
        System.out.println("|     T-T");
        System.out.println("---------");
        break;
      case 5: //draw head
        System.out.println("________ ");
        System.out.println("|      | ");
        System.out.println("|      O ");
        System.out.println("|        ");
        System.out.println("|        ");
        System.out.println("|     T-T");
        System.out.println("---------");
        break;
      case 6: //draw gallows
        System.out.println("________ ");
        System.out.println("|      | ");
        System.out.println("|        ");
        System.out.println("|        ");
        System.out.println("|        ");
        System.out.println("|     T-T");
        System.out.println("---------");
    }
  }
  public static void clearScreen() { //prints a bunch of line feeds to effectively clear the screen
    System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
  }
}
