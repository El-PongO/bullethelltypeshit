public class FPScounter { // untuk sementara masih belum dipake
    protected int frames;
    protected long lastTime;
    protected int currentFPS;

    public FPScounter() {
        frames = 0;
        lastTime = System.currentTimeMillis();
        currentFPS = 0;
    }

    public void frameRendered() {
        frames++;
        long now = System.currentTimeMillis();
        if (now - lastTime >= 1000) {
            currentFPS = frames;
            frames = 0;
            lastTime = now;
        }
    }

    public int getFPS() {
        return currentFPS;
    }
}
