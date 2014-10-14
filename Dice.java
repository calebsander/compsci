/*
	Name: Caleb Sander
	Date: 10/14/2014
	Lab 2
*/

class Dice {
	public static void main(String[] args) {
		int moneys = 100; //current dollars
		int roll; //temporary value to store rolled number
		int rollcount = 0; //total rolls
		int maxmoneys = 100; //maximum dollars
		int maxrolls = 0; //count of the roll with maximum summed value
		while (moneys > 0) { //go until running out of money
			roll = (int)(Math.random() * 6 + 1) + (int)(Math.random() * 6 + 1); //sum two random dice
			if (roll == 7 || roll == 11) moneys += 3;
			else moneys--;
			rollcount++;
			if (moneys > maxmoneys) { //if reaching a new max, record it from current values
				maxmoneys = moneys;
				maxrolls = rollcount;
			}
		}
		System.out.println("Rolls before running out: " + rollcount); //display number of rolls taken
		System.out.println("Maximum money: $" + maxmoneys); //display maximum dollars
		if (maxrolls == 1) System.out.println("Should have stopped after 1 roll!"); //don't pluralize a single roll
		else System.out.println("Should have stopped after " + maxrolls + " rolls!"); //display count of the roll that got the maximum value
	}
}
