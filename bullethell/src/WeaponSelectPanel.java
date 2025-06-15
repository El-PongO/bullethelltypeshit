import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class WeaponSelectPanel extends JPanel {
    public interface WeaponSelectListener {
        void onWeaponSelected(String weaponName);
    }

    public WeaponSelectPanel(WeaponSelectListener listener) {
        setLayout(new GridLayout(1, 3, 20, 20));
        
        // Create fancy buttons with icons if available
        JButton revolverBtn = createWeaponButton("Revolver", "/Assets/player/Guns/revolver.png");
        JButton shotgunBtn = createWeaponButton("Shotgun", "/Assets/player/Guns/shotgun.png");
        JButton placeholderBtn = new JButton("Coming Soon");
        placeholderBtn.setEnabled(false);

        ActionListener buttonListener = e -> {
            JButton src = (JButton) e.getSource();
            listener.onWeaponSelected(src.getText());
        };
        
        revolverBtn.addActionListener(buttonListener);
        shotgunBtn.addActionListener(buttonListener);
        
        add(revolverBtn);
        add(shotgunBtn);
        add(placeholderBtn);
    }
    
    private JButton createWeaponButton(String name, String iconPath) {
        JButton button = new JButton(name);
        try {
            BufferedImage image = ImageIO.read(getClass().getResource(iconPath));
            if (image != null) {
                Image scaledImage = image.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledImage));
                button.setHorizontalTextPosition(SwingConstants.CENTER);
                button.setVerticalTextPosition(SwingConstants.BOTTOM);
            }
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Could not load icon for " + name + ": " + e.getMessage());
        }
        return button;
    }
}
