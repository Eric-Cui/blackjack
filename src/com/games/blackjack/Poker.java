package com.games.blackjack;

enum Suit
{
	CLUB,
	DIAMOND,
	HEART,
	SPADE
}

enum Value
{
	TWO (2),
	THREE (3),
	FOUR(4),
	FIVE(5),
	SIX(6),
	SEVEN(7),
	EIGHT(8),
	NINE(9),
	TEN(10),
	JACK(10),
	QUEEN(10),
	KING(10),
	ACE(1); // ACE could be 1 or 11 depending on different scenerios
	
	private final int point;
	Value(int point)
	{
		this.point = point;
	}
	
	public int getPoint()
	{
		return point;
	}
	
	public boolean isTenCard()
	{
		return (point == TEN.point || point == JACK.point || point == QUEEN.point || point == KING.point);
	}
}

public class Poker {
	@Override
	public String toString() {
		return "Poker [suit=" + suit + ", value=" + value + "]";
	}
	
	private Suit suit;
	private Value value;
	Poker(Suit suit, Value value)
	{
		this.suit = suit;
		this.value = value;
	}
	public Suit getSuit() {
		return suit;
	}
	public void setSuit(Suit suit) {
		this.suit = suit;
	}
	public Value getValue() {
		return value;
	}
	public void setValue(Value value) {
		this.value = value;
	}
	
	public int hashCode()
	{
		return this.suit.ordinal() * 100 + this.value.ordinal();
	}
	
	public boolean equals(Object o)
	{
		return ((o instanceof Poker) && (this.hashCode() == ((Poker)o).hashCode()));
	}
}