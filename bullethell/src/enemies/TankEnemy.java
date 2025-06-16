package enemies;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import players.Player;
import weapons.Bullet;

public class TankEnemy extends Enemy {
    private boolean isMoving;
    private int pauseCounter;
    private int moveTimer;
    public BufferedImage idledown, idleleft, idleright, idleup, up1, up2, down1, down2, left1, left2, right1, right2;
    public String direction;
    public boolean idling;
    public int spritecounter=0;
    public int spritenum=1;
      public TankEnemy(int x, int y) {
        super(x, y);
        // Tank enemies have quick shooting but move slowly
        this.shootDelay = 1300; // 1.3 seconds between shots
        this.health = 200; // More health than basic enemies
        this.maxHealth = 200;
        this.bulletSpeed = 2; // Slower but more frequent bullets
        this.size = 30; // Larger size
        
        // Initialize movement variables
        isMoving = true;
        pauseCounter = 0;
        moveTimer = 0;
        getEnemyImage();
        direction="down";
        idling=true;
    }

    public void getEnemyImage(){
        try {
            up1 = ImageIO.read(getClass().getResource("/Assets/TankEnemy/up1.png"));
            up2 = ImageIO.read(getClass().getResource("/Assets/TankEnemy/up2.png"));
            down1 = ImageIO.read(getClass().getResource("/Assets/TankEnemy/down1.png")); // FIXED
            down2 = ImageIO.read(getClass().getResource("/Assets/TankEnemy/down2.png"));
            left1 = ImageIO.read(getClass().getResource("/Assets/TankEnemy/left1.png"));
            left2 = ImageIO.read(getClass().getResource("/Assets/TankEnemy/left2.png"));
            right1 = ImageIO.read(getClass().getResource("/Assets/TankEnemy/right1.png"));
            right2 = ImageIO.read(getClass().getResource("/Assets/TankEnemy/right2.png")); // not "right.png"
            idledown = ImageIO.read(getClass().getResource("/Assets/TankEnemy/idledown.png"));
            idleleft = ImageIO.read(getClass().getResource("/Assets/TankEnemy/idleleft.png"));
            idleright = ImageIO.read(getClass().getResource("/Assets/TankEnemy/idleright.png"));
            idleup = ImageIO.read(getClass().getResource("/Assets/TankEnemy/idleup.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void update(Player player, ArrayList<Bullet> enemyBullets, int[][] collisionMap, int tileSize) {
        // Tank enemy moves slowly with periodic pauses
        double dx = 0, dy = 0;
        if (isMoving) {
            double angle = Math.atan2(player.getY() - y, player.getX() - x);
            dx = Math.cos(angle) * (speed - 1);
            dy = Math.sin(angle) * (speed - 1);
            moveWithCollision(dx, dy, collisionMap, tileSize);
            moveTimer--;
            if (moveTimer <= 0) {
                isMoving = false;
                pauseCounter = 30 + rand.nextInt(30);
            }
        } else {
            pauseCounter--;
            if (pauseCounter <= 0) {
                isMoving = true;
                moveTimer = 20 + rand.nextInt(30);
            }
        }
        
        // Sprite animation logic
        if (isMoving) {
            this.idling = false; // Tank enemies are not idling
            if(dx > 0) {
                direction = "right";
            } 
            else if(dx < 0) {
                direction = "left";
            }
            if(dy > 0 && dx < 1 && dx > -1) {
                direction = "down";
            } 
            else if(dy < 0) {
                direction = "up";
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
        }else{
            this.idling = true; // Tank enemies are idling when paused
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
        // g.setColor(new Color(50, 50, 150)); // Dark blue color for tank enemy
        // g.fillRect(ex, ey, size, size); // Tank is square-shaped
        // Draw tank "turret"
        // g.setColor(new Color(100, 100, 200));
        // g.fillOval(ex + size/4, ey + size/4, size/2, size/2);
    }
}
