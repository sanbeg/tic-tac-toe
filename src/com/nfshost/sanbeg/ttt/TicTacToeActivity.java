package com.nfshost.sanbeg.ttt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.webkit.WebView;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
//import android.widget.*;
public class TicTacToeActivity extends Activity {
	WebView mWebView;

	static final int DIALOG_ABOUT = 5;
	static final int DIALOG_WIN_X = 0;
	static final int DIALOG_WIN_O = 1;

	static final String[] smPlayers = {"X", "O"};
	int mCurrentPlayer = -1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mWebView = (WebView) findViewById(R.id.game_board);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.loadUrl("file:///android_asset/ttt.html");
        mWebView.addJavascriptInterface(this, "TicTacToe");

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
		case R.id.item_about:
			//startActivity(new Intent(this,About.class));
			showDialog(DIALOG_ABOUT);
			break;
		}
		return true;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    switch(id) {
	    case DIALOG_ABOUT:
	    	builder.setMessage(this.getText(R.string.about_content));
	    	break;
	    case DIALOG_WIN_X:
    	case DIALOG_WIN_O:
	    	builder.setMessage( this.getText(R.string.winner_is) + " " + smPlayers[id]);
	    	builder.setPositiveButton("reset", new Dialog.OnClickListener(){
	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
	    			mWebView.loadUrl("javascript:wipe_board()");

				}
	    	});
	    	
	    	break;

	    }
		return builder.create();
	};
	

	public void win() {
		showDialog(mCurrentPlayer);
	}
	public String next_player() {
		++mCurrentPlayer;
		mCurrentPlayer %= smPlayers.length;
		return smPlayers[ mCurrentPlayer ];
	}
}