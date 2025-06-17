import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class WeaponSelectPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(30, 34, 42); // Dark gray background
    private static String lastSelectedWeapon = null; // Track last selected weapon
    private static final String[] AVAILABLE_WEAPONS = {
        "Revolver", "Shotgun", "SMG", "Glock", "Sniper", "Rocket Launcher"
    };
    JPanel[] weaponButtons;

    // Button dimensions and spacing
    int buttonWidth = 200;
    int buttonHeight = 200;
    int spacing = 50;
    private WeaponSelectListener listener;

    public interface WeaponSelectListener {
        void onWeaponSelected(String weaponName);
    }

    public WeaponSelectPanel(WeaponSelectListener listener) {
        setLayout(null);
        setBackground(BACKGROUND_COLOR);
        this.listener = listener;
        this.weaponButtons = new JPanel[3];
        
        // Add component listener for layout
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                layoutButtons();
            }

            public void componentShown(java.awt.event.ComponentEvent evt) {
                refreshWeapons(); // Randomize weapons when panel is shown
            }
        });
    }

    private void refreshWeapons() {
        // Remove existing Container panels
        for (JPanel panel : weaponButtons) {
            if (panel != null) {
                remove(panel);
            }
        }

        // Get new random weapons
        String[] randomWeapons = getRandomWeapons(3, lastSelectedWeapon);

        // Create new container panels for each weapon
        for (int i = 0; i < 3; i++) {
            String weaponName = randomWeapons[i];
            String iconPath = getWeaponIconPath(weaponName);
            JPanel container = createWeaponButton(weaponName, iconPath);
            weaponButtons[i] = container;
            JButton button = (JButton) container.getComponent(0);
            button.addActionListener(e -> {
                lastSelectedWeapon = button.getText();
                listener.onWeaponSelected(lastSelectedWeapon);
            });
            add(container);
        }

        layoutButtons();
        revalidate();
        repaint();
    }

    private void layoutButtons() {
        int totalHeight = buttonHeight + 100; // Added extra height for description
        int centerY = getHeight()/2 - totalHeight/2;
        for (int i = 0; i < weaponButtons.length; i++) {
            if (weaponButtons[i] != null) {
                int x = getWidth()/2 + (i-1)*(buttonWidth + spacing) - buttonWidth/2;
                weaponButtons[i].setBounds(x, centerY, buttonWidth, totalHeight);
            }
        }
    }

    private String[] getRandomWeapons(int count, String exclude) {
        List<String> available = new ArrayList<>(Arrays.asList(AVAILABLE_WEAPONS));
        if (exclude != null) {
            available.remove(exclude);
        }
        Collections.shuffle(available);
        return available.subList(0, Math.min(count, available.size())).toArray(new String[0]);
    }

    private String getWeaponIconPath(String weaponName) {
        switch (weaponName) {
            case "Revolver":
                return "/Assets/player/Guns/revolver.png";
            case "Shotgun":
                return "/Assets/player/Guns/shotgun.png";
            case "SMG":
                return "/Assets/player/Guns/smg1.png";
            case "Glock":
                return "/Assets/player/Guns/glock.png";
            case "Sniper":
                return "/Assets/player/Guns/sniper.png";
            case "Rocket Launcher":
                return "/Assets/player/Guns/m20.png";
            default:
                return null; // No icon available
        }
    }

    private JPanel createWeaponButton(String name, String iconPath) {
        // Create container panel for button and description
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
    
        // Create weapon button
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(0, 0, 0, 0));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
    
        // Style the button
        button.setText(name);
        button.setContentAreaFilled(false);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        // Load weapon icon
        if (iconPath != null) {
            try {
                BufferedImage image = ImageIO.read(getClass().getResourceAsStream(iconPath));
                if (image != null) {
                    Image scaledImage = image.getScaledInstance(128, 128, Image.SCALE_SMOOTH);
                    button.setIcon(new ImageIcon(scaledImage));
                    button.setHorizontalTextPosition(SwingConstants.CENTER);
                    button.setVerticalTextPosition(SwingConstants.BOTTOM);
                }
            } catch (Exception e) {
                System.out.println("Could not load icon for " + name + ": " + e.getMessage());
            }
        }
    
        // Create description label
        JLabel descLabel = new JLabel(getWeaponDescription(name));
        descLabel.setForeground(Color.WHITE);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        // Add components to container
        container.add(button);
        container.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing
        container.add(descLabel);
    
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            }
        });
    
        return container;
    }

    private String getWeaponDescription(String weaponName) {
        switch (weaponName) {
            case "Revolver":
                return String.format("<html><div style='text-align: center;'>" +
                                   "Ammo: 7<br>" +
                                   "Damage: 150<br>" +
                                   "Fire Rate: 433ms<br>" +
                                   "Reload Time: 2.7s<br>" +
                                   "<i>A reliable sidearm with good stopping power</i></div></html>");
            case "Shotgun":
                return String.format("<html><div style='text-align: center;'>" +
                                   "Ammo: 8<br>" +
                                   "Damage: 100 x 8 pellets<br>" +
                                   "Fire Rate: 1000ms<br>" +
                                   "Reload Time: 3.0s<br>" +
                                   "<i>Devastating at close range with spread shot</i></div></html>");
            case "SMG":
                return String.format("<html><div style='text-align: center;'>" +
                                   "Ammo: 30<br>" +
                                   "Damage: 30<br>" +
                                   "Fire Rate: 80ms<br>" +
                                   "Reload Time: 2.5s<br>" +
                                   "<i>High rate of fire automatic weapon</i></div></html>");
            case "Glock":
                return String.format("<html><div style='text-align: center;'>" +
                                   "Ammo: 18<br>" +
                                   "Damage: 50<br>" +
                                   "Fire Rate: 225ms<br>" +
                                   "Reload Time: 2.0s<br>" +
                                   "<i>Fast-firing pistol with good capacity</i></div></html>");
            case "Sniper":
                return String.format("<html><div style='text-align: center;'>" +
                                   "Ammo: 5<br>" +
                                   "Damage: 300<br>" +
                                   "Fire Rate: 1600ms<br>" +
                                   "Reload Time: 3.0s<br>" +
                                   "<i>High damage rifle that can penetrate up to 3 targets</i></div></html>");
            case "Rocket Launcher":
                return String.format("<html><div style='text-align: center;'>" +
                                   "Ammo: 1<br>" +
                                   "Damage: 500<br>" +
                                   "Fire Rate: N/A<br>" +
                                   "Reload Time: 3.2s<br>" +
                                   "<i>Explosive weapon with splash damage</i></div></html>");
            default:
                return "";
        }
    }
}
