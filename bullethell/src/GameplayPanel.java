import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GameplayPanel extends JPanel implements MouseMotionListener, MouseListener, KeyListener {

    // ========================= ENTITY =====================================================
    private static Player player;
    private static ArrayList<Enemy> enemies = new ArrayList<>();

    // ========================= BULLET =====================================================
    private static ArrayList<Bullet> playerBullets = new ArrayList<>();
    private static ArrayList<Bullet> enemyBullets = new ArrayList<>();

    // ========================= LOGIC =====================================================
    private Random rand;
    private int spawnDelay = 1000;
    private static Timer spawnTimer;
    private static Timer gameLoop;
    static boolean gameActive = false;
    private static boolean isGameOver = false;
    private FPScounter fpscounter = new FPScounter("FPS: ");
    private static Game_clock gameClock = new Game_clock();

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

    // ========================= SFX =====================================================
    private Sfx soundsfx = new Sfx();

    // ========================= MUSIC =====================================================
    private Music musiclobby = new Music();
    private static Music music1 = new Music();

    // ========================= KEY MOVEMENT =====================================================
    private boolean upPressed, downPressed, leftPressed, rightPressed;
    
    // ========================= GAME SETTING =====================================================
    private Settingmenu settingmenu = new Settingmenu();

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

    public void startGame() {
        music1.fadeIn(1000);
        music1.loop();
        player = new Player(getWidth()/2, getHeight()/2); // Default spawn nya Player, 500 x 400 karena ukuran layar 1000 x 800, jadi di tengah
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
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    private static void gameOver() {
        isGameOver = true;
        music1.fadeOut(2500);
        // music1.stop();
        stopGame();
        gameClock.reset();
        gameClock.setVisible(false);
    }

    // ========================= SPAWN =====================================================
    private void spawnEnemy() {
        int spawnX = rand.nextInt(getWidth());
        int spawnY = rand.nextInt(getHeight());
        enemies.add(new Enemy(spawnX, spawnY));
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
        if(!gameActive) return;
        else {
            player.updateDash();
            player.move(upPressed, downPressed, leftPressed, rightPressed, grid,TILE_SIZE); // PLAYER MOVEMENT + SPRITE
            updateBullets();
            checkCollisions();
            for (Enemy enemy : enemies) {//gae musuh bisa nembak
                enemy.update(player, enemyBullets);
                Bullet bullet = enemy.tryShoot(player.getX(), player.getY());
                if (bullet != null) {
                    enemyBullets.add(bullet);
            }
            }
            //gae bullet e musuh idk why chatgpt literally makes it another new variable tp haruse bullet isa dewek so idk
            fpscounter.frameRendered();
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
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Health: " + player.getHealth() + "/" + player.getMaxHealth(), 10, 20);
        g.drawString("Dash Charges: " + player.getCurrentDashCharges() + "/" + player.getMaxDashCharges(), 10, 40);
        
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
    // ========================= FUNCTION =====================================================
    public void sfxmanager(){
        soundsfx.load("dice", "/Audio/Sfx/Dice_Roll.wav");
        soundsfx.load("shoot", "/Audio/Sfx/Atk_LeweiGun.wav");
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
            repaint();
        } else {
            gameLoop.start();
            spawnTimer.start();
            gameClock.timer.start();
            pauseMenu.setVisibility(false);
            requestFocusInWindow();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (gameActive && e.getButton() == MouseEvent.BUTTON1){
            playerBullets.add(player.shoot(e.getX()/ZOOM + cameraPixelX, e.getY()/ZOOM + cameraPixelY)); //ya tau lah iki apa dari nama function
            Sfx.playWithRandomPitch("shoot");
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
    @Override public void mouseReleased(MouseEvent e) {} // lepas mouse
    @Override public void mouseEntered(MouseEvent e) {} // masuk mouse ke dalam window
    @Override public void mouseExited(MouseEvent e) {} // ya bisa di baca sendiri lah km ws tua berjembut

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
        }

        // Check bullet-enemy collisions
        playerBullets.removeIf(bullet -> {
            Rectangle bulletBounds = new Rectangle((int)bullet.x, (int)bullet.y, bullet.getSize(), bullet.getSize());
            for (int i = enemies.size() - 1; i >= 0; i--) {
                Enemy enemy = enemies.get(i);
                Rectangle enemyBounds = new Rectangle(enemy.x, enemy.y, enemy.size, enemy.size);
                if (bulletBounds.intersects(enemyBounds)) {
                    enemies.remove(i);
                    return true;
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
        music1.fadeOut(1000);
        
        // Reset pause menu
        pauseMenu.setVisibility(false);
    }
}
