import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Player {
    int x;
    int y;
    static int size = 20;
    static int speed = 4;
    static int bulletSpeed = 3; // Bullet speed
    private int dashspeed = 15; // dash distance
    private boolean isDashing = false;
    private boolean isInvincible = false; // Invincibility flag
    private long dashDuration = 200; // Dash duration in milliseconds
    private long invincibilityDuration = 200; // Invincibility duration (same as dash duration)
    private long dashStartTime = 0; // When the current dash started
    private int maxDashCharges = 2; // Maximum number of dash charges 
    private int currentDashCharges = maxDashCharges; // Current number of dash charges ambil dari (maxDashCharges)
    private int dashChargeCooldown = 5000; // Cooldown time for dash charges in milliseconds
    private long lastChargeTime = 0; // Last time a dash charge was used
    public String direction;
    public boolean idling;
    public BufferedImage idledown, idleleft, idleright, idleup, up1, up2, down1, down2, left1, left2, right1, right2;
    public int spritecounter=0;
    public int spritenum=1;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        getPlayerImage();
        direction="down";
        idling=true;
    }

    public void getPlayerImage(){
        try {
            up1 = ImageIO.read(getClass().getResource("/Assets/Hunter/up1.png"));
            up2 = ImageIO.read(getClass().getResource("/Assets/Hunter/up2.png"));
            down1 = ImageIO.read(getClass().getResource("/Assets/Hunter/down1.png")); // FIXED
            down2 = ImageIO.read(getClass().getResource("/Assets/Hunter/down2.png"));
            left1 = ImageIO.read(getClass().getResource("/Assets/Hunter/left1.png"));
            left2 = ImageIO.read(getClass().getResource("/Assets/Hunter/left2.png"));
            right1 = ImageIO.read(getClass().getResource("/Assets/Hunter/right1.png"));
            right2 = ImageIO.read(getClass().getResource("/Assets/Hunter/right2.png")); // not "right.png"
            idledown = ImageIO.read(getClass().getResource("/Assets/Hunter/idledown.png"));
            idleleft = ImageIO.read(getClass().getResource("/Assets/Hunter/idleleft.png"));
            idleright = ImageIO.read(getClass().getResource("/Assets/Hunter/idleright.png"));
            idleup = ImageIO.read(getClass().getResource("/Assets/Hunter/idleup.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g, int px, int py, int zoom) {
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
        g.drawImage(bimage, px, py, size*zoom, size*zoom, null);
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;//gae movement e player receiver wasd ne
    }
    
    public Bullet shoot(int targetX, int targetY) {
        double angle = Math.atan2(targetY - (y + Player.getSize()/2), 
                                targetX - (x + Player.getSize()/2));
        int dx = (int)(Math.cos(angle) * 3) * bulletSpeed;
        int dy = (int)(Math.sin(angle) * 3) * bulletSpeed;
        return new Bullet(x + Player.getSize()/2, y + Player.getSize()/2, dx, dy);
    }

    public void move(boolean upPressed,boolean downPressed, boolean leftPressed, boolean rightPressed,int[][] grid, int tileSize){       
        if (upPressed || downPressed || leftPressed || rightPressed){
            this.idling=false; // cek player kalau jalan berati tidak idle
            int dx = 0, dy = 0;
            if (upPressed) {
                dy -= speed;
                this.direction="up";
            }
            if (downPressed){
                dy += speed;
                this.direction="down";
            } 
            if (leftPressed){
                dx -= speed;
                this.direction="left";
            } 
            if (rightPressed){
                dx += speed;
                this.direction="right";
            } 
            int currentSpeed = isDashing ? dashspeed/3 : 1;
            int newX = x + dx*currentSpeed, newY = y + dy*currentSpeed;
            int gridX = (newX + size / 2) / tileSize;
            int gridY = (newY + size / 2) / tileSize;
            if (gridY >= 0 && gridY < grid.length && gridX >= 0 && gridX < grid[0].length && grid[gridY][gridX] == 0) {
            x = newX;
            y = newY;
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
            this.idling=true;
            this.spritenum=1; // set sprite ke 1
        }
    }

    public void updateDash() {
        long currentTime = System.currentTimeMillis();

        // End dash after the duration
        if (isDashing && currentTime - dashStartTime >= dashDuration) {
            isDashing = false;
        }

        // End invincibility after the duration
        if (isInvincible && currentTime - dashStartTime >= invincibilityDuration) {
            isInvincible = false;
        }

        // Recharge dash charges
        if (currentDashCharges < maxDashCharges && currentTime - lastChargeTime >= dashChargeCooldown) {
            currentDashCharges++;
            lastChargeTime = currentTime; // Reset recharge timer
        }
    }
    public void dash() {
        long currentTime = System.currentTimeMillis();
        if (currentDashCharges > 0 && !isDashing) {
            isDashing = true;
            isInvincible = true; // Set invincibility when dashing
            dashStartTime = currentTime;
            currentDashCharges--; // Consume one dash charge
        }
    }

    public boolean isInvincible() {
        return isInvincible; // Return the invincibility status
    }
    
    public int getCurrentDashCharges() {
        return currentDashCharges;
    }

    public int getMaxDashCharges() {
        return maxDashCharges;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public static int getSize() { return size; }
    public static int getSpeed() { return speed; }
}
