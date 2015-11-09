package com.games.blackjack;

import java.util.EnumMap;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.games.blackjack.State;

enum ButtonStatus {
	GAME_STARTED,
	DEAL_CLICKED,
	STAND_CLICKED,
	SURRENDER_CLICKED, 
	HIT_CLICKED,
	DOUBLE_CLICKED,
	SPLITABLE
}

enum Order {
	FIRST_HAND,
	SECOND_HAND
}

class BetResult {
	double winTimes;
	String message;
	public BetResult(double winTimes, String message) {
		this.winTimes = winTimes;
		this.message = message;
	}
}

public class BlackJackView extends View {
	private int width, height;
	private static final int offset = 50;
	private BlackJack blackJack = new BlackJack();
	private PokerSet pokerSet = BlackJack.pokerSet;
	private Toast toast;
	private final Paint paint = new Paint();
	private static boolean gameBegin = false;
	private static Resources res;
	private static String pkgName;
	private Context context;
	private Button surrenderButton, standButton, dealButton, hitButton, doubleButton, splitButton;
	private static Wager wager = new Wager(500, 0, 10);
	private TextView totalCash, lastWin, betAmount;
	private boolean split = false;
	private Order handOrder = Order.FIRST_HAND;
	
	private void getControls()
	{
		surrenderButton = ((MainActivity)context).surrenderButton;
		standButton = ((MainActivity)context).standButton;
		dealButton = ((MainActivity)context).dealButton;
		hitButton = ((MainActivity)context).hitButton;
		doubleButton = ((MainActivity)context).doubleButton;
		splitButton = ((MainActivity)context).splitButton;
		totalCash = (TextView)((MainActivity)context).findViewById(R.id.totalWagerTextView);
		lastWin = (TextView)((MainActivity)context).findViewById(R.id.lastWinTextView);
		betAmount = (TextView)((MainActivity)context).findViewById(R.id.betAmountTextView);
	}
	

