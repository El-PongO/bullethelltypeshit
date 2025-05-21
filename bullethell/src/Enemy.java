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
        // Behavior soul knight
        int behavior = rand.nextInt(3); // 0, 1, or 2 
        int dx = 0, dy = 0;

        // Behavior 1: move toward or away from player based on distance
        double distance = Math.hypot(player.getX() - x, player.getY() - y);
        if (behavior == 0) {
            if (distance > 150) {
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

        // Behavior 3: shoot if cooldown is ready
        if (behavior == 2 && cooldownTimer <= 0) {
            enemyBullets.add(new Bullet(x, y, player.getX(), player.getY(), 5));
            cooldownTimer = shootCooldown;
        }

        x += dx * 2; // move speed
        y += dy * 2;

        if (cooldownTimer > 0) cooldownTimer--;
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
