package sd.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sd.util.Constants;

public class CoreGame implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<Partecipant> partecipants;
	private GameBoard gameBoard;
	private String ipCurrentPartecipant;
	private String winner;
	private boolean isDoubleTurn;
	private int turn; 
	private boolean turnActive;

	/**  Create a new empty cell
	 *   @param ipGamers, IP list of all player
	 */
	public CoreGame(List<String> ipGamers) {
		this.winner = null;
		this.partecipants = new ArrayList<Partecipant>();
		this.isDoubleTurn = false;
		this.turn = 0;
		this.turnActive = false;
		// generate the partecipants giving them a color according to their
		// registration order
		for (int i=0; i<ipGamers.size(); i++) {
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
	
	public GameBoard getGameBoard() {
		return this.gameBoard;
	}
	
	/**
	 * 
	 * @return boolean, TRUE if how invoke is the current partecipant
	 */
	public boolean amItheCurrentPartecipant( ){
		return this.ipCurrentPartecipant.equals(this.getMyPartecipant().getIp());
	}
	
	/**
	 * 
	 * @return random int from 0 to 6
	 */
	public int launchDie() {
		//TODO 
		//return 1 + new Random().nextInt(6);
		return 5;
	}
	
	/**
	 * 
	 * @return int represent the current turn
	 * 
	 */
	public int getRound() {
		return (this.turn % (this.partecipants.size())) + 1;
	}

	/**
	 * 
	 * @return partecipant of client that invoke
	 */
	public Partecipant getMyPartecipant() {
		for (int i = 0; i < this.partecipants.size(); i++) {
			if (this.partecipants.get(i).isMine()) {
				return this.partecipants.get(i);
			}
		}
		return null;
	}
	
	/**
	 *  get the current partecipant
	 * @return the current partecipant in according to the current ip
	 */
	public Partecipant getCurrentPartecipant() {
		for (int i = 0; i < this.partecipants.size(); i++) {
			if (this.partecipants.get(i).getIp().equals(ipCurrentPartecipant)) {
				return this.partecipants.get(i);
			}
		}
		return null;
	}

	/**
	 * 
	 * @param ip, String ip of a specific partecipant
	 * @return partecipant, the next player
	 */
	public Partecipant getNextPartecipant(String ip) {
		for (int i = 0; i < this.partecipants.size(); i++) {
			if (ip.equals(this.partecipants.get(i).getIp())) {
				return this.partecipants.get( (i +1) % this.partecipants.size());
			}
		}
		return null;
	}

	/**
	 * 
	 * @param color of the partecipant
	 * @return int, index in partecipant list 
	 */
	public int getIDPartecipantByColor(String color) {
		for (int i=0; i<this.partecipants.size(); i++) {
			if (this.partecipants.get(i).getColor().equals(color)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Prepares the turn by setting the current player and returning his list of possible moves, setting
	 * isDoubleTurn to true if the launch die result is equal to 6
	 * @param resultDie, int the result of launch die
	 * @return List<Move>, all the possibile for partecipant 
	 */
	public List<Move> initTurn(int resultDie) {
		Partecipant tempPartecipant = this.getMyPartecipant();
		this.ipCurrentPartecipant = tempPartecipant.getIp();
		if (resultDie == 6 && !this.isDoubleTurn) {
			this.isDoubleTurn = true;
		} else {
			this.isDoubleTurn = false;
		}
		return this.gameBoard.suggestMoves(tempPartecipant, resultDie);
	}
	
	/**
	 * make move choosen and check if the partecipant win
	 * @param chosenMove, Move choosen by partecipant
	 * @return String, the color of eatean pawn if present
	 */
	public String handleTurn(Move chosenMove) {
		String result = this.gameBoard.makeMove(chosenMove,this.getMyPartecipant());
		if (result != null) {
			this.partecipants.get(this.getIDPartecipantByColor(result)).addPawnsInBench();
		}
		if (this.gameBoard.isVictory(this.getMyPartecipant())) {
			this.winner = this.getMyPartecipant().getColor();
		}
		this.turnActive = false;
		return result;
	}
	
	/**
	 * 
	 * @param partecipant, list of all partecipant 
	 * @param gameBoard, current game board of the match with pawn in correct position
	 * @param ipCurrentPartecipant, IP of the new current partecipant
	 * @return int the result of the updateStatus (tell if the player have to play again, send update to next partecipant...)
	 */
	public int updateStatus(List<Partecipant> partecipant, GameBoard gameBoard, String ipCurrentPartecipant) {
		this.ipCurrentPartecipant = ipCurrentPartecipant;
		/* check if my ip is equals to the last that has just played,
		 * means that you received the message that you have send */
		if (this.getMyPartecipant().getIp().equals(this.ipCurrentPartecipant)) {
			if (this.winner != null) {
				return Constants.END_GAME;
			} else if(this.isDoubleTurn)
				return Constants.PLAY_AGAIN;
			else
				return Constants.PLAY_NEXT;
		} else {
			this.partecipants = partecipant;
			this.gameBoard = gameBoard;
			return Constants.UPDATE_NEXT;
		}
	}

	/**
	 * @return the possibility to play
	 */
	public boolean isTurnActive() {
		return turnActive;
	}

	/** set the possibility to play
	 * @param turnActive, the possibility to play
	 */
	public void setTurnActive(boolean turnActive) {
		this.turnActive = turnActive;
	}
}