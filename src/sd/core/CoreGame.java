package sd.core;

import java.util.ArrayList;
import java.util.List;

import sd.util.Constants;

public class CoreGame {
	
	private List<Partecipant> partecipants;
	private GameBoard gameBoard;
	
	public CoreGame(List<String> ipGamers) {
		
		this.partecipants = new ArrayList<Partecipant>();
		
		for(int i=0; i<ipGamers.size(); i++) {
			
			Partecipant partecipant = new Partecipant(ipGamers.get(i), Constants.COLOR[i], i);
			this.partecipants.add(partecipant);
		}
		
		this.gameBoard = new GameBoard();
		// TODO inizializzare gameBoard in base ai partecipanti
	}
	
	public List<Partecipant> getPartecipants() {
		return this.partecipants;
	}
}
