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
		hands[0].printGraphic();
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
			hands[i].sort();
			transfer = nextTransfer;
		}
		hands[0].add(transfer);
		hands[0].sort();
		hands[0].printGraphic();
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
		int input = 0;
		String suit = "";
		ArrayList<Integer> possibilities;
		boolean invalidInput;
		for (int round = 0, player, line, card, possibility; round < Hand.HAND_SIZE; round++) {
			clearScreen();
			playedCards = new ArrayList<String[]>();
			for (player = 0; player < 4; player++) {
				if (player == 0) {
					if ((firstPlay + player) % 4 == 0) {
						System.out.println();
						System.out.println("Which card to play? ");
						hands[0].printGraphic();
						while ((input = scanner.nextInt()) < 1 || (input > Hand.HAND_SIZE - round));
						currentCards[0] = hands[0].play(input - 1);
						suit = currentCards[0].getSuit();
					}
					else {
						currentCards[(firstPlay + player) % 4] = hands[(firstPlay + player) % 4].play();
						suit = currentCards[(firstPlay + player) % 4].getSuit();
						playedCards.add(currentCards[(firstPlay + player) % 4].graphic());
					}
				}
				else {
					if ((firstPlay + player) % 4 == 0) {
						System.out.println("Played so far:");
						cardDisplay = new String[playedCards.get(0).length];
						for (line = 0; line < playedCards.get(0).length; line++) {
							cardDisplay[line] = "";
							for (card = 0; card < playedCards.size(); card++) cardDisplay[line] += " " + playedCards.get(card)[line];
							System.out.println(cardDisplay[line]);
						}
						System.out.println();
						System.out.println("Which card to play? ");
						possibilities = hands[0].printSuit(suit);
						invalidInput = true;
						while (invalidInput) {
							input = scanner.nextInt();
							for (possibility = 0; possibility < possibilities.size(); possibility++) {
								System.out.println(possibilities.get(possibility));
								if (possibilities.get(possibility).intValue() + 1 == input) {
									invalidInput = false;
									break;
								}
							}
						}
						currentCards[0] = hands[0].play(input - 1);
					}
					else {
						currentCards[(firstPlay + player) % 4] = hands[(firstPlay + player) % 4].play(suit);
						playedCards.add(currentCards[(firstPlay + player) % 4].graphic());
					}
				}
			}
		}
	}
	private static void clearScreen() {
		System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
	}
}