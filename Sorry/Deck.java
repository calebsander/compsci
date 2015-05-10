import java.util.ArrayList;

import java.awt.Color;
import java.awt.Graphics2D;

class Deck {
	private ArrayList<Card> cards;

	Deck(boolean fill) {
		this.cards = new ArrayList<Card>(45);
		if (fill) {
			this.cards.add(new Card(1));
			for (int i = 1, j; i < 13; i++) {
				if (i == 6 || i == 9) continue;
				for (j = 0; j < 4; j++) this.cards.add(new Card(i));
			}
		}
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
	public Card deal() {
		return this.cards.remove(0);
	}
	public void add(Card card) {
		this.cards.add(card);
	}
	public int size() {
		return this.cards.size();
	}
	public Card last() {
		return this.cards.get(this.cards.size() - 1);
	}
	public void display(Graphics2D g, int x, int y) {
		if (this.size() == 0) {
			g.setColor(Board.BACKGROUND);
			g.fillRect(x + 8, y + 8, 272, 176);
		}
		else {
			g.setColor(Color.WHITE);
			g.fillRect(x + 8, y + 8, 272, 176);
			g.setColor(Color.BLACK);
			g.drawString(Integer.valueOf(this.last().getValue()).toString(), x + 120, y + 120);
		}
	}
}