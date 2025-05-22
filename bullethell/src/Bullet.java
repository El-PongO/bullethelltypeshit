import java.awt.*;

public class Bullet {
    int x, y;
    int dx, dy;
    private static final int size = 10;
    
    public Bullet(int x, int y, int dx, int dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }
    
    public void update() {
        x += dx;
        y += dy;
    }
    
    public void draw(Graphics g,int ex,int ey, Color color) { // ini buat gambar bullet nya
        g.setColor(color);
        g.fillOval(ex, ey, size, size);
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
