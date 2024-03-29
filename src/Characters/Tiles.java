package Characters;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import app.Game;
import app.Pseudo3D;
import rafgfxlib.GameFrame.GFMouseButton;
import rafgfxlib.Util;

public class Tiles {

	int x, y, size;
	BufferedImage image1, image2, image;
	int offsetX = 300, offsetY = 240;
	float alpha = 0.2f;
	float scale;
	double mov = 0;
	int i, j;
	int width, height;

	public int I, J;

	public Tiles(int i, int j) {// i - horizontalno j - vertikalno
		this.i = i;
		this.j = (4 - j) * 3;
		x = i;
		y = j;
		I = i;
		J = j;
		image1 = Util.loadImage("/assets/tile.png");
		image2 = Util.loadImage("/assets/tile3.png");
		size = 65;

		x *= size;
		y *= size;

		x += offsetX;
		y += offsetY;

		Point2D p0;
		Point2D p1;
		Point2D p2;
		Point2D p3;

		int tmp = size;
		if (i < 4) {// levo
			double s = size * 1.8;
			mov = (j) * (4 * 2 - i * 2 + 1);

			double shift = (10 - i * 1.8) * 2;
			p0 = new Point2D.Double(0 + shift, 0 + this.j);
			p1 = new Point2D.Double(0 + s + shift, 0 + this.j);
			p2 = new Point2D.Double(0 + s, 0 + s);
			p3 = new Point2D.Double(0, 0 + s);
			image1 = Pseudo3D.computeImage(image1, p0, p1, p2, p3);
			image2 = Pseudo3D.computeImage(image2, p0, p1, p2, p3);

			x = (int) (x - mov);
		}

		if (i == 4) {// centar
			double s = size * 1.8;

			p0 = new Point2D.Double(0 + 3, 0 + this.j);
			p1 = new Point2D.Double(0 + s - 5, 0 + this.j);
			p2 = new Point2D.Double(0 + s + 5, 0 + s);
			p3 = new Point2D.Double(0 - 5, 0 + s);
			image1 = Pseudo3D.computeImage(image1, p0, p1, p2, p3);
			image2 = Pseudo3D.computeImage(image2, p0, p1, p2, p3);

		}

		if (i > 4) { // desno
			i = i - 4;
			double s = size * 1.8;
			mov = (j) * (i * 2 + 1);

			double shift = ((10 - 4 * 1.8) + i * 1.8) * 2;
			p0 = new Point2D.Double(0 + shift, 0 + this.j);
			p1 = new Point2D.Double(0 + s + shift, 0 + this.j);
			p2 = new Point2D.Double(0 + s, 0 + s);
			p3 = new Point2D.Double(0, 0 + s);
			image1 = Pseudo3D.computeImage(image1, p0, p1, p2, p3);
			image2 = Pseudo3D.computeImage(image2, p0, p1, p2, p3);

			x = (int) (x + mov);
			x += size - 5;
		}
		size = tmp;
		this.j = j;

		height = size;
		if (this.i > 4)
			width = -size;
		if (this.i < 4)
			width = size;
		if (this.i == 4)
			width = size + j;
		image = image1;
	}

	public void render(Graphics2D g, int sw, int sh) {

		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

		g.drawImage(image, x, y, width, height, null);

		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

	}

	public void update(int mouseX, int mouseY) {
		if (i > 4) {
			mouseX += size;
		}
		if (mouseX > x && mouseX < (x + size) && mouseY > y && mouseY < (y + size)) {
			alpha = 1;
		} else {
			alpha = 0.2f;
		}

		if (alpha == 1) {
			Player p1 = Game.get().playScreen.player1;
			Player p2 = Game.get().playScreen.player2;

			if (Game.get().playerTurn) {
				if (check(p1, p2)) {
					if (p2.tile == this)
						Game.get().changeCoursor(Game.get().cursorAttack);
					else
						Game.get().changeCoursor(Game.get().cursor);
					return;
				} else {
					if (p2.tile == this)
						Game.get().changeCoursor(Game.get().cursorBlock);
					else
						Game.get().changeCoursor(Game.get().cursor);
					return;
				}
			} else {
				if (check(p1, p2)) {
					if (p1.tile == this)
						Game.get().changeCoursor(Game.get().cursorAttack);
					else
						Game.get().changeCoursor(Game.get().cursor);
					return;
				} else {
					if (p1.tile == this)
						Game.get().changeCoursor(Game.get().cursorBlock);
					else
						Game.get().changeCoursor(Game.get().cursor);
					return;
				}
			}
		}
	}

	public void handleMouseUp(int mouseX, int mouseY, GFMouseButton button) {

		if (alpha == 1) {
			Player p1 = Game.get().playScreen.player1;
			Player p2 = Game.get().playScreen.player2;

			if (p2.tile == this && Game.get().playerTurn) {// napada player1
				if (check(p1, p2)) {
					endTurn(p1, p2);
					p1.atack(x + (width / 2), y + (height / 2));
					p1.idle = false;
					p1.attacking = true;
					p2.hb.dealDmg(p1.dmg);
					return;
				} else {
					return;
				}
			}
			if (p1.tile == this && !Game.get().playerTurn) {// napada player2
				if (check(p1, p2)) {
					endTurn(p1, p2);
					p2.atack(x + (width / 2), y + (height / 2));
					p2.idle = false;
					p2.attacking = true;
					p1.hb.dealDmg(p2.dmg);
					return;
				} else {
					return;
				}
			}

			if (Game.get().playerTurn) {
				if (p1.tile == this)
					return;
				p1.setCoordinates(x + (width / 2), y + (height / 2));
				p1.setTile(this);
			} else {
				if (p2.tile == this)
					return;
				p2.setCoordinates(x + (width / 2), y + (height / 2));
				p2.setTile(this);
			}

			endTurn(p1, p2);
		}
	}

	public void endTurn(Player p1, Player p2) {
		Tiles tiles[][] = Game.get().playScreen.tiles;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				tiles[i][j].setPaintedTile(false);
			}
		}
		if (Game.get().playerTurn) {
			p2.tile.setPaintedTile(true);
		} else {
			p1.tile.setPaintedTile(true);
		}
		Game.get().playerTurn = !Game.get().playerTurn;
	}

	public boolean check(Player p1, Player p2) {
		int resX = Math.abs(p1.tile.I - p2.tile.I);
		int resY = Math.abs(p1.tile.J - p2.tile.J);

		if (resX <= 1 && resY <= 1)
			return true;

		return false;
	}

	public void setPaintedTile(boolean p) {
		if (p)
			image = image2;
		else
			image = image1;
	}

}
