package sd.core.player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface UserPlayerInterface extends Remote {
	void start(List<String> gamersIp) throws RemoteException;

}
