/*
	Caleb Sander
	03/02/2015
	Hearts
	Deck class
*/

import java.util.ArrayList;

class Deck {
	private ArrayList<Card> cards;

	Deck() {
		this.cards = new ArrayList<Card>(Card.SUITS.length * Card.VALUES.length);
		for (int i = 0, j; i < Card.SUITS.length; i++) {
			for (j = 0; j < Card.VALUES.length; j++) this.cards.add(new Card(Card.SUITS[i], Card.VALUES[j]));
		}
	}

	public Card deal() {
		Card returnCard = this.cards.get(0);
		this.cards.remove(0);
		return returnCard;
	}
	public void shuffle() {
		ArrayList<Card> oldCards = this.cards.clone();
		ArrayList<Card> newCards = new ArrayList<Card>();
		for (int i = 0, selected; oldCards.size() > 0; i++) {
			selected = (int)Math.floor(Math.random() * oldCards.size());
			newCards.add(oldCards.get(selected));
			oldCards.remove(selected);
		}
		this.cards = newCards;
	}
}
