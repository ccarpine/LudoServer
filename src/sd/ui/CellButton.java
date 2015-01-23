package sd.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import sd.core.Cell;

public class CellButton extends JButton {
	
	private static final long serialVersionUID = 1L;
	private int row;
	private int col;
	private int rowOnGameBoard;
	private int colOnGameBoard;
		
	public CellButton(int pRow, int pCol, String path, Cell cell) {
		super("");
		this.row = pRow;
		this.col = pCol;
		if (cell != null) {
			this.rowOnGameBoard = cell.getRow();
			this.colOnGameBoard = cell.getColumn();
		}
		this.setIcon(new javax.swing.ImageIcon(getClass().getResource(path)));
		this.setBorder(null);
		this.setFocusPainted(false);
		this.setBorderPainted(false);
		this.setContentAreaFilled(false);
		this.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Hai cliccato:\nla cella in matrice ("+rowOnGameBoard+", "+colOnGameBoard+")'\nla cella nella gui ("+row+", "+col+")");
			}
		});
		
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public int getRowOnGameBoard() {
		return rowOnGameBoard;
	}

	public void setRowOnGameBoard(int rowOnGameBoard) {
		this.rowOnGameBoard = rowOnGameBoard;
	}

	public int getColOnGameBoard() {
		return colOnGameBoard;
	}

	public void setColOnGameBoard(int colOnGameBoard) {
		this.colOnGameBoard = colOnGameBoard;
	}
	
	public void setFlash(){
		
	}
	
	private void flashImage(JButton button) {
//		Color origColor = button.getBackground();
//		button.setBackground(color);
//		pause(250);
//		button.setBackground(origColor);
//		pause(250);
//		button.setBackground(color);
//		pause(250);
//		button.setBackground(origColor);
	}
}