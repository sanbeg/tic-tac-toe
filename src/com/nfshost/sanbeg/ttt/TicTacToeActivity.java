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
	
	static final int NUMBER_OF_SQUARES = 9;
	
	static final int DIALOG_ABOUT = 5;
	static final int DIALOG_WIN_X = 0;
	static final int DIALOG_WIN_O = 1;
	static final int DIALOG_TIE = 2;
	static final int DIALOG_ALERT = 6;
	static final int UI_UPDATE_PLAYER = 7;
	
	static final String[] smPlayers = {"X", "O"};
	private byte mCurrentPlayer = -1;
	private byte []  mBoardState;
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putByte("cp",mCurrentPlayer);
		savedInstanceState.putByteArray("boardState", mBoardState);
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
    		mCurrentPlayer = savedInstanceState.getByte("cp");
    		mBoardState = savedInstanceState.getByteArray("boardState");
    		Log.i("TTT", "create board");
        } 
        if (mBoardState == null) {
        	mBoardState = new byte[NUMBER_OF_SQUARES];
        	for (int i=0; i<mBoardState.length; ++i)
            	mBoardState[i] = -1;   	
        }
 
         
        // show next player, and update when we get a message
    	mNextPlayerView = (TextView) findViewById(R.id.next_player);
        mNextPlayerView.setText(next_label());
        mHandler = new Handler() {
        	@Override
        	public void handleMessage(Message msg) {
        		switch(msg.what){
        		case UI_UPDATE_PLAYER:
        			mNextPlayerView.setText(next_label());
        			break;
        		case DIALOG_TIE:
        		case DIALOG_WIN_X:
        		case DIALOG_WIN_O:
        			showDialog(msg.what);
        			break;
        		}
        	}
        };

        mWebView.addJavascriptInterface(this, "TicTacToe");
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
			reset_board();
			break;
		case R.id.item_about:
			showDialog(DIALOG_ABOUT);
			break;
		}
		return true;
	}
	
	private void reset_board() {
        for (int i=0; i<mBoardState.length; ++i)
        	mBoardState[i] = -1;
		mWebView.loadUrl("javascript:wipe_board()");

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
					reset_board();
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
		mHandler.sendEmptyMessage(mCurrentPlayer);
	}
	public void tie() {
		mHandler.sendEmptyMessage(DIALOG_TIE);
	}
	
	public void alert(String msg) {
		Bundle bundle = new Bundle();
		bundle.putString("msg", msg);
		showDialog(DIALOG_ALERT, bundle);
	}

	public void jsdebug(String msg) {
		Log.i("TTT-JS", msg);
	}
	public String restore() {
		
		String board = "";
		for (int i=0; i<mBoardState.length; ++i){
			if (mBoardState[i] < 0)
				board += " ";
			else
				board += smPlayers[mBoardState[i]];
			board += "|";
		}
		Log.i("TTT","restore from js:" + board);

		return board;
	}
	public String next_player(String s) {
		int pos = Integer.parseInt(s);
		++mCurrentPlayer;
		mCurrentPlayer %= smPlayers.length;
		mHandler.sendEmptyMessage(UI_UPDATE_PLAYER);
		
		mBoardState[pos] = mCurrentPlayer;
		Log.d("TTT", "set square " + pos + "  = " + smPlayers[mCurrentPlayer]);
		return smPlayers[mCurrentPlayer];
	}
	public int auto_place() {
		int rv = -1;
		for (int i=0; i<mBoardState.length; ++i){
			if (mBoardState[i]  >= 0)
				continue;
			rv = i;
			int low = i%3;
			int high = i/3;

			//look for match left/right
			int left = (low+2)%3+high*3;
			int right = (low+1)%3+high*3;
			if (	
					(mBoardState[left] >= 0) 
					&&
					(mBoardState[left] == mBoardState[right])
				){
				rv = i;
				break;
			}
			
			//look for match above/below
			int below = low + (high+2)%3*3;
			int above = low + (high+1)%3*3;
			if (
					(mBoardState[above] >= 0)
					&&
					(mBoardState[above] == mBoardState[below])
					){
				rv = i;
				break;
			}
			
			if (low == high) {
				int prev = (low+2)%3+(high+2)%3*3;
				int next = (low+1)%3+(high+1)%3*3;
				if (
						(mBoardState[prev] >= 0)
						&&
						(mBoardState[prev] == mBoardState[next])
						){
					rv = i;
					break;
				}
				
			}
			if (low+high == 2){
				int prev = (low+1)%3+(high+2)%3*3;
				int next = (low+2)%3+(high+1)%3*3;
				if (
						(mBoardState[prev] >= 0)
						&&
						(mBoardState[prev] == mBoardState[next])
						){
					rv = i;
					break;
				}
			
			}
			
		}
		return rv;
	}
}