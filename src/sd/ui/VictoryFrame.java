package sd.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import sd.core.CoreGame;
import sd.core.player.UserPlayer;
import sd.util.Constants;
import sd.util.MyFont;

/**
 * VictoryFrame, window to show when a partecipant win
 */
public class VictoryFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	/***
	 * create panel show the victory
	 * @param userPlayer, the partecipant who invoce the method
	 * @param coreGame, from which we can deduce who is the winner
	 */
	public VictoryFrame(final UserPlayer userPlayer, CoreGame coreGame) {
		super();
		String colorWinner = coreGame.getCurrentPartecipant().getColor();
		this.setIconImage(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("sd/ui/images/icon.png")));
		this.setTitle(Constants.NAME_GAME);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setSize(new Dimension(400, 400));
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.getContentPane().removeAll();
		this.setLayout(new BorderLayout());
		BGPanel panel = new BGPanel("images/sky.png");
		this.add(panel);
		panel.setLayout(null);
		JLabel winner = new JLabel();
		winner.setIcon(new ImageIcon(ClassLoader.getSystemResource("sd/ui/images/victory/"+colorWinner+".png")));
		winner.setBounds(0, -35, 300, 400);
		JButton newGame = new JButton();
		newGame.setBounds(220, 240, 170, 50);
		newGame.setIcon(new ImageIcon(ClassLoader.getSystemResource("sd/ui/images/again.png")));
		newGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newGame(userPlayer);
			}
		});
		panel.add(newGame);
		String sentence = "<html><center>"+colorWinner+"<br>is the<br>winner!</center></html>";
		if (colorWinner.equals(coreGame.getMyPartecipant().getColor())) {
			sentence = "<html><center>You<br>are the<br>winner!</center></html>";
		}
		JLabel message = new JLabel(sentence);
		message.setBounds(215, -5, 200, 250);
		message.setFont(new MyFont().getMyFont(Font.PLAIN, 32));
		if (colorWinner.equals(Constants.COLOR[0])) {
			message.setForeground(Color.RED);
		} else if (colorWinner.equals(Constants.COLOR[1])) {
			message.setForeground(Color.GREEN);
		} else if (colorWinner.equals(Constants.COLOR[2])) {
			message.setForeground(Color.decode("#990099"));
		} else if (colorWinner.equals(Constants.COLOR[3])) {
			message.setForeground(Color.YELLOW);
		} else if (colorWinner.equals(Constants.COLOR[4])) {
			message.setForeground(Color.BLACK);
		} else if (colorWinner.equals(Constants.COLOR[5])) {
			message.setForeground(Color.BLUE);
		}
		panel.add(message);
		JButton exit = new JButton();
		exit.setBounds(220, 300, 170, 50);
		exit.setIcon(new ImageIcon(ClassLoader.getSystemResource("sd/ui/images/exit.png")));
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		panel.add(exit);
		panel.add(winner);
		panel.updateUI();
	}

	/***
	 * action for the button play again on the victory window
	 * @param userPlayer, take the player has instantiated the object
	 */
	private void newGame(UserPlayer userPlayer) {
		this.setVisible(false);
		MainFrame mainFrame = new MainFrame();
		mainFrame.addPanel(new IntroPanel(userPlayer), BorderLayout.CENTER);
		this.dispose();
	}
	
	@Override
	public void setSize(Dimension d) {
        Insets i = this.getInsets();
        this.setSize(d.width + i.left + i.right, d.height + i.top + i.bottom);
    }
	
}