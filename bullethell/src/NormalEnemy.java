import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import players.Bullet;
import players.Player;

public class NormalEnemy extends Enemy {
    
    public NormalEnemy(int x, int y) {
        super(x, y, 20, 2, new Color(255, 0, 0), 0, 0, 3); // Red color, no shooting ability, 3 health
        
        // Try to load sprite
        try {
            sprite = ImageIO.read(getClass().getResource("/Assets/player/bullets/enemy_normal.png"));
        } catch (Exception e) {
            // Keep using color fallback if sprite loading fails
            System.out.println("Could not load normal enemy sprite");
        }
    }
    
    @Override
    public void update(Player player, ArrayList<Bullet> enemyBullets) {
        // Normal enemy just moves toward the player
        double dx = 0, dy = 0;
        
        // Calculate direction to the player
        double angle = Math.atan2(player.getY() - y, player.getX() - x);
        dx = Math.cos(angle) * speed;
        dy = Math.sin(angle) * speed;
        
        // Move toward the player
        x += dx;
        y += dy;
    }
}
