import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import players.Bullet;
import players.Player;

public abstract class Enemy {
    int x, y;
    int size;
    long lastShotTime;
    protected int speed;
    int shootDelay;
    int bulletSpeed;
    protected Color color;
    protected BufferedImage sprite;
    protected int health;
    protected int maxHealth;
    //====================//
    protected Random rand = new Random();    public Enemy(int x, int y, int size, int speed, Color color, int shootDelay, int bulletSpeed, int health) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
        this.color = color;
        this.shootDelay = shootDelay;
        this.bulletSpeed = bulletSpeed;
        this.health = health;
        this.maxHealth = health;
        this.lastShotTime = System.currentTimeMillis();
        
        // Load sprite is done in subclasses
    }
    
    // This method is deprecated and will be removed after migration
    public void update(int playerX, int playerY) {
        // Legacy method, use update(Player, ArrayList<Bullet>) instead
    }    protected Bullet tryShoot(int playerX, int playerY) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime > shootDelay) {
            double angle = Math.atan2(playerY - y, playerX - x);
            double dx = Math.cos(angle) * bulletSpeed;
            double dy = Math.sin(angle) * bulletSpeed;
            lastShotTime = currentTime;
            return createBullet((int)dx, (int)dy, angle);
        }
        return null;
    }
    
    // Allows enemy subclasses to customize their bullet creation
    protected Bullet createBullet(int dx, int dy, double angle) {
        return new Bullet(x + size/2, y + size/2, dx, dy, null, 1); // Default bullet with damage 1
    }public abstract void update(Player player, ArrayList<Bullet> enemyBullets);    public void draw(Graphics g, int ex, int ey) {
        if (sprite != null) {
            // Draw sprite if available
            g.drawImage(sprite, ex, ey, size, size, null);
        } else {
            // Fallback to colored shape
            g.setColor(color);
            g.fillOval(ex, ey, size, size);
        }
        
        // Draw health bar
        int healthBarWidth = size;
        int healthBarHeight = 4;
        g.setColor(Color.RED);
        g.fillRect(ex, ey - 8, healthBarWidth, healthBarHeight);
        g.setColor(Color.GREEN);
        g.fillRect(ex, ey - 8, (int)(healthBarWidth * ((double)health / maxHealth)), healthBarHeight);
    }    public int getX() { return x; }
    public int getY() { return y; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    
    public boolean takeDamage(int damage) {
        health -= damage;
        return health <= 0; // Returns true if the enemy is dead
    }

    public boolean checkCollision(Bullet bullet) {
        double distance = Math.hypot(x - bullet.getX(), y - bullet.getY());
        return distance < (size / 2 + bullet.getSize() / 2);
    }
    public Bullet checkCollision(ArrayList<Bullet>  playerBullets) {
        Rectangle enemyBounds = new Rectangle(x, y, size, size);
        for (Bullet bullet : playerBullets) {
            Rectangle bulletBound = new Rectangle(bullet.x, bullet.y, bullet.getSize(), bullet.getSize());
            if (enemyBounds.intersects(bulletBound)) {
                return bullet;
            }
        }
        return null;
    }
}
