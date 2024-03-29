package Characters;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import rafgfxlib.Util;

public class Player {

	public Tiles tile;
	public int x, y, dX, dY;
	float angle, speed = 5;
	public Sprite sprite, impactSprite;
	public int dir;
	public BufferedImage sheet, impactSheet;
	int width, height;
	int X;
	
	HealthBar hb;
	int dmg = 15;
	
	String name;
	Font font;
	int dmgTextLife = 10;
	Color red = new Color(1f, 0f, 0f, 1f);

	public int anim = 0;

	public boolean attacking = false;
	public ArrayList<Sprite> idleSprites;
	int idlePos = 0;

	public boolean idle = true;
	public ArrayList<Sprite> attackSprites;
	int attackPos = 0;

	public boolean walking = false;
	public ArrayList<Sprite> walkingSprites;
	int walkingPos = 0;
	
	public boolean dead;
	public ArrayList<Sprite> deathSprites;
	int deathPos = 0;

	public ArrayList<Sprite> impactSprites;
	int impactPos = 0;

	public ArrayList<ArrayList<Sprite>> list;

	public Player(int x, int y, String sheetPath, String descPath, String impactSheetPath, String name){
		this.x = x;
		this.y = y;

		idleSprites = new ArrayList<>();
		attackSprites = new ArrayList<>();
		walkingSprites = new ArrayList<>();
		deathSprites = new ArrayList<>();
		impactSprites = new ArrayList<>();

		list = new ArrayList<>();
		list.add(idleSprites);
		list.add(attackSprites);
		list.add(walkingSprites);
		list.add(deathSprites);

		sheet = Util.loadImage(sheetPath);
		loadSprites(descPath);

		impactSheet = Util.loadImage(impactSheetPath);

		sprite = idleSprites.get(0);
		impactSprite = impactSprites.get(0);

		width = sprite.SIZE;
		height = sprite.SIZE;
		dX = x;
		dY = y;
		hb = new HealthBar(60,5, this);
		hb.x = x;
		hb.y = y;
		this.name = name;
		font = new Font("Arial", Font.PLAIN, 12);
	}

	public void render(Graphics2D g, int sw, int sh) {
		g.drawImage(sheet, x, y, x + width, y + height, sprite.x, sprite.y, sprite.cx, sprite.cy, null);
		hb.render(g, sw, sh);
		g.setColor(hb.c1);
		g.setFont(font);
		g.drawString(name,x+(width-g.getFontMetrics().stringWidth(name))/2, (y+height/6)-8+5);
	}

	public void renderImpact(Graphics2D g, int sw, int sh, Tiles tile) {
		X = tile.x;
		if (tile.I > 4)
			X += tile.width;

		g.drawImage(impactSheet, X, tile.y, X + impactSprite.SIZE, tile.y + impactSprite.SIZE, impactSprite.x,
				impactSprite.y, impactSprite.cx, impactSprite.cy, null);
		if (attacking)
			g.setColor(red);
			g.drawString("-"+dmg, X, tile.y-dmgTextLife);
	}

	public void update() {
		anim++;
		if (anim == 1000)
			anim = 0;

		if (idle) {
			if (idlePos == idleSprites.size())
				idlePos = 0;
			if (anim % 2 == 0)
				sprite = idleSprites.get(idlePos++);
		}

		if (attacking) {
			dmgTextLife--;
			if (attackPos == attackSprites.size()) {
				attackPos = 0;
				impactPos = 0;
				attacking = false;
				idle = true;
				dmgTextLife = 0;
			}
			if (anim % 2 == 0) {
				sprite = attackSprites.get(attackPos++);
			}
			impactSprite = impactSprites.get(impactPos++);
			if (impactPos == impactSprites.size()) {
				impactPos = 0;
			}
		}

		if (walking) {
			if (walkingPos == walkingSprites.size())
				walkingPos = 0;
			if (anim % 2 == 0)
				sprite = walkingSprites.get(walkingPos++);
		}
		
		if (dead){
			if (deathPos == deathSprites.size())
				deathPos--;
			if (anim % 2 == 0)
				sprite = deathSprites.get(deathPos++);
		}

		move();
		hb.update(x+(width-hb.width)/2, y+height/6);
	}

	public void loadSprites(String path) {
		try {
			InputStream in = getClass().getResourceAsStream(path);
			Scanner s = new Scanner(in);
			int size = Integer.parseInt(s.nextLine());
			int columnNum = Integer.parseInt(s.nextLine());
			int impactSize = Integer.parseInt(s.nextLine());
			int columnNumImpact = Integer.parseInt(s.nextLine());
			int i = 0;
			while (s.hasNextLine()) {
				String input[] = (s.nextLine()).split(";");
				String start[] = (input[0]).split(",");
				String end[] = (input[1].split(","));
				int startOuter = Integer.parseInt(start[0]);
				int endOuter = Integer.parseInt(end[0]);
				int startInner = Integer.parseInt(start[1]);
				int endInner = Integer.parseInt(end[1]);

				while (startOuter >= endOuter) {

					while (startInner >= 0) {
						if (i < 4) {
							Sprite sprite = new Sprite(size, startOuter, startInner);
							list.get(i).add(sprite);
						} else {
							Sprite sprite = new Sprite(impactSize, startOuter, startInner);
							impactSprites.add(sprite);
						}
						if (startInner == endInner && startOuter == endOuter)
							break;
						startInner--;
					}

					startOuter--;
					startInner = columnNum;

				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCoordinates(int x, int y) {
		dX = x - (width / 2);
		dY = (int) (y - (height / 1.5));

		if (dX < this.x && width > 0) {
			mirror();
			this.x += sprite.SIZE;
			dX += sprite.SIZE;
			return;
		}

		if (dX > this.x && width < 0) {
			mirror();
			this.x -= sprite.SIZE;
			dX -= sprite.SIZE;
		}
	}

	public void atack(int x, int y) {
		if (x < this.x && width > 0) {
			mirror();
			this.x += sprite.SIZE;
			dX += sprite.SIZE;
			return;
		}

		if (x > this.x && width < 0) {
			mirror();
			this.x -= sprite.SIZE;
			dX -= sprite.SIZE;
			return;
		}
	}

	public void move() {
		if (x == dX && y == dY) {
			walking = false;
			idle = true;
			return;
		}

		double distance = Math.sqrt((x - dX) * (x - dX) + (y - dY) * (y - dY));
		if (distance < 3) {
			x = dX;
			y = dY;
			return;
		}

		float deltaX = dX - x;
		float deltaY = dY - y;
		angle = (float) Math.atan2(deltaY, deltaX);

		x += speed * Math.cos(angle);
		y += speed * Math.sin(angle);

		walking = true;
		idle = false;
		attacking = false;
	}

	public void mirror() {
		width = -width;
	}

	public void setTile(Tiles tile) {
		this.tile = tile;
	}

}
