package Screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

import app.Game;
import rafgfxlib.Util;

public class EscScreen {

	int width, height;
	BufferedImage backgroundSave;
	BufferedImage background;
	boolean down = false;
	int screen = 0;
	boolean reverse = false;
	ArrayList<BufferedImage> blurImages = new ArrayList<>();

	int counter = 1;
	int rgb[] = new int[3];
	WritableRaster source;
	float X,Y;

	public EscScreen() {
		Game.get();
		width = Game.width;
		height = Game.height;
	}

	public void render(Graphics2D g, int sw, int sh) {
		g.drawImage(background, 0, 0, width, height, null);
	}

	public void update(int mouseX, int mouseY, boolean escDown) {
		if(reverse)
		{
			if(counter == 0)
			{
				blurImages.clear();
				Game.get().screenNumber = screen;
				reverse = false;
				return;
			}
			counter--;
			background = blurImages.get(counter);
			return;
		}
		
		
		if (!escDown) {
			if (down) {
				down = false;
				reverse = true;
				return;
			}
		} else
			down = true;
		
		
		if (counter < 10) {
			counter++;
			blur(counter);
		}

	}

	public void setBackground(BufferedImage background, int scNum) {
		counter = 0;
		this.background = background;
		backgroundSave = background;
		screen = scNum;
	}

	public void blur(int radius) {
		source = backgroundSave.getRaster();
		BufferedImage img = new BufferedImage(600, 400, BufferedImage.TYPE_INT_RGB);

		for (int y = 0; y < 400; y++) {
			for (int x = 0; x < 600; x++) {

				X = (float) (Math.random() - 0.5) * radius * 2.0f;
				Y = (float) (Math.random() - 0.5) * radius * 2.0f;

				Util.bilSample(source, x + X, y + Y, rgb);

				Color col = new Color(rgb[0] / 255f, rgb[1] / 255f, rgb[2] / 255f);
				img.setRGB(x, y, col.getRGB());
				background.setRGB(x, y, col.getRGB());
			}
		}
		blurImages.add(img);
	}

}
