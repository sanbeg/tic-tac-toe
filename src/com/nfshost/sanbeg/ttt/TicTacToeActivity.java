package com.nfshost.sanbeg.ttt;

import android.app.Activity;
import android.webkit.WebView;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
//import android.widget.*;
public class TicTacToeActivity extends Activity {
	WebView mWebView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mWebView = (WebView) findViewById(R.id.game_board);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.loadUrl("file:///android_asset/ttt.html");

    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.item_reset:
			mWebView.loadUrl("javascript:wipe_board()");
			break;
		}
		return true;
	}
}