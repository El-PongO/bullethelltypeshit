package enemies;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import players.Player;
import weapons.Bullet;

public class TankBoss extends Enemy {
    private boolean isMoving;
    private boolean isCharging;
    private int pauseCounter;
    private int moveTimer;
    private int chargeTimer;
    private int chargeCooldown;
    private double chargeAngle;
    private int chargeSpeed;
    private int normalSpeed;
      // Sprite variables
    public BufferedImage[] walkSprites = new BufferedImage[6]; // 6 walk sprites
    public BufferedImage[] idleSprites = new BufferedImage[6]; // 6 idle sprites
    public BufferedImage[] chargeAttackRightSprites = new BufferedImage[7]; // 7 charge attack right sprites
    public BufferedImage[] chargeAttackLeftSprites = new BufferedImage[7]; // 7 charge attack left sprites
    public String direction;
    public boolean idling;
    public boolean isAttacking;
    public int spritecounter = 0;
    public int spritenum = 0;
    public int attackFrame = 0;
      
    public TankBoss(int x, int y) {
        super(x, y);        // Tank boss has more health and is larger
        this.shootDelay = 2000; // 2 seconds between shots (if we implement shooting)
        this.health = 1000; // Increased health from 600 to 1000
        this.maxHealth = 1000;        this.bulletSpeed = 3;
        this.size = 150; // Much larger size (3x the original size of 50)
        
        // Initialize movement variables        
        isMoving = true;
        isCharging = false;
        pauseCounter = 0;
        moveTimer = 0;
        chargeTimer = 0;
        chargeCooldown = 180; // 3 seconds cooldown between charges        
        chargeSpeed = 12; // Increased speed during charge (was 8)
        normalSpeed = 1; // Normal movement speed (slower than regular enemies)
        
        // Load sprites
        getEnemyImage();
        direction = "down";
        idling = true;
    }    public void getEnemyImage() {
        try {
            // Load walk sprites (6 frames)
            for (int i = 0; i < 6; i++) {
                String filename = String.format("/Assets/SamuraiTankBoss/SamuraiTankBossWalk%03d.png", i);
                walkSprites[i] = ImageIO.read(getClass().getResource(filename));
            }
            
            // Load idle sprites (6 frames)
            for (int i = 0; i < 6; i++) {
                String filename = String.format("/Assets/SamuraiTankBoss/SamuraiIdle%03d.png", i);
                idleSprites[i] = ImageIO.read(getClass().getResource(filename));
            }
            
            // Load charge attack right sprites (7 frames, 0-6)
            for (int i = 0; i < 7; i++) {
                String filename = String.format("/Assets/SamuraiTankBoss/SamuraiChargeAttack%03d.png", i);
                chargeAttackRightSprites[i] = ImageIO.read(getClass().getResource(filename));
            }
            
            // Load charge attack left sprites (7 frames, 0-6)
            for (int i = 0; i < 7; i++) {
                String filename = String.format("/Assets/SamuraiTankBoss/SamuraiChargeAttackLeft%03d.png", i);
                chargeAttackLeftSprites[i] = ImageIO.read(getClass().getResource(filename));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }      @Override
    public void update(Player player, ArrayList<Bullet> enemyBullets) {
        int dx = 0, dy = 0;
        
        // Decrement charge cooldown if it's active
        if (chargeCooldown > 0) {
            chargeCooldown--;
        }
          // Handle charge state if active
        if (isCharging) {
            // When charging, move in a straight line at high speed
            // Use exact floating point values to avoid rounding to zero
            double exactDx = Math.cos(chargeAngle) * chargeSpeed;
            double exactDy = Math.sin(chargeAngle) * chargeSpeed;
            dx = (int) Math.round(exactDx); // Round instead of cast to avoid losing small movements
            dy = (int) Math.round(exactDy);
            
            // Ensure the boss is always moving during charge, even if the angle would make dx or dy zero
            if (dx == 0 && dy == 0) {
                // If both rounded to zero, use the sign of the exact values
                dx = exactDx > 0 ? 1 : (exactDx < 0 ? -1 : 0);
                dy = exactDy > 0 ? 1 : (exactDy < 0 ? -1 : 0);
            }
            
            // Update direction based on movement
            updateDirection(dx, dy);
            
            // Animation during charge
            this.idling = false;
            
            // Manage attack frame progression
            if (attackFrame < 2) {
                // First phase - preparing to charge (frames 0-2)
                if (spritecounter % 10 == 0) {
                    attackFrame++;
                }
            }
            else if (chargeTimer < 30) {
                // Final phase - attacking (frames 3-6)
                if (spritecounter % 8 == 0 && attackFrame < 6) {
                    attackFrame++;
                }
            }
            
            updateSpriteCounter();
            
            // Decrease charge timer
            chargeTimer--;
            if (chargeTimer <= 0) {
                isCharging = false;
                // After charging, pause for a bit
                isMoving = false;
                pauseCounter = 60; // 1 second pause after charge
                chargeCooldown = 180; // 3 seconds before can charge again
            }
              // Charging in progress
        }
        // Normal movement when not charging
        else if (isMoving) {
            // When moving, head toward the player slowly
            double angle = Math.atan2(player.getY() - y, player.getX() - x);
            dx = (int) Math.signum(Math.cos(angle));
            dy = (int) Math.signum(Math.sin(angle));
            
            // Update direction based on movement
            updateDirection(dx, dy);
            
            // Animation during movement
            this.idling = false;
            updateSpriteCounter();
            
            // Move timer counts down until we pause
            moveTimer--;
            if (moveTimer <= 0) {
                isMoving = false;
                pauseCounter = 60 + rand.nextInt(60); // Pause for 1-2 seconds
            }
            
            // Check if we should initiate a charge (when cooldown is 0)
            if (chargeCooldown <= 0 && rand.nextInt(60) == 0) { // ~1.7% chance per frame when cooldown is ready
                startCharge(player);
            }
        } else {
            // When paused, we're idling
            this.idling = true;
            
            // When paused, count down until we move again
            pauseCounter--;
            if (pauseCounter <= 0) {
                isMoving = true;
                moveTimer = 60 + rand.nextInt(90); // Move for 1-2.5 seconds
                
                // Check if we should charge after the pause
                if (chargeCooldown <= 0 && rand.nextInt(3) == 0) { // 33% chance after pause
                    startCharge(player);
                }
            }
        }
          // Move at appropriate speed based on state
        if (isCharging) {
            // When charging, we've already calculated dx and dy with chargeSpeed applied
            x += dx;
            y += dy;
              // Position updated during charge
        } else if (isMoving) {
            // Normal movement
            x += dx * normalSpeed;
            y += dy * normalSpeed;
        }
        
        // Optionally, the boss could also shoot        // Optionally, the boss could also shoot
        /* Uncomment if you want the boss to shoot as well
        Bullet bullet = tryShoot(player.getX(), player.getY());
        if (bullet != null) {
            enemyBullets.add(bullet);
        }
        */
    }
      private void startCharge(Player player) {
        isCharging = true;
        isMoving = false;
        attackFrame = 0; // Reset the attack animation frame
        
        // Calculate angle to player for charging
        chargeAngle = Math.atan2(player.getY() - y, player.getX() - x);
        
        // Set charge duration based on distance (farther charges last longer)
        double distance = Math.hypot(player.getX() - x, player.getY() - y);
        chargeTimer = 60 + (int)(distance / 15); // Between 1-3 seconds based on distance
        
        // Increase minimum charge time to ensure visible movement
        if (chargeTimer < 60) {
            chargeTimer = 60; // At least 1 second of charging (to allow animation to play)
        }
        
        // Update direction for charge animation using the floating point angle for better precision
        double exactDx = Math.cos(chargeAngle);
        double exactDy = Math.sin(chargeAngle);
        int dx = (int) Math.round(exactDx);
        int dy = (int) Math.round(exactDy);
        updateDirection(dx, dy);
          
        // TankBoss starting charge
    }
    
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
        this.spritecounter++; // delay for sprite animation
        
        // Different animation speed based on state
        int animSpeed = isCharging ? 6 : 12;
        
        if (this.spritecounter > animSpeed) {
            // Cycle through animation frames
            this.spritenum = (this.spritenum + 1) % 6; // 6 frames for walk/idle animations
            this.spritecounter = 0;
        }
    }      @Override
    public void draw(Graphics g, int ex, int ey) {
        BufferedImage bimage = null;
        
        // Choose sprite based on state
        if (isCharging) {
            // When charging, use charge attack animation based on direction
            // If player is on the right, use right charge attack sprites
            // If player is on the left, use left charge attack sprites
            if (chargeAngle > -Math.PI/2 && chargeAngle < Math.PI/2) {
                // Moving right
                int frame = Math.min(attackFrame, chargeAttackRightSprites.length - 1);
                bimage = chargeAttackRightSprites[frame];
            } else {
                // Moving left
                int frame = Math.min(attackFrame, chargeAttackLeftSprites.length - 1);
                bimage = chargeAttackLeftSprites[frame];
            }
        } else if (idling) {
            // Use idle animation
            int frame = spritenum % idleSprites.length;
            bimage = idleSprites[frame];
        } else {
            // Use walking animation (6 frames)
            int frame = spritenum % walkSprites.length;
            bimage = walkSprites[frame];
        }
          // Draw with larger size appropriate for boss while maintaining aspect ratio
        if (bimage != null) {
            int spriteWidth = bimage.getWidth();
            int spriteHeight = bimage.getHeight();
            
            // Calculate scale factor to maintain aspect ratio
            double scale = Math.min((double)(size*2) / spriteWidth, (double)(size*2) / spriteHeight);
            
            // Calculate new dimensions
            int width = (int) (spriteWidth * scale);
            int height = (int) (spriteHeight * scale);
            
            // Center the sprite
            int xOffset = (size*2 - width) / 2;
            int yOffset = (size*2 - height) / 2;
            
            // Draw the sprite with proper scaling
            g.drawImage(bimage, ex + xOffset, ey + yOffset, width, height, null);
        }
        
        // Visual indicator when charging (red glow)
        if (isCharging) {
            g.setColor(new Color(255, 0, 0, 80)); // Semi-transparent red
            g.fillOval(ex - 10, ey - 10, size*2 + 20, size*2 + 20);
        }
        
        // Visual indicator when about to charge (yellow pulse)
        if (!isCharging && chargeCooldown <= 30) {
            g.setColor(new Color(255, 255, 0, 50 + (30 - chargeCooldown) * 5)); // Increasing intensity yellow
            g.fillOval(ex - 5, ey - 5, size*2 + 10, size*2 + 10);
        }
        
        // Draw health bar
        if (health < maxHealth) {
            // Health bar background
            g.setColor(Color.RED);
            g.fillRect(ex, ey - 20, size*2, 10);
            
            // Current health
            g.setColor(Color.GREEN);
            int healthBarWidth = (int)((float)health / maxHealth * size*2);
            g.fillRect(ex, ey - 20, healthBarWidth, 10);
        }
    }
}
