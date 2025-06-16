import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import players.Player;

public class Skillbar {
    private BufferedImage skillIcon;
    private static final int ICON_SIZE = 32;
    private static final Color READY_COLOR = new Color(50, 200, 50);
    private static final Color ACTIVE_COLOR = new Color(200, 180, 20);
    private static final Color COOLDOWN_COLOR = new Color(200, 50, 50);
    private static final Color BG_COLOR = new Color(30, 30, 30, 180);
    private static final Color BORDER_COLOR = new Color(200, 200, 200);
    
    public Skillbar() {
        try {
            // You can replace this with an appropriate skill icon
            skillIcon = ImageIO.read(getClass().getResource("/Assets/Hunter/idledown.png"));
        } catch (Exception e) {
            System.out.println("Error loading skill icon: " + e.getMessage());
        }
    }    public void draw(Graphics2D g, Player player, int screenWidth, int screenHeight) {
        // Set skill icon based on player class
        try {
            if (player instanceof players.Brute) {
                skillIcon = ImageIO.read(getClass().getResource("/Assets/skillicon/Punch.png"));
            } else if (player instanceof players.Vampire) {
                skillIcon = ImageIO.read(getClass().getResource("/Assets/skillicon/Heal.png"));
            } else if (player instanceof players.Gunslinger) {
                skillIcon = ImageIO.read(getClass().getResource("/Assets/skillicon/Bulletbarage.png"));
            } else if (player instanceof players.Bomber) {
                skillIcon = ImageIO.read(getClass().getResource("/Assets/skillicon/Selfdestruct.png"));
            } else {
                skillIcon = ImageIO.read(getClass().getResource("/Assets/skillicon/Nullicon.png"));
            }
        } catch (Exception e) {
            System.out.println("Error loading skill icon: " + e.getMessage());
        }
        int barWidth = 120;
        int barHeight = 16;
        int iconSize = ICON_SIZE;
        int x = screenWidth / 2 - barWidth / 2;
        int y = screenHeight - barHeight - 40; // 40 pixels from bottom
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(BG_COLOR);
        g.fillOval(x - iconSize - 5, y - (iconSize - barHeight) / 2, iconSize, iconSize);
        
        // Draw skill icon
        if (skillIcon != null) {
            g.drawImage(skillIcon, x - iconSize - 5, y - (iconSize - barHeight) / 2, iconSize, iconSize, null);
        }
        
        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(2));
        g.drawOval(x - iconSize - 5, y - (iconSize - barHeight) / 2, iconSize, iconSize);
        
        g.setColor(BG_COLOR);
        g.fillRoundRect(x, y, barWidth, barHeight, 8, 8);
        
        long currentTime = System.currentTimeMillis();
        double progress = 0;
        Color progressColor;
        String statusText;
          if (player.isSkillActive()) {            // Skill is active
            long elapsedTime = currentTime - player.getSkillStartTime();
            progress = 1.0 - Math.min(1.0, (double)elapsedTime / player.getSkillDuration());
            progressColor = ACTIVE_COLOR;
            long remainingSeconds = Math.max(0, (player.getSkillDuration() - elapsedTime) / 1000);
            statusText = getSkillName(player) + ": " + remainingSeconds + "s";

        } else if (currentTime - player.getLastSkillUseTime() < player.getSkillCooldown()) { //skill cooldown
            long elapsedTime = currentTime - player.getLastSkillUseTime();
            progress = Math.min(1.0, (double)elapsedTime / player.getSkillCooldown());
            progressColor = COOLDOWN_COLOR;
            long remainingSeconds = Math.max(0, (player.getSkillCooldown() - elapsedTime) / 1000);
            statusText = "Cooldown: " + remainingSeconds + "s";

        } else { //skill ready
            progress = 1.0;
            progressColor = READY_COLOR;
            statusText = getSkillName(player) + " (F)";
        }
        
        // Draw progress bar
        g.setColor(progressColor);
        g.fillRoundRect(x, y, (int)(barWidth * progress), barHeight, 8, 8);
        
        // Draw border
        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(x, y, barWidth, barHeight, 8, 8);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("F", x - iconSize + 10, y - (iconSize - barHeight) / 2 + iconSize - 8);
          // Draw status text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        int textWidth = g.getFontMetrics().stringWidth(statusText);
        g.drawString(statusText, x + barWidth / 2 - textWidth / 2, y + barHeight - 4);
    }
    
    private String getSkillName(Player player) {
        if (player instanceof players.Gunslinger) {
            return "High noon";
        } else if (player instanceof players.Bomber) {
            return "Explosion";
        } else if (player instanceof players.Brute) {
            return "Enforce";
        } else if (player instanceof players.Vampire) {
            return "Life Drain";
        } else {
            return "Skill";
        }
    }
}
