import java.awt.*;
import java.util.ArrayList;
import players.Player;
import players.Bullet;
public class FastEnemy extends Enemy {
    private int changeDirectionCounter;
    private int directionX;
    private int directionY;
      public FastEnemy(int x, int y) {
        super(x, y);
        // Fast enemies have longer shooting delay but move faster
        this.shootDelay = 3500; // 3.5 seconds between shots
        this.bulletSpeed = 4; // Slightly faster bullets
        this.size = 16; // Slightly smaller size
        this.health = 50; // Less health than basic enemies
        
        // Initialize movement variables
        changeDirectionCounter = 0;
        directionX = rand.nextInt(3) - 1; // -1, 0, or 1
        directionY = rand.nextInt(3) - 1; // -1, 0, or 1
    }
    
    @Override
    public void update(Player player, ArrayList<Bullet> enemyBullets) {
        // Fast enemy uses erratic movement pattern
        
        // Periodically change direction
        if (changeDirectionCounter <= 0) {
            // Pick a new random direction
            directionX = rand.nextInt(3) - 1; // -1, 0, or 1
            directionY = rand.nextInt(3) - 1; // -1, 0, or 1
            
            // Make sure we're not completely stopped
            if (directionX == 0 && directionY == 0) {
                if (rand.nextBoolean()) {
                    directionX = rand.nextBoolean() ? 1 : -1;
                } else {
                    directionY = rand.nextBoolean() ? 1 : -1;
                }
            }
            
            // Set a new counter for next direction change
            changeDirectionCounter = 15 + rand.nextInt(30); // Change direction every 15-45 frames
        }
        
        // Move faster than normal enemies
        x += directionX * (speed + 1); // Move at speed + 2
        y += directionY * (speed + 1);
        
        // Decrease the counter
        changeDirectionCounter--;
        
        // Occasionally dart toward the player
        if (rand.nextInt(50) == 0) { // 1 in 50 chance each frame
            double angle = Math.atan2(player.getY() - y, player.getX() - x);
            x += Math.cos(angle) * (speed * 3);
            y += Math.sin(angle) * (speed * 3);
        }
        
        // Shoot less frequently
        Bullet bullet = tryShoot(player.getX(), player.getY());
        if (bullet != null) {
            enemyBullets.add(bullet);
        }
    }
    
    @Override
    public void draw(Graphics g, int ex, int ey) {
        g.setColor(new Color(255, 102, 0)); // Orange color for fast enemy
        g.fillOval(ex, ey, size, size);
    }
}
