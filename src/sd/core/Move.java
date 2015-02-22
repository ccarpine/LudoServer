package sd.core;

import java.io.Serializable;

public class Move implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Cell start;
	private Cell destination;
	
	/** 
	 * Creates a move for a pawn from a cell to another
	 * @param start, the cell from which the move will be performed
	 * @param destination, the cell into which the move will get
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