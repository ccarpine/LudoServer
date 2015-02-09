package sd.core.player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import sd.core.GameBoard;
import sd.core.Partecipant;

/***
 * remoted methods exposed by the user player
 */
public interface UserPlayerInterface extends Remote {
	
	/** it creates the CoreGame and prepares the game
	 * @param the ip address of the user player that wants to start the game
	 */
	void start(List<String> gamersIp) throws RemoteException;
	
	/** allows player to initialize gui
	 */
	void buildGUI(List<Partecipant> partecipants) throws RemoteException;
	
	/** it updates the game status
	 * @param partecipants, the user game player still taking part into the match. This list can change 
	 * in case of crash of a user player
	 * @param gameBoard, the game board visibile by the current player
	 * @param ipCurrentPartecipant
	 */
	void updateStatus(final List<Partecipant> partecipants, final GameBoard gameBoard, final String ipCurrentPartecipant, boolean isDoubleTurn, int currentTurn) throws RemoteException;
	
	/** it allows partcipant to start a turn
	 * @throws RemoteException
	 */
	void initTurn(List<Partecipant> partecipants) throws RemoteException;
	
	/** ping to a client-player to check if it's alive
	 * @ip, the client-player ip address to check
	 */
	void isAlive(int phase, String color, int currentTurn) throws RemoteException;

}