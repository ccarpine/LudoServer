package sd.ui;

import javax.swing.JButton;

public class CellButton extends JButton {
	
	private static final long serialVersionUID = 1L;
	private int row, col;
		
	public CellButton(int row, int col, String nameColor) {
		super("");
		this.row = row;
		this.col = col;
		this.setIcon(new javax.swing.ImageIcon(getClass().getResource("images/"+nameColor+".png")));
		this.setBorder(null);
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

}