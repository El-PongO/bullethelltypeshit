import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Player {
    private int x, y;
    private int size = 10;
    private int speed = 5; // speed player
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

    public void move(int dx, int dy) {
        int currentSpeed = isDashing ? dashspeed : speed;
        x += dx * currentSpeed;
        y += dy * currentSpeed;
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
    
    public boolean isInvincible() {
        return isInvincible; // Return the invincibility status
    }

    public int getHitboxSize() { // ini buat hitbox player
        return size;
    }

    public int getCurrentDashCharges() {
        return currentDashCharges;
    }

    public int getMaxDashCharges() {
        return maxDashCharges;
    }

    public void updatePosition(int mouseX, int mouseY) {
        this.x = mouseX;  // gerakan buat sekarang itu pake mouse, mungkin lebih gampang dari pada WASD atau arrow key
        this.y = mouseY; // tapi ini ya pre-alpha so stfu
    }

    public boolean checkCollision(Bullet bullet) {
        double distance = Math.sqrt(Math.pow(x - bullet.getX(), 2) + Math.pow(y - bullet.getY(), 2)); // ini buat hitbox player
        return distance < (size / 2 + bullet.getHitboxSize() / 2);
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

    public void draw(Graphics2D g) {
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
        
        g.drawImage(bimage, x, y, 40, 40, null);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    
    void shootBullet(int targetX, int targetY, ArrayList<Bullet> playerBullets) {
        Bullet b = new Bullet(x, y, targetX, targetY, 10);
        b.setColor(Color.GREEN);//gae buat warna bullet seng ditembak player hijau
        playerBullets.add(b);
    }
}
