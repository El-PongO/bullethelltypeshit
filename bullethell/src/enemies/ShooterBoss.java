package enemies;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import players.Player;
import weapons.Bullet;

public class ShooterBoss extends Enemy {
    // Movement variables
    private boolean isJumping;
    private double jumpProgress;
    private int jumpDuration;
    private int startX, startY;
    private int targetX, targetY;
    private int jumpCooldown;
    private int jumpCooldownMax = 60; // 1 second cooldown between jumps
    
    // Shooting variables
    private boolean isShootingBurst;
    private int burstCounter;    
    private int burstInterval = 15; // Frames between shots in a burst
    private int burstTimer;
    private int shotsPerBurst = 5; // Increased from 3 to 5 bullets per burst
    
    // Sprite and animation variables
    public BufferedImage[] walkSprites = new BufferedImage[12]; // 12 walk sprites
    public BufferedImage[] idleSprites = new BufferedImage[6]; // 6 idle sprites
    public BufferedImage[] jumpSprites = new BufferedImage[8]; // 8 jump sprites
    public String direction;
    public boolean idling;
    public int spritecounter = 0;
    public int spritenum = 0;
    public int jumpFrame = 0;
    
    public ShooterBoss(int x, int y) {
        super(x, y);        
        // Shooter Boss properties
        this.shootDelay = 3000; // 3 seconds between burst sequences
        this.bulletSpeed = 4;   // Faster bullets than regular enemies
        this.health = 750;      // Increased health from 400 to 750
        this.maxHealth = 750;
        this.size = 35;         // Larger than regular shooter but smaller than tank boss
        
        // Initialize state
        isJumping = false;
        isShootingBurst = false;
        jumpProgress = 0.0;
        jumpCooldown = 0;
        burstCounter = 0;
        burstTimer = 0;
        
        // Load sprites
        getEnemyImage();
        direction = "down";
        idling = true;
    }    public void getEnemyImage() {
        try {
            // Load walk sprites (12 frames)
            for (int i = 0; i < 12; i++) {
                String filename = String.format("/Assets/BambooBossShooter/BambooBossWalk%03d.png", i);
                walkSprites[i] = ImageIO.read(getClass().getResource(filename));
            }
            
            // Load idle sprites (6 frames)
            for (int i = 0; i < 6; i++) {
                String filename = String.format("/Assets/BambooBossShooter/BambooBossIdle%03d.png", i);
                idleSprites[i] = ImageIO.read(getClass().getResource(filename));
            }
            
            // Load jump sprites (8 frames)
            try {
                for (int i = 0; i < 8; i++) {
                    String filename = String.format("/Assets/BambooBossShooter/ChargeJump%03d.png", i);
                    jumpSprites[i] = ImageIO.read(getClass().getResource(filename));
                }
            } catch (Exception e) {
                // If not available, use idle sprite instead
                for (int i = 0; i < 8; i++) {
                    jumpSprites[i] = idleSprites[0];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void update(Player player, ArrayList<Bullet> enemyBullets) {
        // Decrements cooldown if active
        if (jumpCooldown > 0) {
            jumpCooldown--;
        }
        
        // Handle jump animation if we're currently jumping        if (isJumping) {
            // Increase jump progress
            jumpProgress += 1.0 / jumpDuration;
            
            // Use easing function for smoother movement
            double easedProgress = jumpProgress < 0.5 ? 
                2 * jumpProgress * jumpProgress : // Ease in (first half)
                1 - Math.pow(-2 * jumpProgress + 2, 2) / 2; // Ease out (second half)
            
            // Calculate current position based on progress
            int dx = (int)((targetX - startX) * easedProgress);
            int dy = (int)((targetY - startY) * easedProgress);
            x = startX + dx;
            y = startY + dy;
            
            // Update direction based on movement
            updateDirection(targetX - startX, targetY - startY);
            
            // Progress jump animation based on jump phase
            if (jumpProgress < 0.3 && jumpFrame < 3) {
                // Beginning of jump - prep frames
                if (spritecounter % 8 == 0) {
                    jumpFrame++;
                }
            } else if (jumpProgress > 0.7 && jumpFrame < 7) {
                // End of jump - landing frames
                if (spritecounter % 5 == 0) {
                    jumpFrame++;
                }
            }

            // Check if jump is complete
            if (jumpProgress >= 1.0) {
                isJumping = false;
                jumpProgress = 0.0;
                jumpCooldown = jumpCooldownMax; // Cooldown after landing
                
                // Start a burst of shots after landing
                if (!isShootingBurst && rand.nextInt(2) == 0) { // 50% chance to fire after jump
                    startBurst();
                }
            }
        } 
        // Burst shooting behavior
        else if (isShootingBurst) {
            // Boss stays still while shooting
            this.idling = true;
            
            // Make sure boss faces the player while shooting
            double angle = Math.atan2(player.getY() - y, player.getX() - x);
            updateDirection((int) Math.cos(angle), (int) Math.sin(angle));
            
            burstTimer--;
            if (burstTimer <= 0) {
                // Time to fire the next shot in the burst
                double angle2 = Math.atan2(player.getY() - y, player.getX() - x);
                double dx = Math.cos(angle2) * bulletSpeed;
                double dy = Math.sin(angle2) * bulletSpeed;
                
                // Create bullet
                Bullet bullet = new Bullet(x + size/2, y + size/2, (int)dx, (int)dy, null);
                enemyBullets.add(bullet);
                  // Update burst count
                burstCounter++;
                burstTimer = burstInterval;
                
                // Check if burst is complete
                if (burstCounter >= shotsPerBurst) {
                    isShootingBurst = false;
                    lastShotTime = System.currentTimeMillis();
                    
                    // Only jump after completing all 3 shots
                    if (!isJumping && jumpCooldown <= 0) {
                        startJump(player, true); // Jump away from player after finishing the burst
                    }
                }
            }
        }
        // Normal movement when not jumping or shooting
        else {
            // Check if we should start a burst
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShotTime > shootDelay && rand.nextInt(60) == 0) { // ~1.7% chance when ready
                startBurst();
            }
            
            // If we're close to the player, try to jump away
            double distance = Math.hypot(player.getX() - x, player.getY() - y);
            if (distance < 150 && !isJumping && jumpCooldown <= 0) {
                startJump(player, true); // Jump away when too close
            }
            
            // If no special action and not too close, just stand and wait
            // No need to move toward optimal range anymore
        }
        
        // Update sprite counter for animations
        updateSpriteCounter();
    }
      private void startJump(Player player, boolean jumpAway) {
        isJumping = true;
        jumpProgress = 0.0;
        jumpFrame = 0; // Reset jump animation frame
        
        // Store starting position
        startX = x;
        startY = y;
        
        double angle;
        if (jumpAway) {
            // Jump away from player
            angle = Math.atan2(y - player.getY(), x - player.getX());
        } else {
            // Jump toward player
            angle = Math.atan2(player.getY() - y, player.getX() - x);
        }
        
        // Calculate distance to jump
        double distance = Math.hypot(player.getX() - x, player.getY() - y);
        double jumpDistance;
        
        if (jumpAway) {
            jumpDistance = Math.min(100, distance * 0.5); // Jump back up to 100 pixels
        } else {
            // Jump to optimal shooting range (not too close)
            jumpDistance = Math.max(0, distance - 150); // Try to maintain ~150px distance
        }
        
        // Calculate target position
        targetX = (int)(x + Math.cos(angle) * jumpDistance);
        targetY = (int)(y + Math.sin(angle) * jumpDistance);
        
        // Jump duration proportional to distance
        jumpDuration = 15 + (int)(jumpDistance / 10);
    }
    
    private void startBurst() {
        isShootingBurst = true;
        burstCounter = 0;
        burstTimer = 10; // Small initial delay before first shot
    }
      // We've removed the moveToOptimalRange method since the shooter boss
    // now jumps rather than walks to maintain distance
    
    private void updateDirection(int dx, int dy) {
        if (dx > 0) {
            direction = "right";
        } else if (dx < 0) {
            direction = "left";
        }
        
        if (dy > 0 && dx < 1 && dx > -1) {
            direction = "down";
        } else if (dy < 0) {
            direction = "up";
        }
    }
      private void updateSpriteCounter() {
        this.spritecounter++;
        
        int animSpeed = isJumping ? 5 : 12; // Faster animation when jumping
        
        if (this.spritecounter > animSpeed) {
            if (isJumping) {
                // Advance jump frame animation
                jumpFrame = (jumpFrame + 1) % jumpSprites.length;
            } else {
                // Advance walk or idle animation
                this.spritenum = (this.spritenum + 1) % (idling ? idleSprites.length : walkSprites.length);
            }
            this.spritecounter = 0;
        }
    }
      @Override
    public void draw(Graphics g, int ex, int ey) {
        BufferedImage bimage = null;
        
        // Choose appropriate sprite based on state
        if (isJumping) {
            // Use jumping animation when jumping
            int frame = Math.min(jumpFrame, jumpSprites.length - 1);
            bimage = jumpSprites[frame];
        } else if (idling) {
            // Use idle animation
            int frame = spritenum % idleSprites.length;
            bimage = idleSprites[frame];
        } else {
            // Use walking animation
            int frame = spritenum % walkSprites.length;
            bimage = walkSprites[frame];
        }
        
        // Draw with appropriate size
        g.drawImage(bimage, ex, ey, size*2, size*2, null);
        
        // Visual effects based on state
        if (isJumping) {
            // Add jump effect/shadow
            g.setColor(new Color(0, 0, 0, 80)); // Semi-transparent shadow
            g.fillOval(ex + size/2, ey + (int)(size*1.5), size, size/2);
        }
        
        if (isShootingBurst) {
            // Add visual charging effect
            int pulseSize = 5 + (burstInterval - burstTimer)/2;
            g.setColor(new Color(255, 100, 0, 150 - pulseSize*5));
            g.fillOval(ex - pulseSize, ey - pulseSize, size*2 + pulseSize*2, size*2 + pulseSize*2);
        }
        
        // Draw "gun" with different position/look for boss
        g.setColor(new Color(180, 180, 180));
        int gunLength = size/2 + 10;
        int gunWidth = 6;
        
        // Position gun based on direction
        switch (direction) {
            case "right":
                g.fillRect(ex + size, ey + size - gunWidth/2, gunLength, gunWidth);
                break;
            case "left":
                g.fillRect(ex + size - gunLength, ey + size - gunWidth/2, gunLength, gunWidth);
                break;
            case "up":
                g.fillRect(ex + size - gunWidth/2, ey + size - gunLength, gunWidth, gunLength);
                break;
            case "down":
                g.fillRect(ex + size - gunWidth/2, ey + size, gunWidth, gunLength);
                break;
        }
        
        // Draw health bar
        if (health < maxHealth) {
            // Health bar background
            g.setColor(Color.RED);
            g.fillRect(ex, ey - 15, size*2, 8);
            
            // Current health
            g.setColor(Color.GREEN);
            int healthBarWidth = (int)((float)health / maxHealth * size*2);
            g.fillRect(ex, ey - 15, healthBarWidth, 8);
        }
    }
}
