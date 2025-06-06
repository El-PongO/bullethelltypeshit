import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class Music {
    private Clip clip;
    private FloatControl gainControl;
    private long pausePosition = 0;
    private boolean isLooping = false;
    protected static List<Music> allInstances = new java.util.ArrayList<>();

    public Music() {
        allInstances.add(this);
    }

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
        System.out.println("Playing music in loop");
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
        allInstances.remove(this);
    }

    public void setVolume(float volume) {
        if (gainControl != null) {
            // volume: 0.0 (mute) to 1.0 (max)
            float min = gainControl.getMinimum();
            float max = gainControl.getMaximum();
            float gain = min + (max - min) * volume;
            gainControl.setValue(gain);
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
                        if (isGloballyMuted()) {
                            gainControl.setValue(min); // Set to minimum if globally muted
                            clip.stop();
                            clip.setMicrosecondPosition(0); // Reset the clip position
                            return;
                        }
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
                    clip.setMicrosecondPosition(0); // Reset the clip position after stopping
                } catch (InterruptedException ignored) {}
            }).start();
        }
    }

    public static boolean isGloballyMuted() {
        return MusicManager.getGlobalVolume() <= 0.001f;
    }

    public static void updateAllInstanceVolumes(float globalVolume) {
        for (Music music : allInstances) {
            if (music.gainControl != null) {
                music.setVolume(globalVolume);
            }
        }
    }
}
