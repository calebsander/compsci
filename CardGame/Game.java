/*
	Caleb Sander
	03/02/2015
	Hearts
	Main class
*/

import java.util.ArrayList;
import java.util.Scanner;

class Game {
	public static void main(String[] args) {
		Deck deck = new Deck();
		deck.shuffle();
		Hand[] hands = new Hand[4];
		for (int i = 0; i < 4; i++) {
			hands[i] = new Hand(deck);
			hands[i].sort();
		}
		clearScreen();
		hands[0].printPossibilities(hands[0].getPossibilities(true));
		Scanner scanner = new Scanner(System.in);
		scanner.useDelimiter("\n");
		String[] switchStrings;
		int[] switchIndices;
		while (true) {
			System.out.println();
			System.out.print("Which three cards to pass (space-separated)? ");
			switchStrings = scanner.next().split(" ");
			if (switchStrings.length != 3) continue;
			switchIndices =  new int[3];
			for (int i = 0; i < 3; i++) switchIndices[i] = Integer.parseInt(switchStrings[i]) - 1;
			if (switchIndices[0] == switchIndices[1] || switchIndices[2] == switchIndices[1] || switchIndices[0] == switchIndices[2]) continue;
			break;
		}
		clearScreen();
		ArrayList<Card> transfer = hands[0].pickOut(switchIndices), nextTransfer;
		for (int i = 1; i < 4; i++) {
			nextTransfer = hands[i].pickOut();
			hands[i].add(transfer);
			transfer = nextTransfer;
		}
		hands[0].add(transfer);
		hands[0].sort();
		int firstPlay = 0;
		Card twoOfClubs = new Card("clubs", "2");
		for (int i = 0; i < hands.length; i++) {
			if (hands[i].contains(twoOfClubs)) {
				firstPlay = i;
				break;
			}
		}
		ArrayList<String[]> playedCards;
		String[] cardDisplay;
		Card[] currentCards = new Card[4];
		int input;
		String suit = "";
		ArrayList<Integer> possibilities;
		boolean heartsBroken = false;
		int absolutePlayer;
		int maxPlayer;
		for (int round = 0, player, line, card; round < Hand.HAND_SIZE; round++) {
			playedCards = new ArrayList<String[]>();
			for (player = 0; player < 4; player++) {
				absolutePlayer = (firstPlay + player) % 4;
				if (player == 0) {
					possibilities = hands[absolutePlayer].getPossibilities(heartsBroken);
					if (absolutePlayer == 0) {
						System.out.println();
						System.out.println("Which card to play? ");
						hands[absolutePlayer].printPossibilities(possibilities);
						while (invalidInput(possibilities, input = scanner.nextInt()));
						currentCards[absolutePlayer] = hands[absolutePlayer].play(input - 1);
					}
					else {
						currentCards[absolutePlayer] = hands[absolutePlayer].play(Hand.pickRandom(possibilities));
						playedCards.add(currentCards[absolutePlayer].graphic());
					}
					suit = currentCards[absolutePlayer].getSuit();
					if (suit.equals("hearts")) heartsBroken = true;
				}
				else {
					possibilities = hands[absolutePlayer].getPossibilities(suit, heartsBroken);
					if (absolutePlayer == 0) {
						System.out.println();
						System.out.println("Played so far:");
						cardDisplay = new String[playedCards.get(0).length];
						for (line = 0; line < cardDisplay.length; line++) {
							cardDisplay[line] = playedCards.get(0)[line];
							for (card = 1; card < playedCards.size(); card++) cardDisplay[line] += " " + playedCards.get(card)[line];
							System.out.println(cardDisplay[line]);
						}
						System.out.println();
						System.out.println("Which card to play? ");
						hands[absolutePlayer].printPossibilities(possibilities);
						while (invalidInput(possibilities, input = scanner.nextInt()));
						currentCards[absolutePlayer] = hands[absolutePlayer].play(input - 1);
					}
					else {
						currentCards[absolutePlayer] = hands[absolutePlayer].play(Hand.pickRandom(possibilities));
						playedCards.add(currentCards[absolutePlayer].graphic());
					}
					if (currentCards[absolutePlayer].getSuit().equals("hearts")) heartsBroken = true;
				}
			}
			clearScreen();
			System.out.println("Last round:");
			cardDisplay = new String[currentCards[0].graphic().length];
			for (line = 0; line < cardDisplay.length; line++) {
				cardDisplay[line] = currentCards[0].graphic()[line];
				for (card = 1; card < currentCards.length; card++) cardDisplay[line] += " " + currentCards[card].graphic()[line];
				System.out.println(cardDisplay[line]);
			}
			maxPlayer = firstPlay;
			for (player = 1; player < 4; player++) {
				absolutePlayer = (firstPlay + player) % 4;
				if (currentCards[absolutePlayer].getSuit().equals(suit) && currentCards[absolutePlayer].getValue() > currentCards[maxPlayer].getValue()) maxPlayer = absolutePlayer;
			}
			firstPlay = maxPlayer;
			for (player = 0; player < 4; player++) {
				if (player == maxPlayer) System.out.print("   *    ");
				else System.out.print("        ");
			}
		}
	}
	private static void clearScreen() {
		System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
	}
	private static boolean invalidInput(ArrayList<Integer> possibilities, int input) {
		for (int possibility = 0; possibility < possibilities.size(); possibility++) {
			if (possibilities.get(possibility).intValue() + 1 == input) return false;
		}
		return true;
	}
}