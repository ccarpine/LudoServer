package sd.core.register;

import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import sd.core.player.UserPlayerInterface;

public class Register  extends UnicastRemoteObject implements RegisterInterface{

	private static final long serialVersionUID = 1L;
	private final long timeLimit = 3000000000L;
	private List<String> gamersIp;
	private Thread ludoChronometer;
	private long counter;
	
	protected Register() throws RemoteException {
		this.initVariable();
	}
	
	public long register(String clientIp) throws RemoteException {
		System.out.println("register!");
		if (this.gamersIp.size() == 0) {
			/* start timer */
			this.startTimer();
		} 
		/* add the partecipant ip to the list */
		this.gamersIp.add(clientIp);
		
		System.out.println("SERVER ---- client ip:" + clientIp);
		System.out.println("------------------------");
		System.out.println("SERVER ---- Client registrati per la partita:" + this.gamersIp.size());
		System.out.println("------------------------");
		
		/* partecipant limit reached, start the game */
		if (this.gamersIp.size() == 3) {
			System.out.println("SI PARTE!");
			/*stop timer */
			this.endTimer();
			this.startGame();
			/* reset variables */
			this.initVariable();
			return 0;
		}
		return (this.timeLimit-this.counter);
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
		System.out.println("SERVER ---- counter:" + counter);
		for (int i = 0 ; i < this.gamersIp.size() ; i++){
			System.out.println("SERVER ---- ciclo per informare i player:" + i);
			try {
				UserPlayerInterface gamer = (UserPlayerInterface) Naming.lookup("rmi://"+ this.gamersIp.get(i)+"/RMIGameClient");
				//PlayerInterface gamer = (PlayerInterface) Naming.lookup("rmi://localhost/RMIGameClient");
				gamer.start(this.gamersIp);
			} catch (/*MalformedURLException | RemoteException | NotBoundException | */Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public static void main(String[] args) {
		try {
			RegisterInterface server = new Register();
			String ipAddress = Inet4Address.getLocalHost().getHostAddress();
			Naming.rebind("//"+ipAddress+"/RMILudoServer", server);
		} catch (RemoteException | MalformedURLException | UnknownHostException e) {
			e.printStackTrace();
		}
	}


}
