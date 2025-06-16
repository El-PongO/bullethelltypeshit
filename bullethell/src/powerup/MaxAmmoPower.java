package powerup;

import players.Player;
import java.awt.*;
import java.awt.Graphics;

public class MaxAmmoPower extends PowerUp {
    public MaxAmmoPower(int x, int y, Player player) {
        this.x = x;
        this.y = y;
        this.player = player;
    }

    @Override
    public void activate() {
        player.getCurrentWeapon().setCurrentAmmo(player.getCurrentWeapon().getMaxAmmo());
    }

    @Override
    public void draw(Graphics g, int px, int py) {
        g.setColor(new Color(128, 128, 128, 150));
        g.fillOval(px, py+18, size, size/5);
        int bulletHeight = 8;
        int bulletWidth = 4;
        int spacing = 1;

        g.setColor(new Color(50, 50, 50)); // dark background
        g.fillOval(px, py, size, size);


        for (int i = 0; i < 3; i++) {
            int bx = px + 3 + i * (bulletWidth + spacing);
            int by = py + 5;

            // Bullet head (top oval)
            g.setColor(Color.GRAY); // bullet head color
            g.fillOval(bx, by - 1, bulletWidth, bulletWidth);

            // Bullet body (rectangle)
            g.setColor(Color.YELLOW); // bullet color
            g.fillRect(bx, by+2, bulletWidth, bulletHeight);
        }
    }
}
