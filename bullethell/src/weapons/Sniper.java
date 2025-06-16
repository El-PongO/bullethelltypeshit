package weapons;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;

public class Sniper extends Weapon{

    private static final int PENETRATION_COUNT = 3; // How many enemies it can go through

    public static class SniperBullet extends Bullet {
        private int penetrationsLeft;

        public SniperBullet(int x, int y, int dx, int dy, BufferedImage sprite) {
            super(x, y, dx, dy, sprite);
            this.penetrationsLeft = PENETRATION_COUNT;
        }

        public boolean canPenetrate() {
            return penetrationsLeft > 0;
        }

        public void penetrate() {
            penetrationsLeft--;
        }
    }

    public Sniper(){
        super("Sniper",
              loadImage("sniper.png"),
              loadImage("sniper_bullet.png"),
              5, // max ammo
              4500, // reload delay in ms
              2000, // fire rate in ms
              false, // semi-auto
              300); // damage per shot
    }

    private static BufferedImage loadImage(String filename) {
        try {
            if (filename.equals("sniper.png")) {
                return ImageIO.read(Sniper.class.getResourceAsStream("/Assets/player/Guns/sniper.png"));
            } else if (filename.equals("sniper_bullet.png")) {
                BufferedImage img = null;
                
                // Try to load the specific bullet
                img = ImageIO.read(Sniper.class.getResourceAsStream("/Assets/player/bullets/sniper/sniper_bullet.png"));
                
                // If that failed, try with lowercase
                if (img == null) {
                    img = ImageIO.read(Sniper.class.getResourceAsStream("/Assets/player/bullets/smg1/bullet2.png"));
                }
                
                return img;
            }
            return null;
        } catch (IOException e) {
            System.out.println("Error loading image " + filename + ": " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public List<Bullet> fire(int x, int y, int targetX, int targetY, int playerSize) {
        List<Bullet> bullets = new ArrayList<>();
        
        if (getCurrentAmmo() > 0) {
            double angle = Math.atan2(targetY - (y + playerSize/2), targetX - (x + playerSize/2));
            // Double the bullet speed (6 instead of 3)
            int dx = (int)(Math.cos(angle) * 6) * 3;
            int dy = (int)(Math.sin(angle) * 6) * 3;
            
            bullets.add(new SniperBullet(x + playerSize/2, y + playerSize/2, dx, dy, getBulletSprite()));
            
            useAmmo();
            recordShot();
        }
        
        return bullets;
    }
}
