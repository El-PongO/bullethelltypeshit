import java.awt.*;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class Settingmenu extends JPanel{
    private Rectangle[] categoryBounds; // Array to hold the bounding rectangles for each category (used for hover/click detection)
    private int hoveredIndex = -1; // cek tab yang di hover
    private int selectedIndex = -1; // current active tab
    private String[] categories = {"Video", "Audio", "Controls", "Others"}; // list isinya
    
    private JCheckBox fpsCheckbox; // Checkbox for FPS counter
    private JCheckBox devmodeCheckbox; // Checkbox for dev mode
    private JCheckBox holdweaponCheckbox; // Checkbox for hold weapon
    private JCheckBox disableupgradeCheckbox; // Checkbox for disable upgrade
    
    public Settingmenu() {
        setLayout(null);
        categoryBounds = new Rectangle[categories.length];

        // Video settings
        // Audio settings
        // Controls settings
        // Others settings
        Create_Settings_Others();
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

        if (selectedIndex == 0){
            setvisibleoption("Video");
        }else if (selectedIndex == 1){
            setvisibleoption("Audio");
        }else if (selectedIndex == 2){
            setvisibleoption("Controls");
        }else if (selectedIndex == 3){
            setvisibleoption("Others");
            // Draw the FPS checkbox
            int checkboxY = rowY + 50; // Adjust Y position as needed
            fpsCheckbox.setBounds(20, checkboxY, 200, 30); 
            fpsCheckbox.setVisible(true);

            // Draw the dev mode checkbox
            devmodeCheckbox.setBounds(20, checkboxY + 20, 200, 30); 
            devmodeCheckbox.setVisible(true);

            // Draw the hold weapon checkbox
            holdweaponCheckbox.setBounds(20, checkboxY + 40, 320, 30); 
            holdweaponCheckbox.setVisible(true);

            // Draw the disable upgrade checkbox
            disableupgradeCheckbox.setBounds(20, checkboxY + 60, 200, 30); 
            disableupgradeCheckbox.setVisible(true);
        }
    }

    // Set the active tab by matching a given name
    public void setActiveTab(String tabName) {
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equalsIgnoreCase(tabName)) {
                selectedIndex = i;
                fpsCheckbox.setVisible(categories[i].equals("Others"));
                return;
            }
        }
    }

    public void setvisibleoption(String tabname){
        switch (tabname){
            case "Video":
                // Visible for video settings
                
                // Invisible for video settings
                fpsCheckbox.setVisible(false);
                devmodeCheckbox.setVisible(false);
                holdweaponCheckbox.setVisible(false);
                disableupgradeCheckbox.setVisible(false);
                break;
            case "Audio":
                // Visible for audio settings
                
                // Invisible for audio settings
                fpsCheckbox.setVisible(false);
                devmodeCheckbox.setVisible(false);
                holdweaponCheckbox.setVisible(false);
                disableupgradeCheckbox.setVisible(false);
                break;
            case "Controls":
                // Visible for controls settings
                
                // Invisible for controls settings
                fpsCheckbox.setVisible(false);
                devmodeCheckbox.setVisible(false);
                holdweaponCheckbox.setVisible(false);
                disableupgradeCheckbox.setVisible(false);
                break;
            case "Others":
                // Visible for others settings
                fpsCheckbox.setVisible(true);
                devmodeCheckbox.setVisible(true);
                holdweaponCheckbox.setVisible(true);
                disableupgradeCheckbox.setVisible(true);

                // Invisible for others settings
                break;
            case "quit":
                fpsCheckbox.setVisible(false);
                devmodeCheckbox.setVisible(false);
                holdweaponCheckbox.setVisible(false);
                disableupgradeCheckbox.setVisible(false);
                break;
        }
    }

    public JCheckBox getFpsCheckbox() {
        return fpsCheckbox;
    }

    public JCheckBox getDevCheckbox() {
        return devmodeCheckbox;
    }

    public JCheckBox getHoldWeaponCheckbox() {
        return holdweaponCheckbox;
    }

    public JCheckBox getDisableUpgradeCheckbox() {
        return disableupgradeCheckbox;
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

    public void Create_Settings_Others(){
        Create_fpsCheckbox();
        Create_devmodeCheckbox();
        Create_holdweaponCheckbox();
        Create_disableupgradeCheckbox();
        add(fpsCheckbox);
        add(devmodeCheckbox);
        add(holdweaponCheckbox);
        add(disableupgradeCheckbox);
    }

    public void Create_fpsCheckbox() {
        fpsCheckbox = new Customcheckbox("Show FPS Counter");
        fpsCheckbox.setFont(new Font("Arial", Font.PLAIN, 16));
        fpsCheckbox.setForeground(Color.WHITE);
        fpsCheckbox.setBackground(new Color(28, 51, 92)); // Match the background color
        fpsCheckbox.setFocusPainted(false);
        fpsCheckbox.setVisible(false); // Initially hidden
        fpsCheckbox.setSelected(false); // Default state
    }

    public void Create_devmodeCheckbox() { // Unused method
        devmodeCheckbox = new Customcheckbox("Cheats :)");
        devmodeCheckbox.setFont(new Font("Arial", Font.PLAIN, 16));
        devmodeCheckbox.setForeground(Color.WHITE);
        devmodeCheckbox.setBackground(new Color(28, 51, 92)); // Match the background color
        devmodeCheckbox.setFocusPainted(false);
        devmodeCheckbox.setVisible(false); // Initially hidden
        devmodeCheckbox.setSelected(false); // Default state
    }

    public void Create_holdweaponCheckbox() { // Unused method
        holdweaponCheckbox = new Customcheckbox("Enable hold (FIRE_KEY) to shoot");
        holdweaponCheckbox.setFont(new Font("Arial", Font.PLAIN, 16));
        holdweaponCheckbox.setForeground(Color.WHITE);
        holdweaponCheckbox.setBackground(new Color(28, 51, 92)); // Match the background color
        holdweaponCheckbox.setFocusPainted(false);
        holdweaponCheckbox.setVisible(false); // Initially hidden
        holdweaponCheckbox.setSelected(false); // Default state
    }

    public void Create_disableupgradeCheckbox() { // Unused method
        disableupgradeCheckbox = new Customcheckbox("Disable upgrade");
        disableupgradeCheckbox.setFont(new Font("Arial", Font.PLAIN, 16));
        disableupgradeCheckbox.setForeground(Color.WHITE);
        disableupgradeCheckbox.setBackground(new Color(28, 51, 92)); // Match the background color
        disableupgradeCheckbox.setFocusPainted(false);
        disableupgradeCheckbox.setVisible(false); // Initially hidden
        disableupgradeCheckbox.setSelected(false); // Default state
    }
}
