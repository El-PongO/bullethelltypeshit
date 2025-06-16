package players;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class DamageCircle {
    int x, y, radius, damage;
    long startTime, duration;
    Color color;
    boolean active;
    boolean invincible = true; // Always invincible for now

    static BufferedImage[] sprite; // Array to hold animation frames
    static final int FRAME_COUNT = 10;
    static {

        String basePath = "/Assets/explosionsfx/";
        sprite = new BufferedImage[FRAME_COUNT];

        for (int i = 1; i <= FRAME_COUNT; i++) {
            String filename = String.format("%sexplosion%03d.png", basePath, i - 1);
            try {
                sprite[i - 1] = ImageIO.read(DamageCircle.class.getResource(filename));
            } catch (Exception e) {
                System.err.println("Failed to load: " + filename);
            }
        }
    }

    public DamageCircle(int x, int y, int radius, int damage, long duration, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.damage = damage;
        this.duration = duration;
        this.color = color;
        this.startTime = System.currentTimeMillis();
        this.active = true;
    }

    public boolean isActive() {
        if (active && System.currentTimeMillis() - startTime > duration) {
            active = false;
        }
        return active;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getRadius() { return radius; }
    public int getDamage() { return damage; }
    public boolean isInvincible() { return invincible; }
    public void setInvincible(boolean inv) { this.invincible = inv; }

    public void draw(Graphics2D g, int cameraX, int cameraY, int zoom) {
        if (!isActive()) return;
        int px = (x - cameraX) * zoom;
        int py = (y - cameraY) * zoom;
        int r = radius * zoom;
        Composite oldComp = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
        int frameIdx = 0;

        long elapsed = System.currentTimeMillis() - startTime;
        int frame = (int)((elapsed * FRAME_COUNT) / duration);
        frameIdx = Math.min(frame, FRAME_COUNT - 1);


        g.drawImage(sprite[frameIdx], px - r, py - r, r * 2, r * 2, null);
    }

    public boolean collides(int ex, int ey, int esize) {
        if (!isActive()) return false;
        // ex, ey are top-left; convert to center
        int enemyCenterX = ex + esize / 2;
        int enemyCenterY = ey + esize / 2;
        double dist = Math.hypot(x - enemyCenterX, y - enemyCenterY);
        return dist < radius + esize / 2;
    }
}
