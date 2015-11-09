package com.games.blackjack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.util.Log;

public class PokerSet {
	private List<Poker> pokerSet;
	private final static int cardNum = 52;
	private final static int deck = 1;
	private static int totalCards = deck * cardNum;
	private static int remCards = totalCards;
	private Map<Poker, Bitmap> pokerMaps = new HashMap<Poker, Bitmap>();
	private static int round = 0;
	
	PokerSet()
	{
		reShuffle();
	}
	
	private void reShuffle()
	{
		pokerSet = new LinkedList<Poker>();
		for (Suit suit: Suit.values()) {
			for (Value value: Value.values()) {
				pokerSet.add(new Poker(suit, value));
			}
		}
		remCards = totalCards;
	}
	
	public Poker selectRandomCard()
	{
		if (remCards <= totalCards/2) {
			Log.i("blackjack", "Reshuffling......");
			reShuffle();
			return selectRandomCard();
		}
		int random = (int)Math.floor(Math.random() * remCards);
		remCards--;
		//Log.i("blackjack", "picking out card " + random + " from cards " + pokerSet.size());
		Poker poker = pokerSet.get(random);
		pokerSet.remove(random);
		return poker;
	}
	
	public Poker[] dealCards()
	{
		Poker[] hand = new Poker[4];
		
		if (round == 0) { // player blackJack
			hand[0] = new Poker(Suit.CLUB, Value.ACE);
			hand[1] = new Poker(Suit.CLUB, Value.KING);
			hand[2] = new Poker(Suit.CLUB, Value.JACK);
			hand[3] = new Poker(Suit.DIAMOND, Value.THREE);
		} else if (round == 1) { // house blackJack
			hand[1] = new Poker(Suit.CLUB, Value.ACE);
			hand[0] = new Poker(Suit.CLUB, Value.KING);
			hand[3] = new Poker(Suit.CLUB, Value.JACK);
			hand[2] = new Poker(Suit.DIAMOND, Value.THREE);
		} else if (round == 2) { // both to blackJack
			hand[0] = new Poker(Suit.CLUB, Value.ACE);
			hand[1] = new Poker(Suit.CLUB, Value.KING);
			hand[2] = new Poker(Suit.CLUB, Value.JACK);
			hand[3] = new Poker(Suit.HEART, Value.ACE);
		} else if (round == 3) {
			hand[0] = new Poker(Suit.CLUB, Value.ACE);
			hand[1] = new Poker(Suit.DIAMOND, Value.JACK);
			hand[2] = new Poker(Suit.HEART, Value.ACE);
			hand[3] = new Poker(Suit.SPADE, Value.FOUR);
		} else if (round == 4) {
			hand[0] = new Poker(Suit.CLUB, Value.EIGHT);
			hand[1] = new Poker(Suit.DIAMOND, Value.JACK);
			hand[2] = new Poker(Suit.HEART, Value.EIGHT);
			hand[3] = new Poker(Suit.SPADE, Value.FOUR);
		} else {
			for (int i = 0; i < 4; i++) {
				hand[i] = selectRandomCard();
			}
		}
		round++;
		return hand;
	}

	public List<Poker> getPokers() {
		return pokerSet;
	}

	public void setPokerImage(Poker poker, Bitmap bitmap)
	{
		pokerMaps.put(poker, bitmap);
	}
	
	public int size()
	{
		return pokerMaps.size();
	}
	
	public Bitmap getPokerImage(Poker poker)
	{
		return pokerMaps.get(poker);
	}
}
