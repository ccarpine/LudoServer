package sd.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import sd.core.Cell;

public class CellButton extends JButton implements Runnable {
	
	private static final long serialVersionUID = 1L;
	private int row;
	private int col;
	private int rowOnGameBoard;
	private int colOnGameBoard;
	private boolean isOn;
	private String pathOn;
	private String pathOff;
		
	public CellButton(int pRow, int pCol, String pathOn, String pathOff, Cell cell) {
		super("");
		this.isOn = false;
		this.pathOn = pathOn;
		this.pathOff = pathOff;
		this.row = pRow;
		this.col = pCol;
		if (cell != null) {
			this.rowOnGameBoard = cell.getRow();
			this.colOnGameBoard = cell.getColumn();
		} else {
			this.rowOnGameBoard = -1;
			this.colOnGameBoard = -1;
		}
		this.setIcon(new javax.swing.ImageIcon(getClass().getResource(this.pathOff)));
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
	
	public void changeState() {
		if (isOn) {
			this.isOn = false;
			this.setIcon(new javax.swing.ImageIcon(getClass().getResource(this.pathOff)));
		} else {
			this.isOn = true;
			new Thread(this).start();
		}
	}

	@Override
	public void run() {
		while (this.isOn) {
			try {
				this.setIcon(new javax.swing.ImageIcon(getClass().getResource(this.pathOn)));
				Thread.sleep(500);
				this.setIcon(new javax.swing.ImageIcon(getClass().getResource(this.pathOff)));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
	}
}