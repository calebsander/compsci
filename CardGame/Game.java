class Game {
	public static void main(String[] args) {
		Deck deck = new Deck();
		System.out.println(deck);
		System.out.println();
		deck.shuffle();
		System.out.println(deck);
	}
}