package sd.core.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import sd.core.client.ClientInterface;

public class LudoServer  extends UnicastRemoteObject implements ServerInterface{

	private static final long serialVersionUID = 1L;
	private List<String> gamersIp;
	private Thread ludoChronometer;
	private static final long timeLimit = 300000L;
	private long counter;
	
	protected LudoServer() throws RemoteException {
		this.initVariable();
	}
	
	@Override
	public long register(String clientIp) throws RemoteException {
		if (this.gamersIp.size() == 0) {
			/* start timer */
			this.startTimer();
		} 
		/* add the partecipant ip to the list */
		this.gamersIp.add(clientIp);
		/* partecipant limit reached, start the game */
		if (this.gamersIp.size() == 6) {
			/*stop timer */
			this.endTimer();
			this.startGame();
			/* reset variables */
			this.initVariable();
			return 0;
		}
		return this.counter;
	}
	
	private void startTimer(){
		this.ludoChronometer = new Thread(new Runnable( ) {
			@Override
			public void run() {
				counter = 0;
				long startedAt=System.currentTimeMillis();
				while (counter < timeLimit) {
					counter = System.currentTimeMillis() - startedAt;
				}
				startGame();
				initVariable();
			}
		});
		this.ludoChronometer.start();
			
	}
	
	private void endTimer(){
		this.ludoChronometer.interrupt();
	}
	
	private void initVariable() {
		this.gamersIp = new ArrayList<String>();
	}
	
	private void startGame() {
		for (int i = 0 ; i < this.gamersIp.size() ; i++){
			try {
				ClientInterface gamer = (ClientInterface) Naming.lookup("rmi://"+ this.gamersIp.get(i)+"/RMIGameClient");
				gamer.start(this.gamersIp);
			} catch (MalformedURLException | RemoteException
					| NotBoundException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public static void main(String[] args) {
		try {
			ServerInterface server = new LudoServer();
			Naming.rebind("//localhost/RMILudoServer", server);
		} catch (RemoteException | MalformedURLException e) {
			e.printStackTrace();
		}
	}


}
