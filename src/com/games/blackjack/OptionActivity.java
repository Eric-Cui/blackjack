package com.games.blackjack;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by cuiw on 11/17/2015.
 */
public class OptionActivity extends Activity {
    Button standaloneButton, networkButton;

    public void clickHandler(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        standaloneButton = (Button)findViewById(R.id.standaloneButton);
        networkButton = (Button)findViewById(R.id.networkButton);
    }
}
