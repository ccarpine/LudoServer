package sd.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.Timer;

import sd.core.Cell;

public class CellButton extends JButton implements Runnable {

	private static final long serialVersionUID = 1L;
	private int row;
	private int col;
	private int rowOnGameBoard;
	private int colOnGameBoard;
	private boolean isOn;
	private boolean flashing = false;
	private String pathOn;
	private String pathOff;

	/** it creates a graphical cel and associates it to the corresponding one in memory
	 * 
	 * @param pRow, the row of the GUI in which the cell will be inserted
	 * @param pCol, the col of the GUI in which the cell will be inserted
	 * @param pathOn, path to an icon representing the cell as on
	 * @param pathOff, path to an icon representing the cell as turned off
	 * @param cell, the cell in memory to which the GUI cell will be associated
	 */
	public CellButton(int pRow, int pCol, String pathOn, String pathOff,
			Cell cell) {
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
		this.setIcon(new javax.swing.ImageIcon(ClassLoader.getSystemResource(
				"sd/ui/"+this.pathOff)));
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

	/**
	 * If this GUI  cell is selectable as a move to apply, this method changes its state by making it clickable and making it flash
	 */
	public void changeState() {
		if (isOn) {
			this.isOn = false;
			this.setIcon(new javax.swing.ImageIcon(getClass().getResource(
					this.pathOff)));
		} else {
			this.isOn = true;
			new Thread(this).start();
		}
	}

	/*
	 * @Override public void run() { while (this.isOn) { try { this.setIcon(new
	 * javax.swing.ImageIcon(getClass().getResource(this.pathOn)));
	 * Thread.sleep(500); this.setIcon(new
	 * javax.swing.ImageIcon(getClass().getResource(this.pathOff))); } catch
	 * (InterruptedException e) { e.printStackTrace(); } } }
	 */

	@Override
	public void run() {
		Timer timer = new Timer(500, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!flashing) {
					setIcon(new javax.swing.ImageIcon(getClass().getResource(
							pathOn)));
					flashing = true;
				}

				else {
					setIcon(new javax.swing.ImageIcon(getClass().getResource(
							pathOff)));
					flashing = false;
				}

			}
		});
		while (this.isOn) {

			timer.start();
		}
	}
}