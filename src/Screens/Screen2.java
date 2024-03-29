package Screens;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

import app.Game;
import rafgfxlib.Util;

public class Screen2 {
	int width, height;

	double Hscale;
	double Wscale;

	public BufferedImage background;
	public BufferedImage midground;
	public BufferedImage cliff;

	public int Wmg;
	public int Hmg;
	public int mgX;
	public int mgTX;

	public int Wcliff;
	public int Hcliff;
	public int cliffX, cliffY;
	public int cliffTX, cliffTY;

	public BufferedImage fog;
	public int Wfog;
	public int Hfog;
	public int fogX1, fogX2, fogX3;

	BufferedImage light;
	public int lightX = 0;
	public int lightTX;

	public boolean beamDir1 = true;
	public boolean beamDir2 = true;
	public boolean beamDir3 = true;
	public boolean beamDir4 = true;

	public static int PARTICLE_MAX = 100000;
	public Particle particles[];

	Random rand;
	BufferedImage particle;

	BufferedImage midgroundMask;

	public Screen2() {
		Game.get();
		width = Game.width;
		height = Game.height;

		background = Util.loadImage("/assets/background2.jpg");
		Hscale = height / (float) background.getHeight();
		Wscale = width / (float) background.getWidth();

		midground = Util.loadImage("/assets/midground.png");
		Wmg = (int) (midground.getWidth() * Wscale) + 20;
		Hmg = (int) (midground.getHeight() * Hscale);
		mgX = -10;

		cliff = Util.loadImage("/assets/cliff2.png");
		Wcliff = (int) (cliff.getWidth() * Wscale);
		Hcliff = (int) (cliff.getHeight() * Hscale);
		cliffX = width - Wcliff + 25;
		cliffY = height - Hcliff + 25;

		fog = Util.loadImage("/assets/fog.png");
		Wfog = (int) (cliff.getWidth() * Wscale);
		Hfog = (int) (cliff.getHeight() * Hscale) - 200;
		fogX1 = 0;
		fogX2 = Wfog;
		fogX3 = Wfog * 2;

		light = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		generateLightBeam(740, 450, 40);
		generateLightBeam(615, 425, 40);
		generateLightBeam(137, 370, 40);
		generateLightBeam(675, 315, 40);

		// iscrtavanje particla
		particle = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
		int centerX = particle.getWidth() / 2;
		int centerY = particle.getHeight() / 2;
		float radius = 5f;

		float r = 227, g = 120, b = 255;
		float alpha;
		for (int y = 0; y < particle.getHeight(); y++) {
			for (int x = 0; x < particle.getWidth(); x++) {
				float dx = centerX - x;
				float dy = centerY - y;
				float daljina = (float) Math.sqrt(dx * dx + dy * dy);

				if (daljina <= radius) {
					alpha = daljina / radius;
					alpha = 1f - alpha;
					Color col = new Color(r / 255f, g / 255f, b / 255f, alpha);
					if (alpha >= 0.8)
						col = new Color(1, 1, 1, alpha);
					particle.setRGB(x, y, col.getRGB());
				}

			}
		}

		midgroundMask = Util.loadImage("/assets/midground Mask.png");
		particles = new Particle[PARTICLE_MAX];
		for (int i = 0; i < PARTICLE_MAX; i++) {
			particles[i] = new Particle();
		}
		rand = new Random();

	}

	public void render(Graphics2D g, int sw, int sh) {
		g.drawImage(background, 0, 0, width, height, null);

		g.drawImage(midground, mgX - mgTX, 0, Wmg, Hmg, null);

		g.drawImage(fog, fogX1, height - Hfog, Wfog, Hfog, null);
		g.drawImage(fog, fogX2, height - Hfog, Wfog, Hfog, null);
		g.drawImage(fog, fogX3, height - Hfog, Wfog, Hfog, null);

		g.drawImage(cliff, cliffX - cliffTX, cliffY - cliffTY, Wcliff, Hcliff, null);

		g.drawImage(light, lightX - lightTX, 0, width, height, null);

		AffineTransform transform = new AffineTransform();
		for (Particle p : particles) {
			if (p.life <= 0)
				continue;

			transform.setToIdentity();
			transform.translate(p.posX - mgTX, p.posY);
			transform.scale(p.scale, p.scale);

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (p.alpha) / (float) p.alphaMax));

