import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.*;
import javax.swing.Timer;

public class FPScounter extends JLabel{ // untuk sementara masih belum dipake
    protected int frames = 0;
    protected long lastTime = System.currentTimeMillis();
    protected int currentFPS = 0;

    protected JLabel fpsLabel;
    public FPScounter(String nama) {
        setText(nama + "0");
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.PLAIN, 12));
        setHorizontalAlignment(SwingConstants.RIGHT);

        // Timer to update FPS label every second
        new Timer(1000, e -> {
            setText(nama + currentFPS);
            currentFPS = frames;
            frames = 0;
        }).start();
    }

    public void setVisible(boolean visible) {
        if (visible) {
            fpsLabel.setVisible(true);
        } else {
            fpsLabel.setVisible(false);
        }
    }

    public void frameRendered() {
        frames++;
    }

    public int getFPS() {
        return currentFPS;
    }
}
