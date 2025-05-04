import java.awt.*;
import java.util.Random;

public class Enemy {
    private int x, y;
    private int size = 15;
    private int shootCooldown = 60; // frames
    private int cooldownTimer = 0;
    private Random rand = new Random();

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update(Player player, java.util.List<Bullet> enemyBullets) {
        int behavior = rand.nextInt(3); // 0, 1, or 2
        int dx = 0, dy = 0;

        // Behavior 1: move toward or away from player based on distance
        double distance = Math.hypot(player.getX() - x, player.getY() - y);
        
        if (distance > 150) {
            dx = (player.getX() > x) ? 1 : -1;
            dy = (player.getY() > y) ? 1 : -1;
        }
        
        if (cooldownTimer > 0) {
            cooldownTimer--;
        }
        else{
            enemyBullets.add(new Bullet(x, y, player.getX(), player.getY(), 5));
            cooldownTimer = shootCooldown;
        }

        x += dx * 2; // move speed
        y += dy * 2;

        
    }

    public void draw(Graphics g) {
        g.setColor(new Color(102, 51, 153));//mek warna gae ungu idk
        g.fillOval(x - size / 2, y - size / 2, size, size);
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public boolean checkCollision(Bullet bullet) {
        double distance = Math.hypot(x - bullet.getX(), y - bullet.getY());
        return distance < (size / 2 + bullet.getHitboxSize() / 2);
    }
}
