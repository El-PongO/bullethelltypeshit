import enemies.BomberEnemy;
import enemies.Enemy;
import enemies.LurkerEnemy;
import enemies.NormalEnemy;
import enemies.ShooterBoss;
import enemies.ShooterEnemy;
import enemies.TankBoss;
import enemies.TankEnemy;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import players.DamageCircle;
import players.Gunslinger;
import players.Player;
import powerup.HealPower;
import powerup.MaxAmmoPower;
import powerup.PowerUp;
import powerup.SpeedPower;
import weapons.Bullet;
import weapons.Rocket;
import weapons.Sniper;
import weapons.Weapon;

public class GameplayPanel extends JPanel implements MouseMotionListener, MouseListener, KeyListener {

    // ========================= ENTITY =====================================================
    private static Player player;
    private static ArrayList<Enemy> enemies = new ArrayList<>();
    private static ArrayList<PowerUp> power = new ArrayList<>();

    // ========================= BULLET =====================================================
    private static ArrayList<Bullet> playerBullets = new ArrayList<>();
    private static ArrayList<Bullet> enemyBullets = new ArrayList<>();

    // ========================= LOGIC =====================================================
    private static JFrame window;
    private Random rand;
    private int spawnDelay = 1000;
    private int spawnPower = 5000;
    private static Timer spawnTimer;
    private static Timer bossSpawnTimer; // Timer for spawning bosses
    private static Timer gameLoop;
    static boolean gameActive = false;
    private static boolean isGameOver = false;
    private FPScounter fpscounter = new FPScounter("FPS: ");
    private static Game_clock gameClock = new Game_clock();

    // Debug counters for boss spawns
    private static int tankBossSpawnCount = 0;
    private static int shooterBossSpawnCount = 0;
    private static String[] weaponTypes = { "Glock", "Revolver", "Rocket", "Shotgun", "Smg", "Sniper" };

    static int[][] grid;
    static BufferedImage[] tilesSprite = new BufferedImage[9];
    static final int TILE_SIZE = 32;
    static final int VIEWPORT_WIDTH = 16;
    static final int VIEWPORT_HEIGHT = 12;
    static final int ZOOM = 2;
    static final int MAP_WIDTH = 16; // Add map dimensions
    static final int MAP_HEIGHT = 12;
    static int cameraPixelX, cameraPixelY;
    CursorManager cursormanager = new CursorManager();
    private boolean mouseHeld = false;
    private boolean showOutOfAmmoMsg = false;
    private long outOfAmmoMsgTime = 0;
    private static final int OUT_OF_AMMO_MSG_DURATION = 1000; // ms
    private static List<DamageCircle> activeExplosions = new ArrayList<>();

    // ========================= MAP AND TILE LOADING (NEW) =========================
    private static int[][] map1, map2, map3; // main, decor, collision
    private static boolean hasMap2, hasMap3;
    private static ImageIcon[] tiles, decors;
    private static int tileW = 32, tileH = 32;
    // ========================= SFX =====================================================
    public static Sfx soundsfx = new Sfx();
    private long lastEmptySfxTime = 0;
    private static final int EMPTY_SFX_DELAY = 400; // ms, adjust to match your empty SFX duration
    private long lastReloadSfxTime = 0;
    private boolean playedEmptySfxForCurrentEmpty = false;
    // ========================= MUSIC =====================================================
    private Music musiclobby = new Music();
    private static Music music1 = new Music();
    private static Music music2 = new Music();
    private static Music music3 = new Music();

    // ========================= KEY MOVEMENT =====================================================
    private boolean upPressed, downPressed, leftPressed, rightPressed;
    // ========================= GAME SETTING =====================================================
    private static Settingmenu settingmenu;

    // ========================= PAUSE MENU =====================================================
    PauseMenu pauseMenu;
    private boolean isPaused = false;

    // ========================= SKILL BAR =====================================================
    // Skill bar for displaying skill status
    private Skillbar skillbar = new Skillbar();

