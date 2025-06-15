public class MusicManager {
    private static float globalVolume = 1.0f;

    public static void setGlobalVolume(float volume) {
        globalVolume = Math.max(0, Math.min(1, volume));
        updateAllMusicVolumes();
    }

    public static float getGlobalVolume() {
        return globalVolume;
    }

    private static void updateAllMusicVolumes() {
        // This will be called whenever volume changes to update all music instances
        Music.updateAllInstanceVolumes(globalVolume);
    }
} 
