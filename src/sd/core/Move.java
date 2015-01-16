package sd.core;

public class Move {
	
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
