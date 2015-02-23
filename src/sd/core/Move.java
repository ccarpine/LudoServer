package sd.core;

import java.io.Serializable;

public class Move implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Cell start;
	private Cell destination;
	
	/** 
	 * It creates a move for a pawn from a cell (which might be null if a new pawn is going to be inserted) to another
	 * @param start Cell from which the move will be performed
	 * @param destination Cell into which the move will get
	 */
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