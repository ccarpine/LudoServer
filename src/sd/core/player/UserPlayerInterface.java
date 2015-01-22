package sd.core.player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import sd.core.GameBoard;
import sd.core.Partecipant;

public interface UserPlayerInterface extends Remote {
	
	//it creates the CoreGame and prepares the game
	void start(List<String> gamersIp) throws RemoteException;
	
	// allows player to initialize gui
	void buildGUI() throws RemoteException;
	
	//it updates the game status
	void updateStatus(List<Partecipant> partecipants, GameBoard gameBoard, String ipCurrentPartecipant) throws RemoteException;
	
	//allows partcipant to start a turn
	void initTurn() throws RemoteException;
	
	

}
