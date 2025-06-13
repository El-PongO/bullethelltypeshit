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
        this.nama = nama;
        new_button = new JButton(nama) {
            private boolean hovered = false;
    
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                        if (cursorManager != null && glowCursorName != null && targetComponent != null) {
                            cursorManager.setCursor(targetComponent, glowCursorName);
                        }
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                        if (cursorManager != null && targetComponent != null) {
                            if (!GameplayPanel.gameActive) {
                                cursorManager.setCursor(targetComponent, "cursor");
                            }
                        }
                    }
                });
            }
    
            @Override
            protected void paintComponent(Graphics g) {
                // Paint background but skip default text
                if (isOpaque()) {
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
                }

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                FontMetrics fm = getFontMetrics(getFont());
                String text = getText();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                int textX = (getWidth() - textWidth) / 2;
                int textY = (getHeight() + textHeight) / 2 - fm.getDescent();

                if (hovered) {
                    Color base = getForeground();
                    Color glow = new Color(
                        Math.min(255, base.getRed() + 60),
                        Math.min(255, base.getGreen() + 60),
                        Math.min(255, base.getBlue() + 60),
                        90
                    );
                    int glowRadius = 8;
                    for (int i = glowRadius; i > 0; i--) {
                        float alpha = (float) (glow.getAlpha()) / 255f * (i / (float) glowRadius);
                        g2.setColor(new Color(glow.getRed(), glow.getGreen(), glow.getBlue(), (int)(alpha * 255)));
                        g2.drawString(text, textX - i/2, textY + i/2);
                    }
                }

                g2.setColor(getForeground());
                g2.setFont(getFont());
                g2.drawString(text, textX, textY);
                g2.dispose();
            }
    
            @Override
            public boolean contains(int x, int y) {
                FontMetrics fm = getFontMetrics(getFont());
                String text = getText();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                int textX = (getWidth() - textWidth) / 2;
                int textY = (getHeight() + textHeight) / 2 - fm.getDescent();
                int padding = 10;
                return x >= textX - padding && x <= textX + textWidth + padding
                    && y >= textY - textHeight - padding && y <= textY + padding;
            }
        };
        new_button.setContentAreaFilled(false);
        new_button.setBorderPainted(false);
        new_button.setForeground(Color.WHITE);
        new_button.setFont(new Font("Arial", Font.BOLD, 20)); 
        new_button.setFocusPainted(false);
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
