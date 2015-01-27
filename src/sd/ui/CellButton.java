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
	private Cell cell;
	private boolean isOn;
	private String folder;
	private String basePath;

	/** it creates a graphical cel and associates it to the corresponding one in memory
	 * 
	 * @param pRow, the row of the GUI in which the cell will be inserted
	 * @param pCol, the col of the GUI in which the cell will be inserted
	 * @param basePath, path to an icon representing the cell as on
	 * @param cell, the cell in memory to which the GUI cell will be associated
	 */
	public CellButton(int pRow, int pCol, String basePath, Cell cell) {
		super("");
		this.isOn = false;
		this.folder = "off";
		this.basePath = basePath;
		this.row = pRow;
		this.col = pCol;
		this.cell = cell;
		this.setIcon(new javax.swing.ImageIcon(ClassLoader.getSystemResource(
				"sd/ui/"+this.basePath)));
		this.setBorder(null);
		this.setFocusPainted(false);
		this.setBorderPainted(false);
		this.setContentAreaFilled(false);
	}

	public int getRow() {
		return this.row;
	}

	public int getCol() {
		return this.col;
	}
	
	public Cell getCell() {
		return this.cell;
	}

	/**
	 * If this GUI cell is selectable as a move to apply, this method changes its state by making it clickable and making it flash
	 */
	public void changeState() {
		System.out.println("sd/ui/images/box/"+folder+"/"+cell.getColor()+"_"+cell.getColor()+".png");
		if (isOn) {
			this.isOn = false;
			if (cell.getPawns().size() == 1) {
				setIcon(new javax.swing.ImageIcon(
								ClassLoader.getSystemResource("sd/ui/images/box/"+folder+"/"+cell.getColor()+"_"+cell.getColor()+".png")));
			} else {
				setIcon(new javax.swing.ImageIcon(
								ClassLoader.getSystemResource("sd/ui/"+basePath)));
			}
		} else {
			this.isOn = true;
			new Thread(this).start();
		}
	}

	@Override
	public void run() {
		Timer timer = new Timer(500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cell.getPawns().size() == 1) {
					setIcon(new javax.swing.ImageIcon(
									ClassLoader.getSystemResource("sd/ui/images/box/"+folder+"/"+cell.getColor()+"_"+cell.getPawns().get(0)+".png")));
				} else {
					setIcon(new javax.swing.ImageIcon(
									ClassLoader.getSystemResource("sd/ui/"+basePath)));
				}
				if (folder.equals("off")) {
					folder = "on";
					basePath = basePath.replace("off", "on");
				} else {
					folder = "off";
					basePath = basePath.replace("on", "off");
				}
			}
		});
		timer.start();
		while (isOn) {
			timer.start();
		}
		timer.stop();
	}
}