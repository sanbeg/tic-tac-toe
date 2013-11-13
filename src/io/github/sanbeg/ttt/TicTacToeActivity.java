package io.github.sanbeg.ttt;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.webkit.WebView;
import android.widget.TextView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class TicTacToeActivity extends Activity {
	WebView mWebView;
	TextView mNextPlayerView;
	Handler mHandler, mInstanceHandler=null;
	
	static final int NUMBER_OF_SQUARES = 9;
	
	static final int DIALOG_WIN_X = 0;
	static final int DIALOG_WIN_O = 1;
	static final int DIALOG_TIE = 2;
	static final int DIALOG_YOU_WIN = 3;
	static final int DIALOG_YOU_LOSE = 4;
	
	static final int DIALOG_ABOUT = 5;
	static final int DIALOG_ALERT = 6;
	static final int UI_UPDATE_PLAYER = 7;
	
	static final String[] smPlayers = {"X", "O"};
	private byte mCurrentPlayer = -1, mHumanPlayer = -1;
	private byte []  mBoardState;
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putByte("cp",mCurrentPlayer);
		savedInstanceState.putByte("hp",mHumanPlayer);
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
    		mHumanPlayer = savedInstanceState.getByte("hp",(byte) -1);
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
        		case DIALOG_YOU_WIN:
        		case DIALOG_YOU_LOSE:
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
		case R.id.item_settings:
			startActivity(new Intent(this,Prefs.class));
		}
		return true;
	}
	
	/**
	 * Called from the menu to reset the board.
	 */
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
    	case DIALOG_YOU_WIN:
    	case DIALOG_YOU_LOSE:
    		
    		switch(id){
    	    	case DIALOG_WIN_X:
    	    	case DIALOG_WIN_O:
    	    		builder.setMessage( 
    	    				String.format(this.getString(R.string.winner_is),(smPlayers[id]))
    	    		);
    	    		break;
    	    	case DIALOG_TIE:
    	    		builder.setMessage(R.string.tied_game);
    	    		break;
    	    	case DIALOG_YOU_WIN:
    	    		builder.setMessage(R.string.you_win);
    	    		break;
    	    	case DIALOG_YOU_LOSE:
    	    		builder.setMessage(R.string.you_lose);
    	    		break;
    		}
    		
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
	
	/**
	 * Called from JavaScript to display the winner dialog
	 */
	public void win() {
		if (mHumanPlayer >= 0){
			if (mHumanPlayer == mCurrentPlayer)
				mHandler.sendEmptyMessage(DIALOG_YOU_WIN);
			else
				mHandler.sendEmptyMessage(DIALOG_YOU_LOSE);
		}
		else		
			mHandler.sendEmptyMessage(mCurrentPlayer);
	}
	/**
	 * Called from JavaScript to display the tie dialog
	 */
	public void tie() {
		mHandler.sendEmptyMessage(DIALOG_TIE);
	}
	
	/**
	 * Replacement for the normal JavaScript alert() function
	 * @param msg
	 */
	public void alert(String msg) {
		Bundle bundle = new Bundle();
		bundle.putString("msg", msg);
		showDialog(DIALOG_ALERT, bundle);
	}

	/**
	 * Simple debug method to write to the log from JavaScript
	 * @param msg the message to write
	 */
	public void jsdebug(String msg) {
		Log.i("TTT-JS", msg);
	}
	
	/**
	 * Serialize the board into a string; called 
	 * from JavaScript to update the HTML.
	 * @return String representation of the board
	 */
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
	/**
	 * Called from JavaScript to register a move with the java app, and
	 * get the string representing the player for the UI.
	 * @param s The square where the move was placed (0-8)
	 * @return The player to assign the move to.
	 */
	public String next_player(String s) {
		int pos = Integer.parseInt(s);
		++mCurrentPlayer;
		mCurrentPlayer %= smPlayers.length;
		mHandler.sendEmptyMessage(UI_UPDATE_PLAYER);
		
		mBoardState[pos] = mCurrentPlayer;
		Log.d("TTT", "set square " + pos + "  = " + smPlayers[mCurrentPlayer]);
		return smPlayers[mCurrentPlayer];
	}
	/**
	 * Find a location to place the next move in a single-player game
	 * @return square number (0-8), or -1 to skip move.
	 */
	public int auto_place() {
		if (! Prefs.getSP(getBaseContext())) {
			mHumanPlayer = -1;	
			return -1;
		} else {
			mHumanPlayer = mCurrentPlayer;
			Log.d("TTT", "Human is " + smPlayers[mCurrentPlayer]);
		}
		
		//return auto_place_easy();
		return auto_place_normal();
		//return auto_place_randomly();
	}
	private int auto_place_easy() {
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

	private int auto_place_for(byte me) {
		int rv = -1;
		for (int i=0; i<mBoardState.length; ++i){
			if (mBoardState[i]  >= 0)
				continue;

			int low = i%3;
			int high = i/3;

			//look for match left/right
			int left = (low+2)%3+high*3;
			int right = (low+1)%3+high*3;
			if (	
					(mBoardState[left] == me) 
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
					(mBoardState[above] == me)
					&&
					(mBoardState[above] == mBoardState[below])
					){
				rv = i;
				break;
			}
			//look for 2 diagonal matches
			if (low == high) {
				int prev = (low+2)%3+(high+2)%3*3;
				int next = (low+1)%3+(high+1)%3*3;
				if (
						(mBoardState[prev] == me)
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
						(mBoardState[prev] == me)
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

	int auto_place_randomly() {
		int square = -1;
		double tried = 0;
		Random rand = new Random();
		
		for (int i=0; i<mBoardState.length; ++i){
			if (mBoardState[i]  >= 0)
				continue;
			if ( rand.nextDouble() < 1/++tried )
				square = i;

		}		
		return square;
	}
	private int auto_place_normal() {
		byte bot = mHumanPlayer;
		++bot;
		bot %= smPlayers.length;
		
		int square = auto_place_for(bot);
		if (square < 0){
			square = auto_place_for(mHumanPlayer);
			Log.d("TTT", "Try block @" + square);
		}
		if (square < 0){
			square = auto_place_randomly();
			Log.d("TTT", "randome place " + square);
		}
		return square;		
	}
}