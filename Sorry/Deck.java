import java.util.ArrayList;

class Deck {
	private ArrayList<Card> cards;

	Deck() {
		this.cards = new ArrayList<Card>();
	}

	public Card deal() {
		return this.cards.remove(0);
	}
	public void shuffle() {
		ArrayList<Card> newCards = new ArrayList<Card>(this.cards.size());
		for (int i = 0, selected; this.cards.size() != 0; i++) {
			selected = (int)Math.floor(Math.random() * this.cards.size());
			newCards.add(this.cards.get(selected));
			this.cards.remove(selected);
		}
		this.cards = newCards;
	}
}