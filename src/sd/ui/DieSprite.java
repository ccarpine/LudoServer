package sd.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class DieSprite {

    private static final int TILE_SIZE = 37;

    public static BufferedImage getSprite(int xGrid, int yGrid) {
    	try {
        	BufferedImage sprite = ImageIO.read(new File("src/sd/ui/images/diceSprite.png"));
        	return sprite.getSubimage(xGrid * TILE_SIZE, yGrid * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}