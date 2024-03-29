package app;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import Menu.MenuLayer;
import Screens.EscScreen;
import Screens.PlayScreen;
import Screens.Screen1;
import Screens.Screen2;
import Screens.Screen3;
import Screens.TransitionScreen;
import rafgfxlib.GameFrame;
import rafgfxlib.Util;

public class Game extends GameFrame {
	private BufferedImage icon;

	public static int width = 1200;
	public static int height = 800;
	public int screenNumber = 3;
	public int MinScNum = 1;
	public int MaxScNum = 3;
	
	public Cursor cursor;
	public Cursor cursorAttack;
	public Cursor cursorBlock;

	public MenuLayer menuLayer;
	public TransitionScreen transitionScreen;
	public EscScreen escScreen;
	public Screen1 screen1;
	public Screen2 screen2;
	public Screen3 screen3;

	public boolean playerTurn = false;

	boolean escDown = false;
	boolean rightDown = false;
	boolean leftDown = false;

	public PlayScreen playScreen;

	public static Game instance;

	public static Game get() {
		if (instance == null)
			new Game();
		return instance;
	}

	public Game() {
		super("Racunarska Grafika", width, height);
		instance = this;
		setHighQuality(true);

		icon = Util.loadImage("/assets/icon.png");

		menuLayer = new MenuLayer();
		// screens
		transitionScreen = new TransitionScreen();
		escScreen = new EscScreen();
		screen1 = new Screen1();
		screen2 = new Screen2();
		screen3 = new Screen3();

		playScreen = new PlayScreen();

		BufferedImage cursorImg = Util.loadImage("/assets/cs4.png");
		cursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "cursor");
		
		cursorImg = Util.loadImage("/assets/block.png");
		cursorBlock = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "block");
		
		cursorImg = Util.loadImage("/assets/attack.png");
		cursorAttack = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "atack");
		
		changeCoursor(cursor);

		startThread();
	}

	@Override
	public void render(Graphics2D g, int sw, int sh) {

		if (screenNumber == 4) {
			playScreen.render(g, sw, sh);
		}

		if (screenNumber == -1)
			transitionScreen.render(g, sw, sh);

		if (screenNumber == 0) {
			escScreen.render(g, sw, sh);
			return;
		}

		if (screenNumber == 1)
			screen1.render(g, sw, sh);

		if (screenNumber == 2)
			screen2.render(g, sw, sh);

		if (screenNumber == 3)
			screen3.render(g, sw, sh);

		if (screenNumber != 4)
			menuLayer.render(g, sw, sh);
	}

	@Override
	public void update() {

		if (screenNumber == 4) {
			playScreen.update(getMouseX(), getMouseY());
		}

		if (screenNumber != 4)
			menuLayer.update(getMouseX(), getMouseY());

		if (screenNumber == -1) {
			transitionScreen.update();
			return;
		}

		if (screenNumber == 0) {
			escScreen.update(getMouseX(), getMouseY(), isKeyDown(KeyEvent.VK_ESCAPE));
			return;
		}

		if (screenNumber == 1)
			screen1.update(getMouseX(), getMouseY());
		if (screenNumber == 2)
			screen2.update(getMouseX(), getMouseY());
		if (screenNumber == 3)
			screen3.update(getMouseX(), getMouseY());

		if (screenNumber != 4) {
			if (!isKeyDown(KeyEvent.VK_LEFT)) {
				if (leftDown) {
					leftDown = false;
					next(screenNumber - 1);
				}
			} else
				leftDown = true;

			if (!isKeyDown(KeyEvent.VK_RIGHT)) {
				if (rightDown) {
					rightDown = false;
					next(screenNumber + 1);
				}
			} else
				rightDown = true;
		}

		if (!isKeyDown(KeyEvent.VK_ESCAPE)) {
			if (escDown) {
				escDown = false;
				BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D g2 = bImage.createGraphics();
				render(g2, width, height);

				BufferedImage bufImage = new BufferedImage(600, 400, BufferedImage.TYPE_INT_RGB);
				Graphics2D g3 = bufImage.createGraphics();
				g3.drawImage(bImage, 0, 0, 600, 400, null);

				escScreen.setBackground(bufImage, screenNumber);
				screenNumber = 0;
			}
		} else
			escDown = true;

	}

	@Override
	public void handleWindowInit() {
		setIcon(icon);

	}

	@Override
	public void handleWindowDestroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleMouseDown(int x, int y, GFMouseButton button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleMouseUp(int x, int y, GFMouseButton button) {
		if (screenNumber == 4)
			playScreen.handleMouseUp(x, y, button);
		else
			menuLayer.handleMouseUp(x, y, button);
	}

	@Override
	public void handleMouseMove(int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleKeyDown(int keyCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleKeyUp(int keyCode) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		GameFrame gf = new Game();
		gf.initGameWindow();
	}

	public void next(int scNum) {
		BufferedImage screen1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = screen1.createGraphics();
		render(g2, width, height);

		if (scNum > Game.get().MaxScNum)
			scNum = Game.get().MinScNum;

		if (scNum < Game.get().MinScNum)
			scNum = Game.get().MaxScNum;

		screenNumber = scNum;

		BufferedImage screen2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g3 = screen2.createGraphics();
		render(g3, width, height);

		screenNumber = -1;
		transitionScreen.setScreens(screen1, screen2, scNum);

	}

	public void changeCoursor(Cursor cursor) {
		this.setCursor(cursor);
	}

}
