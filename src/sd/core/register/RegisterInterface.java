package sd.core.register;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegisterInterface extends Remote {
	
	/** allows the registration for a player
	 * @param clientIp, the ip of the player
	 * @return long, the remaining time for the start of the match
	 * @throws RemoteException
	 */
	long register(String clientIp) throws RemoteException;
	
	void deletePartecipant(String ip) throws RemoteException;
}