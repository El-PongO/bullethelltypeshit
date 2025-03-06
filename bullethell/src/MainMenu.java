import java.awt.*;

public class MainMenu {
    public void draw(Graphics g, int width, int height){
        g.setColor(new Color(28, 51, 92));
        g.fillRect(0, 0, width, height);

        g.setColor(Color.WHITE); // masak perlu tak jelasin ini apa, GBLOG
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("Bullet hell", 380, 300);
        g.setFont(new Font("Arial", Font.PLAIN, 30));
        g.drawString("Click to Start", 420, 400);
    }
}
