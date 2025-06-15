import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import players.Player;
import weapons.Bullet;

public class BomberEnemy extends Enemy {
    private double moveSpeed = 3.5; // Faster than normal enemies
    public BufferedImage up1, up2, up3, up4, down1, down2, down3, down4, left1, left2, left3, left4, right1, right2, right3, right4;
    public String direction;
    public int spritecounter=0;
    public int spritenum=1;

    public BomberEnemy(int x, int y) {
        super(x, y);
        // Bombers don't shoot, they just chase the player
        this.size = 18; // Medium size
        getEnemyImage();
        direction="down";
    }
    
    public void getEnemyImage(){
        try {
            up1 = ImageIO.read(getClass().getResource("/Assets/BomberEnemy/up1.png"));
            up2 = ImageIO.read(getClass().getResource("/Assets/BomberEnemy/up2.png"));
            up3 = ImageIO.read(getClass().getResource("/Assets/BomberEnemy/up3.png"));
            up4 = ImageIO.read(getClass().getResource("/Assets/BomberEnemy/up4.png"));
            down1 = ImageIO.read(getClass().getResource("/Assets/BomberEnemy/down1.png")); // FIXED
            down2 = ImageIO.read(getClass().getResource("/Assets/BomberEnemy/down2.png"));
            down3 = ImageIO.read(getClass().getResource("/Assets/BomberEnemy/down3.png"));
            down4 = ImageIO.read(getClass().getResource("/Assets/BomberEnemy/down4.png"));
            left1 = ImageIO.read(getClass().getResource("/Assets/BomberEnemy/left1.png"));
            left2 = ImageIO.read(getClass().getResource("/Assets/BomberEnemy/left2.png"));
            left3 = ImageIO.read(getClass().getResource("/Assets/BomberEnemy/left3.png"));
            left4 = ImageIO.read(getClass().getResource("/Assets/BomberEnemy/left4.png"));
            right1 = ImageIO.read(getClass().getResource("/Assets/BomberEnemy/right1.png"));
            right2 = ImageIO.read(getClass().getResource("/Assets/BomberEnemy/right2.png"));
            right3 = ImageIO.read(getClass().getResource("/Assets/BomberEnemy/right3.png"));
            right4 = ImageIO.read(getClass().getResource("/Assets/BomberEnemy/right4.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Player player, ArrayList<Bullet> enemyBullets) {
        // Bomber always moves directly toward the player
        double angle = Math.atan2(player.getY() - y, player.getX() - x);
        double dx = Math.cos(angle) * moveSpeed;
        double dy = Math.sin(angle) * moveSpeed;

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
        this.spritecounter++; // delay buat ganti jenis sprite
        if (this.spritecounter > 12){ // di panggil 5x tiap jalan program (60/12)
            if (this.spritenum==4){
                this.spritenum=1; // reset to first sprite after 4th
            }else if (this.spritenum==1){
                this.spritenum++;
            }
            this.spritecounter=0; // kalau sudah ganti varian set counter ke 0
        }
        // Move faster toward player
        x += Math.cos(angle) * moveSpeed;
        y += Math.sin(angle) * moveSpeed;
        
        // Bombers don't shoot
    }
    
    @Override
    public void draw(Graphics g, int ex, int ey) {
        BufferedImage bimage = null;
        switch (direction) {
            case "up":
                if (spritenum==1){
                    bimage=up1;
                }else if (spritenum==2){
                    bimage=up2;
                }else if (spritenum==3){
                    bimage=up3;
                }else if (spritenum==4){
                    bimage=up4;
                }
                break;
            case "down":
                if (spritenum==1){
                    bimage=down1;
                } else if (spritenum==2){
                    bimage=down2;
                }else if (spritenum==3){
                    bimage=down3;
                }else if (spritenum==4){
                    bimage=down4;
                }
                break;
            case "left":
                if (spritenum==1){
                    bimage=left1;
                } else if (spritenum==2){
                    bimage=left2;
                }else if (spritenum==3){
                    bimage=left3;
                }else if (spritenum==4){
                    bimage=left4;
                }
                break;
            case "right":
                if (spritenum==1){
                    bimage=right1;
                } else if (spritenum==2){
                    bimage=right2;
                }else if (spritenum==3){
                    bimage=right3;
                }else if (spritenum==4){
                    bimage=right4;
                }
                break;
            default: break;
        }
        // g.setColor(new Color(255, 50, 50)); // Red color for bomber enemy
        // g.fillOval(ex, ey, size, size);
        g.drawImage(bimage, ex, ey, size*2, size*2, null);
        // Draw "explosive" markings
        // g.setColor(Color.ORANGE);
        // g.drawLine(ex + size/2, ey, ex + size/2, ey + size); // Vertical line
        // g.drawLine(ex, ey + size/2, ex + size, ey + size/2); // Horizontal line
    }
}
