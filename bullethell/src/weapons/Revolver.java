package weapons;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;

public class Revolver extends Weapon {
    public Revolver() {
        super("Revolver",
              loadImage("revolver.png"),
              loadImage("revolver_bullet.png"),
              7, // max ammo
              2700, // reload delay in ms
              433, // fire rate in ms
              false, // semi-auto
              150); // damage per shot
    }    
    
    private static BufferedImage loadImage(String filename) {
        try {
            if (filename.equals("revolver.png")) {
                return ImageIO.read(Revolver.class.getResourceAsStream("/Assets/player/Guns/revolver.png"));
            } else if (filename.equals("revolver_bullet.png")) {
                BufferedImage img = null;
                
                // Try to load the specific bullet
                img = ImageIO.read(Revolver.class.getResourceAsStream("/Assets/player/bullets/revolver/revolver_bullet.png"));
                
                // If that failed, try with lowercase
                if (img == null) {
                    img = ImageIO.read(Revolver.class.getResourceAsStream("/Assets/player/bullets/revolver/bullet2.png"));
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
