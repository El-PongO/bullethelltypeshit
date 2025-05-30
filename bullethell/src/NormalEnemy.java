import java.awt.*;
import java.util.ArrayList;

public class NormalEnemy extends Enemy {
    
    public NormalEnemy(int x, int y) {
        super(x, y);
        // Normal enemies don't shoot, they just follow the player
        this.size = 20; // Standard size
    }
    
    @Override
    public void update(Player player, ArrayList<Bullet> enemyBullets) {
        // Simple movement: always go toward the player
        double angle = Math.atan2(player.getY() - y, player.getX() - x);
        
        // Move at normal speed
        x += Math.cos(angle) * speed;
        y += Math.sin(angle) * speed;
        
        // Normal enemies don't shoot
    }
    
    @Override
    public void draw(Graphics g, int ex, int ey) {
        g.setColor(new Color(100, 100, 200)); // Blue color for normal enemy
        g.fillOval(ex, ey, size, size);
    }
}
