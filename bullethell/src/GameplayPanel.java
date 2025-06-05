import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;
import players.Bullet;
import players.Gunslinger;
import players.Player;
import players.Weapon;


public class GameplayPanel extends JPanel implements MouseMotionListener, MouseListener, KeyListener {

    // ========================= ENTITY =====================================================
    private static Player player;
    private static ArrayList<Enemy> enemies = new ArrayList<>();

    // ========================= BULLET =====================================================
    private static ArrayList<Bullet> playerBullets = new ArrayList<>();
    private static ArrayList<Bullet> enemyBullets = new ArrayList<>();    // ========================= LOGIC =====================================================
    private static JFrame window;
    private Random rand;
    private int spawnDelay = 1000;
    private static Timer spawnTimer;
    private static Timer gameLoop;
    static boolean gameActive = false;
    private static boolean isGameOver = false;
    private FPScounter fpscounter = new FPScounter("FPS: ");
    private static Game_clock gameClock = new Game_clock();
    private static int score = 0; // Player score

    static int[][] grid;
    static BufferedImage[] tilesSprite = new BufferedImage[9];
    static final int TILE_SIZE = 32;
    static final int VIEWPORT_WIDTH = 16; 
    static final int VIEWPORT_HEIGHT = 12;
    static final int ZOOM = 2; 
    static final int MAP_WIDTH = 16;  // Add map dimensions
    static final int MAP_HEIGHT = 12;
    static int vpw = VIEWPORT_WIDTH * TILE_SIZE;  // Viewport dimensions in pixels
    static int vph = VIEWPORT_HEIGHT * TILE_SIZE;
    static int cameraPixelX, cameraPixelY;
    CursorManager cursormanager = new CursorManager();
    private boolean mouseHeld = false;
    private boolean showOutOfAmmoMsg = false;
    private long outOfAmmoMsgTime = 0;
    private static final int OUT_OF_AMMO_MSG_DURATION = 1000; // ms
    // ========================= SFX =====================================================
    private Sfx soundsfx = new Sfx();
    private long lastShotgunShotTime = 0;
    private boolean shotgunLoadQueued = false;
    private boolean shotgunLockQueued = false;
    private long lastEmptySfxTime = 0;
    private static final int EMPTY_SFX_DELAY = 400; // ms, adjust to match your empty SFX duration
    private boolean emptySfxQueued = false;
    // ========================= MUSIC =====================================================
    private Music musiclobby = new Music();
    private static Music music1 = new Music();

    // ========================= KEY MOVEMENT =====================================================
    private boolean upPressed, downPressed, leftPressed, rightPressed;
    
    // ========================= GAME SETTING =====================================================
    private static Settingmenu settingmenu = new Settingmenu(window);

    // ========================= PAUSE MENU =====================================================
    PauseMenu pauseMenu;
    private boolean isPaused = false;

    // ========================= MAIN =====================================================
    public GameplayPanel(FPScounter fpscounter) throws Exception {
        this.rand = new Random();
        grid = loadMapFromFile("bullethell/src/map.txt");
        for (int i = 0; i < 9; i++) {
            int idx;
            switch (i) {
                case 0: idx = 23; break;
                case 1: idx = 1; break;
                case 2: idx = 22; break;
                case 3: idx = 24; break;
                case 4: idx = 45; break;
                case 5: idx = 0; break;
                case 6: idx = 2; break;
                case 7: idx = 44; break;
                default: idx = 46; break;
            }
            tilesSprite[i] = ImageIO.read(App.class.getResource("Assets/tile/tile0" + String.format("%02d", idx) + ".png"));
        }
        add(gameClock.label);
        addMouseMotionListener(this);
        addMouseListener(this);
        addKeyListener(this);
        setFocusable(true);//gae keyboard
        requestFocusInWindow();
        // innitmenubutton(); // buat button
        sfxmanager();
        musicmanager();

        spawnTimer = new Timer(spawnDelay, e -> {
            spawnEnemy();
            updateSpawnDelay();
        });
        spawnTimer.setRepeats(false);

        gameLoop = new Timer(16, e -> updateGame());
        setLayout(null);
        initializeFPSconfig(fpscounter);

        // PAUSE MENU INITIALIZATION
        pauseMenu = new PauseMenu();
        add(pauseMenu);
        setComponentZOrder(pauseMenu, 0); // Ensure pause menu is always on top
    }

