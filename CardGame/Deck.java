/*
	Caleb Sander
	03/02/2015
	Hearts
	Deck class
*/

import java.util.ArrayList;

class Deck {
	private Card[] cards;

	Deck() {
		this.cards = new Card[Card.SUITS.length * Card.VALUES.length];
		for (int i = 0, j; i < Card.SUITS.length; i++) {
			for (j = 0; j < Card.VALUES.length; j++) this.cards[i * Card.VALUES.length + j] = new Card(Card.SUITS[i], Card.VALUES[j]);
		}
	}

	public Card deal() {
		Card returnCard = this.cards[0];
		Card[] newCards = new Card[this.cards.length - 1];
		for (int i = 1; i < this.cards.length; i++) newCards[i - 1] = this.cards[i];
		this.cards = newCards;
		return returnCard;
	}
	public void shuffle() {
		ArrayList<Card> oldCards = new ArrayList<Card>();
		for (int i = 0; i < this.cards.length; i++) oldCards.add(this.cards[i]);
		ArrayList<Card> newCards = new ArrayList<Card>();
		for (int i = 0, selected; i < this.cards.length; i++) {
			selected = (int)Math.floor(Math.random() * oldCards.size());
			newCards.add(oldCards.get(selected));
			oldCards.remove(selected);
		}
		for (int i = 0; i < this.cards.length; i++) this.cards[i] = newCards.get(i);
	}
	public String toString() {
		String returnString = new String();
		for (int i = 0; i < this.cards.length; i++) {
			returnString += this.cards[i].toString();
			if (i != this.cards.length - 1) returnString += "\n";
		}
		return returnString;
	}
}