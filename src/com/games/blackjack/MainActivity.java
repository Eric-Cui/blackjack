package com.games.blackjack;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.games.blackjack.R;

public class MainActivity extends Activity implements AssuranceDialog.AssuranceDialogListener {
	private BlackJackView blackjackView;
	public Button dealButton, surrenderButton, standButton, hitButton, doubleButton, splitButton, shareButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dealButton = (Button) findViewById(R.id.dealButton);
		surrenderButton = (Button) findViewById(R.id.surrenderButton);
		standButton = (Button) findViewById(R.id.standButton);
		hitButton = (Button) findViewById(R.id.hitButton);
		doubleButton = (Button) findViewById(R.id.doubleButton);
		splitButton = (Button) findViewById(R.id.splitButton);
		shareButton = (Button) findViewById(R.id.shareButton);
		
		blackjackView = (BlackJackView) findViewById(R.id.blackjack);
		
		dealButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				blackjackView.beginGame();
			}
		});
		
		surrenderButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				blackjackView.surrender();
			}
		});
		
		standButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				blackjackView.stand();
			}
		});
		
		hitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				blackjackView.hit();
			}
		});
		
		doubleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				blackjackView.doubleBet();
			}
		});
		
		splitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				blackjackView.split();
			}
		});
		
		shareButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(Intent.ACTION_SEND);
		         intent.setType("text/plain");
		         intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
		         intent.putExtra(Intent.EXTRA_TEXT, "testing text");
		         startActivity(Intent.createChooser(intent, "Text sharing"));
			}
		});
	}

	public void showDialog()
	{
		DialogFragment dialog = new AssuranceDialog();
		dialog.show(getFragmentManager(), "AssuranceDialogFragment");
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		blackjackView.buyAssurance(true);
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		blackjackView.buyAssurance(false);
	}
}
