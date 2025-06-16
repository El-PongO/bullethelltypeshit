package powerup;

import java.awt.*;
import java.awt.Graphics;

import players.Player;

public abstract class PowerUp {
    public Player player;
    public int x, y;
    public int size = 20;
    public int duration = 10000; // 10 seconds
    public abstract void activate();

    public void draw(Graphics g, int x, int y) {
        g.setColor(Color.WHITE);
        g.fillOval(x, y, size, size);
    }

}
