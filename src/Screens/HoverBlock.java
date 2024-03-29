package Screens;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import rafgfxlib.Util;

public class HoverBlock {

	public static BufferedImage fl1 = Util.loadImage("/assets/fl1.png");
	public static BufferedImage fl2 = Util.loadImage("/assets/fl2.png");
	public static BufferedImage fl3 = Util.loadImage("/assets/fl3.png");
	public static BufferedImage fl4 = Util.loadImage("/assets/fl4.png");
	public static BufferedImage fl5 = Util.loadImage("/assets/fl5.png");
	public static BufferedImage fl6 = Util.loadImage("/assets/fl6.png");

	int imageID;
	float x1, y1;
	float radiusX, radiusY;
	float theta;
	int locX = 975, locY;
	float scaleY;
	float scaleX;
	float speed;
	public BufferedImage img;
	float alpha = 1;
	int dir;

	double hScale;
	double wScale;

	int pillarX1, pillarX2;

	public HoverBlock(int imageID, float radiusX, float radiusY, int locY, float speed, double Hscale, double Wscale,int dir,
			int pillarX1, int pillarX2) {
		this.imageID = imageID;
		this.radiusX = radiusX;
		this.radiusY = radiusY;
		this.locY = locY;
		this.speed = speed;
		this.hScale = Hscale;
		this.wScale = Wscale;
		if (imageID == 1)
			img = fl1;
		if (imageID == 2)
			img = fl2;
		if (imageID == 3)
			img = fl3;
		if (imageID == 4)
			img = fl4;
		if (imageID == 5)
			img = fl5;
		if (imageID == 6)
			img = fl6;
		x1 = 0;
		y1 = 0;
		theta = 0;
		this.dir = dir;
		this.pillarX1 = pillarX1;
		this.pillarX2 = pillarX2;
	}

	public void render(Graphics2D g, int sw, int sh) {
		AffineTransform transform = new AffineTransform();
		transform.setToIdentity();
		transform.scale(wScale, hScale);
		transform.scale(scaleY, scaleY);
		transform.scale(scaleX, scaleX);
		g.translate((int) x1, (int) y1);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

		if (!(y1 < locY && x1 > pillarX1 && x1 < pillarX2)) {
				g.drawImage(img, transform, null);
		}
		g.translate(-(int) x1, -(int) y1);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
	}

	public void update() {
		float tempSpeed = speed;
		scaleY = y1 / (float) locY;
		if (scaleY > 1)
			scaleY = 1;
		tempSpeed *= scaleY;

		if (y1 > locY) {
			scaleX = x1 / (float) locX;
			if (scaleX > 1)
				scaleX = 2 - scaleX;
			tempSpeed *= scaleX;
			alpha = 1;
		}
		theta += tempSpeed;
		x1 = locX + dir*(float) (radiusX * Math.cos(theta));
		y1 = locY + (float) (radiusY * Math.sin(theta));
		if (y1 <= locY) {
			alpha = Math.abs(x1 - locX) / (float)radiusX;
			alpha *= 0.9f;
		}
	}

}
