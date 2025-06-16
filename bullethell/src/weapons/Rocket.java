package weapons;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;

public class Rocket extends Weapon {

    public static class RocketBullet extends Bullet {
        public RocketBullet(int x, int y, int dx, int dy, BufferedImage sprite) {
            super(x, y, dx, dy, sprite);
        }
    }

    public Rocket() {
        super("Rocket Launcher",
              loadImage("rocket_launcher.png"),
              loadImage("rocket_bullet.png"),
              1, // max ammo
              3200, // reload delay in ms
              0, // fire rate in ms
              false, // semi-auto
              500); // damage per shot
    }

    private static BufferedImage loadImage(String filename) {
        try {
            if (filename.equals("rocket_launcher.png")) {
                return ImageIO.read(Rocket.class.getResourceAsStream("/Assets/player/Guns/m20.png"));
            } else if (filename.equals("rocket_bullet.png")) {
                BufferedImage img = null;
                
                // Try to load the specific bullet
                img = ImageIO.read(Rocket.class.getResourceAsStream("/Assets/player/bullets/Tbazooka/M20_ThickRocket.png"));
                
                // If that failed, try with lowercase
                if (img == null) {
                    img = ImageIO.read(Rocket.class.getResourceAsStream("/Assets/player/bullets/smg1/bullet2.png"));
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
            int dx = (int)(Math.cos(angle) * 3) * 3;
            int dy = (int)(Math.sin(angle) * 3) * 3;
            bullets.add(new RocketBullet(x + playerSize/2, y + playerSize/2, dx, dy, getBulletSprite()));
            
            useAmmo();
            recordShot();
        }
        
        return bullets;
    }
}
