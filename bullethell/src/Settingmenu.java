import java.awt.*;

import javax.swing.JCheckBox;
public class Settingmenu {
    private Rectangle[] categoryBounds; // Array to hold the bounding rectangles for each category (used for hover/click detection)
    private int hoveredIndex = -1; // cek tab yang di hover
    private int selectedIndex = -1; // current active tab
    private String[] categories = {"Video", "Audio", "Controls", "Others"}; // list isinya
    
    private JCheckBox fpsCheckbox; // Checkbox for FPS counter
    
    public Settingmenu() {
        categoryBounds = new Rectangle[categories.length];
        // Initialize the FPS checkbox
        fpsCheckbox = new JCheckBox("Show FPS Counter");
        fpsCheckbox.setBounds(20, 150, 200, 30); // Position in the "Others" settings
        fpsCheckbox.setForeground(Color.WHITE);
        fpsCheckbox.setBackground(new Color(28, 51, 92)); // Match the background color
        fpsCheckbox.setFocusPainted(false);
        fpsCheckbox.setVisible(false); // Initially hidden
    }

    public void draw(Graphics g, int panelWidth, int panelHeight) {
        Graphics2D g2d = (Graphics2D) g;

        // Background color
        g2d.setColor(new Color(28, 51, 92));
        g2d.fillRect(0, 0, panelWidth, panelHeight);

        // Title text
        String title = "Settings";
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        g2d.setColor(Color.WHITE);

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(title);
        int textHeight = fm.getAscent();
        int y = (panelHeight / 5) - 80;

        int x = (panelWidth - textWidth) / 2;
        g2d.drawString(title, x, y);

        // Draw category buttons
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        FontMetrics catMetrics = g2d.getFontMetrics();

        int numCols = categories.length;
        int colWidth = panelWidth / numCols;
        int rowY = y + 50;

        for (int i = 0; i < numCols; i++) {
            int catTextWidth = catMetrics.stringWidth(categories[i]);
            int catX = i * colWidth + (colWidth - catTextWidth) / 2;

            // Store bounds for mouse detection
            int boxX = i * colWidth;
            int boxY = rowY - 25;
            int boxWidth = colWidth;
            int boxHeight = 40;

            categoryBounds[i] = new Rectangle(boxX, boxY, boxWidth, boxHeight);

            // Highlight on hover
            if (i == hoveredIndex) {
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.fillRect(boxX, boxY, boxWidth, boxHeight);
            }

            // Draw selected underline
            if (i == selectedIndex) {
                g2d.setColor(Color.YELLOW);
                g2d.fillRect(boxX + 10, boxY + boxHeight - 5, boxWidth - 20, 3);
            }

            g2d.setColor(Color.WHITE);
            g2d.drawString(categories[i], catX, rowY);

            // Optional dividers
            if (i < numCols - 1) {
                int lineX = (i + 1) * colWidth;
                g2d.drawLine(lineX, rowY - 20, lineX, rowY + 10);
            }
        }

        // Border lines
        g2d.drawLine(0, rowY + 15, panelWidth, rowY + 15);
        g2d.drawLine(0, rowY - 25, panelWidth, rowY - 25);
    }

    // Set the active tab by matching a given name
    public void setActiveTab(String tabName) {
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equalsIgnoreCase(tabName)) {
                selectedIndex = i;
                return;
            }
        }
    }

    // Update the hovered index when the mouse moves
    public void handleMouseMoved(int mouseX, int mouseY) {
        hoveredIndex = -1;
        for (int i = 0; i < categoryBounds.length; i++) {
            if (categoryBounds[i].contains(mouseX, mouseY)) {
                hoveredIndex = i;
                break;
            }
        }
    }

    public void handleMouseClicked(int mouseX, int mouseY) {
        for (int i = 0; i < categoryBounds.length; i++) {
            if (categoryBounds[i].contains(mouseX, mouseY)) {
                selectedIndex = i;
                break;
            }
        }
    }
}