    public void setPlayer(Player p) {
        player = p;
    }

    public void startGame() {
        if (controlFade()){
            music1.fadeIn(1000);
        }
        music1.loop();
        if (player == null) {
            player = new Gunslinger(getWidth()/2, getHeight()/2); // fallback default
        } else {
            // Always reset the selected hero's position to center
            player.setPosition(getWidth()/2, getHeight()/2);
        }
        enemies.clear();//spawn enemy sama pelurunya
        enemyBullets.clear();
        playerBullets.clear();
        spawnDelay = 2000;

        isGameOver = false;
        gameActive = true;

        gameClock.setPosition(getWidth()/2, 5, 100, 50);
        gameClock.setVisible(true);
        gameClock.timer.start();
        
        spawnTimer.start();
        gameLoop.start();
        
        // set button to invisible when game start
        requestFocusInWindow();        
    }

    public static void stopGame() {
        gameActive = false;
        spawnTimer.stop();
        gameLoop.stop();
        return;
    }    public boolean isGameOver() {
        return isGameOver;
    }
    
    public static int getScore() {
        return score;
    }

    private static void gameOver() {
        isGameOver = true;
        // if (controlFade()){
        //     music1.fadeOut(2500);
        // }else{
        //     music1.stop();
        // }
        music1.stop();
        stopGame();
        gameClock.reset();
        gameClock.setVisible(false);
    }    // ========================= SPAWN =====================================================
    private void spawnEnemy() {
        int spawnX = rand.nextInt(getWidth());
        int spawnY = rand.nextInt(getHeight());
        
        // Randomly choose an enemy type based on game progress
        int enemyType = rand.nextInt(100);
        Enemy newEnemy;
        
        // The longer the game runs, the more varied and difficult enemies will spawn
        long gameTime = gameClock.getElapsedTime() / 1000; // Time in seconds
        
        if (gameTime < 10) {
            // Early game - mostly normal enemies
            newEnemy = new NormalEnemy(spawnX, spawnY);
        } else if (gameTime < 30) {
            // Mid game - normal and shooter enemies
            if (enemyType < 70) {
                newEnemy = new NormalEnemy(spawnX, spawnY);
            } else {
                newEnemy = new ShooterEnemy(spawnX, spawnY);
            }
        } else if (gameTime < 60) {
            // Late mid-game - add lurkers and bombers
            if (enemyType < 40) {
                newEnemy = new NormalEnemy(spawnX, spawnY);
            } else if (enemyType < 70) {
                newEnemy = new ShooterEnemy(spawnX, spawnY);
            } else if (enemyType < 85) {
                newEnemy = new LurkerEnemy(spawnX, spawnY);
            } else {
                newEnemy = new BomberEnemy(spawnX, spawnY);
            }
        } else {
            // Late game - all enemy types including tanks
            if (enemyType < 30) {
                newEnemy = new NormalEnemy(spawnX, spawnY);
            } else if (enemyType < 55) {
                newEnemy = new ShooterEnemy(spawnX, spawnY);
            } else if (enemyType < 75) {
                newEnemy = new LurkerEnemy(spawnX, spawnY);
            } else if (enemyType < 90) {
                newEnemy = new BomberEnemy(spawnX, spawnY);
            } else {
                newEnemy = new TankEnemy(spawnX, spawnY);
            }
        }
          enemies.add(newEnemy);
        System.out.println("Spawned " + newEnemy.getClass().getSimpleName() + " at " + gameTime + " seconds");
        spawnTimer.setInitialDelay(spawnDelay);
        spawnTimer.restart();
    }
      // ========================= GAME UPDATE =====================================================
    private void updateSpawnDelay() {
        // Make spawn rate faster as game progresses
        long gameTime = gameClock.getElapsedTime() / 1000; // Time in seconds
        
        if (gameTime < 30) {
            // Early game - gradually decrease to 800ms
            spawnDelay = Math.max(800, 2000 - (int)(gameTime * 40));
        } else if (gameTime < 60) {
            // Mid game - gradually decrease to 600ms
            spawnDelay = Math.max(600, 800 - (int)((gameTime - 30) * 7));
        } else {
            // Late game - gradually decrease to 400ms (hard cap)
            spawnDelay = Math.max(400, 600 - (int)((gameTime - 60) * 4));
        }
    }    private void updateGame() {
        if(!gameActive) return;
        else {
            player.updateDash();
            player.move(upPressed, downPressed, leftPressed, rightPressed, grid,TILE_SIZE); // PLAYER MOVEMENT + SPRITE
            updateBullets();
            checkCollisions();
            for (Enemy enemy : enemies) {
                enemy.update(player, enemyBullets);
            }
            //gae bullet e musuh idk why chatgpt literally makes it another new variable tp haruse bullet isa dewek so idk
            if (mouseHeld && player.getCurrentWeapon().isFullAuto()) {
                Point mouse = getMousePosition();
                List<Bullet> bullet = player.shoot(mouse.x/ZOOM + cameraPixelX, mouse.y/ZOOM + cameraPixelY);
                if (mouse != null) {
                    // Bullet bullet = player.shoot(mouse.x/ZOOM + cameraPixelX, mouse.y/ZOOM + cameraPixelY);
                    if (bullet != null) {
                        playerBullets.addAll(bullet);
                        playsfx(false);
                    }else if (!player.getCurrentWeapon().hasAmmo()) {
                        // Only play empty SFX if enough time has passed
                        long now = System.currentTimeMillis();
                        if (now - lastEmptySfxTime >= EMPTY_SFX_DELAY) {
                            playsfx(true);
                            lastEmptySfxTime = now;
                        }
                    }
                }
            }
            if (shotgunLoadQueued) {
                int shotgunFireRate = 600; // ms, adjust to your shotgun's fire rate
                if (System.currentTimeMillis() - lastShotgunShotTime >= shotgunFireRate) {
                    soundsfx.playWithRandomPitch("shotgunload");
                    shotgunLoadQueued = false;
                }
            }

            if (shotgunLockQueued) {
                int shotgunFireRate = 600; // ms, adjust as needed
                if (System.currentTimeMillis() - lastShotgunShotTime >= shotgunFireRate) {
                    soundsfx.playWithRandomPitch("shotgunlock");
                    shotgunLockQueued = false;
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
        drawGame((Graphics2D)g);

        // Center camera on player with bounds checking
        cameraPixelX = Math.max(0, Math.min(player.getX() - vpw/2, (grid[0].length * TILE_SIZE) - vpw));
        cameraPixelY = Math.max(0, Math.min(player.getY() - vph/2, (grid.length * TILE_SIZE) - vph));

        // Calculate visible tile range
        int startY = cameraPixelY / TILE_SIZE;
        int startX = cameraPixelX / TILE_SIZE;
        int endY = (cameraPixelY + vph) / TILE_SIZE + 1;
        int endX = (cameraPixelX + vpw) / TILE_SIZE + 1;

        // Draw visible tiles
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                // Convert tile position to screen coordinates
                int drawX = (x * TILE_SIZE - cameraPixelX) * ZOOM;
                int drawY = (y * TILE_SIZE - cameraPixelY) * ZOOM;
                
                // Draw tile or background if out of bounds
                if (y >= 0 && y < grid.length && x >= 0 && x < grid[0].length) {
                    g.drawImage(tilesSprite[grid[y][x]], drawX, drawY, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM, null);
                } else {
                    g.setColor(Color.BLUE);
                    g.fillRect(drawX, drawY, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);
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
            enemy.draw(g,ex,ey);
        }
        for (Bullet bullet : enemyBullets) {
            int ex = (bullet.x - cameraPixelX) * ZOOM;
            int ey = (bullet.y - cameraPixelY) * ZOOM;
            bullet.draw(g, ex, ey, Color.RED);
        }        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Health: " + player.getHealth() + "/" + player.getMaxHealth(), 10, 20);
        g.drawString("Dash Charges: " + player.getCurrentDashCharges() + "/" + player.getMaxDashCharges(), 10, 40);
        g.drawString("Score: " + score, 10, 60);
        drawWeaponHUD((Graphics2D)g);
        if (isPaused) {
            pauseMenu.draw(g, getWidth(), getHeight());
        }
    }
    
    private void drawGame(Graphics2D g) {        
        // Draw background
        cameraPixelX = Math.max(0, Math.min(player.getX() - vpw/2, (grid[0].length * TILE_SIZE) - vpw));
        cameraPixelY = Math.max(0, Math.min(player.getY() - vph/2, (grid.length * TILE_SIZE) - vph));
        int startY = cameraPixelY / TILE_SIZE;
        int startX = cameraPixelX / TILE_SIZE;
        int endY = (cameraPixelY + vph) / TILE_SIZE + 1;
        int endX = (cameraPixelX + vpw) / TILE_SIZE + 1;
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                // Convert tile position to screen coordinates
                int drawX = (x * TILE_SIZE - cameraPixelX) * ZOOM;
                int drawY = (y * TILE_SIZE - cameraPixelY) * ZOOM;
                
                // Draw tile or background if out of bounds
                if (y >= 0 && y < grid.length && x >= 0 && x < grid[0].length) {
                    g.drawImage(tilesSprite[grid[y][x]], drawX, drawY, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM, null);
                } else {
                    g.setColor(Color.BLUE);
                    g.fillRect(drawX, drawY, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);
                }
            }
        }
    }

    private void drawWeaponHUD(Graphics2D g) {
        Weapon currentWeapon = player.getCurrentWeapon();
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
            g.drawImage(player.changewp, offsetX + iconSize - changeSize/2, offsetY + iconSize - changeSize/2, changeSize, changeSize, null);
        }
        
        if (currentWeapon.isReloading()) {
            g.setColor(Color.ORANGE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("Reloading...", offsetX - 8, offsetY - 4);
        }

        g.setColor(new Color(255, 215, 0, 180));
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(offsetX-2, offsetY-2, iconSize+4, iconSize+4, 10, 10);
    }

    // ========================= FUNCTION =====================================================
    public void sfxmanager(){
        soundsfx.load("dice", "/Audio/Sfx/Dice_Roll.wav");
        soundsfx.load("shoot", "/Audio/Sfx/Atk_LeweiGun.wav");
        soundsfx.load("empty", "/Audio/Sfx/wep_empty.wav");
        // revolver
        soundsfx.load("shootrevolver", "/Audio/Sfx/rev_shot.wav");
        soundsfx.load("emptyrevolver", "/Audio/Sfx/rev_empty.wav");
        // shotgun
        soundsfx.load("shootshotgun", "/Audio/Sfx/shotgun_fire.wav");
        soundsfx.load("shotgunload", "/Audio/Sfx/shotgun_load.wav");
        soundsfx.load("shotgunreload", "/Audio/Sfx/shotgun_reload.wav");
        soundsfx.load("shotgunlock", "/Audio/Sfx/shotgun_lock.wav");
        soundsfx.load("shogunempty", "/Audio/Sfx/shotgun_empty.wav");
    }

    public void playsfx(boolean isEmpty){
        if (!isEmpty) {
            if (player.getCurrentWeaponIndex() == 0){
                soundsfx.playWithRandomPitch("shootrevolver");
            }else if (player.getCurrentWeaponIndex() == 3){
                soundsfx.playWithRandomPitch("shootshotgun");
                lastShotgunShotTime = System.currentTimeMillis();
                Weapon Shotgun = player.getCurrentWeapon();
                if (Shotgun.getCurrentAmmo() == 0){
                    shotgunLockQueued = true;
                }else{
                    shotgunLoadQueued = true;
                }
                
            }else{
                soundsfx.playWithRandomPitch("shoot");
            }
        }else{
            if (player.getCurrentWeaponIndex() == 0) {
                soundsfx.play("emptyrevolver");
            }else if (player.getCurrentWeaponIndex() == 3) {
                soundsfx.play("shogunempty");
            }else{
                soundsfx.play("empty");
            }
        }
    }

    public void reloadsfx(){
        if (player.getCurrentWeaponIndex() == 3){
            soundsfx.play("shotgunreload");
        }else{
            
        }
    }

    public void musicmanager(){
        musiclobby.load("/Audio/Music/lobby.wav");
        music1.load("/Audio/Music/game1.wav");
    }

    public void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            gameLoop.stop();
            spawnTimer.stop();
            gameClock.timer.stop();
            pauseMenu.setVisibility(true);
            if (getParent() instanceof MainPanel) {
                ((MainPanel)getParent()).cursormanager.setCursor(getParent(), "cursor");
            }
            repaint();
        } else {
            gameLoop.start();
            spawnTimer.start();
            gameClock.timer.start();
            pauseMenu.setVisibility(false);
            requestFocusInWindow();
            if (getParent() instanceof MainPanel) {
                ((MainPanel)getParent()).cursormanager.setCursor(getParent(), "crosshair");
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (gameActive && e.getButton() == MouseEvent.BUTTON1){
            List<Bullet> bullet = player.shoot(e.getX()/ZOOM + cameraPixelX, e.getY()/ZOOM + cameraPixelY);
            if (bullet != null && !bullet.isEmpty()) {
                playerBullets.addAll(bullet);
                soundsfx.playWithRandomPitch("shoot");
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_ESCAPE && gameActive) {
            togglePause();
            return;
        }
        if (code == KeyEvent.VK_W) upPressed = true;
        if (code == KeyEvent.VK_S) downPressed = true;
        if (code == KeyEvent.VK_A) leftPressed = true;
        if (code == KeyEvent.VK_D) rightPressed = true;
        if (code == KeyEvent.VK_SHIFT) {
            player.dash(); // dash
            System.out.println("Dash activated! " + player.direction);
        }
        if (code == KeyEvent.VK_Q){
            if (player.getCurrentWeaponIndex() == player.getWeaponMinIndex()){
                player.switchWeapon(player.getWeaponMaxIndex()); // switch to max 
            }else{
                player.switchWeapon(-1); // switch weapon left
            }
        }  

        if (code == KeyEvent.VK_E){
            if (player.getCurrentWeaponIndex() == player.getWeaponMaxIndex()){
                player.setWeaponIndex(player.getWeaponMinIndex()); // wrap to min
            }else{
                player.switchWeapon(1); // switch weapon right
            }
        }

        if (code == KeyEvent.VK_R) {
            player.reloadCurrentWeapon();
            reloadsfx();
            System.out.println("Reloading " + player.getCurrentWeapon().getName() + "...");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) upPressed = false;
        if (code == KeyEvent.VK_S) downPressed = false;
        if (code == KeyEvent.VK_A) leftPressed = false;
        if (code == KeyEvent.VK_D) rightPressed = false;
    }

    @Override public void keyTyped(KeyEvent e) {} // unused
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {} //gerakkan mouse
    @Override public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseHeld = false;
        }
    } // lepas mouse
    @Override public void mouseEntered(MouseEvent e) {} // masuk mouse ke dalam window
    @Override public void mouseExited(MouseEvent e) {} // ya bisa di baca sendiri lah km ws tua berjembut

