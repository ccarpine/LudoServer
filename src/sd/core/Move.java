package sd.core;

import java.io.Serializable;

public class Move implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Cell start;
	private Cell destination;
	
	public Move(Cell start, Cell destination) {
		this.start = start;
		this.destination = destination;
	}

	public Cell getStart() {
		return start;
	}

	public Cell getDestination() {
		return destination;
	}
	
}
