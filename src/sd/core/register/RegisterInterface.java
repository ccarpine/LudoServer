package sd.core.register;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegisterInterface extends Remote {
	long register(String clientIp) throws RemoteException;
}
