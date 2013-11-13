package io.github.sanbeg.ttt;

import java.util.Random;

import android.util.Log;

class Robot {

	final Random random = new Random( System.currentTimeMillis() );
	final byte mBoardState[];
	final int mNumPlayers;
	
	public Robot (byte state[], int n_players) {
		mBoardState = state;
		mNumPlayers = n_players;
	}
	public int auto_place_easy() {
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

	private int auto_place_randomly() {
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
	
	public int auto_place_normal(byte human) {
		byte bot = (byte) ((human + 1) % mNumPlayers);
		
		int square = auto_place_for(bot);
		if (square < 0){
			square = auto_place_for(human);
			Log.d("TTT", "Try block @" + square);
		}
		if (square < 0){
			square = auto_place_randomly();
			Log.d("TTT", "random place " + square);
		}
		return square;		
	}
}
