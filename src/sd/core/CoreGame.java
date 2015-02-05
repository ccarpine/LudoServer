package sd.core;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sd.util.Constants;

public class CoreGame implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<Partecipant> partecipants;
	private GameBoard gameBoard;
	private String ipCurrentPartecipant;
	private boolean isDoubleTurn;
	private int turn;
	private boolean turnActive;

	/**
	 * Create a new empty cell
	 * 
	 * @param ipGamers
	 *            , IP list of all player
	 */
	public CoreGame(List<String> ipGamers) {
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
	
	public void setCurrentPartecipant(String ip) {
		this.ipCurrentPartecipant = ip;
	}

	public void incrementTurn() {
		this.turn++;
	}

	public int getTurn() {
		return this.turn;
	}

	public List<Partecipant> getPartecipants() {
		return this.partecipants;
	}

	public void setPartecipants(List<Partecipant> partecipants) {
		this.partecipants = partecipants;
	}

	public GameBoard getGameBoard() {
		return this.gameBoard;
	}

	/**
	 * 
	 * @return boolean, TRUE if how invoke is the current partecipant
	 */
	public boolean amItheCurrentPartecipant() {
		try {
			return this.ipCurrentPartecipant.equals(Inet4Address.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * @return random int from 0 to 6
	 */
	public int launchDie() {
		return 1 + new Random().nextInt(6);
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
	 * get the current partecipant
	 * 
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
	 * @param ip
	 *            , String ip of a specific partecipant
	 * @return partecipant, the next player
	 */
	public Partecipant getNextActivePartecipant(String ip) {
		for (int i = 0; i < this.partecipants.size(); i++) {
			if (ip.equals(this.partecipants.get(i).getIp())) {
				
				for(int j=0; j<this.partecipants.size(); j++) {
					if (this.partecipants.get((i + 1 + j) % this.partecipants.size()).isStatusActive())
						return this.partecipants.get((i + 1 + j) % this.partecipants.size());
				}
				
			}

		}
		return this.getMyPartecipant();
	}

	/**
	 * 
	 * @param color
	 *            of the partecipant
	 * @return int, index in partecipant list
	 */
	public int getIDPartecipantByColor(String color) {
		for (int i = 0; i < this.partecipants.size(); i++) {
			if (this.partecipants.get(i).getColor().equals(color)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Prepares the turn by setting the current player and returning his list of
	 * possible moves, setting isDoubleTurn to true if the launch die result is
	 * equal to 6
	 * 
	 * @param resultDie
	 *            , int the result of launch die
	 * @return List<Move>, all the possibile for partecipant
	 */
	public List<Move> initTurn() {
		Partecipant tempPartecipant = this.getMyPartecipant();
		this.ipCurrentPartecipant = tempPartecipant.getIp();
		if (tempPartecipant.getLastLaunch() == 6 && !this.isDoubleTurn) {
			this.isDoubleTurn = true;
		} else {
			this.isDoubleTurn = false;
		}
		return this.gameBoard.suggestMoves(tempPartecipant);
	}

	/**
	 * make move choosen and check if the partecipant win
	 * 
	 * @param chosenMove
	 *            , Move choosen by partecipant
	 * @return String, the color of eatean pawn if present
	 */
	public String handleTurn(Move chosenMove) {
		String result = this.gameBoard.makeMove(chosenMove,
				this.getMyPartecipant());
		if (result != null) {
			this.partecipants.get(this.getIDPartecipantByColor(result))
					.addPawnsInBench();
		}
		return result;
	}

	/**
	 * 
	 * @param partecipant
	 *            , list of all partecipant
	 * @param gameBoard
	 *            , current game board of the match with pawn in correct
	 *            position
	 * @param ipCurrentPartecipant
	 *            , IP of the new current partecipant
	 * @return int the result of the updateStatus (tell if the player have to
	 *         play again, send update to next partecipant...)
	 */
	public int updateStatus(List<Partecipant> partecipant, GameBoard gameBoard,
			String ipCurrentPartecipant) {
		this.ipCurrentPartecipant = ipCurrentPartecipant;
		this.partecipants = partecipant;
		this.gameBoard = gameBoard;
		/*
		 * check if my ip is equals to the last that has just played, means that
		 * you received the message that you have send
		 */
		if (this.gameBoard.isVictory(this.getCurrentPartecipant())) {
			return Constants.END_GAME;
		}
		if (this.getMyPartecipant().getIp().equals(this.ipCurrentPartecipant)) {
			if (this.isDoubleTurn)
				return Constants.PLAY_AGAIN;
			else
				return Constants.PLAY_NEXT;
		} else {
			return Constants.UPDATE_NEXT;
		}
	}

	/**
	 * @return the possibility to play
	 */
	public boolean isTurnActive() {
		return turnActive;
	}

	/**
	 * set the possibility to play
	 * 
	 * @param turnActive
	 *            , the possibility to play
	 */
	public void setTurnActive(boolean turnActive) {
		this.turnActive = turnActive;
	}

	public boolean isDoubleTurn() {
		return this.isDoubleTurn;
	}

	/**
	 * 
	 * @return, the number of ACTIVE partecipants before the caller
	 */
	private int getPreviousActivePartecipants() {

		int result = 0;
		for (int i = 0; i < this.getPartecipants().size(); i++) {
			if (this.getPartecipants().get(i).isStatusActive()) {
				if (!(this.getPartecipants().get(i).getIp().equals(this.getMyPartecipant().getIp()))) {
					result++;
				}

				else
					return result;
			}
		}

		return result;

	}
	
	/***
	 * 
	 * @param position of the invoking player (if the param is equal to size return the 
	 * number of all active partecipants)
	 * @return the number of the active partecipant before a specific position
	 */
	public int getNrActivePartecipantBefore(int position){
		int nrActivePartecipant = 0;
		for (int i = 0; i < position; i++ ){
			if (this.partecipants.get(i).isStatusActive()){
				nrActivePartecipant++;
			}
		}
		return nrActivePartecipant; 
	}
	
	/***
	 * 
	 * @param position of the invoking player  (if the param is equal to  return the 
	 * number of all active partecipants)
	 * @return the number of the active partecipant after a specific position
	 */
	public int getNrActivePartecipantAfter(int position){
		int nrActivePartecipant = 0;
		for (int i = position; i < this.partecipants.size(); i++ ){
			if (this.partecipants.get(i).isStatusActive()){
				nrActivePartecipant++;
			}
		}
		return nrActivePartecipant; 
	}
	/**
	 * 
	 * @return, the maximum time that the invoking partecipant MUST wait to
	 *          receive a message for buildGUI; if 0 is returned it means that
	 *          it must not wait for a message but it must build the gui and
	 *          forward it
	 */
	public long getTimeForBuildGUI() {
		/* for active partecipants ONLY */
		int numberPreviousAlive = this.getPreviousActivePartecipants();
		return (Constants.MAX_TIME_TO_BUILD_GUI + Constants.LATENCY)* numberPreviousAlive;
	}
	
	/***
	 * 
	 * @return, the maximun time that the invoking partecipant have to wait
	 * 			to recevice a messagge for the first update after the first turn
	 * 			exept for the fist player that wait for init turn (he has to receive the last buil gui message)
	 */
	public long getTimeForTheFirstCycle() {
		long timeToWait = 0;
		int activePartecipantBeforeMe = 0;
		
		activePartecipantBeforeMe = this.getNrActivePartecipantBefore(this.getMyPartecipant().getColorPosition());
		timeToWait = this.getNrActivePartecipantAfter(0) * Constants.LATENCY + 
				this.getNrActivePartecipantAfter((this.getMyPartecipant().getColorPosition()+1)) * Constants.MAX_TIME_TO_BUILD_GUI;
		
		if (activePartecipantBeforeMe > 0) {
			timeToWait += Constants.MAX_TIME_FOR_TURN;
			timeToWait += (activePartecipantBeforeMe -1) * Constants.MAX_TIME_FOR_UPDATE;
		}
		return timeToWait;
	}

	/***
	 * 
	 * @param type, can be UPDATE_NEXT or PLAY_NEXT, in the first case you send the update to the next
	 * partecipant, otherwise you send the play
	 * @param isDubleTurn tell if the current partecipant is also the next partecipant
	 * @return max number of millisecond the partecipant have to wait before ask the partecipant before IsAlive
	 * you have to wait for different time
	 * - if you are the current or the next of the current you don't have to wait for turn time 
	 * (you have to wait only for the update time)
	 * otherwise you have to wait for the round and for the update 
	 * Special case for the duble turn - the next player have to wait also for the turn time
	 */
	public long getTimeForCycle(int type,  boolean isDubleTurn) {
		long timeToWait = this.getNrActivePartecipantAfter(0) * Constants.LATENCY;
		timeToWait += Constants.MAX_TIME_FOR_TURN +  (this.getNrActivePartecipantAfter(0) - 1)  * Constants.MAX_TIME_FOR_UPDATE;
		
		/*Partecipant myPartecipant = this.getMyPartecipant();
		if (type == Constants.UPDATE_NEXT) { 
			if(myPartecipant.getIp().equals(this.ipCurrentPartecipant) ){ 
				timeToWait +=  (this.getNrActivePartecipantAfter(0) - 1)  * Constants.MAX_TIME_FOR_UPDATE;
			} else if (myPartecipant.getIp().equals(this.getNextActivePartecipant(this.ipCurrentPartecipant).getIp())) {
				if (isDubleTurn)
					timeToWait += Constants.MAX_TIME_FOR_TURN + (this.getNrActivePartecipantAfter(0) - 2)  * Constants.MAX_TIME_FOR_UPDATE;
				else 
					timeToWait +=  (this.getNrActivePartecipantAfter(0) - 1)  * Constants.MAX_TIME_FOR_UPDATE;
			} else 
				timeToWait += Constants.MAX_TIME_FOR_TURN + (this.getNrActivePartecipantAfter(0) - 2)  * Constants.MAX_TIME_FOR_UPDATE;
		} else 
				timeToWait += Constants.MAX_TIME_FOR_TURN + (this.getNrActivePartecipantAfter(0) - 2)  * Constants.MAX_TIME_FOR_UPDATE;
		*/
		return timeToWait;
	}
	
	/**
	 * 
	 * @param color
	 *            , the color from which to find the first active partecipant
	 *            before it
	 * @return, it returns the first previous active partecipant from the given
	 *          color
	 */
	public Partecipant getPreviousActive(String color) {

		int position = this.getIDPartecipantByColor(color);
		int difference = 1;
		Partecipant partecipant = this.getPartecipants().get(
				(position + this.getPartecipants().size() - difference)
						% this.getPartecipants().size());

		while (!partecipant.isStatusActive()) {

			difference++;
			partecipant = this.getPartecipants().get(
					(position + this.getPartecipants().size() - difference)
							% this.getPartecipants().size());
		}

		return partecipant;

	}

	/**
	 * this method set the player of the given color as not active and if he was
	 * the current partecipant then a new current partecipant is set, the first
	 * active after him.
	 * 
	 * @param color
	 *            , the color of the player that has crashed
	 */
	public void setUnactivePartecipant(String color) {
		int position = this.getIDPartecipantByColor(color);
		this.partecipants.get(position).setStatusActive(false);
		if (this.partecipants.get(position).getIp().equals(this.ipCurrentPartecipant)) {
			this.ipCurrentPartecipant = this.getNextActivePartecipant(this.ipCurrentPartecipant).getIp();
		}
		this.gameBoard.clearPawnByColor(color);
	}
	
	/**
	 * 
	 * @return, the index/position of the first active player 
	 */
	public int getFirstActiveIndex() {
		for(int i=0; i<this.partecipants.size(); i++) {
			if(this.partecipants.get(i).isStatusActive())
				return i;
		}
		return -1;
	}
	
	public boolean isVictory(Partecipant partecipant){
		if (this.getNrActivePartecipantAfter(0) == 1)
			return true;
		return this.gameBoard.isVictory(partecipant);
		/* per giocare da soli commentare tutto il corpo del metodo e lasciare solo il seguente*/
		//return false;
	}
}