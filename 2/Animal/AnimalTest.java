/*
	Caleb Sander
	12/02/2014
	Lab 4 Rabbit-tester
*/

class AnimalTest {
	public static void main(String[] args) {
		//Creat three different instances to test with
		Rabbit testRabbit = new Rabbit();
		Rabbit dyingRabbit = new Rabbit(0.5);
		Rabbit fullRabbit = new Rabbit(1.0, 1.0);

		System.out.println("Testing age system");
		System.out.println("Aging one year");
		System.out.println("Age is now: " + testRabbit.age());
		System.out.println("Aging five years");
		System.out.println("Age is now: " + testRabbit.age(5));
		System.out.println();

		System.out.println("Testing health system");
		System.out.println("Damaging rabbit");
		dyingRabbit.damage(0.3);
		if (dyingRabbit.alive()) System.out.println("Still alive");
		else System.out.println("Dead");
		System.out.println("Killing rabbit");
		dyingRabbit.damage(0.3);
		if (dyingRabbit.alive()) System.out.println("Still alive");
		else System.out.println("Dead");
		System.out.println("Reviving");
		dyingRabbit = new Rabbit();
		System.out.println("Current health: " + Math.round(dyingRabbit.health * 10) / 10.0); //to fix rounding errors from binary to decimal
		System.out.println("Being damaged");
		dyingRabbit.damage(0.3);
		System.out.println("Current health: " + Math.round(dyingRabbit.health * 10) / 10.0);
		System.out.println("Healing");
		dyingRabbit.heal();
		System.out.println("Current health: " + Math.round(dyingRabbit.health * 10) / 10.0);
		System.out.println();

		System.out.println("Testing food and hopping systems");
		if (fullRabbit.hungry()) System.out.println("Getting hungry");
		else System.out.println("Not hungry");
		System.out.println("Going for a walk");
		fullRabbit.hopForward(0.5);
		if (fullRabbit.hungry()) System.out.println("Getting hungry");
		else System.out.println("Not hungry");
		System.out.println("Going for a walk backwards");
		fullRabbit.hopBackward(0.4);
		if (fullRabbit.hungry()) System.out.println("Getting hungry");
		else System.out.println("Not hungry");
		System.out.println("Current position: " + Math.round(fullRabbit.position() * 10) / 10.0);
		System.out.println("Eating a carrot");
		System.out.println("Food level is now: " + Math.round(fullRabbit.eat("carrot") * 10) / 10.0);
		if (fullRabbit.hungry()) System.out.println("Still hungry");
		else System.out.println("No longer hungry");
		System.out.println("Total number of vegetables eaten: " + fullRabbit.vegetablesEaten.size());
		System.out.println("Eating a mystery vegetable");
		String[] vegetablePossibilities = {"Lettuce", "Onions", "Green Beans", "Corn", "Radishes"};
		fullRabbit.eat(vegetablePossibilities[(int)Math.floor(Math.random() * vegetablePossibilities.length)]);
		System.out.println("Vegetable eaten: " + fullRabbit.vegetablesEaten.get(1));
	}
}
