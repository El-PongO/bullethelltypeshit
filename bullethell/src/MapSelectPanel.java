import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MapSelectPanel extends JPanel {
    public interface MapSelectListener {
        void onMapSelected(int mapSetNumber);
    }

    public MapSelectPanel(MapSelectListener listener) {
        setBackground(Color.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title
        JLabel title = new JLabel("Pick A Map");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setForeground(new Color(40, 40, 120));
        add(title);

        add(Box.createRigidArea(new Dimension(0, 40)));

        // Map thumbnails
        JPanel mapsPanel = new JPanel();
        mapsPanel.setBackground(Color.WHITE);
        mapsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 60, 20));

        mapsPanel.add(createMapCard("bullethell/src/Assets/mapselectimg/map1img.jpg", 1, listener));
        mapsPanel.add(createMapCard("bullethell/src/Assets/mapselectimg/map2img.png", 2, listener));

        add(mapsPanel);
    }

    private JPanel createMapCard(String imagePath, int mapNumber, MapSelectListener listener) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setIcon(loadImage(imagePath, 300, 250));
        imageLabel.setPreferredSize(new Dimension(300, 250));
        card.add(imageLabel, BorderLayout.CENTER);

        String mapLabel = mapNumber == 1 ? "Old Meadow" : "Autism Dungeon";
        JLabel textLabel = new JLabel(mapLabel, SwingConstants.CENTER);
        textLabel.setFont(new Font("Arial", Font.BOLD, 18));
        textLabel.setForeground(new Color(60, 60, 60));
        textLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        card.add(textLabel, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                listener.onMapSelected(mapNumber);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
            }
        });

        return card;
    }

    private ImageIcon loadImage(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}
