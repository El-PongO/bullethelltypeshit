package weapons;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;

public class Glock extends Weapon{
    public Glock(){
        super("Glock",
              loadImage("glock.png"),
              loadImage("glock_bullet.png"),
              18, // max ammo
              2000, // reload delay in ms
              225, // fire rate in ms
              false, // semi-auto
              50); // damage per shot
    }

    private static BufferedImage loadImage(String filename) {
        try {
            if (filename.equals("glock.png")) {
                return ImageIO.read(Glock.class.getResourceAsStream("/Assets/player/Guns/glock.png"));
            } else if (filename.equals("glock_bullet.png")) {
                BufferedImage img = null;
                
                // Try to load the specific bullet
                img = ImageIO.read(Glock.class.getResourceAsStream("/Assets/player/bullets/PistolAmmoBig.png"));
                
                // If that failed, try with lowercase
                if (img == null) {
                    img = ImageIO.read(Glock.class.getResourceAsStream("/Assets/player/bullets/glock/bullet2.png"));
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
            int dx = (int)(Math.cos(angle) * 3) * 3; // 3 is bullet speed
            int dy = (int)(Math.sin(angle) * 3) * 3;
            bullets.add(new Bullet(x + playerSize/2, y + playerSize/2, dx, dy, getBulletSprite()));
            
            useAmmo();
            recordShot();
        }
        
        return bullets;
    }
}
