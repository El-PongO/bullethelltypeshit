package powerup;

import players.Player;
import java.awt.*;
import java.awt.Graphics;

public class SpeedPower extends PowerUp {
    public SpeedPower(int x, int y, Player player) {
        this.x = x;
        this.y = y;
        this.player = player;
    }

    @Override
    public void activate() {
        player.speedBoost(duration, 2);
    }

    @Override
    public void draw(Graphics g, int px, int py) {
        g.setColor(new Color(128, 128, 128, 150));
        g.fillOval(px, py+18, size, size/5);
        
        g.setColor(Color.WHITE); // dark background
        g.fillOval(px, py, size, size);

        py+=20;
        px+=1;
        g.setColor(Color.BLACK);
        g.fillRect(px+5, py-5, 10, 1);
        g.setColor(new Color(160, 90, 60));
        g.fillRect(px+5, py-14, 5, 9);
        g.fillRect(px+10, py-9, 1, 4);
        g.fillRect(px+11, py-8, 3, 3);
        g.fillRect(px+14, py-7, 1, 2);
    }
}
