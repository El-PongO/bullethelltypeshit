import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import players.Bullet;
import players.Player;

public class TankEnemy extends Enemy {
    
    public TankEnemy(int x, int y) {
        super(x, y, 40, 1, new Color(50, 120, 50), 3000, 2, 10); // Green color, larger size, slower speed, high health
        
        // Try to load sprite
        try {
            sprite = ImageIO.read(getClass().getResource("/Assets/player/bullets/enemy_tank.png"));
        } catch (Exception e) {
            // Keep using color fallback if sprite loading fails
            System.out.println("Could not load tank enemy sprite");
        }
    }
      private BufferedImage bulletSprite;
    
    @Override
    public void update(Player player, ArrayList<Bullet> enemyBullets) {
        // Tank enemy moves slowly toward the player and occasionally shoots
        double dx = 0, dy = 0;
        
        // Slow movement toward player
        double angle = Math.atan2(player.getY() - y, player.getX() - x);
        dx = Math.cos(angle) * speed;
        dy = Math.sin(angle) * speed;
        
        // Move toward the player slowly
        x += dx;
        y += dy;
        
        // Tank also shoots occasionally but slower than shooter
        Bullet bullet = tryShoot(player.getX(), player.getY());
        if (bullet != null) {
            enemyBullets.add(bullet);
        }
    }
    
    @Override
    protected Bullet createBullet(int dx, int dy, double angle) {
        // Tank has stronger bullets
        try {
            if (bulletSprite == null) {
                bulletSprite = ImageIO.read(getClass().getResource("/Assets/player/bullets/bullet3.png"));
            }
        } catch (Exception e) {
            // Use default if sprite fails to load
        }
        return new Bullet(x + size/2, y + size/2, dx, dy, bulletSprite, 2); // Higher damage
    }
    
    @Override
    public void draw(Graphics g, int ex, int ey) {
        g.setColor(color);
        g.fillOval(ex, ey, size, size);
        
        // Add some details to make it look like a tank
        g.setColor(Color.DARK_GRAY);
        int barrelLength = size / 2;
        g.fillRect(ex + size/2 - 2, ey - barrelLength/2 + size/2, 4, barrelLength);
    }
}
