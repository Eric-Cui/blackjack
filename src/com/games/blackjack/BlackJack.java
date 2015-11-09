package com.games.blackjack;

import android.util.Log;

enum State {
	PLAYER_IS_BLACKJACK,
	HOUSE_IS_BLACKJACK,
	BOTH_BLACKJACK,
	HOUSE_SHOW_ACE,
	PLAYER_WIN,
	PLAYER_BUSTED,
	PLAYER_LOSE,
	HOUSE_BUSTED,
	PUSH,
	NORMAL
}

public class BlackJack {
	private Poker[] hand;
	private boolean split;
	public Hand houseHand;
	public Hand playerHand;
	public Hand playerHand2;
	public static final PokerSet pokerSet = new PokerSet();

	public State askBlackJack() {
		if (playerHand.isBlackJack()) {
			if (!houseHand.isBlackJack()) {
				return State.PLAYER_IS_BLACKJACK;
			} else {
				houseHand.setFaceUp(1);
				return State.BOTH_BLACKJACK;
			}
		} else if (houseHand.isBlackJack()) {
			houseHand.setFaceUp(1);
			return State.HOUSE_IS_BLACKJACK;
		} else if (houseHand.firstCardIsAce()){
			return State.HOUSE_SHOW_ACE;
		}
		return State.NORMAL;
	}

	public void startGame() {
		houseHand  = new Hand();
		playerHand = new Hand();
		hand = pokerSet.dealCards();
		split = false;
		houseHand.add(new PokerDetail(hand[1], false));
		houseHand.add(new PokerDetail(hand[3], true));
		playerHand.add(new PokerDetail(hand[0], false));
		playerHand.add(new PokerDetail(hand[2], false));
		//Log.i("blackjack", String.format(
		//		"poker1 = %s, poker2 = %s, poker3 = %s, poker4 = %s\n",
		//		hand[0], hand[1], hand[2], hand[3]));
		//Log.i("blackjack", "width = " + width + " height = " + height);
		//blackjackView.invalidate();
		askBlackJack();
	}
	
	public boolean splitable()
	{
		return playerHand.getCard(0).getValue() == playerHand.getCard(1).getValue();
	}
	
	public void splitHand()
	{
		split = true;
		playerHand2 = new Hand();
		playerHand2.add(playerHand.removeCard(1));
	}
	
	public void dealerDraw() {
		houseHand.setFaceUp(1);
		houseHand.hitUntil17(pokerSet);
	}
	
	public State hit(Order handOrder) {
		Poker poker = pokerSet.selectRandomCard();
		Hand ph;
		if (handOrder == Order.FIRST_HAND)
			ph = playerHand;
		else
			ph = playerHand2;
		ph.add(new PokerDetail(poker, false));
		if (ph.sumPoint() > 21) {
			return State.PLAYER_BUSTED;
		}
		Log.i("blackjack", "playerhand = " + playerHand.toString());
		if (split)
			Log.i("blackjack", "playerhand2 = " + playerHand2.toString());
		return State.NORMAL;
	}
	
	public State computeResult(Order handOrder) {
		int playerPoint = (handOrder == Order.FIRST_HAND) ? playerHand.sumPoint() : playerHand2.sumPoint();
		int housePoint = houseHand.sumPoint();
		Log.i("blackjack", String.format("playerPoint = %d, housePoint = %d\n",
				playerPoint, housePoint));
		if (playerPoint > 21) {
			return State.PLAYER_BUSTED;
		} else if (housePoint > 21) {
			return State.HOUSE_BUSTED;
		} else {
			if (housePoint > playerPoint) {
				return State.PLAYER_LOSE;
			} else if (playerPoint > housePoint) {
				return State.PLAYER_WIN;
			} else {
				return State.PUSH;
			}
		}
	}
}