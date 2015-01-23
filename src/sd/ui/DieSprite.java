package sd.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class DieSprite {

    private static BufferedImage spriteSheet;
    private static final int TILE_SIZE = 37;


    public static BufferedImage loadSprite(String filePath) {
    	BufferedImage sprite = null;
        try {
        	sprite = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sprite;
    }

    public static BufferedImage getSprite(int xGrid, int yGrid) {
    	spriteSheet = loadSprite("src/sd/ui/images/diceSprite.png");
        return spriteSheet.getSubimage(xGrid * TILE_SIZE, yGrid * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

}