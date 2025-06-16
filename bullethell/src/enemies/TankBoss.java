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
    
    public BufferedImage idledown, idleleft, idleright, idleup, up1, up2, down1, down2, left1, left2, right1, right2;
    public String direction;
    public boolean idling;
    public int spritecounter = 0;
    public int spritenum = 1;
      
    public TankBoss(int x, int y) {
        super(x, y);
        // Tank boss has more health and is larger
        this.shootDelay = 2000; // 2 seconds between shots (if we implement shooting)
        this.health = 600; // 3 times the health of a regular tank
        this.maxHealth = 600;
        this.bulletSpeed = 3;
        this.size = 50; // Much larger size (TankEnemy is 30)
        
        // Initialize movement variables        isMoving = true;
        isCharging = false;
        pauseCounter = 0;
        moveTimer = 0;
        chargeTimer = 0;
        chargeCooldown = 180; // 3 seconds cooldown between charges
        chargeSpeed = 8; // Increased speed during charge (was 5)
        normalSpeed = 1; // Normal movement speed (slower than regular enemies)
        
        // Load sprites
        getEnemyImage();
        direction = "down";
        idling = true;
    }

    public void getEnemyImage() {
        try {
            // Currently using TankEnemy sprites - would be better to have dedicated boss sprites
            up1 = ImageIO.read(getClass().getResource("/Assets/TankEnemy/up1.png"));
            up2 = ImageIO.read(getClass().getResource("/Assets/TankEnemy/up2.png"));
            down1 = ImageIO.read(getClass().getResource("/Assets/TankEnemy/down1.png"));
            down2 = ImageIO.read(getClass().getResource("/Assets/TankEnemy/down2.png"));
            left1 = ImageIO.read(getClass().getResource("/Assets/TankEnemy/left1.png"));
            left2 = ImageIO.read(getClass().getResource("/Assets/TankEnemy/left2.png"));
            right1 = ImageIO.read(getClass().getResource("/Assets/TankEnemy/right1.png"));
            right2 = ImageIO.read(getClass().getResource("/Assets/TankEnemy/right2.png"));
            idledown = ImageIO.read(getClass().getResource("/Assets/TankEnemy/idledown.png"));
            idleleft = ImageIO.read(getClass().getResource("/Assets/TankEnemy/idleleft.png"));
            idleright = ImageIO.read(getClass().getResource("/Assets/TankEnemy/idleright.png"));
            idleup = ImageIO.read(getClass().getResource("/Assets/TankEnemy/idleup.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
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
            
            // Debug output to verify charge is working
            System.out.println("Charging: dx=" + dx + ", dy=" + dy);
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
            
            // Add extra debug to confirm position change
            System.out.println("TankBoss position updated to: " + x + ", " + y);
        } else if (isMoving) {
            // Normal movement
            x += dx * normalSpeed;
            y += dy * normalSpeed;
        }
        
        // Optionally, the boss could also shoot
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
        
        // Calculate angle to player for charging
        chargeAngle = Math.atan2(player.getY() - y, player.getX() - x);
        
        // Set charge duration based on distance (farther charges last longer)
        double distance = Math.hypot(player.getX() - x, player.getY() - y);
        chargeTimer = 30 + (int)(distance / 15); // Between 0.5-2 seconds based on distance
        
        // Increase minimum charge time to ensure visible movement
        if (chargeTimer < 30) {
            chargeTimer = 30; // At least 0.5 seconds of charging
        }
        
        // Update direction for charge animation using the floating point angle for better precision
        double exactDx = Math.cos(chargeAngle);
        double exactDy = Math.sin(chargeAngle);
        int dx = (int) Math.round(exactDx);
        int dy = (int) Math.round(exactDy);
        updateDirection(dx, dy);
        
        // Debug output to confirm charge initiation
        System.out.println("TankBoss starting charge toward player: angle=" + chargeAngle + ", timer=" + chargeTimer);
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
        if (this.spritecounter > (isCharging ? 6 : 12)) { // Faster animation when charging
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
        
        // Choose sprite based on state and direction
        if (idling){
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
                    if (spritenum == 1){
                        bimage = up1;
                    } else if (spritenum == 2){
                        bimage = up2;
                    }
                    break;
                case "down":
                    if (spritenum == 1){
                        bimage = down1;
                    } else if (spritenum == 2){
                        bimage = down2;
                    }
                    break;
                case "left":
                    if (spritenum == 1){
                        bimage = left1;
                    } else if (spritenum == 2){
                        bimage = left2;
                    }
                    break;
                case "right":
                    if (spritenum == 1){
                        bimage = right1;
                    } else if (spritenum == 2){
                        bimage = right2;
                    }
                    break;
                default: break;
            }
        }
        
        // Draw with larger size appropriate for boss
        g.drawImage(bimage, ex, ey, size*2, size*2, null);
        
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
