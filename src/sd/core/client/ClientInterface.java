package sd.core.client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientInterface extends Remote {
	void start(List<String> gamersIp) throws RemoteException;

}
