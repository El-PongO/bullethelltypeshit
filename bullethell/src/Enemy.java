import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import players.Bullet;
import players.Player;

public class Enemy {
    int x, y;
    int size = 20;
    long lastShotTime;
    private static final int speed = 2;
    int shootDelay = 2000; // 2 second
    int bulletSpeed = 3;
    //====================//
    private Random rand = new Random();

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
        this.lastShotTime = System.currentTimeMillis();
    }
    
    public void update(int playerX, int playerY) {
        int behavior = rand.nextInt(2); // 0, 1, or 2 
        int dx = Integer.compare(playerX - x, 0);
        int dy = Integer.compare(playerY - y, 0);
        x += dx * speed;
        y += dy * speed;
        double distance = Math.hypot(playerX - x, playerY - y);
        if (behavior == 0) {
            if (distance > 100) {
                dx = (playerX > x) ? 1 : -1;
                dy = (playerY > y) ? 1 : -1;
            } else {
                dx = (playerX < x) ? 1 : -1;
                dy = (playerY < y) ? 1 : -1;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (behavior == 1) {
            int move = rand.nextInt(3); // 0 = left, 1 = right, 2 = idle
            if (move == 0) dx = -1;
            if (move == 1) dx = 1;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Bullet tryShoot(int playerX, int playerY) {
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

    public void update(Player player, ArrayList<Bullet> enemyBullets) {
        int behavior = rand.nextInt(2); // 0, 1, or 2 
        int dx = 0, dy = 0;

        // Behavior 1: move toward or away from player based on distance
        double distance = Math.hypot(player.getX() - x, player.getY() - y);
        if (behavior == 0) {
            if (distance > 50) {
                dx = (player.getX() > x) ? 1 : -1;
                dy = (player.getY() > y) ? 1 : -1;
            } else {
                dx = (player.getX() < x) ? 1 : -1;
                dy = (player.getY() < y) ? 1 : -1;
            }
        }
        // Behavior 2: random lateral movement or idle
        if (behavior == 1) {
            int move = rand.nextInt(3); // 0 = left, 1 = right, 2 = idle
            if (move == 0) dx = -1;
            if (move == 1) dx = 1;
        }

        x += dx * speed; // move speed
        y += dy * speed;
        // Behavior 3: shoot if cooldown is ready
    }

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
