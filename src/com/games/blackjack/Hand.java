package com.games.blackjack;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

class PokerDetail
{
	public static Bitmap faceDownImage;
	private Poker poker;
	private boolean isHoleCard;
	
	public PokerDetail(Poker poker, boolean isHoleCard)
	{
		this.poker = poker;
		this.isHoleCard = isHoleCard;
	}
	
	// when drawing to Canvas, needs to know what the poker is, not only the value, but the suit
	public Poker getPoker()
	{
		return poker;
	}
	
	// only getting the card value
	public Value getValue()
	{
		return poker.getValue();
	}
	
	public boolean getIsHoleCard()
	{
		return isHoleCard;
	}
	
	public void setHoldCard()
	{
		isHoleCard = false;
	}
}

public class Hand {
	private List<PokerDetail> hand;
	
	Hand()
	{
		hand = new ArrayList<PokerDetail>();
	}
	
	public boolean firstCardIsAce()
	{
		return (hand.size() > 1 && hand.get(0).getValue() == Value.ACE);
	}
	
	public PokerDetail getCard(int index)
	{
		return hand.get(index);
	}
	
	public PokerDetail removeCard(int index)
	{
		return hand.remove(index);
	}
	
	public Value getHoleCardValue()
	{
		return hand.get(1).getValue();
	}
	
	public void add(PokerDetail card)
	{
		hand.add(card);
	}
	
	public int sumPoint()
	{
		int sum = 0;
		boolean aceFound = false;
		
		// there may be multiple Aces, each Ace can be 11 or 1, but only one Ace actually may be 11
		// so if there is at least one Ace, and total points is less than 12, just add 10, that must
		// be the maximum point the hand can sum to
		for (PokerDetail pokerDetail: hand) {
			Value value = pokerDetail.getValue();
			sum += value.getPoint();
			if (value == Value.ACE)
				aceFound = true;
		}
		if (sum < 12 && aceFound) return sum + 10;
		return sum;
	}
	
	public void setFaceUp(int index)
	{
		hand.get(index).setHoldCard();
	}
	
	public boolean isBlackJack()
	{
		if (hand.size() != 2) return false;
		Value card1 = hand.get(0).getValue();
		Value card2 = hand.get(1).getValue();
		return ((card1 == Value.ACE && (card2 == Value.TEN || card2 == Value.JACK
			  || card2 == Value.QUEEN || card2 == Value.KING)) || 
			  	card2 == Value.ACE && (card1 == Value.TEN || card1 == Value.JACK
			  	|| card1 == Value.QUEEN || card1 == Value.KING));
	}

	// continuously select card from the remaining deck until the total points hit 17
	public boolean hitUntil17(PokerSet pokerSet) {
		while (sumPoint() < 17) {
			Poker card = pokerSet.selectRandomCard();
			hand.add(new PokerDetail(card, false));
		}
		if (sumPoint() > 21)
			return false;
		return true;
	}
	
	public void drawToCanvas(Canvas canvas, PokerSet pokerSet, float x, float y, Paint paint, int offset)
	{
		Bitmap mBitmap;
		for (PokerDetail poker: hand)
		{
			if (poker.getIsHoleCard())
				mBitmap = PokerDetail.faceDownImage;
			else
				mBitmap = pokerSet.getPokerImage(poker.getPoker());
			canvas.drawBitmap(mBitmap, x, y, paint);
			x+= offset;
		}
	}
	
	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < hand.size(); i++) {
			PokerDetail poker = hand.get(i);
			s.append(i + " " + poker.getPoker());
		}
		return s.toString();
	}
}
