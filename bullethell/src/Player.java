import java.awt.*;
import java.util.ArrayList;

public class Player {
    private int x, y;
    private int size = 10;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getHitboxSize() { // ini buat hitbox player
        return size;
    }

    public void updatePosition(int mouseX, int mouseY) {
        this.x = mouseX;  // gerakan buat sekarang itu pake mouse, mungkin lebih gampang dari pada WASD atau arrow key
        this.y = mouseY; // tapi ini ya pre-alpha so stfu
    }

    public boolean checkCollision(Bullet bullet) {
        double distance = Math.sqrt(Math.pow(x - bullet.getX(), 2) + Math.pow(y - bullet.getY(), 2)); // ini buat hitbox player
        return distance < (size / 2 + bullet.getHitboxSize() / 2);
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE); // gambar dot nya yg di cursor
        g.fillOval(x - size / 2, y - size / 2, size, size);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void move(int dx, int dy) {
        x += dx;
        y += dy;//gae movement e player receiver wasd ne
    }

    void shootBullet(int targetX, int targetY, ArrayList<Bullet> playerBullets) {
        Bullet b = new Bullet(x, y, targetX, targetY, 10);
        b.setColor(Color.GREEN);//gae buat warna bullet seng ditembak player hijau
        playerBullets.add(b);
    }
}
