import java.awt.*;
import java.awt.event.*;
import java.net.URI;

public class MainMenu {
    private Hyperlink hyperlink; // New Hyperlink class to handle the link

    public MainMenu() {
        hyperlink = new Hyperlink("Visit Page");
    }

    public void draw(Graphics g, int panelWidth, int panelHeight) {
        Graphics2D g2d = (Graphics2D) g;
        // Background color
        g2d.setColor(new Color(28, 51, 92));
        g2d.fillRect(0, 0, panelWidth, panelHeight);

        // Title text
        String title = "Bullet Hell";
        g2d.setFont(new Font("Arial", Font.BOLD, 64));
        g2d.setColor(Color.WHITE);

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(title);
        int textHeight = fm.getAscent();

        // Position title near the top center
        int x = ((panelWidth - textWidth) / 2) + 10; // (lebar layar - lebar teks) / 2 + 10
        int y = (panelHeight / 2) - 50;  // 1/2 from top + 50

        g2d.drawString(title, x, y);
        
        // Hyperlink (ni hyperlink cuma ngetes doang)
        // Position the hyperlink at the bottom right with a little margin
        int marginRight = 20;  // Margin from the right side
        int marginBottom = 20; // Margin from the bottom
        int hyperlinkWidth = g2d.getFontMetrics().stringWidth(hyperlink.getText());
        int hyperlinkHeight = g2d.getFontMetrics().getAscent();

        // Set the x and y position for the hyperlink
        int hyperlinkX = panelWidth - hyperlinkWidth - marginRight;
        int hyperlinkY = panelHeight - marginBottom;

        // Call draw with additional yPosition argument
        hyperlink.draw(g, (int) (panelWidth*1.88), panelHeight, hyperlinkY);
    }

    // function buat hyperlink pas linknya nanti di klik 
    public void handleMouseClick(MouseEvent e) {
        if (hyperlink.isClicked(e.getPoint())) {
            try {
                Desktop.getDesktop().browse(new URI("https://en.wikipedia.org/wiki/Bullet_Hell"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}