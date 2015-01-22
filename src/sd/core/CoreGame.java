package sd.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sd.util.Constants;

public class CoreGame implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<Partecipant> partecipants;
	private GameBoard gameBoard;
	private String ipCurrentPartecipant;
	private String winner;
	private boolean isDoubleTurn;
	private int turn; /*increase each time a player play*/
	private int round; 

	public CoreGame(List<String> ipGamers) {

		this.winner = null;
		this.partecipants = new ArrayList<Partecipant>();
		this.isDoubleTurn = false;
		this.turn = 0;

		// generate the partecipants giving them a color according to their
		// registration order
		for (int i = 0; i < ipGamers.size(); i++) {
			Partecipant partecipant = new Partecipant(ipGamers.get(i),
					Constants.COLOR[i], i);
			this.partecipants.add(partecipant);
		}
		// sets the partecipant that begins the game, the first of the list
		this.ipCurrentPartecipant = ipGamers.get(0);
		this.gameBoard = new GameBoard();
	}

	public List<Partecipant> getPartecipants() {
		return this.partecipants;
	}
	
	// returns the die launch result
	private int getDie() {
		return 1 + new Random().nextInt(6);
	}
	
	public int getRound() {
		return (this.turn % (this.partecipants.size())) + 1;
	}
	
	public int updateStatus(List<Partecipant> partecipant, GameBoard gameBoard, String ipCurrentPartecipant) {
		
		this.ipCurrentPartecipant = ipCurrentPartecipant;
		// check if my ip is equals to the last that has just played
		String myIP = this.getMyPartecipant().getIp();
		if (myIP.equals(this.ipCurrentPartecipant)) {
			if (this.winner != null) {
				return Constants.END_GAME;
			} else if(this.isDoubleTurn)
				return Constants.PLAY_AGAIN;
			else
				return Constants.PLAY_NEXT;
		}
		else {
			this.partecipants = partecipant;
			this.gameBoard = gameBoard;
			return Constants.UPDATE_NEXT;
		}
	}

	public Partecipant getMyPartecipant() {

		Partecipant myPartecipant = null;
		for (int i = 0; i < this.partecipants.size(); i++) {

			if (this.partecipants.get(i).isMine()) {
				myPartecipant = this.partecipants.get(i);
				break;
			}
		}
		return myPartecipant;
	}

	// return the next player from the given IP
	public Partecipant getNextPartecipant(String ip) {
		
		Partecipant partecipant = null;
		for (int i = 0; i < this.partecipants.size(); i++) {
			if (ip.equals(this.partecipants.get(i).getIp())) {
				partecipant = this.partecipants.get( (i +1) % this.partecipants.size());
				break;
			}
		}
		return partecipant;
	}

	public int getIDPartecipantByColor(String color) {

		for (int i = 0; i < this.partecipants.size(); i++) {
			if (this.partecipants.get(i).getColor().equals(color)) {
				return i;
			}
		}
		/* it should never get here */
		return -1;
	}

	/* prepares the turn by setting the current player and returning his list of possible moves */
	public List<Move> initTurn() {
		Partecipant tempPartecipant = this.getMyPartecipant();
		this.ipCurrentPartecipant = tempPartecipant.getIp();
		int resultDie = this.getDie();
		System.out.println("result die: "+ resultDie);
		if (resultDie == 6 && !this.isDoubleTurn) {
			this.isDoubleTurn = true;
		} else {
			this.isDoubleTurn = false;
		}
		return this.gameBoard.suggestMoves(tempPartecipant, resultDie);
	}

	public void handleTurn(Move chosenMove) {
		String result = this.gameBoard.makeMove(chosenMove,this.getMyPartecipant());

		if (result != null) {
			this.partecipants.get(this.getIDPartecipantByColor(result)).addPawnsInBench();
		}

		Partecipant partecipant = this.getMyPartecipant();

		if (this.gameBoard.isVictory(partecipant)) {
			this.winner = partecipant.getColor();
		}

	}
	
	public GameBoard getGameBoard() {
		return this.gameBoard;
	}

	public boolean amItheCurrentPartecipant( ){
		return this.ipCurrentPartecipant.equals(this.getMyPartecipant().getIp());
	}
}