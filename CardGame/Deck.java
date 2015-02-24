import java.util.ArrayList;

class Deck {
	private Card[] cards;

	Deck() {
		String[] suits = {"clubs", "diamonds", "spades", "hearts"};
		String[] values = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
		this.cards = new Card[suits.length * values.length];
		for (int i = 0, j; i < suits.length; i++) {
			for (j = 0; j < values.length; j++) this.cards[i * values.length + j] = new Card(suits[i], values[j]);
		}
	}

	public Card deal() {
		Card[] newCards = new Card[this.cards.length - 1];
		for (int i = 1; i < this.cards.length; i++) newCards[i - 1] = this.cards[i];
		this.cards = newCards;
		return this.cards[0];
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