import javax.swing.*;

public class HeroSelectTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("Hero Selection");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 500);

            HeroSelectPanel heroSelectPanel = new HeroSelectPanel(heroName -> {
                System.out.println("Selected hero: " + heroName);
            });

            frame.getContentPane().add(heroSelectPanel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
