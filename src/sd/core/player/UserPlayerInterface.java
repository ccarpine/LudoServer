package sd.core.player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import sd.core.GameBoard;
import sd.core.Partecipant;

/***
 * Remoted methods exposed by the UserPlayer class
 */
public interface UserPlayerInterface extends Remote {
	
	/** 
	 * It creates the CoreGame and prepares the game
	 * @param String, the ip address of the user player that wants to start the game
	 * @throws RemoteException
	 */
	void start(List<String> gamersIp) throws RemoteException;
	
	/** 
	 * Allows a player to initialize his GUI
	 * @param List<Partecipant>, all the registered partecipants
	 * @throws RemoteException
	 */
	void buildGUI(List<Partecipant> partecipants) throws RemoteException;
	
	/** 
	 * It updates the game status of the player in which this method is invokated
	 * @param List<Partecipant>, the users game players still taking part into the match. This list can change 
	 * 		  in case of crash of a user player
	 * @param GameBoard, the game board visibile by the current player
	 * @param String, the ip address of the current partecipants
	 * @param boolean, it establishes if the current partecipant can parform a double turn
	 * @param int, the value of the current turn
	 * @throws RemoteException
	 */
	void updateStatus(final List<Partecipant> partecipants, final GameBoard gameBoard, final String ipCurrentPartecipant, boolean isDoubleTurn, int currentTurn) throws RemoteException;
	
	/** 
	 * It allows partcipant to start a turn after having updated the list of the status of all partecipants
	 * @param List<Partecipant>, the users game players still taking part into the match. This list can change 
	 * 		  in case of crash of a user player
	 * @throws RemoteException
	 */
	void initTurn(List<Partecipant> partecipants) throws RemoteException;
	
	/** 
	 * It pings to a client-player to check if it is atill alive
	 * @throws RemoteException
	 */
	void isAlive(int phase, String color, int currentTurn) throws RemoteException;

}