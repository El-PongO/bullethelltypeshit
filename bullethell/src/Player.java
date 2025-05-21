import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Player {
    static int x, y;
    static int size = 10;
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

    public int getHitboxSize() { // Player Hitbox
        return size;
    }
    // public void updatePosition(int mouseX, int mouseY) {
    //     this.x = mouseX;  // gerakan buat sekarang itu pake mouse, mungkin lebih gampang dari pada WASD atau arrow key
    //     this.y = mouseY; // tapi ini ya pre-alpha so stfu
    // }
    public boolean checkCollision(ArrayList<Enemy> enemies, ArrayList<Bullet>  enemyBullet) {
        Rectangle playerBounds = new Rectangle(x, y, size, size);
        for (Enemy enemy : enemies) {
            Rectangle enemyBounds = new Rectangle(enemy.x, enemy.y, enemy.size, enemy.size);
            if (playerBounds.intersects(enemyBounds)) {
                return true;
            }
        }
        for (Bullet bullet : enemyBullet) {
            Rectangle bulletBound = new Rectangle(bullet.x, bullet.y, bullet.size, bullet.size);
            if (playerBounds.intersects(bulletBound)) {
                return true;
            }
        }
        return false;
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
        
        // Draw relative to camera position
        g.drawImage(bimage, x, y, 40, 40, null);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void move(int dx, int dy) {
        x += dx;
        y += dy;//gae movement e player receiver wasd ne
    }
    
    void shootBullet(int targetX, int targetY, ArrayList<Bullet> playerBullets) {
        // Calculate position offset to shoot from center of player
        int centerX = x + 20; // Half of player width (40)
        int centerY = y + 20; // Half of player height (40)
        
        // Create bullet directly towards target position
        Bullet b = new Bullet(centerX, centerY, targetX, targetY, 10);
        b.setColor(Color.GREEN);
        playerBullets.add(b);
    }

    public void move(boolean upPressed,boolean downPressed, boolean leftPressed, boolean rightPressed,int height,int width){       
        if (upPressed || downPressed || leftPressed || rightPressed){
            this.idling=false; // cek player kalau jalan berati tidak idle

            if (upPressed && this.getY()>2){ //gae wasd
                this.move(0, -5);
                this.direction="up"; // ini set directionnya
            } 
            if (downPressed && this.getY()<height-60){
                this.move(0, 5);
                this.direction="down";
            } 
            if (leftPressed && this.getX()>2){
                this.move(-5, 0);
                this.direction="left";
            } 
            if (rightPressed && this.getX()<width-42){
                this.move(5, 0);
                this.direction="right";
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
}
