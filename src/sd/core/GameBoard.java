package sd.core;


public class GameBoard {
	
	private static final int rows = 6;
	private static final int columns = 12;
	private Cell[][] cells;
	
	public GameBoard() {
		this.cells = new Cell[rows][columns];
	}
	
	public Cell getCell(int row, int col) {
		
		return this.cells[row][col];
		
	}

}
