package sd.ui;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import sd.util.Constants;

public class DieSprite {

    public static BufferedImage getSprite(int xGrid, int yGrid) {
    	try {
    		BufferedImage sprite = ImageIO.read(ClassLoader.getSystemResourceAsStream("sd/ui/images/diceSprite.png"));
        	return sprite.getSubimage(xGrid * Constants.DIE_SIZE, yGrid * Constants.DIE_SIZE, Constants.DIE_SIZE, Constants.DIE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}