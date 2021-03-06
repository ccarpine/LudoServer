package sd.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

public class MyFont {
	
	public MyFont() {
	}
	
	public Font getMyFont(int style, int size) {
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, 
					ClassLoader.getSystemResource("sd/ui/font/funSized.ttf").openStream());
			font = font.deriveFont(Font.BOLD, size);
	        GraphicsEnvironment ge =
	            GraphicsEnvironment.getLocalGraphicsEnvironment();
	        ge.registerFont(font);
	        return font;
		} catch (FontFormatException | IOException | NullPointerException e) {
			return new Font("Helvetica", style, size);
		}
	}

}