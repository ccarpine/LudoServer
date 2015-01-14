package sd.ui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.JPanel;

public class BGPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
	private Image image;
    
    public BGPanel(String path) {
    	super(null);
        this.setOpaque(false);
        this.setImageFromResource(path);
    }

    
    public final void setImageFromResource(String path) {
    	URL resource = BGPanel.class.getResource(path);
        this.image = Toolkit.getDefaultToolkit().getImage(resource);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.image != null) {
            g.drawImage(this.image, 0, 0, this.image.getWidth(null), this.image.getHeight(null), this);
        }
    }
}
