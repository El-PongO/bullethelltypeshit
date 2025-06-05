import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import players.Bullet;
import players.Player;

public class BomberEnemy extends Enemy {
    private boolean isCharging = false;
    private int chargeSpeed;
    private int normalSpeed;
    private long chargeStartTime;
    private static final long CHARGE_DURATION = 2000; // 2 seconds of charging
    
    public BomberEnemy(int x, int y) {
        super(x, y, 25, 2, new Color(255, 165, 0), 0, 0, 5); // Orange color, 5 health
        this.normalSpeed = speed;
        this.chargeSpeed = 4; // Faster when charging
        
        // Try to load sprite
        try {
            sprite = ImageIO.read(getClass().getResource("/Assets/player/bullets/enemy_bomber.png"));
        } catch (Exception e) {
            // Keep using color fallback if sprite loading fails
            System.out.println("Could not load bomber enemy sprite");
        }
    }    private boolean hasExploded = false;
    private static final int EXPLOSION_DAMAGE = 3;
    private static final int EXPLOSION_RADIUS = 100;
    
    @Override
    public void update(Player player, ArrayList<Bullet> enemyBullets) {
        double distance = Math.hypot(player.getX() - x, player.getY() - y);
        
        // Check if bomber should explode (very close to player)
        if (distance < 30 && !hasExploded) {
            // Create explosion bullets in all directions
            explode(player, enemyBullets);
            hasExploded = true;
            health = 0; // Kill the bomber after explosion
            return;
        }
        
        double angle = Math.atan2(player.getY() - y, player.getX() - x);
        double dx, dy;
        
        // Start charging when close enough
        if (distance < 150 && !isCharging) {
            isCharging = true;
            chargeStartTime = System.currentTimeMillis();
            speed = chargeSpeed;
        }
        
        // Check if charge duration is over
        if (isCharging && System.currentTimeMillis() - chargeStartTime > CHARGE_DURATION) {
            isCharging = false;
            speed = normalSpeed;
        }
        
        // Calculate movement
        dx = Math.cos(angle) * speed;
        dy = Math.sin(angle) * speed;
        
        // Move toward player
        x += dx;
        y += dy;
    }
      private void explode(Player player, ArrayList<Bullet> enemyBullets) {
        // Create explosion bullets in 8 directions
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * i / 4; // 45 degree increments
            int dx = (int)(Math.cos(angle) * bulletSpeed * 2); // Make explosion bullets faster
            int dy = (int)(Math.sin(angle) * bulletSpeed * 2);
            
            Bullet explosionBullet = new Bullet(x + size/2, y + size/2, dx, dy, null, EXPLOSION_DAMAGE);
            enemyBullets.add(explosionBullet);
        }
        
        // If player is within explosion radius, damage them directly
        double distanceToPlayer = Math.hypot(player.getX() - x, player.getY() - y);
        if (distanceToPlayer < EXPLOSION_RADIUS && !player.isInvincible()) {
            player.takeDamage(50); // Direct explosion damage
        }
    }
    
    @Override
    public void draw(Graphics g, int ex, int ey) {
        // Draw main body
        if (isCharging) {
            g.setColor(Color.RED); // Change color when charging
        } else {
            g.setColor(color);
        }
        g.fillOval(ex, ey, size, size);
        
        // Add some details to make it look like it has a bomb
        g.setColor(Color.BLACK);
        int fuse = 5;
        g.fillRect(ex + size/2 - 1, ey - fuse, 2, fuse);
    }
}
