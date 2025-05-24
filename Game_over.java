import java.awt.*;
public class Game_over {
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
        int y = (panelHeight / 2) - 50;  // 1/2 from top + 50

        g2d.drawString(title, x, y);
    }
}
