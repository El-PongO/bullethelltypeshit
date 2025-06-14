package weapons;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Bullet {
    public int x, y;
    public int dx, dy;
    private static final int size = 10;
    private BufferedImage sprite;

    public Bullet(int x, int y, int dx, int dy, BufferedImage sprite) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.sprite = sprite;
    }
    
    public void update() {
        x += dx;
        y += dy;
    }

    public void draw(Graphics g, int ex, int ey, Color color) {
        if (sprite != null) {
            g.drawImage(sprite, ex, ey, size, size, null);
        } else {
            g.setColor(color);
            g.fillOval(ex, ey, size, size);
        }
    }

    public boolean isOutOfBounds(int[][] grid, int tileSize) {
        int gridX = x / tileSize;
        int gridY = y / tileSize;
        return gridX < 0 || gridY < 0 || gridX >= grid[0].length || gridY >= grid.length 
               || grid[gridY][gridX] == 1;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }
}
