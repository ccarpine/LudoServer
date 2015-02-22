package sd.ui;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class AnimationSprite {

    private int frameCount;                
    private int frameDelay;                
    private int currentFrame;              
    private int animationDirection;        
    private int totalFrames;               
    private boolean stopped;               
    private List<BufferedImage> frames;

    public AnimationSprite(BufferedImage[] frames, int frameDelay) {
    	this.frames = new ArrayList<BufferedImage>();
        this.frameDelay = frameDelay;
        this.stopped = true;
        for (int i=0; i<frames.length; i++) {
        	this.frames.add(frames[i]);
        }
        this.frameCount = 0;
        this.frameDelay = frameDelay;
        this.currentFrame = 0;
        this.animationDirection = 1;
        this.totalFrames = this.frames.size();
    }

    public void start() {
        if (!stopped || frames.size() == 0) {
            return;
        }
        stopped = false;
    }
    
    public BufferedImage getSprite() {
        return frames.get(currentFrame);
    }

    public void update() {
        if (!stopped) {
            frameCount++;
            if (frameCount > frameDelay) {
                frameCount = 0;
                currentFrame += animationDirection;
                if (currentFrame > totalFrames - 1) {
                    currentFrame = 0;
                } else if (currentFrame < 0) {
                    currentFrame = totalFrames - 1;
                }
            }
        }
    }

}