package sd.core.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
	int register(String clientIp) throws RemoteException;
}
