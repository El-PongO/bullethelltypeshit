import java.awt.*;
import java.util.ArrayList;

public class TankEnemy extends Enemy {
    private boolean isMoving;
    private int pauseCounter;
    private int moveTimer;
    
    public TankEnemy(int x, int y) {
        super(x, y);
        // Tank enemies have quick shooting but move slowly
        this.shootDelay = 1300; // 1.3 seconds between shots
        this.bulletSpeed = 2; // Slower but more frequent bullets
        this.size = 30; // Larger size
        
        // Initialize movement variables
        isMoving = true;
        pauseCounter = 0;
        moveTimer = 0;
    }
    
    @Override
    public void update(Player player, ArrayList<Bullet> enemyBullets) {
        // Tank enemy moves slowly with periodic pauses
        int dx = 0, dy = 0;
        
        // State machine for moving and pausing
        if (isMoving) {
            // When moving, head toward the player slowly
            double angle = Math.atan2(player.getY() - y, player.getX() - x);
            dx = (int) Math.signum(Math.cos(angle));
            dy = (int) Math.signum(Math.sin(angle));
            
            // Move timer counts down until we pause
            moveTimer--;
            if (moveTimer <= 0) {
                isMoving = false;
                pauseCounter = 30 + rand.nextInt(40); // Pause for 30-70 frames
            }
        } else {
            // When paused, count down until we move again
            pauseCounter--;
            if (pauseCounter <= 0) {
                isMoving = true;
                moveTimer = 20 + rand.nextInt(30); // Move for 20-50 frames
            }
        }
        
        // Move slower than normal enemies when moving
        if (isMoving) {
            x += dx * (speed - 1); // Move at speed - 1
            y += dy * (speed - 1);
        }
          // Tank doesn't shoot, it just moves toward the player slowly
    }
    
    @Override
    public void draw(Graphics g, int ex, int ey) {
        g.setColor(new Color(50, 50, 150)); // Dark blue color for tank enemy
        g.fillRect(ex, ey, size, size); // Tank is square-shaped
        
        // Draw tank "turret"
        g.setColor(new Color(100, 100, 200));
        g.fillOval(ex + size/4, ey + size/4, size/2, size/2);
    }
}
