import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import players.Bullet;
import players.Player;

public class LurkerEnemy extends Enemy {    private boolean isJumping = false;
    private double jumpHeight = 0;
    private static final int MAX_JUMP_HEIGHT = 30;
    private static final double JUMP_SPEED = 1.5; // Slower jump for smoother animation
    private long lastJumpTime;
    private long jumpCooldown = 3000; // 3 seconds between jumps
    private double targetX, targetY;
    private boolean isPreparing = false;
    private long prepareStartTime;
    private static final long PREPARE_DURATION = 800; // 0.8 seconds of preparation before jump
    
    public LurkerEnemy(int x, int y) {
        super(x, y, 22, 3, new Color(0, 0, 100), 0, 0, 4); // Dark blue color, 4 health
        this.lastJumpTime = System.currentTimeMillis();
        
        // Try to load sprite
        try {
            sprite = ImageIO.read(getClass().getResource("/Assets/player/bullets/enemy_lurker.png"));
        } catch (Exception e) {
            // Keep using color fallback if sprite loading fails
            System.out.println("Could not load lurker enemy sprite");
        }
    }
      @Override
    public void update(Player player, ArrayList<Bullet> enemyBullets) {
        long currentTime = System.currentTimeMillis();
        double distance = Math.hypot(player.getX() - x, player.getY() - y);
        
        if (!isJumping && !isPreparing) {
            // Move slowly toward the player when not jumping or preparing
            double angle = Math.atan2(player.getY() - y, player.getX() - x);
            double dx = Math.cos(angle) * (speed / 2); // Slower when lurking
            double dy = Math.sin(angle) * (speed / 2);
            
            x += dx;
            y += dy;
            
            // Prepare to jump if cooldown passed and close enough
            if (currentTime - lastJumpTime > jumpCooldown && distance < 200) {
                isPreparing = true;
                prepareStartTime = currentTime;
                targetX = player.getX();
                targetY = player.getY();
            }
        } else if (isPreparing) {
            // During preparation, squat down and telegraph the jump
            if (currentTime - prepareStartTime > PREPARE_DURATION) {
                isPreparing = false;
                isJumping = true;
                jumpHeight = 0;
                // Update target position based on player's current position
                targetX = player.getX();
                targetY = player.getY();
            }
        } else if (isJumping) {
            // Handle jump animation and movement
            jumpHeight += JUMP_SPEED;
              
            if (jumpHeight >= MAX_JUMP_HEIGHT) {
                // At the peak of the jump, start heading toward the player's position
                double progress = (jumpHeight - MAX_JUMP_HEIGHT) / (double)MAX_JUMP_HEIGHT;
                if (progress > 1) progress = 1;
                
                x = (int)(x + (targetX - x) * progress * 0.5);
                y = (int)(y + (targetY - y) * progress * 0.5);
            }
            
            // End jump
            if (jumpHeight >= MAX_JUMP_HEIGHT * 2) {
                isJumping = false;
                jumpHeight = 0;
                lastJumpTime = currentTime;
            }
        }
    }
      @Override
    public void draw(Graphics g, int ex, int ey) {
        // Adjust position based on jump height
        int drawY = ey;
        if (isJumping) {
            int jumpOffset = 0;
            // Parabolic jump trajectory
            if (jumpHeight < MAX_JUMP_HEIGHT) {
                jumpOffset = (int)jumpHeight; 
            } else {
                jumpOffset = (int)(MAX_JUMP_HEIGHT * 2 - jumpHeight);
            }
            drawY = ey - jumpOffset;
        } else if (isPreparing) {
            // When preparing to jump, create a squatting animation
            drawY = ey + 3; // Slightly lower position to indicate preparation
        }
        
        // Draw lurker enemy with appropriate color
        if (isPreparing) {
            // Change color when preparing to jump (visual cue)
            g.setColor(new Color(0, 0, 200)); // Brighter blue when preparing
        } else {
            g.setColor(color);
        }
        g.fillOval(ex, drawY, size, size);
        
        // Add eyes
        g.setColor(Color.RED);
        g.fillOval(ex + size/4, drawY + size/3, 4, 4);
        g.fillOval(ex + (size*3)/4 - 4, drawY + size/3, 4, 4);
    }
}
