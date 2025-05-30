import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class NormalEnemy extends Enemy {
    public BufferedImage idledown, idleleft, idleright, idleup, up1, up2, down1, down2, left1, left2, right1, right2;
    public String direction;
    public boolean idling;
    public int spritecounter=0;
    public int spritenum=1;


    public NormalEnemy(int x, int y) {
        super(x, y);
        // Normal enemies don't shoot, they just follow the player
        this.size = 20; // Standard size
        getEnemyImage();
        direction="down";
        idling=true;
    }

    public void getEnemyImage(){
        try {
            up1 = ImageIO.read(getClass().getResource("/Assets/NormalEnemy/up1.png"));
            up2 = ImageIO.read(getClass().getResource("/Assets/NormalEnemy/up2.png"));
            down1 = ImageIO.read(getClass().getResource("/Assets/NormalEnemy/down1.png")); // FIXED
            down2 = ImageIO.read(getClass().getResource("/Assets/NormalEnemy/down2.png"));
            left1 = ImageIO.read(getClass().getResource("/Assets/NormalEnemy/left1.png"));
            left2 = ImageIO.read(getClass().getResource("/Assets/NormalEnemy/left2.png"));
            right1 = ImageIO.read(getClass().getResource("/Assets/NormalEnemy/right1.png"));
            right2 = ImageIO.read(getClass().getResource("/Assets/NormalEnemy/right2.png")); // not "right.png"
            idledown = ImageIO.read(getClass().getResource("/Assets/NormalEnemy/idledown.png"));
            idleleft = ImageIO.read(getClass().getResource("/Assets/NormalEnemy/idleleft.png"));
            idleright = ImageIO.read(getClass().getResource("/Assets/NormalEnemy/idleright.png"));
            idleup = ImageIO.read(getClass().getResource("/Assets/NormalEnemy/idleup.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void update(Player player, ArrayList<Bullet> enemyBullets) {
        // Simple movement: always go toward the player
        double angle = Math.atan2(player.getY() - y, player.getX() - x);
        this.idling = false; // Normal enemies are not idling
        double dx =  Math.cos(angle) * speed;
        double dy =  Math.sin(angle) * speed;
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
        // Move at normal speed
        x += dx;
        y += dy;
        
        // Normal enemies don't shoot
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
        // g.setColor(new Color(100, 100, 200)); // Blue color for normal enemy
        // g.fillOval(ex, ey, size, size);
    }
}
