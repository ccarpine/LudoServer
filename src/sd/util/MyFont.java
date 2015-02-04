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
			System.out.println("Si font");
	        font = font.deriveFont(Font.BOLD, size);
	        GraphicsEnvironment ge =
	            GraphicsEnvironment.getLocalGraphicsEnvironment();
	        ge.registerFont(font);
	        return font;
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
