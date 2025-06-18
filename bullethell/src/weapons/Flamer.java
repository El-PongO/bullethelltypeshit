package weapons;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;

public class Flamer extends Weapon {
    public Flamer() {
        super("Flamer",
              loadImage("flamer.png"),
              loadImage("flamer_bullet.png"),
              250, // max ammo
              5000, // reload delay in ms
              40, // fire rate in ms
              true, // full auto
              10); // damage per shot
    }

    private static BufferedImage loadImage(String filename) {
        try {
            if (filename.equals("flamer.png")) {
                return ImageIO.read(Flamer.class.getResourceAsStream("/Assets/player/Guns/glock.png"));
            } else if (filename.equals("flamer_bullet.png")) {
                BufferedImage img = null;
                
                // Try to load the specific bullet
                img = ImageIO.read(Flamer.class.getResourceAsStream("/Assets/player/bullets/PistolAmmoBig.png"));
                
                // If that failed, try with lowercase
                if (img == null) {
                    img = ImageIO.read(Flamer.class.getResourceAsStream("/Assets/player/bullets/glock/bullet2.png"));
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
