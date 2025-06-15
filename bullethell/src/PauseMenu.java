import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.*;

public class PauseMenu extends JPanel {
    private Button continueButton = new Button("Continue");
    private Button settingsButton = new Button("Settings"); 
    private Button leaveButton = new Button("Leave");
    
    public PauseMenu() {
        setLayout(null);
        initButtons();
        setOpaque(false);
        setVisibility(false);

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                boolean overButton = false;
                for (Component c : getComponents()) {
                    if (c.isVisible() && c.getBounds().contains(e.getPoint())) {
                        overButton = true;
                        break;
                    }
                }
                if (!overButton) {
                    // Find the MainPanel and set cursor to "cursor"
                    Container parent = getParent();
                    while (parent != null && !(parent instanceof MainPanel)) {
                        parent = parent.getParent();
                    }
                    if (parent instanceof MainPanel) {
                        ((MainPanel)parent).cursormanager.setCursor((MainPanel)parent, "cursor");
                    }
                }
            }
        });
    }
    
    private void initButtons() {
        // Add buttons to panel
        add(continueButton.new_button);
        add(settingsButton.new_button);
        add(leaveButton.new_button);
    }
    
    public void draw(Graphics g, int panelWidth, int panelHeight) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Update panel size if needed
        setSize(panelWidth, panelHeight);
        
        // Enable antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Semi-transparent dark background
        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fillRect(0, 0, panelWidth, panelHeight);
        
        // Pause text
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        g2d.setColor(Color.WHITE);
        String text = "PAUSED";
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (panelWidth - fm.stringWidth(text)) / 2;
        int textY = panelHeight / 3;
        g2d.drawString(text, textX, textY);
        
        // Position and update buttons
        int buttonWidth = 200;
        int buttonHeight = 50;
        int spacing = 20;
        int startY = textY + 50;
        
        continueButton.setBound((panelWidth - buttonWidth) / 2, startY, buttonWidth, buttonHeight);
        settingsButton.setBound((panelWidth - buttonWidth) / 2, startY + buttonHeight + spacing, buttonWidth, buttonHeight);
        leaveButton.setBound((panelWidth - buttonWidth) / 2, startY + 2 * (buttonHeight + spacing), buttonWidth, buttonHeight);
        
        // Ensure buttons are at the front
        setComponentZOrder(continueButton.new_button, 0);
        setComponentZOrder(settingsButton.new_button, 1);
        setComponentZOrder(leaveButton.new_button, 2);
    }
    
    public void setButtonListeners(Runnable onContinue, Runnable onSettings, Runnable onLeave) {
        continueButton.addActionListener(e -> onContinue.run());
        settingsButton.addActionListener(e -> onSettings.run());
        leaveButton.addActionListener(e -> onLeave.run());
    }

    public void setVisibility(boolean visible) {
        super.setVisible(visible);
        if(visible) {
            continueButton.setVisible();
            settingsButton.setVisible();
            leaveButton.setVisible();
        } else {
            continueButton.setInvisible();
            settingsButton.setInvisible();
            leaveButton.setInvisible();
        }
    }
}