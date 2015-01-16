package sd.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sd.util.Constants;

public class CoreGame {
	
	private List<Partecipant> partecipants;
	private GameBoard gameBoard;
	
	public CoreGame(List<String> ipGamers) {
		
		this.partecipants = new ArrayList<Partecipant>();
		
		// generate the partecipants giving them a color according to their registration order
		for(int i=0; i<ipGamers.size(); i++) {
			Partecipant partecipant = new Partecipant(ipGamers.get(i), Constants.COLOR[i], i);
			this.partecipants.add(partecipant);
		}
		
		this.gameBoard = new GameBoard();
		
	}
	
	public List<Partecipant> getPartecipants() {
		return this.partecipants;
	}
	
	// returns the die launch result
	private int getDie() {
		return 1 + new Random().nextInt(6);
	}
	
}