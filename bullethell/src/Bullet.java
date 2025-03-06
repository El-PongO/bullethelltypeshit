import java.awt.*;
import java.util.Random;

public class Bullet {
    private int x, y;
    private int size = 20;
    private int speed;
    private double directionX, directionY;

    public Bullet(int targetX, int targetY, int speed) {
        Random rand = new Random(); // Random ya kamu

        int spawnSide = rand.nextInt(4); // ini randomizer buat spawn bulletnya di sisi mana, 0 = atas, 1 = bawah, 2 = kiri, 3 = kanan
        if (spawnSide == 0) {
            x = rand.nextInt(1000); // ini randomizer buat di mana di sisi ini bulletnya spawn
            y = -size; // ini itu agak aneh aku buat spawn nya, jadi code nya sendiri kan gtw mereka spawn itu di mana, jadi fungsi nya y = -size itu biar spawn nya di Top of the screen tapi just enought jadi di luar screen
        } else if (spawnSide == 1) {
            x = rand.nextInt(1000);
            y = 800 + size; // untuk yang ini, kenapa y= 800 + size? karena kan window game nya itu 1000x800, jadi y = 800 + size itu di bawah screen karena code itu hitung lokasi itu dari atas ke bawah, jadi 0 itu atas dan 800 itu bawah, + size itu biar spawn nya itu di luar scren
        } else if (spawnSide == 2) {
            x = -size; // ini logic nya sama kayak yang atas, tapi karena yang di pakai itu x, jadi hitung nya horizontal, dan karena komputer hitung dari sisi kiri ke kanan, maka value x = 0, dan - size itu biar ya tau lah dari atas
            y = rand.nextInt(800);
        } else {
            x = 1000 + size; // tebaken dw iki buat apa
            y = rand.nextInt(800);
        }

        this.speed = speed;

        double diffX = targetX - x; // jadi gini, targetY sama targetX itu posisi player, jadi aku buat bullet nya itu spawn menuju ke arah posisi player
        double diffY = targetY - y; // ngomongnya itu kyk homing lah, biar player nya itu gk bisa sit still di satu tempat
        double length = Math.sqrt(diffX * diffX + diffY * diffY);  // yang ini itu biar bullet nya gerak, ngomongnya itu biar kyk membuat trajectorynya lah
        directionX = diffX / length; // sek sumpah ak cape
        directionY = diffY / length; 
    } 

    public void update() {
        x += directionX * speed; // ini buat gerakin bullet nya
        y += directionY * speed;
    }

    public int getHitboxSize() {
        return size;    // hitbox bang
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, size, size);
    }

    public boolean isOutOfBounds() {
        return x < -size || x > 1000 + size || y < -size || y > 800 + size; // cek lokasi bullet nya, klo di luar ya hilang
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
