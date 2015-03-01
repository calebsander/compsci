import java.util.ArrayList;

class Hand {
	private ArrayList<Card> hand;

	Hand(Deck deck) {
		this.hand = new ArrayList<Card>();
		for (int i = 0; i < HAND_SIZE; i++) this.hand.add(deck.deal());
	}

	public void sort() {
		ArrayList<Card> newHand = new ArrayList<Card>();
		for (int i = 0, j, k; i < SUITS.length; i++) {
			for (j = 0; j < VALUES.length; j++) {
				for (k = 0; k < this.hand.size(); k++) {
					if (this.hand.get(k).equals(new Card(SUITS[i], VALUES[j]))) {
						newHand.add(this.hand.get(k));
						hand.remove(k);
						k--;
					}
				}
			}
		}
		this.hand = newHand;
	}
	public Card getAt(int i) {
		return this.hand.get(i);
	}
	public ArrayList<Card> pickOut() {
		ArrayList<Card> taken = new ArrayList<Card>(3);
		int index;
		for (int i = 0; i < 3; i++) {
			index = (int)Math.floor(Math.random() * this.hand.size());
			taken.add(this.hand.get(index));
			hand.remove(index);
		}
		return taken;
	}
	public ArrayList<Card> pickOut(int[] indices) {
		ArrayList<Card> taken = new ArrayList<Card>();
		for (int i = 0, j; i < indices.length; i++) {
			taken.add(this.hand.get(indices[i]));
			this.hand.remove(indices[i]);
			for (j = i; j < indices.length; j++) {
				if (indices[j] > indices[i]) indices[j]--;
			}
		}
		return taken;
	}
	public void add(ArrayList<Card> cards) {
		this.hand.addAll(cards);
	}
	public void print() {
		for (int i = 0; i < this.hand.size(); i++) System.out.println(new Integer(i + 1).toString() + ": " + this.hand.get(i).toString());
	}
	public void printGraphic() {
		String[] toPrint = this.hand.get(0).graphic(), cardPrint;
		for (int i = 1, j; i < this.hand.size(); i++) {
			cardPrint = this.hand.get(i).graphic();
			for (j = 0; j < toPrint.length; j++) toPrint[j] += " " + cardPrint[j];
		}
		for (int i = 0; i < toPrint.length; i++) System.out.println(toPrint[i]);
		String indexString;
		for (int i = 0; i < this.hand.size(); i++) {
			indexString = new Integer(i + 1).toString();
			if (indexString.length() == 1) System.out.print("   " + indexString + "    ");
			else System.out.print("   " + indexString + "   ");
		}
		System.out.println();
	}
	public boolean contains(Card card) {
		for (int i = 0; i < this.hand.size(); i++) {
			if (this.hand.get(i).equals(card)) return true;
		}
		return false;
	}
	public Card play() {
		int index = (int)Math.floor(Math.random() * this.hand.size());
		return this.play(index);
	}
	public Card play(int index) {
		Card returnCard = this.hand.get(index);
		this.hand.remove(index);
		return returnCard;
	}
	public Card play(String suit) {
		ArrayList<Integer> possibilities = new ArrayList<Integer>();
		for (int i = 0; i < this.hand.size(); i++) {
			if (this.hand.get(i).getSuit().equals(suit)) possibilities.add(i);
		}
		if (possibilities.size() == 0) return this.play();
		return this.play(possibilities.get((int)Math.floor(Math.random() * possibilities.size())));
	}
	public ArrayList<Integer> printSuit(String suit) {
		ArrayList<Integer> possibilities = new ArrayList<Integer>();
		for (int i = 0; i < this.hand.size(); i++) {
			if (this.hand.get(i).getSuit().equals(suit)) possibilities.add(i);
		}
		if (possibilities.size() == 0) {
			this.printGraphic();
			ArrayList<Integer> allPossibilities = new ArrayList<Integer>();
			for (int i = 0; i < this.hand.size(); i++) allPossibilities.add(i);
			return allPossibilities;
		}
		else {
			String[] toPrint = this.hand.get(possibilities.get(0)).graphic(), cardPrint;
			for (int i = 1, j; i < possibilities.size(); i++) {
				cardPrint = this.hand.get(possibilities.get(i)).graphic();
				for (j = 0; j < toPrint.length; j++) toPrint[j] += " " + cardPrint[j];
			}
			for (int i = 0; i < toPrint.length; i++) System.out.println(toPrint[i]);
			String indexString;
			for (int i = 0; i < possibilities.size(); i++) {
				indexString = new Integer(possibilities.get(i) + 1).toString();
				if (indexString.length() == 1) System.out.print("   " + indexString + "    ");
				else System.out.print("   " + indexString + "   ");
			}
			System.out.println();
			return possibilities;
		}
	}

	protected final static int HAND_SIZE = 13;
	private final static String[] SUITS = {"clubs", "diamonds", "spades", "hearts"};
	private final static String[] VALUES = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
}