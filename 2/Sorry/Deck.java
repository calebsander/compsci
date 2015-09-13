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
	public void display(Graphics2D g, int x, int y, float scaling) {
		if (this.empty()) { //don't try to display a card if there is none
			g.setColor(Board.BACKGROUND);
			g.fillRect((int)((x + 8) * scaling), (int)((y + 8) * scaling), (int)(272 * scaling), (int)(176 * scaling));
		}
		else {
			g.setColor(Color.WHITE);
			g.fillRect((int)((x + 8) * scaling), (int)((y + 8) * scaling), (int)(272 * scaling), (int)(176 * scaling));
			g.setColor(Color.BLACK);
			g.setFont(Board.comicSans);
			g.drawString(this.last().getTitle(), (int)((x + 60) * scaling), (int)((y + 60) * scaling));
			g.setFont(Board.comicSansTiny);
			//Splits the annotation text into lines that are no more than LINE_LENGTH characters and draws them
			final int LINE_LENGTH = 45;
			String message = this.last().getMessage();
			String[] words = message.split(" ");
			String currentLine = "";
			int lineNumber = 0;
			for (String word : words) {
				if (currentLine.length() + 1 + word.length() > LINE_LENGTH) {
					g.drawString(currentLine, (int)((x + 10) * scaling), (int)((y + 83 + 15 * lineNumber) * scaling));
					lineNumber++;
					currentLine = word;
				}
				else currentLine += " " + word;
			}
			g.drawString(currentLine, (int)((x + 10) * scaling), (int)((y + 83 + 15 * lineNumber) * scaling));
		}
	}
}