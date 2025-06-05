import java.awt.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import players.Player;
import players.Bullet;

public class ShooterEnemy extends Enemy {
    private int movementCounter = 0;  // Counter for controlling movement duration
    private int movementDirection = 0; // 0 = none, -1 = left, 1 = right
    private int movementDuration = 0; // How long to move in a direction
    private int pauseDuration = 0;    // How long to pause after movement
    public BufferedImage idledown, idleleft, idleright, idleup, up1, up2, down1, down2, left1, left2, right1, right2;
    public String direction;
    public boolean idling;
    public int spritecounter=0;
    public int spritenum=1;
    
    public ShooterEnemy(int x, int y) {
        super(x, y);
        // Specific properties for shooter enemy
        this.shootDelay = 2000; // 2 second delay between shots
        this.bulletSpeed = 3;
        getEnemyImage();
        direction="down";
        idling=true;
    }
    
    public void getEnemyImage(){
        try {
            up1 = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/up1.png"));
            up2 = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/up2.png"));
            down1 = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/down1.png")); // FIXED
            down2 = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/down2.png"));
            left1 = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/left1.png"));
            left2 = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/left2.png"));
            right1 = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/right1.png"));
            right2 = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/right2.png")); // not "right.png"
            idledown = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/idledown.png"));
            idleleft = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/idleleft.png"));
            idleright = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/idleright.png"));
            idleup = ImageIO.read(getClass().getResource("/Assets/ShooterEnemy/idleup.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Player player, ArrayList<Bullet> enemyBullets) {
        int behavior = rand.nextInt(2); // 0, 1, or 2 
        int dx = 0, dy = 0;

        // Behavior 1: move toward or away from player based on distance
        double distance = Math.hypot(player.getX() - x, player.getY() - y);
        if (behavior == 0) {
            // Changed distance threshold to 200 (was 50)
            if (distance > 150) {
                dx = (player.getX() > x) ? 1 : -1;
                dy = (player.getY() > y) ? 1 : -1;
            } else {
                dx = (player.getX() < x) ? 1 : -1;
                dy = (player.getY() < y) ? 1 : -1;
            }
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
        BufferedImage bimage = null;
        if (idling){
            switch (direction) {
                case "up": bimage=idleup; break;
                case "down": bimage=idledown; break;
                case "left": bimage=idleleft; break;
                case "right": bimage=idleright; break;
                default: break;
            }
        }else{
            switch (direction) {
                case "up":
                    if (spritenum==1){
                        bimage=up1;
                    }else if (spritenum==2){
                        bimage=up2;
                    }
                    break;
                case "down":
                    if (spritenum==1){
                        bimage=down1;
                    } else if (spritenum==2){
                        bimage=down2;
                    }
                    break;
                case "left":
                    if (spritenum==1){
                        bimage=left1;
                    } else if (spritenum==2){
                        bimage=left2;
                    }
                    break;
                case "right":
                    if (spritenum==1){
                        bimage=right1;
                    } else if (spritenum==2){
                        bimage=right2;
                    }
                    break;
                default: break;
            }
        }
        
        // Draw relative to camera position
        g.drawImage(bimage, ex, ey, size*2, size*2, null);
        // // Use rectangle for shooter enemies
        // g.setColor(new Color(102, 51, 153)); // Purple color
        // g.fillRect(ex, ey, size, size);
        
        // Add a "gun" indicator
        g.setColor(new Color(150, 150, 150)); // Gray for the gun
        g.fillRect(ex + size/2 - 2, ey + size/2 - 2, size/2, 4);
    }
}
