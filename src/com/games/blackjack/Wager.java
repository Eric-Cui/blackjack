package com.games.blackjack;

public class Wager {
	
	private double totalCash;
	private double lastWin;
	private double betAmount;
	public Wager(double totalCash, double lastWin, double betAmount)
	{
		this.setTotalCash(totalCash);
		this.setLastWin(lastWin);
		this.setBetAmount(betAmount);
	}
	public double getTotalCash() {
		return totalCash;
	}
	public void setTotalCash(double totalCash) {
		this.totalCash = totalCash;
	}
	public double getLastWin() {
		return lastWin;
	}
	public void setLastWin(double lastWin) {
		this.lastWin = lastWin;
		this.totalCash += lastWin;
	}
	public double getBetAmount() {
		return betAmount;
	}
	public void setBetAmount(double betAmount) {
		this.betAmount = betAmount;
	}
}
