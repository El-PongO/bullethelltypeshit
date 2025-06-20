package weapons;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;
public class Smg extends Weapon {
    public Smg() {
        super("SMG",
              loadImage("smg.png"),
              loadImage("smg_bullet.png"),
              30, // max ammo
              2500, // reload delay in ms
              80, // fire rate in ms
              true, // semi-auto
              30); // damage per shot
    }
    
    private static BufferedImage loadImage(String filename) {
        try {
            if (filename.equals("smg.png")) {
                return ImageIO.read(Smg.class.getResourceAsStream("/Assets/player/Guns/smg1.png"));
            } else if (filename.equals("smg_bullet.png")) {
                BufferedImage img = null;
                
                // Try to load the specific bullet
                img = ImageIO.read(Smg.class.getResourceAsStream("/Assets/player/bullets/PistolAmmoBig.png"));
                
                // If that failed, try with lowercase
                if (img == null) {
                    img = ImageIO.read(Smg.class.getResourceAsStream("/Assets/player/bullets/smg1/bullet2.png"));
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
            double dx = (Math.cos(angle) * 3) * 3; // 3 is bullet speed
            double dy = (Math.sin(angle) * 3) * 3;
            bullets.add(new Bullet(x + playerSize/2, y + playerSize/2, (int)dx, (int)dy, getBulletSprite()));
            
            useAmmo();
            recordShot();
        }
        
        return bullets;
    }
}