    // ========================= MAIN =====================================================
    public GameplayPanel(FPScounter fpscounter) throws Exception {
        this.rand = new Random();
        // --- Use new map loading logic ---
        loadMapSet(1); // Default to map set 1; change to 2 for the second map set
        add(gameClock.label);
        addMouseMotionListener(this);
        addMouseListener(this);
        addKeyListener(this);
        setFocusable(true);
        sfxmanager();
        spawnTimer = new Timer(spawnDelay, e -> {
            spawnPower();
            spawnEnemy();
            updateSpawnDelay();
        });
        spawnTimer.setRepeats(false);
        // Create boss spawn timer - Every 60 seconds (1 minute)
        bossSpawnTimer = new Timer(60000, e -> {
            spawnBoss();
        });
        bossSpawnTimer.setRepeats(true); // Keep spawning bosses every minute
        gameLoop = new Timer(16, e -> updateGame());
        setLayout(null);
        initializeFPSconfig(fpscounter);
        // PAUSE MENU INITIALIZATION
        pauseMenu = new PauseMenu();
        add(pauseMenu);
        setComponentZOrder(pauseMenu, 0); // Ensure pause menu is always on top
        // Add a mouse listener to request focus on click
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });
    }

    public void setPlayer(Player p) {
        player = p;
    }

    public void startGame() {
        musicmanager();
        if (player == null) {
            player = new Gunslinger(getWidth() / 2, getHeight() / 2); // fallback default
        } else {
            // Always reset the selected hero's position to center
            player.setPosition(getWidth() / 2, getHeight() / 2);
        }
        enemies.clear();// spawn enemy sama pelurunya
        enemyBullets.clear();
        playerBullets.clear();
        spawnDelay = 2000;

        isGameOver = false;
        gameActive = true;

        gameClock.setPosition(getWidth() / 2, 5, 100, 50);
        gameClock.setVisible(true);
        gameClock.timer.start();

        spawnTimer.start();
        bossSpawnTimer.start(); // Start the boss spawn timer
        gameLoop.start();

        // set button to invisible when game start
        requestFocusInWindow();
        settingmenu.setPlayer(player); // Always update the reference
        cheats();
    }

    public static void stopGame() {
        gameActive = false;
        spawnTimer.stop();
        bossSpawnTimer.stop(); // Stop the boss spawn timer
        gameLoop.stop();
        return;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    private static void gameOver() {
        isGameOver = true;
        stopAllMusic();
        soundsfx.stopAll();
        stopGame();
        gameClock.reset();
        gameClock.setVisible(false);
    }

    // ========================= SPAWN =====================================================
    private void spawnPower() {
        int spawnX,spawnY;
        while(true){
            spawnX = rand.nextInt(getWidth() - TILE_SIZE) + TILE_SIZE;
            spawnY = rand.nextInt(getHeight() - TILE_SIZE) + TILE_SIZE;
            int gridX = spawnX / TILE_SIZE;
            int gridY = spawnY / TILE_SIZE;
            if(gridX < 0 || gridY < 0 || gridX >= map3[0].length || gridY >= map3.length 
               || map3[gridY][gridX] == 0){
                break;
            }
        }
        

        int powerType = rand.nextInt(3); // 0 = Heal, 1 = Max Ammo, 2 = Speed
        if (power.size() < 10) {
            switch (powerType) {
                case 0:
                    power.add(new HealPower(spawnX, spawnY, player)); // Heal Power
                    break;
                case 1:
                    power.add(new MaxAmmoPower(spawnX, spawnY, player)); // Max Ammo Power
                    break;
                case 2:
                    power.add(new SpeedPower(spawnX, spawnY, player)); // Speed Power
                    break;
            }
        }

    }

    private void spawnEnemy() {
        int spawnX = rand.nextInt(getWidth());
        int spawnY = rand.nextInt(getHeight());

        // Regular enemy spawning - bosses now spawn on timer
        int enemyType = rand.nextInt(5); // 0 = Shooter, 1 = Normal, 2 = Tank, 3 = Lurker, 4 = Bomber

        switch (enemyType) {
            case 0:
                enemies.add(new ShooterEnemy(spawnX, spawnY, map1, tileW)); // Only this enemy type shoots
                break;
            case 1:
                enemies.add(new NormalEnemy(spawnX, spawnY)); // Simple follower
                break;
            case 2:
                enemies.add(new TankEnemy(spawnX, spawnY)); // Slow and big
                break;
            case 3:
                enemies.add(new LurkerEnemy(spawnX, spawnY)); // Sudden jumps
                break;
            case 4:
                enemies.add(new BomberEnemy(spawnX, spawnY)); // Fast toward player
                break;
        }
        spawnTimer.setInitialDelay(spawnDelay);
        spawnTimer.restart();
    }

    // ========================= GAME UPDATE =====================================================
    private void updateSpawnDelay() {
        if (spawnDelay > 300) {
            spawnDelay -= 50;
        }
    }

    private void updateGame() {
        if (!gameActive)
            return;
        else {
            if (player.getHealth() <= 0) {
                gameOver();
                return;
            }
            player.move(upPressed, downPressed, leftPressed, rightPressed, map3, tileW, mouseHeld); // PLAYER MOVEMENT + SPRITE
            player.updateDash();
            player.updateSkill(); // Update skill status
            shootingDirection();
            updateBullets();
            checkCollisions(); // Calculate map dimensions once for all enemies
            int mapWidth = map1[0].length * tileW;
            int mapHeight = map1.length * tileH;

            for (Enemy enemy : enemies) { // gae musuh bisa nembak
                enemy.update(player, enemyBullets, map3, tileW);
                // Keep enemies within map boundaries
                enemy.keepWithinMapBoundaries(mapWidth, mapHeight);
            }

            // gae bullet e musuh idk why chatgpt literally makes it another new variable tp
            // haruse bullet isa dewek so idk
            if (mouseHeld && player.getCurrentWeapon().isFullAuto()) {
                Point mouse = getMousePosition();
                boolean autoReload = settingmenu != null && settingmenu.isAutoReloadEnabled();
                if (mouse != null) {
                    List<Bullet> bullet = player.shoot(mouse.x / ZOOM + cameraPixelX, mouse.y / ZOOM + cameraPixelY);
                    if (bullet != null && !bullet.isEmpty()) {
                        if (playedEmptySfxForCurrentEmpty){
                            playedEmptySfxForCurrentEmpty = false; // Reset for next shot
                        }
                        playerBullets.addAll(bullet);
                        playsfx(false);
                    } 

                    if (player.getCurrentWeapon().getCurrentAmmo() == 0) {
                        long now = System.currentTimeMillis();
                        if (autoReload){
                            if (!playedEmptySfxForCurrentEmpty) {
                                playsfx(true);
                                playedEmptySfxForCurrentEmpty = true;
                                showOutOfAmmoMsg = true;
                                outOfAmmoMsgTime = now;
                            }

                            // Automatically reload if not already reloading
                            if (!player.getCurrentWeapon().isReloading()) {
                                player.reloadCurrentWeapon();
                                reloadsfx();
                            }
                        }else{
                            if (now - lastEmptySfxTime >= EMPTY_SFX_DELAY) {
                                playsfx(true);
                                lastEmptySfxTime = now;
                                showOutOfAmmoMsg = true;
                                outOfAmmoMsgTime = now;
                            }
                        }
                    }
                }
            }
            fpscounter.frameRendered();
            player.getCurrentWeapon().updateReload();
            repaint();
        }
    }

    // ========================= PAINT COMPONENT =====================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int rows = map1.length, cols = map1[0].length;
        int vpw = getWidth() / ZOOM;
        int vph = getHeight() / ZOOM;
        int mapPixelWidth = cols * tileW;
        int mapPixelHeight = rows * tileH;
        int playerCenterX = player.getX() + player.getSize() / 2;
        int playerCenterY = player.getY() + player.getSize() / 2;
        int maxCameraX = Math.max(0, mapPixelWidth - vpw);
        int maxCameraY = Math.max(0, mapPixelHeight - vph);
        cameraPixelX = Math.max(0, Math.min(playerCenterX - vpw / 2, maxCameraX));
        cameraPixelY = Math.max(0, Math.min(playerCenterY - vph / 2, maxCameraY));
        int startY = cameraPixelY / tileH;
        int startX = cameraPixelX / tileW;
        int endY = (cameraPixelY + vph) / tileH + 1;
        int endX = (cameraPixelX + vpw) / tileW + 1;

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                int drawX = (x * tileW - cameraPixelX) * ZOOM;
                int drawY = (y * tileH - cameraPixelY) * ZOOM;
                if (y >= 0 && y < rows && x >= 0 && x < cols) {
                    int idx1 = map1[y][x];
                    if (idx1 >= 0 && idx1 < tiles.length && tiles[idx1] != null) {
                        g.drawImage(tiles[idx1].getImage(), drawX, drawY, tileW * ZOOM, tileH * ZOOM, null);
                    }
                } else {
                    g.setColor(Color.BLUE);
                    g.fillRect(drawX, drawY, tileW * ZOOM, tileH * ZOOM);
                }
            }
        }

        int px = (player.getX() - cameraPixelX) * ZOOM;
        int py = (player.getY() - cameraPixelY) * ZOOM;
        player.draw((Graphics2D) g, px, py, ZOOM);
        for (Bullet bullet : playerBullets) {
            int ex = (bullet.x - cameraPixelX) * ZOOM;
            int ey = (bullet.y - cameraPixelY) * ZOOM;
            bullet.draw(g, ex, ey, Color.GREEN);
        }
        for (Enemy enemy : enemies) {
            int ex = (enemy.x - cameraPixelX) * ZOOM;
            int ey = (enemy.y - cameraPixelY) * ZOOM;
            enemy.draw(g, ex, ey);
        }
        for (Bullet bullet : enemyBullets) {
            int ex = (bullet.x - cameraPixelX) * ZOOM;
            int ey = (bullet.y - cameraPixelY) * ZOOM;
            bullet.draw(g, ex, ey, Color.RED);
        }
        for (PowerUp powerUp : power) {
            int ex = (powerUp.x - cameraPixelX) * ZOOM;
            int ey = (powerUp.y - cameraPixelY) * ZOOM;
            powerUp.draw(g, ex, ey);
        }

        if (hasMap2) {
            for (int y = startY; y < endY; y++) {
                for (int x = startX; x < endX; x++) {
                    int drawX = (x * tileW - cameraPixelX) * ZOOM;
                    int drawY = (y * tileH - cameraPixelY) * ZOOM;
                    if (y >= 0 && y < rows && x >= 0 && x < cols) {
                        int idx2 = map2[y][x];
                        if (idx2 > 0 && idx2 < decors.length && decors[idx2] != null) {
                            g.drawImage(decors[idx2].getImage(), drawX, drawY, tileW * ZOOM, tileH * ZOOM, null);
                        }
                    }
                }
            }
        }
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Health: " + player.getHealth() + "/" + player.getMaxHealth(), 10, 20);
        g.drawString("Dash Charges: " + player.getCurrentDashCharges() + "/" + player.getMaxDashCharges(), 10, 40);
        drawWeaponHUD((Graphics2D) g);
        skillbar.draw((Graphics2D) g, player, getWidth(), getHeight());
        if (player instanceof players.Bomber) {
            players.Bomber bomber = (players.Bomber) player;
            players.DamageCircle circle = bomber.getCircleSkill();
            if (circle != null) {
                circle.draw((Graphics2D) g, cameraPixelX, cameraPixelY, ZOOM);
            }
        }

        Iterator<DamageCircle> it = activeExplosions.iterator();
        while (it.hasNext()) {
            DamageCircle explosion = it.next();
            if (explosion.isActive()) {
                explosion.draw((Graphics2D) g, cameraPixelX, cameraPixelY, ZOOM);
            } else {
                it.remove(); // Remove inactive explosions
            }
        }

        if (isPaused) {
            pauseMenu.draw(g, getWidth(), getHeight());
        }
    }

    private void drawWeaponHUD(Graphics2D g) {
        if (player == null) {
            return;
        }

        Weapon currentWeapon = player.getCurrentWeapon();

        // If there's no weapon, don't try to draw the weapon HUD
        if (currentWeapon == null) {
            return;
        }

        int iconSize = 48;
        int margin = 20;
        int offsetX = getWidth() - iconSize - margin;
        int offsetY = margin;

        g.setColor(Color.WHITE);
        g.fillRect(offsetX, offsetY, iconSize, iconSize);

        if (currentWeapon.getSprite() != null) {
            g.drawImage(currentWeapon.getSprite(), offsetX, offsetY, iconSize, iconSize, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(offsetX, offsetY, iconSize, iconSize);
        }

        g.setColor(Color.RED);
        g.drawRect(offsetX, offsetY, iconSize, iconSize);

        // Draw ammo count
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        String ammoText = currentWeapon.getCurrentAmmo() + " / " + currentWeapon.getMaxAmmo();
        g.drawString(ammoText, offsetX + 4, offsetY + iconSize + 16);

        if (player.changewp != null) {
            int changeSize = 32;
            g.drawImage(player.changewp, offsetX + iconSize - changeSize / 2, offsetY + iconSize - changeSize / 2,
                    changeSize, changeSize, null);
        }

        if (currentWeapon.isReloading()) {
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("Reloading...", offsetX - 8, offsetY - 4);
        }

        g.setColor(new Color(255, 215, 0, 180));
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(offsetX - 2, offsetY - 2, iconSize + 4, iconSize + 4, 10, 10);
    }

    // ========================= FUNCTION =====================================================
    public void sfxmanager() {
        soundsfx.load("dice", "/Audio/Sfx/Dice_Roll.wav");
        soundsfx.load("shoot", "/Audio/Sfx/Atk_LeweiGun.wav");
        soundsfx.load("explode", "/Audio/Sfx/Explode.wav");
        soundsfx.load("empty", "/Audio/Sfx/wep_empty.wav");
        soundsfx.load("hit", "/Audio/Sfx/player_hit.wav");
        soundsfx.load("reload", "/Audio/Sfx/reload.wav");
        soundsfx.load("powerup", "/Audio/Sfx/Power Up.wav");
        soundsfx.load("dash", "/Audio/Sfx/dash.wav");

        // player
        soundsfx.load("bomber_skill", "/Audio/Sfx/skill_bomber.wav");
        soundsfx.load("gunslinger_skill", "/Audio/Sfx/skill_gunslinger.wav");
        soundsfx.load("vampire_skill", "/Audio/Sfx/skill_vampire.wav");
        soundsfx.load("brute_skill", "/Audio/Sfx/skill_brute.wav");

        // revolver
        soundsfx.load("shootrevolver", "/Audio/Sfx/rev_shot.wav");
        soundsfx.load("emptyrevolver", "/Audio/Sfx/rev_empty.wav");
        soundsfx.load("reloadrevolver", "/Audio/Sfx/Revolver_Reload.wav");

        // shotgun
        soundsfx.load("shootshotgun", "/Audio/Sfx/shotgun_fire.wav");
        soundsfx.load("shotgunload", "/Audio/Sfx/shotgun_load.wav");
        soundsfx.load("shotgunreload", "/Audio/Sfx/shotgun_reload.wav");
        soundsfx.load("shotgunlock", "/Audio/Sfx/shotgun_lock.wav");
        soundsfx.load("shogunempty", "/Audio/Sfx/shotgun_empty.wav");

        // smg
        soundsfx.load("smgreload", "/Audio/Sfx/MP5_Reload.wav");

        // rpg
        soundsfx.load("rpgreload", "/Audio/Sfx/RPG_Reload.wav");
        soundsfx.load("rpgfire", "/Audio/Sfx/RPG_firing.wav");
        soundsfx.load("rpgexplode", "/Audio/Sfx/RPG_Explode.wav");

        // sniper
        soundsfx.load("sniperfire", "/Audio/Sfx/sniper_fire.wav");
        soundsfx.load("snipereload", "/Audio/Sfx/SniperReload.wav");

        // MG
        soundsfx.load("mgfire", "/Audio/Sfx/machine_gun_fire.wav");
        soundsfx.load("mgreload", "/Audio/Sfx/machine_gun_reload.wav");
    }

    public void playsfx(boolean isEmpty) {
        Weapon current = player.getCurrentWeapon();
        if (current == null) {
            System.out.println("No current weapon to play sound for.");
            return;
        }

        if (!isEmpty) {
            if (current instanceof weapons.Revolver) {
                soundsfx.playWithRandomPitch("shootrevolver");
            } else if (current instanceof weapons.Shotgun) {
                soundsfx.playWithRandomPitch("shootshotgun");
                // Get the duration of the shotgun fire SFX (in ms)
                int fireDuration = soundsfx.getClipDurationMs("shootshotgun");
                // Schedule the load SFX after the fire SFX ends
                new javax.swing.Timer(fireDuration, evt -> {
                    if (current.getCurrentAmmo() <= 0) {
                        soundsfx.playWithRandomPitch("shotgunlock");
                    } else {
                        soundsfx.playWithRandomPitch("shotgunload");
                    }
                    ((javax.swing.Timer) evt.getSource()).stop();
                }).start();
            } else if (current instanceof weapons.Sniper) {
                soundsfx.playWithRandomPitch("sniperfire");
            } else if (current instanceof weapons.Rocket) {
                soundsfx.playWithRandomPitch("rpgfire");
            } else if (current instanceof weapons.MG){
                soundsfx.playWithRandomPitch("mgfire");
            }else {
                soundsfx.playWithRandomPitch("shoot");
            }
        } else {
            System.out.println("Playing weapon empty sfx");
            if (current instanceof weapons.Revolver) {
                soundsfx.play("emptyrevolver");
            } else if (current instanceof weapons.Shotgun) {
                soundsfx.play("shogunempty");
            } else {
                soundsfx.play("empty");
            }
        }
    }

    public void reloadsfx() {
        Weapon current = player.getCurrentWeapon();
        if (current == null) {
            System.out.println("No current weapon to play sound for.");
            return;
        }

        if (current.getCurrentAmmo() == current.getMaxAmmo()) {
            System.out.println("Weapon already full, no reload sound.");
            return;
        }

        // Use the weapon's reload delay as the SFX cooldown
        int reloadDelay = current.getReloadDelayMs();
        long now = System.currentTimeMillis();
        if (now - lastReloadSfxTime < reloadDelay) {
            return; // Too soon, skip playing the sound
        }
        lastReloadSfxTime = now;

        if (current instanceof weapons.Shotgun) {
            soundsfx.play("shotgunreload");
        } else if (current instanceof weapons.Revolver) {
            soundsfx.play("reloadrevolver");
        } else if (current instanceof weapons.Smg) {
            soundsfx.play("smgreload");
        } else if (current instanceof weapons.Rocket) {
            soundsfx.play("rpgreload");
        } else if (current instanceof weapons.Sniper) {
            soundsfx.play("snipereload");
        } else if (current instanceof weapons.MG) {
            soundsfx.play("mgreload");
        } else {
            soundsfx.play("reload");
        }
    }

    public void musicmanager() {
        musiclobby.load("/Audio/Music/lobby.wav");
        Random rand = new Random();
        int n = rand.nextInt(6); // Randomly select a music track from 0 to 5
        if (n == 0) {
            music1.load("/Audio/Music/netzach1.wav");
            music2.load("/Audio/Music/netzach2.wav");
            music3.load("/Audio/Music/netzach3.wav");
        } else if (n == 1) {
            music1.load("/Audio/Music/hod1.wav");
            music2.load("/Audio/Music/hod2.wav");
            music3.load("/Audio/Music/hod3.wav");
        } else if (n == 2) {
            music1.load("/Audio/Music/malkuth1.wav");
            music2.load("/Audio/Music/malkuth2.wav");
            music3.load("/Audio/Music/malkuth3.wav");
        } else if (n == 3) {
            music1.load("/Audio/Music/Undertale OST_ 072 - Song That Might Play When You Fight Sans.wav");
        }else if (n == 4){
            music1.load("/Audio/Music/Undertale OST_ 080 - Finale.wav");
        }else if (n == 5){
            music1.load("/Audio/Music/Holocure Map 1.wav");
        }

        // --- Custom transition logic for n == 0,1,2 ---
        if (n == 0 || n == 1 || n == 2) {
            // Play music1 (no loop)
            music1.play();

            // When music1 ends, fade out, play dice, fade in music2
            new Thread(() -> {
                waitForMusicToEnd(music1);
                if (!gameActive || isGameOver) {
                    // Stop all music to be sure
                    stopAllMusic();
                    return;
                }
                // Start fade out, dice SFX, and fade in next music at the same time
                music1.fadeOut(1000);
                soundsfx.play("dice");
                music2.play();
                music2.fadeIn(1000);
                // sleep a bit to avoid overlapping transitions too quickly
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}

                // When music2 ends, play dice SFX, fade out music2, and start music3
                waitForMusicToEnd(music2);

                if (!gameActive || isGameOver) {
                    // Stop all music to be sure
                    stopAllMusic();
                    return;
                }

                soundsfx.play("dice");
                music3.play();
                music3.loop(); // Loop music3 until game over
            }).start();
        } else {
            // --- Default: just loop music1 as before ---
            music1.loop();
        }
    }

    // Helper method to block until a Music finishes playing
    private void waitForMusicToEnd(Music music) {
        while (true) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {}
            if (music == null) break;
            if (!musicIsPlaying(music)) break;
        }
    }

    // Helper to check if a Music is playing
    private boolean musicIsPlaying(Music music) {
        try {
            java.lang.reflect.Field clipField = Music.class.getDeclaredField("clip");
            clipField.setAccessible(true);
            javax.sound.sampled.Clip clip = (javax.sound.sampled.Clip) clipField.get(music);
            return clip != null && clip.isRunning();
        } catch (Exception e) {
            return false;
        }
    }

    public static void stopAllMusic() {
        if (music1 != null) music1.stop();
        if (music2 != null) music2.stop();
        if (music3 != null) music3.stop();
    }

    public void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            gameLoop.stop();
            spawnTimer.stop();
            gameClock.timer.stop();
            soundsfx.pauseAll();
            pauseMenu.setVisibility(true);
            if (getParent() instanceof MainPanel) {
                ((MainPanel) getParent()).cursormanager.setCursor(getParent(), "cursor");
            }
            repaint();
        } else {
            gameLoop.start();
            spawnTimer.start();
            gameClock.timer.start();
            soundsfx.resumeAll();
            pauseMenu.setVisibility(false);
            requestFocusInWindow();
            if (getParent() instanceof MainPanel) {
                ((MainPanel) getParent()).cursormanager.setCursor(getParent(), "crosshair");
            }
        }
    }

    public void shootingDirection(){
        if(mouseHeld){
            Point mouse = getMousePosition();
            int targetX = mouse.x / ZOOM + cameraPixelX;
            int targetY =  mouse.y / ZOOM + cameraPixelY;
            double angle = Math.atan2(targetY - (player.getY() + player.getSize() / 2), targetX - (player.getX() + player.getSize() / 2));
            double deg = Math.toDegrees(angle);
            if (deg >= -45 && deg < 45) {
                player.direction = "right";
            } else if (deg >= 45 && deg < 135) {
                player.direction = "down";
            } else if (deg >= -135 && deg < -45) {
                player.direction = "up";
            } else {
                player.direction = "left";
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (gameActive && e.getButton() == MouseEvent.BUTTON1) {
            mouseHeld = true;
            tryFire(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_ESCAPE && gameActive) {
            togglePause();
            return;
        }
        if (code == KeyEvent.VK_W)
            upPressed = true;
        if (code == KeyEvent.VK_S)
            downPressed = true;
        if (code == KeyEvent.VK_A)
            leftPressed = true;
        if (code == KeyEvent.VK_D)
            rightPressed = true;
        if (code == KeyEvent.VK_SHIFT) {
            player.dash(); // dash
            soundsfx.play("dash");
            System.out.println("Dash activated! " + player.direction);
        }
        if (code == KeyEvent.VK_Q) {
            if (player.getCurrentWeaponIndex() == player.getWeaponMinIndex()) {
                player.switchWeapon(player.getWeaponMaxIndex()); // switch to max
            } else {
                player.switchWeapon(-1); // switch weapon left
            }
        }

        if (code == KeyEvent.VK_E) {
            if (player.getCurrentWeaponIndex() == player.getWeaponMaxIndex()) {
                player.setWeaponIndex(player.getWeaponMinIndex()); // wrap to min
            } else {
                player.switchWeapon(1); // switch weapon right
            }
        }

        if (code == KeyEvent.VK_R) {
            player.reloadCurrentWeapon();
            reloadsfx();
            System.out.println("Reloading " + player.getCurrentWeapon().getName() + "...");
        }
        // Use skill for any player type when pressing F
        if (code == KeyEvent.VK_F) {
            boolean wasActive = player.isSkillOnCooldown();
            player.useSkill();
            if (!wasActive) {
                if (player instanceof players.Bomber) {
                    soundsfx.play("bomber_skill");
                } else if (player instanceof players.Gunslinger) {
                    soundsfx.play("gunslinger_skill");
                } else if (player instanceof players.Vampire) {
                    soundsfx.play("vampire_skill");
                } else if (player instanceof players.Brute) {
                    soundsfx.play("brute_skill");
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W)
            upPressed = false;
        if (code == KeyEvent.VK_S)
            downPressed = false;
        if (code == KeyEvent.VK_A)
            leftPressed = false;
        if (code == KeyEvent.VK_D)
            rightPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    } // unused

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (gameActive && mouseHeld) {
            tryFire(e);
        }
        
    } // gerakkan mouse

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseHeld = false;
        }
    } // lepas mouse

    @Override
    public void mouseEntered(MouseEvent e) {
    } // masuk mouse ke dalam window

    @Override
    public void mouseExited(MouseEvent e) {
    } // ya bisa di baca sendiri lah km ws tua berjembut

    private void tryFire(MouseEvent e) {
        if (player == null) {
            return;
        }

        Weapon weapon = player.getCurrentWeapon();
        if (weapon == null) {
            return;
        }

        if (weapon.isFullAuto()) {
            // For full auto, firing is handled in updateGame()
        } else {
            List<Bullet> bullet = player.shoot(e.getX()/ZOOM + cameraPixelX, e.getY()/ZOOM + cameraPixelY);
            boolean autoReload = settingmenu != null && settingmenu.isAutoReloadEnabled();
            long now = System.currentTimeMillis();
            if (bullet != null && !bullet.isEmpty()) {
                playerBullets.addAll(bullet);
                playsfx(false);
                if (autoReload && playedEmptySfxForCurrentEmpty){
                    playedEmptySfxForCurrentEmpty = false;
                }
            } 

            if (!weapon.hasAmmo()) {
                if (autoReload){
                    if (!playedEmptySfxForCurrentEmpty) {
                        playsfx(true);
                        playedEmptySfxForCurrentEmpty = true;
                        showOutOfAmmoMsg = true;
                        outOfAmmoMsgTime = now;
                    }

                    // Automatically reload if not already reloading
                    if (!weapon.isReloading()) {
                        player.reloadCurrentWeapon();
                        reloadsfx();
                    }
                }else{
                    if (now - lastEmptySfxTime >= EMPTY_SFX_DELAY) {
                        playsfx(true); // Out of ammo
                        lastEmptySfxTime = now;
                        showOutOfAmmoMsg = true;
                        outOfAmmoMsgTime = now;
                    }
                }
                
                
            }
        }
    }

    private static void updateBullets() {
        // Update existing bullets
        playerBullets.removeIf(bullet -> {
            bullet.update();
            // Remove bullets that are out of bounds or hit walls
            return bullet.isOutOfBounds(map1, tileW);
        });

        // Update enemy bullets
        enemyBullets.removeIf(bullet -> {
            bullet.update();
            return bullet.isOutOfBounds(map1, tileW);
        });
    }

    private static void checkCollisions() {
        // hitboxnya aku kecilin dikit (90% dari sprite)
        int fullSize = player.getSize();
        int hitboxSize = (int) (fullSize * 0.9);
        int offset = (fullSize - hitboxSize) / 2;

        Rectangle playerBounds = new Rectangle(player.getX() + offset, player.getY() + offset, hitboxSize, hitboxSize);
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            Rectangle enemyBounds = new Rectangle(enemy.x, enemy.y, enemy.size, enemy.size);
            // Bomber circle skill damage (refactored)
            if (player instanceof players.Bomber) {
                players.Bomber bomber = (players.Bomber) player;
                players.DamageCircle circle = bomber.getCircleSkill();
                if (circle != null && circle.collides(enemy.x, enemy.y, enemy.size)) {
                    enemy.takeDamage(circle.getDamage());
                    System.out.println("the bomb hit something!");

                    if (enemy.isDead()) {

                        enemies.remove(i);
                        continue;
                    }
                }
            }

            if (playerBounds.intersects(enemyBounds) && !player.isInvincible()) {
                System.out.println("Player hit!");
                // Special cases for different enemy types
                if (enemy instanceof enemies.BomberEnemy) {
                    Sfx.playWithRandomPitch("explode");
                    player.takeDamage(100); // Bomber deals 100 damage when exploding
                    enemy.takeDamage(1000); // Kill the bomber after it explodes
                    System.out.println("Bomber exploded and died!");
                    // Remove the bomber from the list immediately
                    enemies.remove(i);
                    // Skip the rest of the loop since we've removed this enemy
                    continue;
                } else if (enemy instanceof enemies.TankEnemy) {
                    player.takeDamage(25); // Tank enemy deals less damage (25)
                } else {
                    player.takeDamage(50); // Other enemies deal standard 50 damage
                }
                if (player.isDead()) {
                    gameOver();
                }
            }
        }

        // Refactored: process all bullet-enemy collisions, collect enemies to remove,
        // then remove after
        java.util.List<Enemy> enemiesToRemove = new java.util.ArrayList<>();
        playerBullets.removeIf(bullet -> {
            Rectangle bulletBounds = new Rectangle((int) bullet.x, (int) bullet.y, bullet.getSize(), bullet.getSize());
            boolean shouldRemove = false;
            for (int i = enemies.size() - 1; i >= 0; i--) {
                Enemy enemy = enemies.get(i);
                Rectangle enemyBounds = new Rectangle(enemy.x, enemy.y, enemy.size, enemy.size);
                if (bulletBounds.intersects(enemyBounds)) {
                    Weapon currentWeapon = player.getCurrentWeapon();
                    if (bullet instanceof Rocket.RocketBullet) {
                        DamageCircle explosion = new DamageCircle(
                                (int) bullet.x,
                                (int) bullet.y,
                                100, // explosion radius
                                currentWeapon.getWeaponDamage(),
                                500, // duration in ms
                                Color.ORANGE);
                        activeExplosions.add(explosion);
                        int totalDamageDealt = 0;
                        for (int j = enemies.size() - 1; j >= 0; j--) {
                            Enemy targetEnemy = enemies.get(j);
                            if (explosion.collides(targetEnemy.x, targetEnemy.y, targetEnemy.size)) {
                                int damageDealt = Math.min(targetEnemy.getHealth(), currentWeapon.getWeaponDamage());
                                targetEnemy.takeDamage(currentWeapon.getWeaponDamage());
                                totalDamageDealt += damageDealt;
                                if (targetEnemy.isDead()) {
                                    enemiesToRemove.add(targetEnemy);
                                }
                            }
                        }
                        if (player instanceof players.Vampire && totalDamageDealt > 0) {
                            ((players.Vampire) player).stealHealth(totalDamageDealt);
                        }
                        soundsfx.play("rpgexplode");
                        shouldRemove = true;
                        break;
                    } else {
                        enemy.takeDamage(currentWeapon.getWeaponDamage());
                    }
                    if (player instanceof players.Vampire) {
                        ((players.Vampire) player).stealHealth(currentWeapon.getWeaponDamage());
                    }
                    if (enemy.isDead()) {
                        if (enemy instanceof enemies.TankBoss || enemy instanceof enemies.ShooterBoss) {
                            Random rand = new Random();
                            int idx = rand.nextInt(7);
                            weapons.Weapon newWeapon = null;
                            switch (idx) {
                                case 0:
                                    newWeapon = new weapons.Revolver();
                                    break;
                                case 1:
                                    newWeapon = new weapons.Shotgun();
                                    break;
                                case 2:
                                    newWeapon = new weapons.Smg();
                                    break;
                                case 3:
                                    newWeapon = new weapons.Glock();
                                    break;
                                case 4:
                                    newWeapon = new weapons.Sniper();
                                    break;
                                case 5:
                                    newWeapon = new weapons.Rocket();
                                    break;
                                case 6:
                                    newWeapon = new weapons.MG();
                                default:
                                    newWeapon = null;
                                    break;
                            }
                            if (newWeapon != null) {
                                boolean alreadyHas = false;
                                for (int p = 0; p < player.getWeapons().size(); p++) {
                                    Weapon owned = player.getWeapons().get(p);
                                    if (owned.getName().equals(newWeapon.getName())) {
                                        alreadyHas = true;
                                        break;
                                    }
                                }
                                if (!alreadyHas) {
                                    player.getWeapons().add(newWeapon);
                                    System.out.println("Picked up new weapon: " + newWeapon.getName());
                                } else {
                                    System.out.println("Player already has weapon: " + newWeapon.getName());
                                }
                            }
                            System.out.println("hero now has " + player.getWeapons().size() + " weapons!");
                        }
                        enemiesToRemove.add(enemy);
                    }

                    // Handle penetration for sniper bullets
                    if (bullet instanceof Sniper.SniperBullet) {
                        Sniper.SniperBullet sniperBullet = (Sniper.SniperBullet) bullet;
                        if (sniperBullet.canPenetrate()) {
                            sniperBullet.penetrate();
                            continue;
                        }
                    }
                    shouldRemove = true;
                    break;
                }
            }
            return shouldRemove || bullet.isOutOfBounds(map1, tileW);
        });
        // Remove all enemies marked for removal after bullet processing
        if (!enemiesToRemove.isEmpty()) {
            enemies.removeAll(enemiesToRemove);
        }

        // Check enemy bullet-player collisions
        enemyBullets.removeIf(bullet -> {
            Rectangle bulletBounds = new Rectangle(bullet.x, bullet.y, bullet.getSize(), bullet.getSize());
            if (bulletBounds.intersects(playerBounds) && !player.isInvincible()) {
                System.out.println("Player hit by enemy bullet!");
                Sfx.playWithRandomPitch("hit");
                player.takeDamage(50);
                if (player.isDead()) {
                    gameOver();
                }
                return true;
            }
            return false;
        });

        // Check power-up collisions
        for (PowerUp powerUp : power) {
            Rectangle powerUpBounds = new Rectangle(powerUp.x, powerUp.y, powerUp.size, powerUp.size);
            if (playerBounds.intersects(powerUpBounds)) {
                System.out.println("Player collected a power-up!");
                soundsfx.play("powerup");
                powerUp.activate();
                power.remove(powerUp);
                break;
            }
        }
    }

    // GAME SETTINGS
    public static boolean controlFade() {
        if (settingmenu != null && settingmenu.getDisableFade() != null && settingmenu.getDisableFade().isSelected()) {
            return false;
        } else {
            return true;
        }
    }

    public void cheats() {
        if (settingmenu.isDevModeEnabled() && player != null) {
            player.setMaxHealth(1000);
            player.heal(1000, true);
            try {
                boolean hasRevolver = player.getWeapons().stream().anyMatch(w -> w.getName().equals("Revolver"));
                boolean hasShotgun = player.getWeapons().stream().anyMatch(w -> w.getName().equals("Shotgun"));
                boolean hasSmg = player.getWeapons().stream().anyMatch(w -> w.getName().equals("Smg"));
                boolean hasPistol = player.getWeapons().stream().anyMatch(w -> w.getName().equals("Glock"));
                boolean hasSniper = player.getWeapons().stream().anyMatch(w -> w.getName().equals("Sniper"));
                boolean hasRpg = player.getWeapons().stream().anyMatch(w -> w.getName().equals("Rocket Launcher"));
                boolean hasMG = player.getWeapons().stream().anyMatch(w -> w.getName().equals("MG"));
                if (!hasRpg)
                    player.getWeapons().add(new weapons.Rocket());
                if (!hasSniper)
                    player.getWeapons().add(new weapons.Sniper());
                if (!hasRevolver)
                    player.getWeapons().add(new weapons.Revolver());
                if (!hasShotgun)
                    player.getWeapons().add(new weapons.Shotgun());
                if (!hasSmg)
                    player.getWeapons().add(new weapons.Smg());
                if (!hasPistol)
                    player.getWeapons().add(new weapons.Glock());
                if (!hasMG)
                    player.getWeapons().add(new weapons.MG());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void initializeFPSconfig(FPScounter fpscounter) {
        this.fpscounter = fpscounter;
        add(fpscounter);
        updateFPSCounterVisibility();
        positionFPSCounter();
        addFPSCounterResizeListener();
    }

    private void updateFPSCounterVisibility() {
        // Check if settingmenu is initialized
        if (settingmenu != null && settingmenu.getFpsCheckbox() != null) {
            fpscounter.setVisible(settingmenu.getFpsCheckbox().isSelected());
        } else {
            // Default to visible if settings are not available
            fpscounter.setVisible(true);
        }
    }

    private void positionFPSCounter() {
        fpscounter.setBounds(getWidth() - 100, 10, 90, 20);
    }

    private void addFPSCounterResizeListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                positionFPSCounter();
            }
        });
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void resetGame() {
        // Reset game state
        gameActive = false;
        isGameOver = false;
        isPaused = false;

        // Clear all entities
        enemies.clear();
        playerBullets.clear();
        enemyBullets.clear();
        power.clear();

        // Reset timers
        spawnTimer.stop();
        gameLoop.stop();
        gameClock.reset();

        // Reset spawn delay
        spawnDelay = 1000;

        // Reset movement flags
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;

        // Stop music
        stopAllMusic();

        // Reset pause menu
        pauseMenu.setVisibility(false);
    }

    public static void initializeSettingMenu(JFrame frame) {
        // Initialize the settingmenu with the provided JFrame
        settingmenu = new Settingmenu(frame);
    }

    public static Settingmenu getSettingMenu() {
        return settingmenu;
    }

    // Accessor methods for map dimensions
    public static int[][] getGrid() {
        return grid;
    }

    public static int getTileSize() {
        return TILE_SIZE;
    }

    private void spawnBoss() {
        // Calculate spawn position - away from the player
        int playerX = player.getX();
        int playerY = player.getY();

        // Determine spawn position at a safe distance from player (300-500 pixels)
        int distance = 300 + rand.nextInt(200); // Between 300-500 pixels away
        double angle = Math.random() * 2 * Math.PI; // Random angle

        int spawnX = (int) (playerX + distance * Math.cos(angle));
        int spawnY = (int) (playerY + distance * Math.sin(angle));

        // Keep spawn position within map bounds
        int mapWidth = grid[0].length * TILE_SIZE;
        int mapHeight = grid.length * TILE_SIZE;
        spawnX = Math.max(50, Math.min(spawnX, mapWidth - 50));
        spawnY = Math.max(50, Math.min(spawnY, mapHeight - 50));
        // Ensure more balanced distribution over time
        boolean spawnTankBoss;

        // If we've spawned significantly more of one type, bias toward the other
        int difference = Math.abs(tankBossSpawnCount - shooterBossSpawnCount);
        if (difference >= 2) {
            // Bias toward the less frequently spawned boss type
            spawnTankBoss = tankBossSpawnCount < shooterBossSpawnCount;
            System.out.println("Balancing boss distribution due to difference of " + difference);
        } else {
            // Normal 50/50 chance
            spawnTankBoss = rand.nextBoolean();
        }

        // Record the roll for debugging
        int roll = rand.nextInt(100); // Just for logging purposes

        if (spawnTankBoss) {
            enemies.add(new TankBoss(spawnX, spawnY));
            tankBossSpawnCount++;
            System.out.println("Tank Boss spawned at minute " + gameClock.getMinutes() + "! (Roll: " + roll + ")");
            System.out
                    .println("Boss Spawn Counts - Tank: " + tankBossSpawnCount + ", Shooter: " + shooterBossSpawnCount);
        } else {
            enemies.add(new ShooterBoss(spawnX, spawnY));
            shooterBossSpawnCount++;
            System.out.println("Shooter Boss spawned at minute " + gameClock.getMinutes() + "! (Roll: " + roll + ")");
            System.out
                    .println("Boss Spawn Counts - Tank: " + tankBossSpawnCount + ", Shooter: " + shooterBossSpawnCount);
        }
    }
    private int[][] newMap1, newMap2, newMap3;
    private boolean newHasMap2, newHasMap3;
    private javax.swing.ImageIcon[] newTiles, newDecors;
    private int newTileW = 32, newTileH = 32;

    private int[][] loadMapCSV(String filename) {
        java.util.List<int[]> mapRows = new java.util.ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;
                String[] tokens = line.split(",");
                int[] row = new int[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    String t = tokens[i].trim();
                    row[i] = t.isEmpty() ? -1 : Integer.parseInt(t);
                }
                mapRows.add(row);
            }
        } catch (Exception e) {
            return null;
        }
        return mapRows.toArray(new int[mapRows.size()][]);
    }

    private javax.swing.ImageIcon[] loadSheet(String dirPath, String prefix) {
        java.io.File dir = new java.io.File(dirPath);
        int maxIdx = -1;
        if (dir.exists() && dir.isDirectory()) {
            for (java.io.File f : dir.listFiles()) {
                String n = f.getName();
                if (n.startsWith(prefix) && n.endsWith(".png")) {
                    try {
                        int idx = Integer.parseInt(n.substring(prefix.length(), n.length() - 4));
                        if (idx > maxIdx)
                            maxIdx = idx;
                    } catch (Exception ignore) {
                    }
                }
            }
        }
        javax.swing.ImageIcon[] arr = new javax.swing.ImageIcon[maxIdx + 1];
        for (int i = 0; i <= maxIdx; i++) {
            java.io.File f = new java.io.File(dir, prefix + i + ".png");
            if (f.exists())
                arr[i] = new javax.swing.ImageIcon(f.getAbsolutePath());
        }
        return arr;
    }

    public void initNewMapAndTiles() {
        newMap1 = loadMapCSV("bullethell/src/map1.txt");
        newMap2 = loadMapCSV("bullethell/src/map2.txt");
        newMap3 = loadMapCSV("bullethell/src/map3.txt");
        if (newMap1 != null) {
            int rows = newMap1.length, cols = newMap1[0].length;
            newHasMap2 = newMap2 != null && newMap2.length == rows && newMap2[0].length == cols;
            newHasMap3 = newMap3 != null && newMap3.length == rows && newMap3[0].length == cols;
        }
        newTiles = loadSheet("bullethell/src/Assets/tiles", "tile");
        newDecors = loadSheet("bullethell/src/Assets/tiles", "decor");
        for (javax.swing.ImageIcon icon : newTiles) {
            if (icon != null) {
                newTileW = icon.getIconWidth();
                newTileH = icon.getIconHeight();
                break;
            }
        }
    }

    public void loadMapSet(int mapSetNumber) {
        // Map file names
        String basePath = "bullethell/src/";
        String mapMain = basePath + "map" + mapSetNumber + "1.txt";
        String mapDecor = basePath + "map" + mapSetNumber + "2.txt";
        String mapColl = basePath + "map" + mapSetNumber + "3.txt";
        // Tile folder
        String tileFolder = "bullethell/src/Assets/tiles" + mapSetNumber;

        // Load maps
        int[][] loadedMap1 = loadMapCSV(mapMain);
        int[][] loadedMap2 = loadMapCSV(mapDecor);
        int[][] loadedMap3 = loadMapCSV(mapColl);
        if (loadedMap1 == null) throw new RuntimeException("Missing or invalid map file: " + mapMain);
        grid = loadedMap1;
        int rows = loadedMap1.length, cols = loadedMap1[0].length;
        hasMap2 = loadedMap2 != null && loadedMap2.length == rows && loadedMap2[0].length == cols;
        hasMap3 = loadedMap3 != null && loadedMap3.length == rows && loadedMap3[0].length == cols;
        map1 = loadedMap1;
        map2 = loadedMap2;
        map3 = loadedMap3;

        // Load tiles and decors
        tiles = loadSheet(tileFolder, "tile");
        decors = loadSheet(tileFolder, "decor");
        // Set tile size
        for (ImageIcon icon : tiles) {
            if (icon != null) {
                tileW = icon.getIconWidth();
                tileH = icon.getIconHeight();
                break;
            }
        }
    }
}