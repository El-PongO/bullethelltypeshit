import java.awt.*;
import java.util.ArrayList;

public class LurkerEnemy extends Enemy {
    private int stateCounter; // Counter for controlling states
    private boolean isHiding; // Whether the enemy is currently "hiding"
    private boolean isJumping; // Whether enemy is in jump animation
    private int targetX, targetY; // Target position for jumps
    private double jumpProgress; // Progress of jump animation (0.0 to 1.0)
    private int jumpDuration; // How many frames the jump takes
    private int startX, startY; // Starting position for jumps
    
    public LurkerEnemy(int x, int y) {
        super(x, y);
        // Lurkers don't shoot, they jump toward the player
        this.size = 16; // Smaller size
        this.isHiding = true;
        this.isJumping = false;
        this.jumpProgress = 0.0;
        this.jumpDuration = 12; // Frames to complete jump (adjust for speed)
        this.stateCounter = 40 + rand.nextInt(60); // Hide for 40-100 frames initially
    }
      @Override
    public void update(Player player, ArrayList<Bullet> enemyBullets) {
        // Handle the jump animation if we're currently jumping
        if (isJumping) {
            // Increase jump progress
            jumpProgress += 1.0 / jumpDuration;
            
            // Use easing function for smoother movement (ease-in, then ease-out)
            double easedProgress = jumpProgress < 0.5 ? 
                2 * jumpProgress * jumpProgress : // Ease in (first half)
                1 - Math.pow(-2 * jumpProgress + 2, 2) / 2; // Ease out (second half)
            
            // Calculate current position based on progress
            x = startX + (int)((targetX - startX) * easedProgress);
            y = startY + (int)((targetY - startY) * easedProgress);
            
            // Check if jump is complete
            if (jumpProgress >= 1.0) {
                isJumping = false;
                isHiding = true;
                jumpProgress = 0.0;
                stateCounter = 40 + rand.nextInt(40); // Hide for 40-80 frames after jump
            }
            
            return; // Skip the rest while jumping
        }
        
        // Normal state handling
        stateCounter--;
        
        // Toggle states between hiding and preparing to jump
        if (stateCounter <= 0) {
            if (isHiding) {
                // Time to prepare for jump toward the player
                isHiding = false;
                
                // Store starting position
                startX = x;
                startY = y;
                
                // Calculate target position (closer to player, but not too close)
                double angle = Math.atan2(player.getY() - y, player.getX() - x);
                double distance = Math.hypot(player.getX() - x, player.getY() - y);
                
                // Jump up to 70% of the distance or 100px max - reduced from 80%/150px
                double jumpDistance = Math.min(distance * 0.7, 100);
                targetX = (int)(x + Math.cos(angle) * jumpDistance);
                targetY = (int)(y + Math.sin(angle) * jumpDistance);
                
                // Initialize jump
                jumpProgress = 0.0;
                isJumping = true;
                
                // Jump duration varies with distance (longer jumps take more time)
                jumpDuration = 10 + (int)(jumpDistance / 15); // 10-16 frames for jump
            }
        }
        
        // When hiding, do small random movements
        if (isHiding && rand.nextInt(3) == 0) {
            // Small random movements
            x += rand.nextInt(3) - 1;
            y += rand.nextInt(3) - 1;
        }
        
        // Lurkers don't shoot
    }
      @Override
    public void draw(Graphics g, int ex, int ey) {
        // Size modifier for jump animation
        int currentSize = size;
        
        // Color changes based on state
        if (isHiding) {
            g.setColor(new Color(50, 150, 50)); // Darkish green for hiding
        } else if (isJumping) {
            // Create pulsing effect when jumping
            g.setColor(new Color(100, 255, 100)); // Bright green for jumping
            
            // Make the lurker grow and shrink during jump animation
            double sizeFactor = 1.0 + 0.5 * Math.sin(jumpProgress * Math.PI);
            currentSize = (int)(size * sizeFactor);
        } else {
            // About to jump state
            g.setColor(new Color(150, 200, 100)); // Yellowish green for preparing to jump
        }
        
        // Calculate position adjustments for size changes
        int offsetX = (currentSize - size) / 2;
        int offsetY = (currentSize - size) / 2;
        
        // Draw as a diamond shape
        int[] xPoints = {
            ex + currentSize/2 - offsetX, 
            ex + currentSize - offsetX, 
            ex + currentSize/2 - offsetX, 
            ex - offsetX
        };
        int[] yPoints = {
            ey - offsetY, 
            ey + currentSize/2 - offsetY, 
            ey + currentSize - offsetY, 
            ey + currentSize/2 - offsetY
        };
        g.fillPolygon(xPoints, yPoints, 4);
        
        // Add a visual indicator when about to jump
        if (!isHiding && !isJumping) {
            g.setColor(new Color(255, 255, 100, 150)); // Semi-transparent yellow
            g.drawOval(ex - 5, ey - 5, currentSize + 10, currentSize + 10);
        }
    }
}