    private void tryFire(MouseEvent e) {
        Weapon weapon = player.getCurrentWeapon();
        if (weapon.isFullAuto()) {
            // For full auto, firing is handled in updateGame()
        } else {
            // Bullet bullet = player.shoot(e.getX()/ZOOM + cameraPixelX, e.getY()/ZOOM + cameraPixelY);
            List<Bullet> bullet = player.shoot(e.getX()/ZOOM + cameraPixelX, e.getY()/ZOOM + cameraPixelY);
            if (bullet != null) {
                // playerBullets.add(bullet);
                playerBullets.addAll(bullet);
                playsfx(false);
            }else if (!weapon.hasAmmo()) {
                // Show out of ammo message
                playsfx(true);
                showOutOfAmmoMsg = true;
                outOfAmmoMsgTime = System.currentTimeMillis();
            }
        }
    }

    private static void updateBullets() {
        // Update existing bullets
        playerBullets.removeIf(bullet -> {
            bullet.update();
            // Remove bullets that are out of bounds or hit walls
            return bullet.isOutOfBounds(grid, TILE_SIZE);
        });

        // Update enemy bullets
        enemyBullets.removeIf(bullet -> {
            bullet.update();
            return bullet.isOutOfBounds(grid, TILE_SIZE);
        });
    }

