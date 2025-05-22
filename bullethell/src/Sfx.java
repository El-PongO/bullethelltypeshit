import javax.sound.sampled.*;
import java.net.URL;
import java.util.*;

public class Sfx {
    private static final int MAX_POOL_SIZE = 100;

    private static class SoundClipPool {
        private final List<Clip> clipPool = new ArrayList<>();
        private final String filePath;

        public SoundClipPool(String filePath) {
            this.filePath = filePath;
            preloadClips();
        }

        private void preloadClips() {
            for (int i = 0; i < MAX_POOL_SIZE; i++) {
                Clip clip = createClip(filePath);
                if (clip != null) {
                    clipPool.add(clip);
                }
            }
        }

        private Clip createClip(String path) {
            try {
                URL soundURL = getClass().getResource(path);
                if (soundURL == null) {
                    System.err.println("Sound not found in resources: " + path);
                    return null;
                }

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                return clip;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public void play() {
            for (Clip clip : clipPool) {
                if (!clip.isRunning()) {
                    clip.setFramePosition(0);
                    clip.start();
                    return;
                }
            }

            // Fallback if all clips are busy
            Clip extraClip = createClip(filePath);
            if (extraClip != null) {
                extraClip.start();
            }
        }

        public void playWithRandomPitch() {
            float randomPitch = getRandomPitch();
            for (Clip clip : clipPool) {
                if (!clip.isRunning()) {
                    setPlaybackSpeed(clip, randomPitch);
                    clip.setFramePosition(0);
                    clip.start();
                    return;
                }
            }

            // Fallback if all clips are busy
            Clip extraClip = createClip(filePath);
            if (extraClip != null) {
                setPlaybackSpeed(extraClip, randomPitch);
                extraClip.start();
            }
        }

        private void setPlaybackSpeed(Clip clip, float pitch) {
            try {
                AudioFormat format = clip.getFormat();
                float newSampleRate = format.getSampleRate() * pitch;

                AudioFormat newFormat = new AudioFormat(
                    format.getEncoding(),
                    newSampleRate,
                    format.getSampleSizeInBits(),
                    format.getChannels(),
                    format.getFrameSize(),
                    newSampleRate,
                    format.isBigEndian()
                );

                // Reload the audio data with the new format
                URL soundURL = getClass().getResource(filePath); // Ensure you have access to the file path
                if (soundURL == null) {
                    System.err.println("Sound not found in resources: " + filePath);
                    return;
                }

                // Reopen the clip with the new format
                AudioInputStream originalStream = AudioSystem.getAudioInputStream(soundURL);
                AudioInputStream pitchShiftedStream = AudioSystem.getAudioInputStream(newFormat, originalStream);

                clip.close();
                clip.open(pitchShiftedStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private float getRandomPitch() {
            return 0.9f + new Random().nextFloat() * 0.2f; // Random value between 0.9 and 1.1
        }
    }

    private static final Map<String, SoundClipPool> soundPools = new HashMap<>();

    public static void load(String name, String resourcePath) {
        if (!soundPools.containsKey(name)) {
            soundPools.put(name, new SoundClipPool(resourcePath));
        }
    }

    public static void play(String name) {
        SoundClipPool pool = soundPools.get(name);
        if (pool != null) {
            pool.play();
        } else {
            System.err.println("Sound effect not loaded: " + name);
        }
    }

    public static void playWithRandomPitch(String name) {
        SoundClipPool pool = soundPools.get(name);
        if (pool != null) {
            pool.playWithRandomPitch(); // Play with random pitch
        } else {
            System.err.println("Sound effect not loaded: " + name);
        }
    }
}
