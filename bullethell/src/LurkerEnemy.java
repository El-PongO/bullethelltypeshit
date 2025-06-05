import java.awt.*;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import players.Player;
import players.Bullet;
public class LurkerEnemy extends Enemy {
    private int stateCounter; // Counter for controlling states
    private boolean isHiding; // Whether the enemy is currently "hiding"
    private boolean isJumping; // Whether enemy is in jump animation
    private int targetX, targetY; // Target position for jumps
    private double jumpProgress; // Progress of jump animation (0.0 to 1.0)
    private int jumpDuration; // How many frames the jump takes
    private int startX, startY; // Starting position for jumps
    public BufferedImage idledown, idleleft, idleright, idleup, up1, up2, down1, down2, left1, left2, right1, right2, hiding, jumping;
    public String direction;
    public boolean idling;
    public int spritecounter=0;
    public int spritenum=1;
    
    public LurkerEnemy(int x, int y) {
        super(x, y);
        // Lurkers don't shoot, they jump toward the player
        this.size = 16; // Smaller size
        this.isHiding = true;
        this.isJumping = false;
        this.jumpProgress = 0.0;
        this.jumpDuration = 12; // Frames to complete jump (adjust for speed)
        this.stateCounter = 40 + rand.nextInt(60); // Hide for 40-100 frames initially
        getEnemyImage();
        direction="down";
        idling=true;
    }

    public void getEnemyImage(){
        try {
            up1 = ImageIO.read(getClass().getResource("/Assets/LurkerEnemy/up1.png"));
            up2 = ImageIO.read(getClass().getResource("/Assets/LurkerEnemy/up2.png"));
            down1 = ImageIO.read(getClass().getResource("/Assets/LurkerEnemy/down1.png")); // FIXED
            down2 = ImageIO.read(getClass().getResource("/Assets/LurkerEnemy/down2.png"));
            left1 = ImageIO.read(getClass().getResource("/Assets/LurkerEnemy/left1.png"));
            left2 = ImageIO.read(getClass().getResource("/Assets/LurkerEnemy/left2.png"));
            right1 = ImageIO.read(getClass().getResource("/Assets/LurkerEnemy/right1.png"));
            right2 = ImageIO.read(getClass().getResource("/Assets/LurkerEnemy/right2.png")); // not "right.png"
            idledown = ImageIO.read(getClass().getResource("/Assets/LurkerEnemy/idledown.png"));
            idleleft = ImageIO.read(getClass().getResource("/Assets/LurkerEnemy/idleleft.png"));
            idleright = ImageIO.read(getClass().getResource("/Assets/LurkerEnemy/idleright.png"));
            idleup = ImageIO.read(getClass().getResource("/Assets/LurkerEnemy/idleup.png"));
            hiding = ImageIO.read(getClass().getResource("/Assets/LurkerEnemy/hiding.png"));
            jumping = ImageIO.read(getClass().getResource("/Assets/LurkerEnemy/jumping.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Player player, ArrayList<Bullet> enemyBullets) {
        // Handle the jump animation if we're currently jumping
        int dx = 0, dy = 0;
        if (isJumping) {
            // Increase jump progress
            jumpProgress += 1.0 / jumpDuration;
            
            // Use easing function for smoother movement (ease-in, then ease-out)
            double easedProgress = jumpProgress < 0.5 ? 
                2 * jumpProgress * jumpProgress : // Ease in (first half)
                1 - Math.pow(-2 * jumpProgress + 2, 2) / 2; // Ease out (second half)
            
            // Calculate current position based on progress
            dx = (int)((targetX - startX) * easedProgress);
            dy = (int)((targetY - startY) * easedProgress);
            x = startX + dx;
            y = startY + dy;

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

        this.idling = false; // Normal enemies are not idling
        if(dx > 0) {
            direction = "right";
        } else if(dx < 0) {
            direction = "left";
        }
        if(dy > 0 && dx < 1 && dx > -1) {
            direction = "down";
        } else if(dy < 0) {
            direction = "up";
        }
        if(dx+dy==0) {
            idling = true; // If no movement, set idling to true
        }
        this.spritecounter++; // delay buat ganti jenis sprite
        if (this.spritecounter > 12){ // di panggil 5x tiap jalan program (60/12)
            if (this.spritenum==1){
                this.spritenum=2;
            }else if (this.spritenum==2){
                this.spritenum=1;
            }
            this.spritecounter=0; // kalau sudah ganti varian set counter ke 0
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
        if(isJumping){
            // Draw the jump animation with a pulsing effect
            g.drawImage(jumping, ex - offsetX, ey - offsetY, currentSize*2, currentSize*2, null);
        } else {
            // Draw the lurker as a filled diamond shape
            // g.fillPolygon(xPoints, yPoints, 4);
            g.drawImage(hiding, ex - offsetX, ey - offsetY, currentSize*2, currentSize*2, null);
        }
        
        // Add a visual indicator when about to jump
        if (!isHiding && !isJumping) {
            g.setColor(new Color(255, 255, 100, 150)); // Semi-transparent yellow
            g.drawOval(ex - 5, ey - 5, currentSize + 10, currentSize + 10);
        }
    }
}