    private static void checkCollisions() {
        // hitboxnya aku kecilin dikit (90% dari sprite)
        int fullSize = player.getSize();
        int hitboxSize = (int)(fullSize * 0.9);
        int offset = (fullSize - hitboxSize) / 2;

        Rectangle playerBounds = new Rectangle(player.getX() + offset, player.getY() + offset, hitboxSize, hitboxSize);
        for (Enemy enemy : enemies) {
            Rectangle enemyBounds = new Rectangle(enemy.x, enemy.y, enemy.size, enemy.size);
            if (playerBounds.intersects(enemyBounds) && !player.isInvincible()) {
                System.out.println("Player hit!");
                player.takeDamage(50);
                if (player.isDead()) {
                    gameOver();
                }
            }
        }        // Check bullet-enemy collisions
        playerBullets.removeIf(bullet -> {
            Rectangle bulletBounds = new Rectangle((int)bullet.x, (int)bullet.y, bullet.getSize(), bullet.getSize());
            for (int i = enemies.size() - 1; i >= 0; i--) {
                Enemy enemy = enemies.get(i);
                Rectangle enemyBounds = new Rectangle(enemy.x, enemy.y, enemy.size, enemy.size);
                if (bulletBounds.intersects(enemyBounds)) {
                    // Apply damage to enemy based on bullet damage
                    int damage = bullet.getDamage() != 0 ? bullet.getDamage() : 1; // Default damage is 1
                    boolean isDead = enemy.takeDamage(damage);
                    
                    if (isDead) {
                        // Award points based on enemy type
                        if (enemy instanceof NormalEnemy) {
                            score += 10;
                        } else if (enemy instanceof ShooterEnemy) {
                            score += 20;
                        } else if (enemy instanceof LurkerEnemy) {
                            score += 30;
                        } else if (enemy instanceof BomberEnemy) {
                            score += 40;
                        } else if (enemy instanceof TankEnemy) {
                            score += 50;
                        }
                        System.out.println("Score: " + score);
                        enemies.remove(i);
                    }
                    return true; // Remove bullet either way when it hits
                }
            }
            return false;
        });

        // Check enemy bullet-player collisions
        enemyBullets.removeIf(bullet -> {
            Rectangle bulletBounds = new Rectangle(bullet.x, bullet.y, bullet.getSize(), bullet.getSize());
            if (bulletBounds.intersects(playerBounds) && !player.isInvincible()) {
                System.out.println("Player hit by enemy bullet!");
                player.takeDamage(50);
                if (player.isDead()) {
                    gameOver();
                }
                return true;
            }
            return false;
        });
    }

