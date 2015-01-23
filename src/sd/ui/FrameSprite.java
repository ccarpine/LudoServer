package sd.ui;

import java.awt.image.BufferedImage;

public class FrameSprite {

    private BufferedImage frame;
    //private int duration;

    public FrameSprite(BufferedImage frame/*, int duration*/) {
        this.frame = frame;
        //this.duration = duration;
    }

    public BufferedImage getFrame() {
        return frame;
    }

    public void setFrame(BufferedImage frame) {
        this.frame = frame;
    }

    /*public int getDurations() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }*/

}