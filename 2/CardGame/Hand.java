/*
	Caleb Sander
	03/02/2015
	Hearts
	Hand class
*/

import java.util.ArrayList;

class Hand {
	private ArrayList<Card> hand;

	//Instatantiates a new hand by dealing the necessary number of cards from the deck
	Hand(Deck deck) {
		this.hand = new ArrayList<Card>();
		for (int i = 0; i < Card.VALUES.length; i++) this.hand.add(deck.deal());
	}

	//Sorts the hand by suit and then number
	public void sort() {
		ArrayList<Card> newHand = new ArrayList<Card>();
		for (int i = 0, j, k; i < Card.SUITS.length; i++) {
			for (j = 0; j < Card.VALUES.length; j++) {
				for (k = 0; k < this.hand.size(); k++) {
					if (this.hand.get(k).equals(new Card(Card.SUITS[i], Card.VALUES[j]))) {
						newHand.add(this.hand.get(k));
						hand.remove(k);
						k--;
					}
				}
			}
		}
		this.hand = newHand;
	}
	//Randomly takes out 3 cards and returns them
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
	//Take out cards at specified indicies and returns them
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
	//Adds cards to the hand
	public void add(ArrayList<Card> cards) {
		this.hand.addAll(cards);
	}
	//Gets index of a specified card
	public int indexOf(Card card) {
		return this.hand.indexOf(card);
	}
	//Returns whether the hand contains a specified card
	public boolean contains(Card card) {
		return this.indexOf(card) != -1;
	}
	//Returns possible cards to play of any suit (except hearts, if they are not broken)
	public ArrayList<Integer> getPossibilities(boolean heartsBroken) {
		ArrayList<Integer> possibilities = new ArrayList<Integer>();
		if (heartsBroken) {
			for (int i = 0; i < this.hand.size(); i++) possibilities.add(i);
		}
		else {
			for (int i = 0; i < this.hand.size(); i++) {
				if (!this.hand.get(i).getSuit().equals("hearts")) possibilities.add(i);
			}
			if (possibilities.size() == 0) {
				possibilities = new ArrayList<Integer>();
				for (int i = 0; i < this.hand.size(); i++) possibilities.add(i);
			}
		}
		return possibilities;
	}
	//Returns possible cards to play of a specified suit (except hearts, if they are not broken)
	public ArrayList<Integer> getPossibilities(String suit, boolean heartsBroken) {
		ArrayList<Integer> possibilities = new ArrayList<Integer>();
		for (int i = 0; i < this.hand.size(); i++) {
			if (this.hand.get(i).getSuit().equals(suit)) possibilities.add(i);
		}
		if (possibilities.size() == 0) return this.getPossibilities(heartsBroken);
		return possibilities;
	}
	//Plays the card at a specified index
	public Card play(int index) {
		Card returnCard = this.hand.get(index);
		this.hand.remove(index);
		return returnCard;
	}
	//Displays the hand and which cards can be played
	public void printPossibilities(ArrayList<Integer> possibilities) {
		String[] toPrint = this.hand.get(0).graphic(), cardPrint;
		for (int i = 1, j; i < this.hand.size(); i++) {
			cardPrint = this.hand.get(i).graphic();
			for (j = 0; j < toPrint.length; j++) toPrint[j] += " " + cardPrint[j];
		}
		for (int i = 0; i < toPrint.length; i++) System.out.println(toPrint[i]);
		String indexString;
		for (int i = 0; i < this.hand.size(); i++) {
			if (possibilities.indexOf(i) == -1) indexString = "-";
			else indexString = new Integer(i + 1).toString();
			if (indexString.length() == 1) System.out.print("   " + indexString + "    ");
			else System.out.print("   " + indexString + "   ");
		}
		System.out.println();
	}

	//Utility function for picking a random element out of an ArrayList
	public static int pickRandom(ArrayList<Integer> possibilities) {
		return possibilities.get((int)Math.floor(Math.random() * possibilities.size())).intValue();
	}
}