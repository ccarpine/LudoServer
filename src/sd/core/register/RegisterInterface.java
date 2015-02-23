package sd.core.register;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegisterInterface extends Remote {
	
	/** 
	 * It allows a player to register in order to take part into a match
	 * @param String, the ip address of the invoking player
	 * @return long, the remaining time for the beginning of the match
	 * @throws RemoteException
	 */
	long register(String clientIp) throws RemoteException;
	
	/**
	 * It allows a player, who is waiting for a match to begin, to retreat
	 * @param String, the ip address of the invoking player
	 * @throws RemoteException
	 */
	void deletePartecipant(String ip) throws RemoteException;
	
}