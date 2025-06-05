import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import players.Bullet;
import players.Player;

// Making Enemy an abstract class to support multiple enemy types
public abstract class Enemy {
    int x, y;
    int size = 20;
    long lastShotTime;
    protected static final int speed = 2;
    protected int shootDelay = 2000; // 2 second
    protected int bulletSpeed = 3;
    //====================//
    protected Random rand = new Random();    public Enemy(int x, int y) {
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
    
    // Making update abstract so each enemy type must implement it
    public abstract void update(Player player, ArrayList<Bullet> enemyBullets);

    public void draw(Graphics g, int ex, int ey) {
        g.setColor(new Color(102, 51, 153));//mek warna gae ungu idk
        g.fillOval(ex, ey, size, size);
    }

    public int getX() { return x; }
    public int getY() { return y; }

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
