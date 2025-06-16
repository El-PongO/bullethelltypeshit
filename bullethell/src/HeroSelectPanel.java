
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class HeroSelectPanel extends JPanel implements IHeroSelectPanel {
    private static final Color BACKGROUND_COLOR = new Color(30, 34, 42); // Dark grey-bluish

    private PlayerInfoPanel[] playerPanels;
    private String[] playerTypes = {"Gunslinger", "Bomber", "Brute", "Vampire"};
    private int selectedIndex = -1;
    
    public HeroSelectPanel(HeroSelectListener listener) {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("SELECT YOUR HERO", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel playersPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        playersPanel.setBackground(BACKGROUND_COLOR);
        playerPanels = new PlayerInfoPanel[playerTypes.length];
        
        // Create player panels
        for (int i = 0; i < playerTypes.length; i++) {
            final int index = i;
            playerPanels[i] = new PlayerInfoPanel(playerTypes[i]);
            playerPanels[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            // Add click listener to each panel
            playerPanels[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectPlayer(index);
                    listener.onHeroSelected(playerTypes[index]);
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    playerPanels[index].setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    if (selectedIndex != index) {
                        playerPanels[index].setBorder(new EmptyBorder(15, 15, 15, 15));
                    }
                }
            });
            
            playersPanel.add(playerPanels[i]);
        }
        
        add(playersPanel, BorderLayout.CENTER);
        
        // Add description panel at the bottom
        JPanel descPanel = new JPanel();
        descPanel.setBackground(BACKGROUND_COLOR);
        descPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        descPanel.setLayout(new BorderLayout());
        
        JLabel infoLabel = new JLabel("Click on a hero to select", JLabel.CENTER);
        infoLabel.setForeground(Color.WHITE);
        descPanel.add(infoLabel, BorderLayout.CENTER);
        
        add(descPanel, BorderLayout.SOUTH);
    }
    
    private void selectPlayer(int index) {
        // Reset border of previously selected player
        if (selectedIndex >= 0) {
            playerPanels[selectedIndex].setBorder(new EmptyBorder(15, 15, 15, 15));
        }
        
        // Set selected index and highlight selected player
        selectedIndex = index;
        playerPanels[selectedIndex].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
        
        // Update all panels
        revalidate();
        repaint();
    }
}
