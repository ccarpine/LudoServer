package sd.ui;

import javax.swing.JButton;

public class CellButton extends JButton {
	
	private static final long serialVersionUID = 1L;
	private int row, col;
		
	public CellButton(int pRow, int pCol, String path) {
		super("");
		this.row = pRow;
		this.col = pCol;
		this.setIcon(new javax.swing.ImageIcon(getClass().getResource(path)));
		this.setBorder(null);
		this.setFocusPainted(false);
		this.setBorderPainted(false);
		this.setContentAreaFilled(false);
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

}