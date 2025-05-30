import java.awt.*;
import java.util.ArrayList;

public class BomberEnemy extends Enemy {
    private double moveSpeed = 3.5; // Faster than normal enemies
    
    public BomberEnemy(int x, int y) {
        super(x, y);
        // Bombers don't shoot, they just chase the player
        this.size = 18; // Medium size
    }
    
    @Override
    public void update(Player player, ArrayList<Bullet> enemyBullets) {
        // Bomber always moves directly toward the player
        double angle = Math.atan2(player.getY() - y, player.getX() - x);
        
        // Move faster toward player
        x += Math.cos(angle) * moveSpeed;
        y += Math.sin(angle) * moveSpeed;
        
        // Bombers don't shoot
    }
    
    @Override
    public void draw(Graphics g, int ex, int ey) {
        g.setColor(new Color(255, 50, 50)); // Red color for bomber enemy
        g.fillOval(ex, ey, size, size);
        
        // Draw "explosive" markings
        g.setColor(Color.ORANGE);
        g.drawLine(ex + size/2, ey, ex + size/2, ey + size); // Vertical line
        g.drawLine(ex, ey + size/2, ex + size, ey + size/2); // Horizontal line
    }
}
