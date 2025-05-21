import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class GameplayPanel extends JPanel implements MouseMotionListener, MouseListener, KeyListener {

    // ========================= ENTITY =====================================================
    private Player player;
    private ArrayList<Enemy> enemies = new ArrayList<>();

    // ========================= BULLET =====================================================
    private ArrayList<Bullet> playerBullets = new ArrayList<>();
    private ArrayList<Bullet> enemyBullets = new ArrayList<>();

    // ========================= LOGIC =====================================================
    private Random rand;
    private int spawnDelay = 1000;
    private Timer spawnTimer, gameLoop;
    private double bulletSpeedMultiplier = 1.0;
    private boolean gameActive = false;
    private boolean isGameOver = false;

    // ========================= SFX =====================================================
    private Sfx soundsfx = new Sfx();

    // ========================= MUSIC =====================================================
    private Music musiclobby = new Music();
    private Music music1 = new Music();

    // ========================= KEY MOVEMENT =====================================================
    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private int mouseX, mouseY;//beberapa tambahan idk mousex sama mousey gae apa aku agak minta chatgpt soale:" )
    
    // ========================= MAIN =====================================================
    public GameplayPanel() {
        this.player = new Player(getWidth()/2,getHeight()/2); 
        this.rand = new Random();

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
    }

    public void startGame() {
        music1.fadeIn(3000);
        music1.loop();
        player = new Player(getWidth()/2, getHeight()/2); // Default spawn nya Player, 500 x 400 karena ukuran layar 1000 x 800, jadi di tengah
        enemies.clear();//spawn enemy sama pelurunya
        enemyBullets.clear();
        playerBullets.clear();
        spawnDelay = 2000;

        isGameOver = false;
        gameActive = true;

        spawnTimer.start();
        gameLoop.start();
        
        // set button to invisible when game start
        requestFocusInWindow();        
    }

    public void stopGame() {
        gameActive = false;
        spawnTimer.stop();
        gameLoop.stop();
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    private void gameOver() {
        isGameOver = true;
        music1.fadeOut(2500);
        stopGame();
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
            player.move(upPressed, downPressed, leftPressed, rightPressed,getHeight(),getWidth()); // PLAYER MOVEMENT + SPRITE
            
            for (Bullet bullet : playerBullets) {
                bullet.update();
            }
            for (Bullet bullet : enemyBullets) {
                bullet.update();
            }
            if(player.checkCollision(enemies,enemyBullets)){ // PLAYER COLLISION
                gameOver();
                return;
            }
            ArrayList<Bullet> removeBullet = new ArrayList<>();
            ArrayList<Enemy> removeEnemy = new ArrayList<>();
            for (Enemy enemy : enemies){ // ENEMY COLLISION
                Bullet check = enemy.checkCollision(playerBullets);
                if(!(check==null)){
                    removeBullet.add(check);
                    removeEnemy.add(enemy);
                }
            }
            
            enemyBullets.removeAll(removeBullet);
            playerBullets.removeAll(removeBullet);
            enemies.removeAll(removeEnemy);
            playerBullets.removeIf(Bullet::isOutOfBounds);
            enemyBullets.removeIf(Bullet::isOutOfBounds);
        
            for (Enemy enemy : enemies) {//gae musuh bisa nembak
                enemy.update(player, enemyBullets);
            }
            //gae bullet e musuh idk why chatgpt literally makes it another new variable tp haruse bullet isa dewek so idk
            repaint();
        }
    }

    
    // ========================= PAINT COMPONENT =====================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGame((Graphics2D)g);
    }

    GameMap map = new GameMap(); // buat map
    private void drawGame(Graphics2D g) {        
        // Draw background
        g.setColor(new Color(28, 51, 92));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw map first
        map.draw(g, player);
        
        // Draw game objects
        player.draw(g);
        for (Bullet bullet : playerBullets) {
            bullet.draw(g);//pelurue kene
        }
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
        for (Bullet bullet : enemyBullets) {
            bullet.draw(g);
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

    @Override
    public void mousePressed(MouseEvent e) {
        if (gameActive && e.getButton() == MouseEvent.BUTTON1){
            player.shootBullet(e.getX(), e.getY(), playerBullets); //ya tau lah iki apa dari nama function
            Sfx.play("shoot");
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) upPressed = true;
        if (code == KeyEvent.VK_S) downPressed = true;
        if (code == KeyEvent.VK_A) leftPressed = true;
        if (code == KeyEvent.VK_D) rightPressed = true;
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
}
