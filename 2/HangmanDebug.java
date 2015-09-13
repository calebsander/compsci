import java.util.Scanner;
import java.util.ArrayList;

public class HangmanDebug {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.println("Enter the word you would like your opponent to guess.");
		String word = in.next();
		while (word.length() >= 10) {
			System.out.println("You must use a shorter word.");
			word = in.next();
		}
		for (int i = 0; i < 20; i++) System.out.println();
		playHangman(word, in);
	}
	public static ArrayList<Integer> otherOccurences(String guess, String word) {
		char[] wordArray = word.toCharArray();
		ArrayList<Integer> occurences = new ArrayList<Integer>();
		boolean first = true;
		for (int i = 0; i < word.length(); i++) {
			if (wordArray[i] == guess.charAt(0)) {
				if (first) first = false;
				else occurences.add(i);
			}
		}
		return occurences;
	}
	public static void playHangman(String word, Scanner in) {
		int inserted = 0;
		int man = 10;
		String[] guessedWord = new String[word.length()];
		for (int i = 0; i < word.length(); i++) guessedWord[i] = "_";
		String guess;
		while (inserted < word.length() && man > 0) {
			PrintMan(man);
			System.out.println("Guess a letter!");
			guess = in.next();
			if (word.contains(guess)) {
				inserted++;
				int index = word.indexOf(guess);
				ArrayList<Integer> other = otherOccurences(guess, word);
				while (!other.isEmpty()) {
					guessedWord[other.get(0)] = guess;
					other.remove(0);
					inserted++;
				}
				guessedWord[index] = guess;
				String gword = "";
				for (int i = 0; i < word.length(); i++) gword += guessedWord[i];
				System.out.println("You guessed a letter that is in the word: " + gword);
			}
			else {
				man--;
				System.out.println("You guessed a letter that is not in the word. :(");
			}
		}
		if (man == 0) {
			PrintMan(0);
			System.out.println("The man was hung! You LOST!");
		}
		else System.out.println("Congratulations! You guessed the word: " + word);
	}
	public static void PrintMan(int man) {
		System.out.println("Man:");
		System.out.println(man);
	}
}