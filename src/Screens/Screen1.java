package Screens;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

import app.Game;
import rafgfxlib.Util;

public class Screen1 {
	int width, height;

	public static int PARTICLE_MAX = 100;
	public Particle particles[];

	double Hscale;
	double Wscale;
	public BufferedImage background;
	public BufferedImage cliff;
	public BufferedImage sova;
	public int Wcliff;
	public int Hcliff;
	public int cliffX, cliffY;
	public int cliffTX, cliffTY;

	public int Wsova;
	public int Hsova;
	public int sovaX;
	public int sovaTX;

	public BufferedImage fog;
	public int Wfog;
	public int Hfog;
	public int fogX1, fogX2, fogX3;

	BufferedImage lantern;
	BufferedImage smallLantern;
	Random r;
	int spawnCounter = 0;

	public Screen1() {
		width = Game.get().width;
		height = Game.get().height;

		background = Util.loadImage("/assets/background1.jpg");
		Hscale = Game.get().height / (float) background.getHeight();
		Wscale = Game.get().width / (float) background.getWidth();

		cliff = Util.loadImage("/assets/cliff1.png");
		Wcliff = (int) (cliff.getWidth() * Wscale);
		Hcliff = (int) (cliff.getHeight() * Hscale);
		cliffX = width - Wcliff + 25;
		cliffY = height - Hcliff + 25;

		sova = Util.loadImage("/assets/sova.png");
		Wsova = (int) (sova.getWidth() * Wscale);
		Hsova = (int) (sova.getHeight() * Hscale);
		sovaX = -10;

		fog = Util.loadImage("/assets/fog.png");
		Wfog = (int) (cliff.getWidth() * Wscale);
		Hfog = (int) (cliff.getHeight() * Hscale);
		fogX1 = 0;
		fogX2 = Wfog;
		fogX3 = Wfog * 2;

		lantern = Util.loadImage("/assets/lantern.png");
		smallLantern = Util.loadImage("/assets/smallLantern.png");
		particles = new Particle[PARTICLE_MAX];
		for (int i = 0; i < PARTICLE_MAX; i++) {
			particles[i] = new Particle();
		}
		r = new Random();
	}

	public void render(Graphics2D g, int sw, int sh) {
		g.drawImage(background, 0, 0, width, height, null);

		g.drawImage(sova, sovaX - sovaTX, 120, Wsova, Hsova, null);

		g.drawImage(fog, fogX1, height - Hfog, Wfog, Hfog, null);
		g.drawImage(fog, fogX2, height - Hfog, Wfog, Hfog, null);
		g.drawImage(fog, fogX3, height - Hfog, Wfog, Hfog, null);

		g.drawImage(cliff, cliffX - cliffTX, cliffY - cliffTY, Wcliff, Hcliff, null);

		AffineTransform transform = new AffineTransform();
		for (Particle p : particles) {
			if (p.life <= 0)
				continue;

			transform.setToIdentity();
			transform.translate(p.posX, p.posY);
			transform.translate(-16.0, -16.0);
			transform.scale(p.scale, p.scale);

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (p.alpha) / (float) p.alphaMax));

			if (p.imageID == 0)
				g.drawImage(lantern, transform, null);
			else
				g.drawImage(smallLantern, transform, null);
		}
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
	}

	public void update(int mouseX, int mouseY) {
		double delayX = Math.log(Math.abs(width / 2 - (mouseX - width / 2.0)));

		double delayY = Math.log(Math.abs(height / 2 - (mouseY - height / 2.0)));

		cliffTX = (int) ((mouseX - width / 2.0) * (5 + delayX) / (width / 2.0));
		cliffTY = (int) ((mouseY - height / 2.0) * (5 + delayY) / (height / 2.0));

		sovaTX = (int) ((mouseX - width / 2.0) * delayX * 0.8 / (width / 1.8));

		if (fogX1 == -Wfog)
			fogX1 = (2 * Wfog) - 1;
		else
			fogX1 -= 1;

		if (fogX2 == -Wfog)
			fogX2 = (2 * Wfog) - 1;
		else
			fogX2 -= 1;

		if (fogX3 == -Wfog)
			fogX3 = (2 * Wfog) - 1;
		else
			fogX3 -= 1;

		// particles
		spawnCounter++;
		if (spawnCounter == 10) {
			genEx(2f, 300, 0);
			spawnCounter = 0;
		}
		if (spawnCounter == 3 || spawnCounter == 6)
			genEx(2f, 300, 1);

		for (Particle p : particles) {
			if (p.life <= 0)
				continue;

			p.life--;
			p.posX += p.dX;
			p.posY -= p.dY;
			if (p.imageID == 0) {
				p.dX *= 0.995f;
				p.dX += 0.005;
				p.dY *= 0.94f;
			} else {
				p.dX *= 0.96f;
				p.dY *= 0.92f;
			}
			// p.dY *= 0.94f;
			p.dY += 0.1f;
			p.angle += p.rot;
			p.scale += p.growth;
			if (p.life <= p.lifeMax / 2)
				p.alpha--;
			// p.rot *= 0.99f;
		}

	}

	private void genEx(float radius, int life, int ID) {
		for (Particle p : particles) {
			if (p.life <= 0) {
				p.life = p.lifeMax = (int) (Math.random() * life * 0.5) + life / 2;
				p.posX = r.nextInt(400) + 400;
				p.posY = r.nextInt(30) + 180;
				double angle = Math.random() * Math.PI * 2.0;
				double speed = Math.random() * radius;
				if (ID == 1)
					p.dX = (float) ((speed + 1) * 1.5);
				else
					p.dX = (float) (speed);
				p.dY = (float) ((Math.sin(angle) * speed) / 20.0);
				p.angle = (float) (Math.random() * Math.PI * 2.0);
				p.scale = (float) ((r.nextInt(3) + 1) / 10.0);
				if (ID == 1)
					p.scale = (float) (p.scale + 0.2);
				p.growth = (float) ((r.nextInt(3) + 1) / 800.0);
				p.imageID = ID;
				p.alpha = p.alphaMax = p.life / 2;
				return;
			}
		}
	}
}
