package weapons;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;

public class Shotgun extends Weapon {
    public Shotgun() {
        super("Shotgun",
              loadImage("shotgun.png"),
              loadImage("shotgun_bullet.png"),
              8, // max ammo
              3000, // reload delay in ms
              1000, // fire rate in ms
              false, // semi-auto
              100); // damage per pellet (100 biar ngetes nya gampang :v)
    }    
    
    private static BufferedImage loadImage(String filename) {
        try {
            if (filename.equals("shotgun.png")) {
                // Make sure we load from the correct path
                BufferedImage img = ImageIO.read(Shotgun.class.getResourceAsStream("/Assets/player/Guns/shotgun.png"));
                if (img == null) {
                    System.out.println("Failed to load shotgun image, path may be incorrect");
                }
                return img;
            } else if (filename.equals("shotgun_bullet.png")) {
                // Try to find a suitable bullet sprite
                BufferedImage img = null;
                
                // Try revolver bullets since shotgun bullets don't seem to exist
                try {
                    img = ImageIO.read(Shotgun.class.getResourceAsStream("/Assets/player/bullets/revolver/bullet2.png"));
                    if (img != null) {
                        System.out.println("Using revolver bullet for shotgun");
                        return img;
                    }
                } catch (Exception e) {
                    System.out.println("Could not load revolver bullet: " + e.getMessage());
                }
                
                // Try with different case
                try {
                    img = ImageIO.read(Shotgun.class.getResourceAsStream("/Assets/player/bullets/revolver/Bullet2.png"));
                    if (img != null) {
                        System.out.println("Using revolver Bullet2 for shotgun");
                        return img;
                    }
                } catch (Exception e) {
                    System.out.println("Could not load Bullet2: " + e.getMessage());
                }
                
                // Try with basic bullet
                try {
                    img = ImageIO.read(Shotgun.class.getResourceAsStream("/Assets/player/bullets/revolver/Bullet.png"));
                    if (img != null) {
                        System.out.println("Using revolver Bullet for shotgun");
                        return img;
                    }
                } catch (Exception e) {
                    System.out.println("Could not load Bullet: " + e.getMessage());
                }
                
                System.out.println("WARNING: Failed to load any bullet sprite for shotgun!");
                return null;
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
        int pellets = 8;
        double spread = Math.toRadians(45); // 45 degree spread
        double angle = Math.atan2(targetY - (y + playerSize/2), targetX - (x + playerSize/2));
        Random rand = new Random();
        if (getCurrentAmmo() > 0) {
            for (int i = 0; i < pellets; i++) {
                double minAngle = angle - spread / 2;
                double segment = spread / (pellets - 1);
                double jitter = (rand.nextDouble() - 0.5) * (segment * 0.4); // 40% of segment width
                double pelletAngle = minAngle + i * segment + jitter;
                int dx = (int)(Math.cos(pelletAngle) * 3) * 3; // 3 is bullet speed
                int dy = (int)(Math.sin(pelletAngle) * 3) * 3;
                bullets.add(new Bullet(x + playerSize/2, y + playerSize/2, dx, dy, null));
            }
            useAmmo(); // Only use 1 ammo per shot
            recordShot();
        }
        return bullets;
    }
}
