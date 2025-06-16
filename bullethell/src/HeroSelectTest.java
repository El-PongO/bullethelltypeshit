import javax.swing.*;
import java.awt.*;

/**
 * Test class to display the HeroSelectPanel with the new PlayerInfoPanel UI
 */
public class HeroSelectTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set the look and feel to the system default
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            JFrame frame = new JFrame("Hero Selection");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 500);
            
            // Create hero select panel with a listener
            HeroSelectPanel heroSelectPanel = new HeroSelectPanel(heroName -> {
                System.out.println("Selected hero: " + heroName);
            });
            
            frame.getContentPane().add(heroSelectPanel);
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }
}
