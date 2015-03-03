/*
	Caleb Sander
	12/02/2014
	Lab 4 (Animal Class)
*/

import java.util.ArrayList;

class Rabbit {
	//Attributes
	protected ArrayList<String> vegetablesEaten; //stores all vegetables eaten
	private double distanceHopped; //stores current position
	protected double health; //stores current health
	private double food; //stores current food level
	private double age; //stores current age

	//Constructors
	Rabbit() { //constructs a default rabbit
		this.vegetablesEaten = new ArrayList<String>();
		this.distanceHopped = 0.0;
		this.health = 1.0;
		this.food = 0.5;
		this.age = 0.0;
	}
	Rabbit(double health) { //constructs a rabbit with specified health
		this.vegetablesEaten = new ArrayList<String>();
		this.distanceHopped = 0.0;
		this.health = health;
		this.food = 0.5;
		this.age = 0.0;
	}
	Rabbit(double health, double food) { //constructs a rabbit with specified health and food level
		this.vegetablesEaten = new ArrayList<String>();
		this.distanceHopped = 0.0;
		this.health = health;
		this.food = food;
		this.age = 0.0;
	}

	//Methods
	public boolean alive() { //returns whether the rabbit is alive
		return this.health > 0.0;
	}
	public double eat(String food) { //adds food to list of foods eaten, increases foodcount depending on length of food string (only works if rabbit is at least partially alive); returns food level
		if (!alive()) return this.food;
		this.vegetablesEaten.add(food);
		return this.food = Math.min(this.food + food.length() / 20.0, 1.0); //don't overfill food count
	}
	public boolean hungry() { //returns whether the rabbit is hungry
		return this.food < 0.2;
	}
	public void hopForward(double distance) { //moves the rabbit and uses up food (only works if rabbit is at least partially alive)
		if (!alive()) return;
		this.distanceHopped += distance;
		this.food = Math.max(this.food - Math.abs(distance), 0.0);
	}
	public void hopBackward(double distance) { //hops the other way
		hopForward(-distance);
	}
	public double position() { //returns current position (after hopping)
		return this.distanceHopped;
	}
	public double age() { //increments the rabbit's age by one year; returns the rabbit's new age
		return ++this.age;
	}
	public double age(double years) { //increments the rabbit's age by any number of years; returns the rabbit's new age
		return this.age += years;
	}
	public void heal() { //restores health to full (only works if rabbit is at least partially alive)
		if (alive()) this.health = 1.0;
	}
	public double damage(double amount) { //decreases the rabbit's health, but not below 0; returns the new health
		return this.health = Math.max(this.health - amount, 0.0);
	}
}
