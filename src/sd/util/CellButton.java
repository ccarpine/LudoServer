package sd.util;

import java.awt.Color;

import javax.swing.JButton;

import sd.core.Cell;

public class CellButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int row, col;
		
	public CellButton(int row, int col, Color color) {
		super("");
		this.row = row;
		this.col = col;
		this.setBackground(color);
		this.setOpaque(true);
		//this.setBorderPainted(false);
		
		
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	
	

}
