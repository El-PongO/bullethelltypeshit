import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Button {
    public JButton new_button;
    String nama;
    private static CursorManager cursorManager;
    private static String glowCursorName;
    private static Component targetComponent;

    public Button(String nama){
        this.nama=nama;
        new_button = new JButton(nama);
        // new_button.setContentAreaFilled(false);
        // new_button.setBorderPainted(false);
        new_button.setBackground(Color.DARK_GRAY);
        new_button.setForeground(Color.WHITE); // text color
        new_button.setFont(new Font("Arial", Font.BOLD, 20)); 
        new_button.setFocusPainted(false); // ilangin focus border
        new_button.setBorder(BorderFactory.createLineBorder(Color.WHITE)); // kasi border putih

        new_button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (cursorManager != null && glowCursorName != null && targetComponent != null) {
                    cursorManager.setCursor(targetComponent, glowCursorName); // Set "pointer" cursor
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (cursorManager != null && targetComponent != null) {
                    if (!GameplayPanel.gameActive) { // Only reset if not playing
                        cursorManager.setCursor(targetComponent, "cursor"); // Reset to default custom cursor
                    }
                }
            }
        });
    }

    public static void setupGlowCursor(CursorManager cm, String cursorName, Component component) {
        cursorManager = cm;
        glowCursorName = cursorName;
        targetComponent = component;
    }

    public void setBound(int x, int y, int width, int height){
        new_button.setBounds(x, y, width, height);
    }

    public void setInvisible(){
        new_button.setVisible(false);
    }
    
    public void setVisible(){
        new_button.setVisible(true);
    }
    
    public void repaint(){
        repaint();
    }
    
    public void remove(){
        new_button.remove(new_button);
    }

    public void addActionListener(ActionListener listener){
        new_button.addActionListener(listener);
    }

    public Rectangle getBounds(){
        return new_button.getBounds();
    }

    public void alignleft(){
        // new_button.setHorizontalAlignment(SwingConstants.LEFT);
    }
}
