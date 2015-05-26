import java.util.ArrayList;

import java.awt.Color;
import java.awt.Graphics2D;

class Deck {
	private ArrayList<Card> cards;

	Deck(boolean fill) {
		this.cards = new ArrayList<Card>(45);
		if (fill) { //puts in a full deck if requested
			this.cards.add(new Card(1));
			for (int i = 0, j; i < 13; i++) {
				if (i == 6 || i == 9) continue;
				for (j = 0; j < 4; j++) this.cards.add(new Card(i));
			}
		}
	}

	//Randomizes card positions
	public void shuffle() {
		ArrayList<Card> newCards = new ArrayList<Card>(this.cards.size());
		for (int i = 0, selected; this.cards.size() != 0; i++) {
			selected = (int)Math.floor(Math.random() * this.cards.size());
			newCards.add(this.cards.get(selected));
			this.cards.remove(selected);
		}
		this.cards = newCards;
	}
	//Takes off the top card
	public Card deal() {
		return this.cards.remove(0);
	}
	//Adds a card to the end of the deck
	public void add(Card card) {
		this.cards.add(card);
	}
	//Returns whether the deck is empty
	public boolean empty() {
		return this.cards.size() == 0;
	}
	//Returns the last addition to the deck
	public Card last() {
		return this.cards.get(this.cards.size() - 1);
	}
	//Draws the last card
	public void display(Graphics2D g, int x, int y) {
		if (this.empty()) {
			g.setColor(Board.BACKGROUND);
			g.fillRect(x + 8, y + 8, 272, 176);
		}
		else {
			g.setColor(Color.WHITE);
			g.fillRect(x + 8, y + 8, 272, 176);
			g.setColor(Color.BLACK);
			g.drawString(this.last().getTitle(), x + 60, y + 60);
			g.setFont(Board.comicSansTiny);
			//Splits the annotation text into lines that are no more than LINE_LENGTH characters and draws them
			final int LINE_LENGTH = 50;
			String message = this.last().getMessage();
			String[] words = message.split(" ");
			String currentLine = "";
			int lineNumber = 0;
			for (String word : words) {
				if (currentLine.length() + 1 + word.length() > LINE_LENGTH) {
					g.drawString(currentLine, x + 10, y + 83 + 15 * lineNumber);
					lineNumber++;
					currentLine = word;
				}
				else currentLine += " " + word;
			}
			g.drawString(currentLine, x + 10, y + 83 + 15 * lineNumber);
		}
	}
}