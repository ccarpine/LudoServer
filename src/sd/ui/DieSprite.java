package sd.ui;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import sd.util.Constants;

public class DieSprite {

    public static BufferedImage getSprite(int xGrid, int yGrid, int offset) {
    	try {
    		BufferedImage sprite = ImageIO.read(ClassLoader.getSystemResourceAsStream("sd/ui/images/dice/dices.png"));
        	return sprite.getSubimage(xGrid*Constants.DIE_SIZE, yGrid*Constants.DIE_SIZE, Constants.DIE_SIZE, Constants.DIE_SIZE);
        } catch (IOException e) {
        }
        return null;
    }

}