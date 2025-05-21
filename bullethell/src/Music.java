import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class Music {
    private Clip clip;
    private FloatControl gainControl;
    private long pausePosition = 0;
    private boolean isLooping = false;

    public void load(String resourcePath) {
        try {
            URL musicURL = getClass().getResource(resourcePath);
            if (musicURL == null) {
                System.err.println("Music file not found: " + resourcePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicURL);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip != null) {
            clip.setMicrosecondPosition(0);
            clip.start();
            isLooping = false;
        }
    }

    public void loop() {
        if (clip != null) {
            clip.setMicrosecondPosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            isLooping = true;
        }
    }

    public void pause() {
        if (clip != null && clip.isRunning()) {
            pausePosition = clip.getMicrosecondPosition();
            clip.stop();
        }
    }

    public void resume() {
        if (clip != null && !clip.isRunning()) {
            clip.setMicrosecondPosition(pausePosition);
            if (isLooping) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.setMicrosecondPosition(0);
        }
    }

    public void close() {
        if (clip != null) {
            clip.close();
        }
    }

    public void fadeIn(int durationMillis) {
        if (clip != null && gainControl != null) {
            new Thread(() -> {
                try {
                    float min = gainControl.getMinimum();
                    float max = 0.0f;
                    int steps = 50;
                    int sleep = durationMillis / steps;
                    for (int i = 0; i <= steps; i++) {
                        float value = min + (max - min) * i / steps;
                        gainControl.setValue(value);
                        Thread.sleep(sleep);
                    }
                } catch (InterruptedException ignored) {}
            }).start();
        }
    }

    public void fadeOut(int durationMillis) {
        if (clip != null && gainControl != null) {
            new Thread(() -> {
                try {
                    float min = gainControl.getMinimum();
                    float max = gainControl.getValue();
                    int steps = 50;
                    int sleep = durationMillis / steps;
                    for (int i = 0; i <= steps; i++) {
                        float value = max - (max - min) * i / steps;
                        gainControl.setValue(value);
                        Thread.sleep(sleep);
                    }
                    clip.stop();
                } catch (InterruptedException ignored) {}
            }).start();
        }
    }
}
