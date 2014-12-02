/*
	Caleb Sander
	12/02/2014
	Lab 4 (Animal Class)
*/

import java.util.ArrayList;

class Rabbit {
	//Attributes
	protected ArrayList<String> vegetablesEaten;
	private double distanceHopped;
	protected double health;
	private double food;
	private double age;

	//Constructor
	Rabbit() {
		this.vegetablesEaten = new ArrayList<String>();
		this.distanceHopped = 0.0;
		this.health = 1.0;
		this.food = 0.5;
		this.age = 0.0;
	}
	Rabbit(double health) {
		this.vegetablesEaten = new ArrayList<String>();
		this.distanceHopped = 0.0;
		this.health = health;
		this.food = 0.5;
		this.age = 0.0;
	}
	Rabbit(double health, double food) {
		this.vegetablesEaten = new ArrayList<String>();
		this.distanceHopped = 0.0;
		this.health = health;
		this.food = food;
		this.age = 0.0;
	}

	//Methods
	public boolean alive() {
		return this.health > 0.0;
	}
	public double eat(String food) {
		if (!alive()) return this.food;
		this.vegetablesEaten.add(food);
		return this.food = Math.min(this.food + food.length() / 20.0, 1.0);
	}
	public boolean hungry() {
		return this.food < 0.2;
	}
	public void hopForward(double distance) {
		if (!alive()) return;
		this.distanceHopped += distance;
		this.food = Math.max(this.food - Math.abs(distance), 0.0);
	}
	public void hopBackward(double distance) {
		hopForward(-distance);
	}
	public double position() {
		return this.distanceHopped;
	}
	public double age() {
		return ++this.age;
	}
	public double age(double years) {
		return this.age += years;
	}
	public void heal() {
		if (alive()) this.health = 1.0;
	}
	public double damage(double amount) {
		return this.health = Math.max(this.health - amount, 0.0);
	}
}
