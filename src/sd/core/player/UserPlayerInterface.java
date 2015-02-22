package sd.core.player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import sd.core.GameBoard;
import sd.core.Partecipant;

/***
 * Remoted methods exposed by the user player
 */
public interface UserPlayerInterface extends Remote {
	
	/** 
	 * It creates the CoreGame and prepares the game
	 * @param the ip address of the user player that wants to start the game
	 * @throws RemoteException
	 */
	void start(List<String> gamersIp) throws RemoteException;
	
	/** 
	 * Allows player to initialize gui
	 * @throws RemoteException
	 */
	void buildGUI(List<Partecipant> partecipants) throws RemoteException;
	
	/** 
	 * It updates the game status
	 * @param partecipants, the user game player still taking part into the match. This list can change 
	 * 		  in case of crash of a user player
	 * @param gameBoard, the game board visibile by the current player
	 * @param ipCurrentPartecipant
	 * @throws RemoteException
	 */
	void updateStatus(final List<Partecipant> partecipants, final GameBoard gameBoard, final String ipCurrentPartecipant, boolean isDoubleTurn, int currentTurn) throws RemoteException;
	
	/** 
	 * It allows partcipant to start a turn
	 * @throws RemoteException
	 */
	void initTurn(List<Partecipant> partecipants) throws RemoteException;
	
	/** 
	 * Ping to a client-player to check if it's alive
	 * @throws RemoteException
	 */
	void isAlive(int phase, String color, int currentTurn) throws RemoteException;

}