    public static int[][] loadMapFromFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        int rows = 0, cols = 0;
        while ((line = reader.readLine()) != null) {
            cols = line.length();
            rows++;
        }
        reader.close();
        int[][] grid = new int[rows][cols];
        reader = new BufferedReader(new FileReader(filePath));
        int row = 0;
        while ((line = reader.readLine()) != null) {
            for (int col = 0; col < line.length(); col++) {
                grid[row][col] = Character.getNumericValue(line.charAt(col));
            }
            row++;
        }
        reader.close();
        return grid;
    }

    // GAME SETTINGS
    public static boolean controlFade() {
        if (settingmenu.getDisableFade().isSelected()){
            return false;
        }else{
            return true;
        }
    }

    private void initializeFPSconfig(FPScounter fpscounter){
        this.fpscounter = fpscounter;
        add(fpscounter);
        updateFPSCounterVisibility();
        positionFPSCounter();
        addFPSCounterResizeListener();
    }

    private void updateFPSCounterVisibility() {
        fpscounter.setVisible(settingmenu.getFpsCheckbox().isSelected());
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
    }    public void resetGame() {
        // Reset game state
        gameActive = false;
        isGameOver = false;
        isPaused = false;
        score = 0; // Reset score
        
        // Clear all entities
        enemies.clear();
        playerBullets.clear();
        enemyBullets.clear();
        
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
        // music1.fadeOut(3000);
        music1.stop();
        
        // Reset pause menu
        pauseMenu.setVisibility(false);
    }
}
