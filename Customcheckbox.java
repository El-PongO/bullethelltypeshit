import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicCheckBoxUI;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Customcheckbox extends JCheckBox {
    private boolean isHovered = false;

    public Customcheckbox(String text) {
        super(text);
        setOpaque(false);
        setUI(new CustomCheckBoxUI());

        // Mouse listeners to detect hover state
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    public boolean isHovered() {
        return isHovered;
    }

    private class CustomCheckBoxUI extends BasicCheckBoxUI {
        private final Color BOX_BORDER_COLOR = Color.WHITE;
        private final Color BOX_FILL_COLOR = new Color(60, 130, 200);
        private final Color GLOW_COLOR = new Color(173, 216, 230, 120); // Light blue glow
        private final int BOX_SIZE = 16;

        @Override
        public void paint(Graphics g, JComponent c) {
            Customcheckbox checkbox = (Customcheckbox) c;
            ButtonModel model = checkbox.getModel();
            FontMetrics fm = g.getFontMetrics();
            int textX = BOX_SIZE + 8;
            int textY = (c.getHeight() + fm.getAscent() - fm.getDescent()) / 2;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int boxY = (c.getHeight() - BOX_SIZE) / 2;

            // Glow effect (on hover only)
            if (checkbox.isHovered()) {
                int glowSize = 6;
                g2.setColor(GLOW_COLOR);
                g2.fillOval(-glowSize / 2, boxY - glowSize / 2, BOX_SIZE + glowSize, BOX_SIZE + glowSize);
            }

            // Draw checkbox background
            g2.setColor(new Color(255, 255, 255, 40));
            g2.fillRect(0, boxY, BOX_SIZE, BOX_SIZE);

            // Draw border
            g2.setColor(BOX_BORDER_COLOR);
            g2.drawRect(0, boxY, BOX_SIZE, BOX_SIZE);

            // Draw checkmark if selected
            if (model.isSelected()) {
                g2.setColor(BOX_FILL_COLOR);
                g2.fillRect(3, boxY + 3, BOX_SIZE - 5, BOX_SIZE - 5);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(4, boxY + 8, 7, boxY + 11);
                g2.drawLine(7, boxY + 11, 12, boxY + 5);
            }

            // Draw label text
            g2.setColor(checkbox.getForeground());
            g2.setFont(checkbox.getFont());
            g2.drawString(checkbox.getText(), textX, textY);

            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            AbstractButton b = (AbstractButton) c;
            FontMetrics fm = c.getFontMetrics(b.getFont());
            int width = BOX_SIZE + 8 + fm.stringWidth(b.getText());
            int height = Math.max(BOX_SIZE, fm.getHeight());
            return new Dimension(width, height);
        }
    }
}
