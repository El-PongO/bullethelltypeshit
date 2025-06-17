package powerup;

import players.Player;
import java.awt.*;
import java.awt.Graphics;

public class HealPower extends PowerUp {
    public HealPower(int x, int y, Player player) {
        this.x = x;
        this.y = y;
        this.player = player;
    }

    @Override
    public void activate() {
        player.heal(200, true);
    }

    @Override
    public void draw(Graphics g, int px, int py) {
        g.setColor(new Color(128, 128, 128, 150));
        g.fillOval(px, py+18, size, size/5);
        g.setColor(Color.GREEN);
        g.fillOval(px, py, size, size);
        g.setColor(Color.WHITE);
        g.fillRect(px+4, py+8, size-8, size/5);
        g.fillRect(px+8, py+4, size/5, size-8);
    }
}
