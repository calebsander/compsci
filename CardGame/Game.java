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
		Deck deck = new Deck(); //create a deck
		deck.shuffle(); //shuffle the deck
		//Deal hands
		Hand[] hands = new Hand[Card.SUITS.length];
		for (int i = 0; i < hands.length; i++) hands[i] = new Hand(deck);

		clearScreen();
		//Print instructions
		System.out.println("Hearts");
		System.out.println("There are 4 players each with a hand of 13 cards");
		System.out.println("Each player must pass 3 cards at the start of the game to the player to their left");
		System.out.println("Each round, every player plays one card");
		System.out.println("The person who has the 2 of Clubs must play it first");
		System.out.println("The suit of the card played much match the suit of the first card played in the round, unless the player is out");
		System.out.println("Hearts canot be played until someone has been forced to play a heart by having nothing else in their hand");
		System.out.println("The player with the highest card in the suit that was led takes all the cards played in the round and leads the next");
		System.out.println("Each heart taken is worth 1 point, and the Queen of Spades is worth 13");
		System.out.println("Try to gain as few points as possible");
		System.out.println();

		//Ask for which cards to pass
		hands[0].sort();
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
			switchIndices = new int[3];
			for (int i = 0; i < 3; i++) switchIndices[i] = Integer.parseInt(switchStrings[i]) - 1;
			if (switchIndices[0] == switchIndices[1] || switchIndices[2] == switchIndices[1] || switchIndices[0] == switchIndices[2]) continue;
			break;
		}

		clearScreen();
		//Pass cards by taking out 3 cards first, then adding in received cards
		ArrayList<Card> transfer = hands[0].pickOut(switchIndices), nextTransfer;
		for (int i = 1; i < hands.length; i++) {
			nextTransfer = hands[i].pickOut();
			hands[i].add(transfer);
			transfer = nextTransfer;
		}
		hands[0].add(transfer);
		hands[0].sort(); //sort player's hand for nicer display

		//Look through hands to find 2 of clubs, and record its position
		int firstPlayer = 0;
		Card twoOfClubs = new Card("clubs", "2");
		for (int i = 0; i < hands.length; i++) {
			if (hands[i].contains(twoOfClubs)) {
				firstPlayer = i;
				break;
			}
		}

		ArrayList<String[]> playedCards; //keeps track of text display of cards played by opponents in the current round
		String[] cardDisplay; //stores lines to be printed to represent the cards that have been played
		Card[] currentCards = new Card[hands.length]; //has the cards played in the current round
		int input; //stores position of card to play
		String suit = ""; //stores currently played suit of the round
		ArrayList<Integer> possibilities; //stores indices of possible cards to be played from the player's hand
		boolean heartsBroken = false; //stores whether hearts can be played without having to
		int absolutePlayer; //stores index in the hands array of the current player
		int maxPlayer; //stores the index of the player that took the trick
		Card queenOfSpades = new Card("spades", "Q");
		int[] points = new int[hands.length]; //stores the number of points for each player
		for (int player = 0; player < points.length; player++) points[player] = 0;
		for (int round = 0, player, line, card; round < Card.VALUES.length; round++) { //play 13 rounds
			playedCards = new ArrayList<String[]>(); //reset list of text display of played cards in the current round

			//Have the first player play their card
			if (round == 0) { //on the first round, the two of clubs must be played
				if (firstPlayer == 0) { //if the user is playing
					System.out.println();
					System.out.println("Which card to play? ");
					possibilities = new ArrayList<Integer>(1);
					possibilities.add(hands[firstPlayer].indexOf(twoOfClubs)); //get the position of the 2 of clubs
					hands[firstPlayer].printPossibilities(possibilities);
					while (possibilities.indexOf(input = scanner.nextInt() - 1) == -1); //keep asking for which card to play until the user enters the correct index
					currentCards[firstPlayer] = hands[firstPlayer].play(input); //play the card
				}
				else { //if an automated opponent is starting
					currentCards[firstPlayer] = hands[firstPlayer].play(hands[firstPlayer].indexOf(twoOfClubs)); //play the 2 of clubs
					playedCards.add(currentCards[firstPlayer].graphic()); //add the graphic to the list of played cards' graphics
				}
				suit = twoOfClubs.getSuit(); //record that players must play clubs unless they don't have any
			}
			else { //if not on the first round
				possibilities = hands[firstPlayer].getPossibilities(heartsBroken); //find possible cards to play of any suit (except possibly hearts)
				if (firstPlayer == 0) { //if the user is playing
					System.out.println();
					System.out.println("Which card to play? ");
					hands[firstPlayer].printPossibilities(possibilities); //display hand and possible choices
					while (possibilities.indexOf(input = scanner.nextInt() - 1) == -1); //keep asking for which card to play until the user enters a valid index
					currentCards[firstPlayer] = hands[firstPlayer].play(input); //play the card
				}
				else { //if an automated opponent is playing
					currentCards[firstPlayer] = hands[firstPlayer].play(Hand.pickRandom(possibilities)); //play a random possible card
					playedCards.add(currentCards[firstPlayer].graphic()); //add the graphic to the list of played cards' graphics
				}
				suit = currentCards[firstPlayer].getSuit(); //record that players must play the same suit unless they don't have any
				if (suit.equals("hearts")) heartsBroken = true; //if a heart was played, hearts are now broken
			}

			for (player = 1; player < hands.length; player++) { //iterate over the last 3 players
				absolutePlayer = (firstPlayer + player) % 4; //calculate the correct index of the hands array based on who started the round and the current offset
				possibilities = hands[absolutePlayer].getPossibilities(suit, heartsBroken); //find possible cards to play of the original suit (except possibly hearts)
				if (absolutePlayer == 0) { //if the user is playing
					System.out.println();
					System.out.println("Played so far:");
					cardDisplay = new String[playedCards.get(0).length];
					for (line = 0; line < cardDisplay.length; line++) { //for each line of the graphics, join each card's graphic with a space, then print the line
						cardDisplay[line] = playedCards.get(0)[line];
						for (card = 1; card < playedCards.size(); card++) cardDisplay[line] += " " + playedCards.get(card)[line];
						System.out.println(cardDisplay[line]);
					}
					System.out.println();
					System.out.println("Which card to play? ");
					hands[absolutePlayer].printPossibilities(possibilities); //display hand and possible choices
					while (possibilities.indexOf(input = scanner.nextInt() - 1) == -1); //keep asking for which card to play until the user enters a valid index
					currentCards[absolutePlayer] = hands[absolutePlayer].play(input); //play the card
				}
				else { //if an automated opponent is playing
					currentCards[absolutePlayer] = hands[absolutePlayer].play(Hand.pickRandom(possibilities)); //play a random possible card
					playedCards.add(currentCards[absolutePlayer].graphic()); //add the graphic to the list of played cards' graphics
				}
				if (currentCards[absolutePlayer].getSuit().equals("hearts")) heartsBroken = true; //if a heart was played, hearts are now broken
			}

			//Display results of the round
			clearScreen();
			System.out.println("Last round:");
			cardDisplay = new String[currentCards[0].graphic().length];
			for (line = 0; line < cardDisplay.length; line++) { //for each of the cards played, display a line that is the result of joining that line of each of their graphics with spaces
				cardDisplay[line] = currentCards[0].graphic()[line];
				for (card = 1; card < currentCards.length; card++) cardDisplay[line] += " " + currentCards[card].graphic()[line];
				System.out.println(cardDisplay[line]);
			}
			maxPlayer = firstPlayer; //assume the player that led the trick took it
			for (player = 1; player < hands.length; player++) { //for each other player, if they played a card from the led suit and it was the higher than the previous max, then they took the trick
				absolutePlayer = (firstPlayer + player) % 4;
				if (currentCards[absolutePlayer].getSuit().equals(suit) && currentCards[absolutePlayer].getValue() > currentCards[maxPlayer].getValue()) maxPlayer = absolutePlayer;
			}
			for (player = 0; player < hands.length; player++) { //display a star under the card of the player that took the trick
				if (player == maxPlayer) {
					System.out.print("   *    ");
					break;
				}
				else System.out.print("        ");
			}
			System.out.println();
			for (card = 0; card < currentCards.length; card++) { //under each card, display the number of points it is worth if it is a penalty card
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

			firstPlayer = maxPlayer; //the player who took the trick starts the next round
		}

		//Display results of the game
		System.out.println();
		System.out.println("Game finished");
		System.out.println("Player's points: " + new Integer(points[0]).toString()); //display points user got
		for (int player = 1; player < points.length; player++) System.out.println("Opponent " + new Integer(player).toString() + "'s points: " + new Integer(points[player]).toString()); //display points each of the opponents got
	}
	//Utility function for printing a bunch of newlines to clear the screen
	private static void clearScreen() {
		System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
	}
}