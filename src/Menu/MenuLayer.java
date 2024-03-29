package Menu;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import app.Game;
import rafgfxlib.Util;
import rafgfxlib.GameFrame.GFMouseButton;

public class MenuLayer {

	BufferedImage play;
	BufferedImage playGlow;
	BufferedImage connect;
	BufferedImage connectGlow;
	BufferedImage options;
	BufferedImage optionsGlow;
	BufferedImage quit;
	BufferedImage quitGlow;
	
	BufferedImage outerRing;
	BufferedImage innerRing;
	
	Button playButton;
	Button connectButton;
	Button optionsButton;
	Button quitButton;
	
	public float theta1 = 0;
	double scale = 0.7;
	double outerX, outerY;
	int outerWidth, outerHeight;
	
	public float theta2 = 0;
	double innerX, innerY;
	int innerWidth, innerHeight;
	
	public MenuLayer() {
		play = Util.loadImage("/assets/play.png");
		playGlow = Util.loadImage("/assets/play1.png");
		connect = Util.loadImage("/assets/connect.png");
		connectGlow = Util.loadImage("/assets/connect1.png");
		options = Util.loadImage("/assets/options.png");
		optionsGlow = Util.loadImage("/assets/options1.png");
		quit = Util.loadImage("/assets/quit.png");
		quitGlow = Util.loadImage("/assets/quit1.png");
		
		outerRing = Util.loadImage("/assets/outerRing.png");
		innerRing = Util.loadImage("/assets/innerRing.png");
		
		int offsetH = 10;
		playButton = new Button(play, playGlow, 210, 202+offsetH, "play");
		connectButton = new Button(connect, connectGlow, 200, 250+offsetH, "connect");
		optionsButton = new Button(options, optionsGlow, 187, 300+offsetH, "options");
		quitButton = new Button(quit, quitGlow, 202, 355+offsetH, "quit");
		
		outerWidth = (int)(outerRing.getWidth() * scale);
		outerHeight = (int)(outerRing.getHeight() * scale);
		innerWidth = (int)(innerRing.getWidth() * scale);
		innerHeight = (int)(innerRing.getHeight() * scale);
		
	}

	public void render(Graphics2D g, int sw, int sh) {
		

		
		playButton.render(g, sw, sh);
		connectButton.render(g, sw, sh);
		optionsButton.render(g, sw, sh);
		quitButton.render(g, sw, sh);
		
		AffineTransform transform = new AffineTransform();
		transform.setToIdentity();
		transform.translate(-outerWidth/2, 20);
		transform.rotate(theta1,outerWidth/2,outerHeight/2);
		transform.scale(scale, scale);
		g.drawImage(outerRing, transform, null);
		
		transform.setToIdentity();
		transform.translate(-innerWidth/2, 20);
		transform.rotate(theta2,innerWidth/2,innerHeight/2);
		transform.scale(scale, scale);
		g.drawImage(innerRing, transform, null);
			
	}

	public void update(int mouseX, int mouseY) {

		playButton.update(mouseX, mouseY);
		connectButton.update(mouseX, mouseY);
		optionsButton.update(mouseX, mouseY);
		quitButton.update(mouseX, mouseY);
		
		if (playButton.rotFlag || connectButton.rotFlag || optionsButton.rotFlag || quitButton.rotFlag){
			theta1 += 0.025f;
			theta2 -= 0.04f;
		}
		
	}
	
	public void handleMouseUp(int x, int y, GFMouseButton button) {
		playButton.handleMouseUp(x, y, button);
		quitButton.handleMouseUp(x, y, button);
	}

}
