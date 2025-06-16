import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * A simple fallback hero selection panel with buttons
 */
public class SimpleHeroSelectPanel extends JPanel implements IHeroSelectPanel {
    private static final Color BACKGROUND_COLOR = new Color(30, 34, 42); // Dark grey-bluish

    /**
     * Creates a new simple hero selection panel
     * 
     * @param listener The hero selection listener
     */
    public SimpleHeroSelectPanel(IHeroSelectPanel.HeroSelectListener listener) {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("SELECT YOUR HERO", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        
        String[] heroTypes = {"Gunslinger", "Bomber", "Brute", "Vampire"};
        Color[] heroColors = {
            new Color(100, 149, 237), // Cornflower Blue for Gunslinger
            new Color(220, 20, 60),   // Crimson for Bomber
            new Color(34, 139, 34),   // Forest Green for Brute
            new Color(128, 0, 128)    // Purple for Vampire
        };
        
        for (int i = 0; i < heroTypes.length; i++) {
            final String heroType = heroTypes[i];
            JButton button = new JButton(heroType);
            button.setBackground(heroColors[i]);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            // Add hover effect
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBorder(null);
                }
            });
            
            button.addActionListener(e -> listener.onHeroSelected(heroType));
            buttonsPanel.add(button);
        }
        
        add(buttonsPanel, BorderLayout.CENTER);
        
        // Add description
        JLabel infoLabel = new JLabel("Select a hero to begin your adventure", JLabel.CENTER);
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
        add(infoLabel, BorderLayout.SOUTH);
    }
}
