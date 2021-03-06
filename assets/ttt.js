var game_winner = null;
var open_squares = 9;


function flip(node,auto) {
    if ( (node.innerHTML != "") || game_winner ) return false;
    TicTacToe.jsdebug("flip: " + node.id + " = " + node.id.charAt(1));
    
    //send square ID to java so app can update its state,
    //and get correct player ID back from app.
    var player = node.innerHTML = TicTacToe.next_player(node.id.charAt(1));

    game_winner = winner();
    if (game_winner) {
    	TicTacToe.win();
    } else if (--open_squares == 0) {
    	TicTacToe.tie();
    } else if ( ! auto ){
	var put_o = TicTacToe.auto_place();
	if (put_o >= 0) {
	    var squares = document.getElementsByClassName('sq');
	    flip(squares[put_o],true);
	}
    }
}


function wipe_board() {
    game_winner = null;
    open_squares = 9;
    
    var squares = document.getElementsByClassName('sq');
    for (var i=0; i<squares.length; ++i)
	squares[i].innerHTML="";
	     
}

function thaw()
{
    var msg = TicTacToe.restore();
    
    var squares = document.getElementsByClassName('sq');

    if (msg != null) {
    	TicTacToe.jsdebug("got board(" + typeof(msg) + "): " + msg);
	open_squares = 9;
    	for (var i=0; i<squares.length; ++i){
    		var c = msg.charAt(i*2);
    		if (typeof c == "number"){
    			c =  String.fromCharCode(c);
		}
    		if (c != " "){
    			squares[i].innerHTML = c;
			-- open_squares;
		}
    	}
    }
}


// can get width with window.innerWidth. document.body.clientWidth, etc
function fit_board() {
    var land=false;
    var view_size = window.innerWidth;

    if (view_size > window.innerHeight) {
    	view_size = window.innerHeight;
    	land=true;
    }

    var box_size = view_size/4;
    var text_size = box_size * 0.75;
    var squares = document.getElementsByClassName("sq");
    for (var i=0; i<squares.length; ++i) {
    	var s = squares[i].style;
    	s.width = s.height = box_size;
    	//s['line-height'] = s.height;
    	s['font-size'] = text_size;
    }

    var board_table = document.getElementsByTagName('table')[0];
    var board = board_table.style;

    //board.margin=view_size/8;
    board.margin = (view_size - board_table.offsetWidth)/2;
    
    var player = document.getElementsByClassName('player')[0].style;
    if (land) {
    	board.float='left';
    	player.width=view_size-window.innerHeight;
    } else {
    	board.float=null;
    	player.width=null;
    }
}

function winner() {
	for (var r_type in {r:1, c:1, d:1}) {
		for (var i=1; i<=3; ++i) {
			var row = document.getElementsByClassName(r_type+i);
			if (row.length == 0) break; //there's no d3
			var h0=row[0].innerHTML;
			if ((h0) && (h0==row[1].innerHTML) && (h0==row[2].innerHTML))
				return h0;
		}
	}
	return null;
}

