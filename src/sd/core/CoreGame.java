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
	 * It initializes the logical core of the game by inserting the list of the players, setting
	 * the current player and the number of turn and it allocates the representation in memory of the game board
	 * @param ipGamers String with the ip addresses of all players
	 */
	public CoreGame(List<String> ipGamers) {
		this.partecipants = new ArrayList<Partecipant>();
		this.isDoubleTurn = false;
		this.turn = 0;
		this.turnActive = false;
		// generate the partecipants giving them a color according to their
		// registration order
		for (int i=0; i<ipGamers.size(); i++) {
			Partecipant partecipant = new Partecipant(ipGamers.get(i), Constants.COLOR[i], i);
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
	 * @return boolean, TRUE if the invoker process is the current partecipant
	 */
	public boolean amItheCurrentPartecipant() {
		try {
			return this.ipCurrentPartecipant.equals(Inet4Address.getLocalHost()
					.getHostAddress());
		} catch (UnknownHostException | NullPointerException e) {
			return false;
		}
	}

	/**
	 * @return random int from 0 to 6
	 */
	public int launchDie() {
		return 1 + new Random().nextInt(6);
	}

	/**
	 * @return Partecipant of the invoking process
	 */
	public Partecipant getMyPartecipant() {
		for (int i=0; i<this.partecipants.size(); i++) {
			if (this.partecipants.get(i).isMine()) {
				return this.partecipants.get(i);
			}
		}
		return null;
	}

	/**
	 * Get the current partecipant
	 * @return the current Partecipant in according to the current ip
	 */
	public Partecipant getCurrentPartecipant() {
		for (int i=0; i<this.partecipants.size(); i++) {
			if (this.partecipants.get(i).getIp().equals(ipCurrentPartecipant)) {
				return this.partecipants.get(i);
			}
		}
		return null;
	}

	/**
	 * @param ip String with the ip address of a specific partecipant
	 * @return Partecipant, the next player following the one of given ip address
	 */
	public Partecipant getNextActivePartecipant(String ip) {
		for (int i=0; i<this.partecipants.size(); i++) {
			if (ip.equals(this.partecipants.get(i).getIp())) {
				for (int j=0; j<this.partecipants.size(); j++) {
					if (this.partecipants.get((i + 1 + j) % this.partecipants.size())
							.isStatusActive()) {
						return this.partecipants.get((i + 1 + j) % this.partecipants.size());
					}
				}

			}

		}
		return this.getMyPartecipant();
	}

	/**
	 * @param color String of the color partecipant
	 * @return int, index/position partecipant color in list
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
	 * It prepares the turn by setting the current player and returning his list of
	 * possible moves and it sets isDoubleTurn to true if the launch die result is
	 * equal to 6
	 * @return List<Move>, all the possibile moves for partecipant according to his die launch
	 */
	public List<Move> initTurn() {
		Partecipant tempPartecipant = this.getMyPartecipant();
		if (tempPartecipant.getLastLaunch() == 6 && !this.isDoubleTurn) {
			this.isDoubleTurn = true;
		} else {
			this.isDoubleTurn = false;
		}
		return this.gameBoard.suggestMoves(tempPartecipant);
	}

	/**
	 * It applies the chosen mvoe and checks if the partecipant wins
	 * @param chosenMove Move choosen by partecipant
	 * @return String the color of an eatean pawn, if present
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
	 * @param partecipants list of all partecipants, List<Partecipant>
	 * @param gameBoard current game board of the match with pawns in correct
	 *            positions, GameBoard
	 * @param ipCurrentPartecipant String with the IP of the new current partecipant
	 * @return int, the result of the updateStatus (it establishes if the player has to
	 *         play again and sends the update to next partecipant...)
	 */
	public int updateStatus(List<Partecipant> partecipant, GameBoard gameBoard,
			String ipCurrentPartecipant) {
		this.ipCurrentPartecipant = ipCurrentPartecipant;
		this.partecipants = partecipant;
		this.gameBoard = gameBoard;
		/*
		 * check if my ip is equals to the last that has just played: if so it means that
		 * you received the message that you have send
		 */
		if (this.gameBoard.isVictory(this.getCurrentPartecipant())) {
			return Constants.END_GAME;
		} else if (this.getMyPartecipant().getIp().equals(this.ipCurrentPartecipant)) {
			if (this.isDoubleTurn) {
				return Constants.PLAY_AGAIN;
			} else {
				return Constants.PLAY_NEXT;
			}
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
	 * It sets the possibility to play
	 * @param turnActive, the possibility to play
	 */
	public void setTurnActive(boolean turnActive) {
		this.turnActive = turnActive;
	}

	/**
	 * It checks if the double turn for the current partecipant is enabled
	 */
	public boolean isDoubleTurn() {
		return this.isDoubleTurn;
	}

	public void setDoubleTurn(boolean value) {
		this.isDoubleTurn = value;
	}

	/***
	 * @param positon integer related to the position of the invoking player (if the parameter is equal to partecipants size then
	 * 		 it returns the number of all active partecipants)
	 * @return the number of the active partecipant before a specific position
	 */
	public int getNrActivePartecipantBefore(int position) {
		int nrActivePartecipant = 0;
		for (int i=0; i<position; i++) {
			if (this.partecipants.get(i).isStatusActive()) {
				nrActivePartecipant++;
			}
		}
		return nrActivePartecipant;
	}

	/***
	 * @param position an integer related to of the invoking player position in the list
	 * @return the number of the active partecipants after a specific position
	 */
	public int getNrActivePartecipantAfter(int position) {
		int nrActivePartecipant = 0;
		for (int i=position; i<this.partecipants.size(); i++) {
			if (this.partecipants.get(i).isStatusActive()) {
				nrActivePartecipant++;
			}
		}
		return nrActivePartecipant;
	}

	/**
	 * @return long, the maximum time that the invoking partecipant MUST wait to
	 *          receive a message for buildGUI; if 0 is returned then it means that
	 *          the player must not wait for a message but it must build the gui at once and
	 *          forward it
	 */
	public long getTimeForBuildGUI() {
		/* for active partecipants ONLY */
		int numberPreviousAlive = this.getNrActivePartecipantBefore(this.partecipants.size());
		return (Constants.MAX_TIME_TO_BUILD_GUI + Constants.LATENCY)
				* numberPreviousAlive;
	}

	/**
	 * @return long, the maximun time that the invoking partecipant MUST to wait to
	 *          recevice a message for the first update after the first turn
	 *          except for the fist player that will wait for initTurn() (he has to
	 *          receive the last builGui() message)
	 */
	public long getTimeForTheFirstCycle() {
		int activePartecipantBeforeMe = this.getNrActivePartecipantBefore(this
				.getMyPartecipant().getColorPosition());
		long timeToWait = this.getNrActivePartecipantAfter(0)
				* Constants.LATENCY
				+ this.getNrActivePartecipantAfter((this.getMyPartecipant()
						.getColorPosition() + 1))
				* Constants.MAX_TIME_TO_BUILD_GUI;
		if (activePartecipantBeforeMe > 0) {
			timeToWait += Constants.MAX_TIME_FOR_TURN;
			timeToWait += (activePartecipantBeforeMe - 1)
					* Constants.MAX_TIME_FOR_UPDATE;
		}
		return timeToWait;
	}

	/**
	 * @param type an integer can be UPDATE_NEXT or PLAY_NEXT: in the first case you send
	 *            the update to the next partecipant, otherwise you send the
	 *            play permission
	 * @param isDubleTurn a boolean which it establishes if the current partecipant is also the next partecipant
	 * @return long, max number of millisecond the partecipant have to wait before ask
	 *         the partecipant before IsAlive you have to wait for different
	 *         time - if you are the current or the next of the current you
	 *         don't have to wait for turn time (you have to wait only for the
	 *         update time) otherwise you have to wait for the round and for the
	 *         update Special case for the duble turn - the next player have to
	 *         wait also for the turn time
	 */
	public long getTimeForCycle(int type, boolean isDubleTurn) {
		long timeToWait = this.getNrActivePartecipantAfter(0)
				* Constants.LATENCY;
		Partecipant myPartecipant = this.getMyPartecipant();
		if (type == Constants.UPDATE_NEXT) {
			if (myPartecipant.getIp().equals(this.ipCurrentPartecipant)) {
				timeToWait += (this.getNrActivePartecipantAfter(0) - 1)
						* Constants.MAX_TIME_FOR_UPDATE;
			} else if (myPartecipant.getIp().equals(
					this.getNextActivePartecipant(this.ipCurrentPartecipant)
							.getIp())) {
				if (isDubleTurn) {
					timeToWait += Constants.MAX_TIME_FOR_TURN
							+ (this.getNrActivePartecipantAfter(0) - 2)
							* Constants.MAX_TIME_FOR_UPDATE;
				} else {
					timeToWait += (this.getNrActivePartecipantAfter(0) - 1)
							* Constants.MAX_TIME_FOR_UPDATE;
				}
			} else {
				timeToWait += Constants.MAX_TIME_FOR_TURN
						+ (this.getNrActivePartecipantAfter(0) - 2)
						* Constants.MAX_TIME_FOR_UPDATE;
			}
		} else {
			timeToWait += Constants.MAX_TIME_FOR_TURN
					+ (this.getNrActivePartecipantAfter(0) - 2)
					* Constants.MAX_TIME_FOR_UPDATE;
		}
		return timeToWait;
	}

	/**
	 * @param color String with the color from which to find the first active partecipant
	 *            before it
	 * @return Partecipant, it returns the first previous active partecipant before the given
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
	 * It sets the player of the given color as not active and if he was
	 * the current partecipant then a new current partecipant is set, the first
	 * active after him.
	 * @param color String with the color of the player that has crashed
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
	 * @return, the index/position of the first active player
	 */
	public int getFirstActiveIndex() {
		for (int i=0; i<this.partecipants.size(); i++) {
			if (this.partecipants.get(i).isStatusActive()) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 
	 * @param partecioant the Partecipant player whose to check the victory
	 * @return boolean, true if the given partecipant has won the game
	 */
	public boolean isVictory(Partecipant partecipant) {
		if (this.getNrActivePartecipantAfter(0) == 1) {
			return true;
		}
		return this.gameBoard.isVictory(partecipant);
	}

}