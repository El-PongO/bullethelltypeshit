import java.awt.*;

public class Hyperlink {
    private String text; // ini cuma ngetes
    private Rectangle bounds;
    private final Color linkColor = Color.CYAN;

    public Hyperlink(String text) {
        this.text = text;
    }

    public void draw(Graphics g, int panelWidth, int panelHeight, int yPosition) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        g2d.setColor(linkColor);

        FontMetrics fm = g2d.getFontMetrics();
        int x = (panelWidth - fm.stringWidth(text)) / 2;
        int y = yPosition;

        g2d.drawString(text, x, y);

        // Underline the hyperlink
        g2d.drawLine(x, y + 2, x + fm.stringWidth(text), y + 2);

        // Define the clickable area
        bounds = new Rectangle(x, y - fm.getAscent(), fm.stringWidth(text), fm.getHeight());
    }

    public boolean isClicked(Point p) {
        return bounds != null && bounds.contains(p);
    }

    public String getText() {
        return text;
    }
}