			g.drawImage(particle, transform, null);
		}
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
	}

	public void update(int mouseX, int mouseY) {
		double delayX = Math.log(Math.abs(width / 2 - (mouseX - width / 2.0)));

		double delayY = Math.log(Math.abs(height / 2 - (mouseY - height / 2.0)));

		cliffTX = (int) ((mouseX - width / 2.0) * (5 + delayX) / (width / 2.0));
		cliffTY = (int) ((mouseY - height / 2.0) * (5 + delayY) / (height / 2.0));

		mgTX = (int) ((mouseX - width / 2.0) * delayX * 0.8 / (width / 1.8));

		lightTX = (int) ((mouseX - width / 2.0) * delayX * 0.8 / (width / 1.8));

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
		
		
		beamDir1 = lightBeamAnimation(740, 450, 40, beamDir1);
		beamDir2 = lightBeamAnimation(615, 425, 40, beamDir2);
		beamDir3 = lightBeamAnimation(137, 370, 40, beamDir3);
		beamDir4 = lightBeamAnimation(675, 315, 40, beamDir4);
		

		// particles
		for (int i = 0; i < 4; i++) {
			genEx(2f, 100, 1);
			genEx(2f, 100, 0);
		}

		for (Particle p : particles) {
			if (p.life <= 0)
				continue;

			p.life--;
			p.scale += p.growth;
			p.posX -= p.growth * 2f;
			p.posY -= p.growth * 2f;

			if (p.life <= p.lifeMax / 2)
				p.alpha--;

		}
	}

	public void generateLightBeam(float X, float Y, float width) {

		float center = X;
		float alpha = 0;
		float dMax = width / 2f;
		float r = 128f / 255f, g = 0f, b = 255f / 255f;

		float Ycenter = Y;
		float YdMax = Y * 4;

		for (int x = (int) (center - width / 2); x < center + width / 2; x++) {
			for (int y = 0; y < Y; y++) {
				float delta = Math.abs((float) x - center);
				float Ydelta = Math.abs((float) y - Ycenter);
				float alpha1 = 1f - (delta / dMax);
				float alpha2 = (Ydelta / YdMax);
				alpha = (alpha1 + alpha2) / 2.0f;
				Color col = new Color(r, g, b, alpha);
				light.setRGB(x, y, col.getRGB());
			}
		}
		r = 1;
		g = 1;
		b = 1;
		width /= 10;
		dMax = width / 2f;
		for (int x = (int) (center - width / 2); x < center + width / 2; x++) {
			for (int y = 0; y < Y; y++) {
				float delta = Math.abs((float) x - center);
				float Ydelta = Math.abs((float) y - Ycenter);
				float alpha1 = 1f - (delta / dMax);
				float alpha2 = (Ydelta / YdMax);
				alpha = (alpha1 + alpha2) / 2.0f;
				Color col = new Color(r, g, b, alpha);
				light.setRGB(x, y, col.getRGB());
			}
		}

	}

	public boolean lightBeamAnimation(float X, float Y, float width, boolean beamDir) {
		float center = X;
		float alpha;
		float dMax = width / 2f;

		float Ycenter = Y;
		float YdMax = Y * 4;

		width /= 10;
		dMax = width / 2f;

		for (int x = (int) (center - width / 2); x < center + width / 2; x++) {
			for (int y = 0; y < Y; y++) {
				int color = light.getRGB(x, y);
				alpha = (color >> 24) & 0xff;
				Color old = new Color(light.getRGB(x, y));
				if (beamDir)
					alpha = alpha / 1.4f;
				else
					alpha = alpha * 1.4f;

				if (alpha > 255)
					alpha = 255;
				if (alpha < 0)
					alpha = 0;
				Color col = new Color(old.getRed() / 255f, old.getGreen() / 255f, old.getBlue() / 255f, alpha / 255f);
				light.setRGB(x, y, col.getRGB());
			}
		}
		int x = (int) center;
		int y = (int) (Y - 1);
		float delta = Math.abs((float) x - center);
		float Ydelta = Math.abs((float) y - Ycenter);
		float alpha1 = 1f - (delta / dMax);
		float alpha2 = (Ydelta / YdMax);
		float alphaMax = (alpha1 + alpha2) / 2.0f;

		int color = light.getRGB(x, y);
		alpha = (color >> 24) & 0xff;

		if (alpha >= alphaMax || alpha <= 0.1f)
			beamDir = !beamDir;

		return beamDir;
	}

	private void genEx(float radius, int life, int ID) {
		int x, y;

		for (Particle p : particles) {
			if (p.life <= 0) {
				x = rand.nextInt((int) ((width / 3f) * 2f));
				y = rand.nextInt(410) + 220;
				float w = ((float) x / (float) Wmg) * (float) midgroundMask.getWidth();
				float h = ((float) y / (float) Hmg) * (float) midgroundMask.getHeight();

				int color = midgroundMask.getRGB((int) w, (int) h);
				Color col = new Color(color);

				if (col.getRed() == 255 && col.getGreen() == 255 && col.getBlue() == 255) {
					p.posX = x - 16;
					p.posY = y - 5;
				} else
					continue;

				p.life = p.lifeMax = (int) (Math.random() * life * 0.5) + life / 2;

				p.scale = (float) ((rand.nextInt(3) + 1) / 10.0);
				if (ID == 1)
					p.scale = (float) (p.scale + 0.2);
				p.growth = (float) ((rand.nextInt(3) + 1) / 800.0);
				p.imageID = ID;
				p.alpha = p.alphaMax = p.life / 2;
				return;
			}
		}
	}
}
