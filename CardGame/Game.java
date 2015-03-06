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
		System.out.println("Hearts");
		System.out.println("There are 4 players each with a hand of 13 cards");
		System.out.println("Each round, every player plays one card");
		System.out.println("The person who has the 2 of Clubs must play it first");
		System.out.println("The suit of the card played much match the suit of the first card played in the round, unless the player is out");
		System.out.println("Hearts canot be played until someone has been forced to play a heart by having nothing else in their hand");
		System.out.println("The player with the highest card in the suit that was led takes all the cards played in the round and leads the next");
		System.out.println("Each heart taken is worth 1 point, and the Queen of Spades is worth 13");
		System.out.println("Try to gain as few points as possible");
		System.out.println();
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
		int firstPlayer = 0;
		Card twoOfClubs = new Card("clubs", "2");
		for (int i = 0; i < hands.length; i++) {
			if (hands[i].contains(twoOfClubs)) {
				firstPlayer = i;
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
		Card queenOfSpades = new Card("spades", "Q");
		int[] points = new int[4];
		for (int player = 0; player < points.length; player++) points[player] = 0;
		for (int round = 0, player, line, card; round < Hand.HAND_SIZE; round++) {
			playedCards = new ArrayList<String[]>();
			for (player = 0; player < 4; player++) {
				absolutePlayer = (firstPlayer + player) % 4;
				if (player == 0) {
					if (round == 0) {
						if (absolutePlayer == 0) {
							System.out.println();
							System.out.println("Which card to play? ");
							possibilities = new ArrayList<Integer>(1);
							possibilities.add(hands[absolutePlayer].indexOf(twoOfClubs));
							hands[absolutePlayer].printPossibilities(possibilities);
							while (possibilities.indexOf(input = scanner.nextInt() - 1) == -1);
							currentCards[absolutePlayer] = hands[absolutePlayer].play(input);
						}
						else {
							currentCards[absolutePlayer] = hands[absolutePlayer].play(hands[absolutePlayer].indexOf(twoOfClubs));
							playedCards.add(currentCards[absolutePlayer].graphic());
						}
						suit = twoOfClubs.getSuit();
					}
					else {
						possibilities = hands[absolutePlayer].getPossibilities(heartsBroken);
						if (absolutePlayer == 0) {
							System.out.println();
							System.out.println("Which card to play? ");
							hands[absolutePlayer].printPossibilities(possibilities);
							while (possibilities.indexOf(input = scanner.nextInt() - 1) == -1);
							currentCards[absolutePlayer] = hands[absolutePlayer].play(input);
						}
						else {
							currentCards[absolutePlayer] = hands[absolutePlayer].play(Hand.pickRandom(possibilities));
							playedCards.add(currentCards[absolutePlayer].graphic());
						}
						suit = currentCards[absolutePlayer].getSuit();
						if (suit.equals("hearts")) heartsBroken = true;
					}
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
						while (possibilities.indexOf(input = scanner.nextInt() - 1) == -1);
						System.out.println(input);
						currentCards[absolutePlayer] = hands[absolutePlayer].play(input);
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
			maxPlayer = firstPlayer;
			for (player = 1; player < 4; player++) {
				absolutePlayer = (firstPlayer + player) % 4;
				if (currentCards[absolutePlayer].getSuit().equals(suit) && currentCards[absolutePlayer].getValue() > currentCards[maxPlayer].getValue()) maxPlayer = absolutePlayer;
			}
			for (player = 0; player < 4; player++) {
				if (player == maxPlayer) {
					System.out.print("   *    ");
					break;
				}
				else System.out.print("        ");
			}
			System.out.println();
			for (card = 0; card < currentCards.length; card++) {
				if (currentCards[card].equals(queenOfSpades)) {
					System.out.print("   13   ");
					points[maxPlayer] += 13;
				}
				else if (currentCards[card].getSuit().equals("hearts")) {
					System.out.print("   1    ");
					points[maxPlayer]++;
				}
				else System.out.print("        ");
			}
			System.out.println();
			firstPlayer = maxPlayer;
		}
		System.out.println();
		System.out.println("Game finished");
		System.out.println("Player's points: " + new Integer(points[0]).toString());
		for (int player = 1; player < points.length; player++) System.out.println("Opponent " + new Integer(player).toString() + "'s points: " + new Integer(points[player]).toString());
	}
	private static void clearScreen() {
		System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
	}
}