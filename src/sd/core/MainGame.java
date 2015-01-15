package sd.core;

import java.util.ArrayList;
import java.util.List;

public class MainGame {
	
	private List<Partecipant> partecipants;
	private GameBoard gameBoard;
	
	public MainGame(List<String> ipGamers) {
		
		List<String> colors = new ArrayList<String>();
		colors.add("RED");
		colors.add("GREEN");
		colors.add("VIOLET");
		colors.add("YELLOW");
		colors.add("BLACK");
		colors.add("BLUE");
		
		this.partecipants = new ArrayList<Partecipant>();
		
		for(int i = 0; i<ipGamers.size(); i++) {
			
			Partecipant partecipant = new Partecipant(ipGamers.get(i), colors.get(i));
			this.partecipants.add(partecipant);
		}
		
		this.gameBoard = new GameBoard();
		
	}
	
	public List<Partecipant> getPartecipants() {
		return this.partecipants;
	}
}
