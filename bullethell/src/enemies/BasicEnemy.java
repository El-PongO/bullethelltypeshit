package enemies;
import java.awt.*;
import java.util.ArrayList;
import players.Player;
import weapons.Bullet;

public class BasicEnemy extends Enemy {
    private int movementCounter = 0;  // Counter for controlling movement duration
    private int movementDirection = 0; // 0 = none, -1 = left, 1 = right
    private int movementDuration = 0; // How long to move in a direction
    private int pauseDuration = 0;    // How long to pause after movement
      public BasicEnemy(int x, int y) {
        super(x, y);
        // Specific properties for basic enemy
        this.shootDelay = 2000; // 2 second delay between shots
        this.bulletSpeed = 3;
        this.health = 100; // Basic enemy has 100 HP
    }
      @Override
    public void update(Player player, ArrayList<Bullet> enemyBullets) {
        int behavior = rand.nextInt(2); // 0 or 1 for two behavior types
        int dx = 0, dy = 0;

        // Behavior 1: move toward or away from player based on distance
        double distance = Math.hypot(player.getX() - x, player.getY() - y);
        if (behavior == 0) {
            if (distance > 50) {
                dx = (player.getX() > x) ? 1 : -1;
                dy = (player.getY() > y) ? 1 : -1;
            } else {
                dx = (player.getX() < x) ? 1 : -1;
                dy = (player.getY() < y) ? 1 : -1;
            }
        }
        // Behavior 2: random lateral movement with pause or just idle
        if (behavior == 1) {
            // If we're not in a movement sequence, decide on a new one
            if (movementCounter <= 0) {
                int move = rand.nextInt(3); // 0 = left then pause, 1 = right then pause, 2 = just idle
                
                if (move == 0) {
                    // Move left for a short time, then pause
                    movementDirection = -1;
                    movementDuration = 10 + rand.nextInt(20); // Move for 10-30 frames
                    pauseDuration = 20 + rand.nextInt(30);    // Then pause for 20-50 frames
                } else if (move == 1) {
                    // Move right for a short time, then pause
                    movementDirection = 1;
                    movementDuration = 10 + rand.nextInt(20); // Move for 10-30 frames
                    pauseDuration = 20 + rand.nextInt(30);    // Then pause for 20-50 frames
                } else {
                    // Just idle for some time
                    movementDirection = 0;
                    movementDuration = 0;
                    pauseDuration = 30 + rand.nextInt(40);    // Idle for 30-70 frames
                }
                
                movementCounter = movementDuration + pauseDuration;
            }
            
            // Apply movement based on current state
            if (movementCounter > pauseDuration) {
                // We're in the movement phase
                dx = movementDirection;
            } else {
                // We're in the pause phase
                dx = 0;
            }
            
            // Decrement the counter
            movementCounter--;
        }

        x += dx * speed; // move speed
        y += dy * speed;
        
        // Handle shooting internally
        Bullet bullet = tryShoot(player.getX(), player.getY());
        if (bullet != null) {
            enemyBullets.add(bullet);
        }
    }
    
    @Override
    public void draw(Graphics g, int ex, int ey) {
        g.setColor(new Color(102, 51, 153)); // Purple color for basic enemy
        g.fillOval(ex, ey, size, size);
    }
}
