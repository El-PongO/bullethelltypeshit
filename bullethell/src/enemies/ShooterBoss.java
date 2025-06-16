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
    private int shotsPerBurst = 3;
    
    // Sprite and animation variables
    public BufferedImage idledown, idleleft, idleright, idleup, up1, up2, down1, down2, left1, left2, right1, right2, jumping;
    public String direction;
    public boolean idling;
    public int spritecounter = 0;
    public int spritenum = 1;
    
    public ShooterBoss(int x, int y) {
        super(x, y);
        // Shooter Boss properties
        this.shootDelay = 3000; // 3 seconds between burst sequences
        this.bulletSpeed = 4;   // Faster bullets than regular enemies
        this.health = 400;      // More health than regular enemies
        this.maxHealth = 400;
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
    }

    public void getEnemyImage() {
        try {
            // Using ShooterEnemy sprites - would be better to have dedicated boss sprites
            up1 = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/up1.png"));
            up2 = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/up2.png"));
            down1 = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/down1.png"));
            down2 = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/down2.png"));
            left1 = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/left1.png"));
            left2 = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/left2.png"));
            right1 = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/right1.png"));
            right2 = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/right2.png"));
            idledown = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/idledown.png"));
            idleleft = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/idleleft.png"));
            idleright = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/idleright.png"));
            idleup = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/idleup.png"));
            
            // Try to use lurker's jumping image for jump animation
            try {
                jumping = ImageIO.read(getClass().getResource("/Assets/LurkerEnemy/jumping.png"));
            } catch (Exception e) {
                // If not available, we'll use an idle sprite instead
                jumping = idleup;
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
        
        // Handle jump animation if we're currently jumping
        if (isJumping) {
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
        if (this.spritecounter > 12) {
            if (this.spritenum == 1) {
                this.spritenum = 2;
            } else if (this.spritenum == 2) {
                this.spritenum = 1;
            }
            this.spritecounter = 0;
        }
    }
    
    @Override
    public void draw(Graphics g, int ex, int ey) {
        BufferedImage bimage = null;
        
        // Choose appropriate sprite based on state and direction
        if (isJumping) {
            // Use jumping sprite when jumping
            bimage = jumping;
        } else if (idling) {
            switch (direction) {
                case "up": bimage = idleup; break;
                case "down": bimage = idledown; break;
                case "left": bimage = idleleft; break;
                case "right": bimage = idleright; break;
                default: break;
            }
        } else {
            switch (direction) {
                case "up":
                    if (spritenum == 1) {
                        bimage = up1;
                    } else if (spritenum == 2) {
                        bimage = up2;
                    }
                    break;
                case "down":
                    if (spritenum == 1) {
                        bimage = down1;
                    } else if (spritenum == 2) {
                        bimage = down2;
                    }
                    break;
                case "left":
                    if (spritenum == 1) {
                        bimage = left1;
                    } else if (spritenum == 2) {
                        bimage = left2;
                    }
                    break;
                case "right":
                    if (spritenum == 1) {
                        bimage = right1;
                    } else if (spritenum == 2) {
                        bimage = right2;
                    }
                    break;
                default: break;
            }
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