	public BlackJackView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initBlackJackView(context);
	}
	
	public BlackJackView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		initBlackJackView(context);
	}
	
	private void initBlackJackView(Context context) {
		res = context.getResources();
		pkgName = context.getPackageName();
		cacheBitmaps(pokerSet);
		PokerDetail.faceDownImage = getResizedBitmap(BitmapFactory.decodeResource(res,
				res.getIdentifier("b1fv", "drawable", pkgName)), 192+96, 144+72);
	}
	
	public void beginGame() {
		width = this.getWidth();
		height = this.getHeight();
		getControls();
		split = false;
		handOrder = Order.FIRST_HAND;
		blackJack = new BlackJack();
		blackJack.startGame();
		invalidate(); // first show the cards, to avoid directly show the hole card when either side got blackjack
		gameBegin = true;
		State state = blackJack.askBlackJack();
		if (state == State.PLAYER_IS_BLACKJACK || state == State.HOUSE_IS_BLACKJACK || state == State.BOTH_BLACKJACK) {
			Log.i("blackjack", "State = " + state);
			invalidate();
			showResult(state, false);
			return; // the game ends without changing the buttons status
		} else if (state == State.HOUSE_SHOW_ACE){
			((MainActivity)context).showDialog();
		}
		restoreButtonsStatus(ButtonStatus.DEAL_CLICKED);
		if (blackJack.splitable()) {
			restoreButtonsStatus(ButtonStatus.SPLITABLE);
		}
	}
	
	public void stand() {
		// begin drawing house card until reaches 17
		restoreButtonsStatus(ButtonStatus.STAND_CLICKED);
		if (split && handOrder == Order.FIRST_HAND) {
			handOrder = Order.SECOND_HAND;
			restoreButtonsStatus(ButtonStatus.DEAL_CLICKED);
		} else { // no split or the second hand has finished
			blackJack.dealerDraw();
			invalidate(); // show the house cards
			endGame();
		}
	}
	
	public void hit() {
		surrenderButton.setEnabled(false);
		doubleButton.setEnabled(false);
		if (blackJack.hit(handOrder) == State.PLAYER_BUSTED) {
			//showResult(State.PLAYER_BUSTED, false);
			if (split && handOrder == Order.FIRST_HAND) {
				Log.i("blackjack", "First hand busted, move to second hand");
				handOrder = Order.SECOND_HAND;
				restoreButtonsStatus(ButtonStatus.DEAL_CLICKED);
			} else {
				endGame();
			}
		}
		invalidate(); // show the card, no matter busted or not
	}

	public void surrender() {
		wager.setLastWin(-wager.getBetAmount()/2);
		updateMoney(wager);
		restoreButtonsStatus(ButtonStatus.SURRENDER_CLICKED);
	}
	
	public void doubleBet() {
		restoreButtonsStatus(ButtonStatus.DEAL_CLICKED);
		if (blackJack.hit(handOrder) == State.PLAYER_BUSTED){
			//showResult(State.PLAYER_BUSTED, true);
			invalidate();
		} else {
			blackJack.dealerDraw();
			invalidate(); // show the house cards
			endGame();
		}
	}
	
	private void endGame() {
		if (!split) {
			State result = blackJack.computeResult(Order.FIRST_HAND);
			showResult(result, false);
		} else {
			State result = blackJack.computeResult(Order.FIRST_HAND);
			showResult(result, false);
			result = blackJack.computeResult(Order.SECOND_HAND);
			showResult(result, false);
		}
	}

	public void share() {
	}

	public void buyAssurance(boolean b) {
		Log.i("blackjack", "buyAssurance() called");
		Value holeValue = blackJack.houseHand.getHoleCardValue();
		if (holeValue.isTenCard()) {
			if (b) { // buy assurance and host is blackjack, lose half of bet, game ends
				wager.setLastWin(-wager.getBetAmount()/2);
				Toast toast = Toast.makeText(context, "You lose only half of your bet", Toast.LENGTH_SHORT);
				toast.show();
			} else { // didn't buy assurance and host is blackjack, lose 1.5 times bet and game ends
				wager.setLastWin(-wager.getBetAmount()*1.5);
				Toast toast = Toast.makeText(context, "You lose.", Toast.LENGTH_SHORT);
				toast.show();
			}
			updateMoney(wager);
			blackJack.houseHand.setFaceUp(1);
			invalidate();
			restoreButtonsStatus(ButtonStatus.GAME_STARTED);

		} else {
			if (b) { // buy assurance and host is not blackjack, lose assurance and game continues
				Toast toast = Toast.makeText(context, "You lost your assurance.", Toast.LENGTH_SHORT);
				toast.show();
				wager.setLastWin(-wager.getBetAmount()/2);
			} else { // didn't buy assurance and host is not blackjack, game continues
			}
		}
	}
	
	private void updateMoney(Wager wager)
	{
		Log.i("blackjack", String.format("totalCash = %f, lastWin = %f, betAmount = %f",  wager.getTotalCash(), wager.getLastWin(), wager.getBetAmount()));
		totalCash.setText("$ " + wager.getTotalCash());
		lastWin.setText("$ " + wager.getLastWin());
		betAmount.setText("$ " + wager.getBetAmount());
	}
	
	public void restoreButtonsStatus(ButtonStatus status)
	{
		Log.i("blackjack", "Setting button status to " + status);
		switch(status) {
		case GAME_STARTED:
		case SURRENDER_CLICKED:
			surrenderButton.setEnabled(false);
			standButton.setEnabled(false);
			hitButton.setEnabled(false);
			doubleButton.setEnabled(false);
			splitButton.setEnabled(false);
			dealButton.setEnabled(true);
			break;
		case DEAL_CLICKED:
			surrenderButton.setEnabled(true);
			standButton.setEnabled(true);
			hitButton.setEnabled(true);
			doubleButton.setEnabled(true);
			splitButton.setEnabled(false);
			dealButton.setEnabled(false);
			break;
		case STAND_CLICKED:
		case DOUBLE_CLICKED:
			surrenderButton.setEnabled(false);
			standButton.setEnabled(false);
			hitButton.setEnabled(false);
			doubleButton.setEnabled(false);
			splitButton.setEnabled(false);
			break;
		case SPLITABLE:
			splitButton.setEnabled(true);
			break;
		default:
			break;
		}
	}

	private void showResult(State state, Boolean doubled) {
		Map<State, BetResult> betMap = new EnumMap<State, BetResult>(State.class);
		betMap.put(State.PLAYER_IS_BLACKJACK, new BetResult(1.5, "You got blackjack"));
		betMap.put(State.HOUSE_IS_BLACKJACK, new BetResult(-1.5, "The house got blackjack"));
		betMap.put(State.BOTH_BLACKJACK, new BetResult(0, "Push"));
		betMap.put(State.PLAYER_WIN, new BetResult(doubled? 2: 1, "You win"));
		betMap.put(State.PLAYER_BUSTED, new BetResult(doubled? -2: -1, "You busted"));
		betMap.put(State.PLAYER_LOSE, new BetResult(doubled? -2: -1, "You lose"));
		betMap.put(State.HOUSE_BUSTED, new BetResult(doubled? 2: 1, "house busted"));
		betMap.put(State.PUSH,  new BetResult(0, "Push"));
		
		BetResult result = betMap.get(state);
		wager.setLastWin(wager.getBetAmount() * result.winTimes);
		updateMoney(wager);
		toast = Toast.makeText(context, result.message, Toast.LENGTH_SHORT);
		toast.show();
		restoreButtonsStatus(ButtonStatus.GAME_STARTED);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (!gameBegin) return;
		canvas.drawColor(0, Mode.CLEAR);
		if (split) {
			blackJack.playerHand.drawToCanvas(canvas, pokerSet, width/2 - 400, height/2, paint, offset);
			blackJack.playerHand2.drawToCanvas(canvas, pokerSet, width/2 + 100, height/2, paint, offset);
		} else {
			blackJack.playerHand.drawToCanvas(canvas, pokerSet, width/2 - 200, height/2, paint, offset);
		}
		blackJack.houseHand.drawToCanvas(canvas, pokerSet, width/2 - 200, height/12, paint, offset);
	}
	
	private void cacheBitmaps(PokerSet pokerSet)
	{
		for (Poker poker : pokerSet.getPokers()) {
			Suit suit = poker.getSuit();
			Value value = poker.getValue();
				String pokerStr = suit.toString().toLowerCase() + "_" + value.toString().toLowerCase();
				Bitmap mBitmap = BitmapFactory.decodeResource(res,
						res.getIdentifier(pokerStr, "drawable", pkgName));
				Bitmap resizedBitmap = getResizedBitmap(mBitmap, 192+96, 144+72);
				pokerSet.setPokerImage(poker, resizedBitmap);
		}
		Log.i("blackjack", "created cache for images " + pokerSet.size() );
	}
	
	private Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth)
	{
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float)newWidth)/width;
		float scaleHeight = ((float)newHeight)/height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		return resizedBitmap;
	}


	public void split() {
		split = true;
		blackJack.splitHand();
		invalidate();
	}
}
