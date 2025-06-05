import java.awt.*;
public class Game_over {
    private int finalScore = 0;
    
    public void setFinalScore(int score) {
        this.finalScore = score;
    }
    
    public void draw(Graphics g, int panelWidth, int panelHeight) {
        Graphics2D g2d = (Graphics2D) g;

        // Background color
        g2d.setColor(new Color(28, 51, 92));
        g2d.fillRect(0, 0, panelWidth, panelHeight);

        // Title text
        String title = "Game Over";
        g2d.setFont(new Font("Arial", Font.BOLD, 64));
        g2d.setColor(Color.WHITE);

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(title);
        int textHeight = fm.getAscent();

        // Position title near the top center
        int x = ((panelWidth - textWidth) / 2) + 10; // (lebar layar - lebar teks) / 2 + 10
        int y = (panelHeight / 2) - 50;  // 1/2 from top + 50        g2d.drawString(title, x, y);
        
        // Display score
        String scoreText = "Final Score: " + finalScore;
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        fm = g2d.getFontMetrics();
        int scoreTextWidth = fm.stringWidth(scoreText);
        
        // Position score below the title
        int scoreX = (panelWidth - scoreTextWidth) / 2;
        int scoreY = y + textHeight + 30;
        
        g2d.drawString(scoreText, scoreX, scoreY);
    }
}
