package com.nfshost.sanbeg.ttt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.webkit.WebView;
import android.widget.TextView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.widget.*;
public class TicTacToeActivity extends Activity {
	WebView mWebView;
	TextView mNextPlayerView;
	Handler mHandler, mInstanceHandler=null;
	
	static final int DIALOG_ABOUT = 5;
	static final int DIALOG_WIN_X = 0;
	static final int DIALOG_WIN_O = 1;
	static final int DIALOG_TIE = 2;
	static final int DIALOG_ALERT = 6;
	
	static final String[] smPlayers = {"X", "O"};
	int mCurrentPlayer = -1;
	String mBoard = null;
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("cp",mCurrentPlayer);
		

		synchronized (this) {
			Log.i("TTT", "requesting board");
			mWebView.loadUrl("javascript:freeze()");
			try {
				this.wait();
				Log.i("TTT", "Saving board:" + mBoard);
				savedInstanceState.putString("board", mBoard);
				//alert(mBoard);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	private String next_label() {
		return this.getText(R.string.next_is)+" " + smPlayers[(mCurrentPlayer+1)%smPlayers.length];
	}
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // create web view for board
        mWebView = (WebView) findViewById(R.id.game_board);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setVerticalScrollBarEnabled(false);

        //recover previous data
        if (savedInstanceState != null) {
    		mCurrentPlayer = savedInstanceState.getInt("cp");
    		mBoard = savedInstanceState.getString("board");
    		Log.i("TTT", "create board:" + mBoard);
        }
 
        mWebView.addJavascriptInterface(this, "TicTacToe");
        mWebView.loadUrl("file:///android_asset/ttt.html");
        
        // show next player, and update when we get a message
    	mNextPlayerView = (TextView) findViewById(R.id.next_player);
        mNextPlayerView.setText(next_label());
        mHandler = new Handler() {
        	@Override
        	public void handleMessage(Message msg) { 
    			mNextPlayerView.setText(next_label());
    		}
        };
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
	    	builder.setMessage(R.string.about_content);
	    	break;
	    case DIALOG_ALERT:
	    	//will set message later
	    	builder.setMessage("");
	    	break;
	    case DIALOG_WIN_X:
    	case DIALOG_WIN_O:
    	case DIALOG_TIE:
    		if (id == DIALOG_TIE)
        		builder.setMessage(R.string.tied_game);
    		else
    			builder.setMessage( this.getText(R.string.winner_is) + " " + smPlayers[id]);
    		
	    	builder.setPositiveButton("Reset", new Dialog.OnClickListener(){	
				@Override
				public void onClick(DialogInterface dialog, int which) {
	    			mWebView.loadUrl("javascript:wipe_board()");

				}
				
	    	});
	    	builder.setNeutralButton("Dismiss", new Dialog.OnClickListener(){
	    		@Override
	    		public void onClick(DialogInterface dialog, int which){
	    			dialog.dismiss();
	    		}
	    	});
	    	builder.setNegativeButton("Exit", new Dialog.OnClickListener(){
	    		@Override
	    		public void onClick(DialogInterface dialog, int which){
	    			finish();
	    			
	    		}
	    	});
    	
	    	break;
    		
	    }
		return builder.create();
	};
	
	@Override
	public void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
		if (id == DIALOG_ALERT)
			((AlertDialog)dialog).setMessage( bundle.getCharSequence("msg") );
	}
	public void win() {
		showDialog(mCurrentPlayer);
	}
	public void tie() {
		showDialog(DIALOG_TIE);
	}
	
	public void alert(String msg) {
		Bundle bundle = new Bundle();
		bundle.putString("msg", msg);
		showDialog(DIALOG_ALERT, bundle);
	}
	public void save(String str){
		synchronized (this) {
	 		mBoard=str;
			this.notify();			
		}
	}
	public void jsdebug(String msg) {
		Log.i("TTT-JS", msg);
	}
	public String restore() {
		Log.i("TTT","restore from js:" + mBoard);
		return mBoard;
	}
	public String next_player() {
		++mCurrentPlayer;
		mCurrentPlayer %= smPlayers.length;
		mHandler.sendEmptyMessage(0);
		
		return smPlayers[mCurrentPlayer];
	}
}