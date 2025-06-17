package enemies;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import players.Player;
import weapons.Bullet;

// Making Enemy an abstract class to support multiple enemy types
public abstract class Enemy {
    public int x, y;
    public int size = 20;
    protected int health = 100; // Enemy health
    protected int maxHealth = 100; // Maximum health
    long lastShotTime;
    protected static final int speed = 2;
    protected int shootDelay = 2000; // 2 second
    protected int bulletSpeed = 3;
    //====================//
    protected Random rand = new Random();public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
        this.lastShotTime = System.currentTimeMillis();
    }
    
    // Making tryShoot protected so it's only accessible from subclasses
    protected Bullet tryShoot(int playerX, int playerY) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime > shootDelay) {
            double angle = Math.atan2(playerY - y, playerX - x);
            double dx = Math.cos(angle) * bulletSpeed;
            double dy = Math.sin(angle) * bulletSpeed;
            lastShotTime = currentTime;
            return new Bullet(x + size/2, y + size/2, (int)dx, (int)dy, null); // sementara null, sambil cari sprite buat bullet
        }
        return null;
    }
      // Update method for all enemies to support collision-aware movement
    public abstract void update(Player player, ArrayList<Bullet> enemyBullets, int[][] collisionMap, int tileSize);
      // Helper method to keep enemies within map boundaries
    public void keepWithinMapBoundaries(int mapWidth, int mapHeight) {
        // Apply boundary constraints with padding based on enemy size
        int padding = size;
        x = Math.max(padding, Math.min(x, mapWidth - padding));
        y = Math.max(padding, Math.min(y, mapHeight - padding));
    }
    
    public void draw(Graphics g, int ex, int ey) {
        g.setColor(new Color(102, 51, 153));//mek warna gae ungu idk
        g.fillOval(ex, ey, size, size);
        
        // Draw health bar if enemy has taken damage
        if (health < maxHealth) {
            // Health bar background
            g.setColor(Color.RED);
            g.fillRect(ex, ey - 10, size, 5);
            
            // Current health
            g.setColor(Color.GREEN);
            int healthBarWidth = (int)((float)health / maxHealth * size);
            g.fillRect(ex, ey - 10, healthBarWidth, 5);
        }
    }
    public int getX() { return x; }
    public int getY() { return y; }
    
    // Get health methods
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    
    // Take damage method
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }
    
    // Check if enemy is dead
    public boolean isDead() {
        return health <= 0;
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

    public boolean isOutOfBounds(int[][] grid, int tileSize, int newX, int newY) {
        int gridX = newX / tileSize;
        int gridY = newY / tileSize;
        return gridX < 0 || gridY < 0 || gridX >= grid[0].length || gridY >= grid.length 
               || grid[gridY][gridX] != 0;
    }
    protected void moveWithCollision(double dx, double dy, int[][] collisionMap, int tileSize) {
        int newX = (int) (x + dx);
        int newY = (int) (y + dy);
        int gridX = (newX + size / 2) / tileSize;
        int gridY = (newY + size / 2) / tileSize;
        if (gridY >= 0 && gridY < collisionMap.length && gridX >= 0 && gridX < collisionMap[0].length && collisionMap[gridY][gridX] == 0) {
            x = newX;
            y = newY;
        }
    }
}
