import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class GameplayPanel extends JPanel implements MouseMotionListener, MouseListener, KeyListener {
    // ========================= PLAYER =====================================================
    private Player player;

    // ========================= ENEMY =====================================================
    private ArrayList<Enemy> enemies = new ArrayList<>();

    // ========================= BULLET =====================================================
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Bullet> playerBullets = new ArrayList<>();
    private ArrayList<Bullet> enemyBullets = new ArrayList<>();

    // ========================= LOGIC =====================================================
    private Random rand;
    private int spawnDelay = 1000;
    private Timer spawnTimer, gameLoop;
    private double bulletSpeedMultiplier = 1.0;
    private boolean gameActive = false;

    // ========================= KEY MOVEMENT =====================================================
    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private int mouseX, mouseY;

    // ========================= GAME STATUS =====================================================
    private boolean isGameOver = false;
    
    // ========================= MAIN =====================================================
    public GameplayPanel() {
        this.player = new Player(500, 400); 
        this.rand = new Random();

        addMouseMotionListener(this);
        addMouseListener(this);
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();

        spawnTimer = new Timer(spawnDelay, e -> {
            spawnEnemy();
            updateSpawnDelay();
        });
        spawnTimer.setRepeats(false);

        gameLoop = new Timer(16, e -> updateGame());
    }    // ========================= GAME CONTROLS =====================================================
    public void startGame() {
        player = new Player(500, 400);
        bullets.clear();
        enemies.clear();
        enemyBullets.clear();
        playerBullets.clear();
        spawnDelay = 2000;
        isGameOver = false;
        gameActive = true;
        
        // Reset key states
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
        
        spawnTimer.start();
        gameLoop.start();
        
        // Ensure this panel has focus for keyboard controls
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
        if (!gameActive) return;
        
        // Player movement
        if (upPressed && player.getY() > 12) player.move(0, -5);
        if (downPressed && player.getY() < getHeight() - 12) player.move(0, 5);
        if (leftPressed && player.getX() > 12) player.move(-5, 0);
        if (rightPressed && player.getX() < getWidth() - 12) player.move(5, 0);
        
        // Update player bullets
        for (Bullet bullet : playerBullets) {
            bullet.update();
        }
        
        ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
        ArrayList<Bullet> playerBulletsToRemove = new ArrayList<>();
        
        // Collision detection: player bullets vs enemy bullets
        for (Bullet pBullet : playerBullets) {
            for (Bullet eBullet : enemyBullets) {
                double distance = Math.sqrt(Math.pow(pBullet.getX() - eBullet.getX(), 2) + Math.pow(pBullet.getY() - eBullet.getY(), 2));
                if (distance < (pBullet.getHitboxSize() / 2 + eBullet.getHitboxSize() / 2)) {
                    bulletsToRemove.add(eBullet);
                    playerBulletsToRemove.add(pBullet);
                }
            }
        }
        enemyBullets.removeAll(bulletsToRemove);
        playerBullets.removeAll(playerBulletsToRemove);

        // Collision detection: player bullets vs enemies
        ArrayList<Enemy> enemiesToRemove = new ArrayList<>();
        playerBulletsToRemove.clear();
        
        for (Bullet pBullet : playerBullets) {
            for (Enemy enemy : enemies) {
                if (enemy.checkCollision(pBullet)) {
                    enemiesToRemove.add(enemy);
                    playerBulletsToRemove.add(pBullet);
                    break;
                }
            }
        }
        enemies.removeAll(enemiesToRemove);
        playerBullets.removeAll(playerBulletsToRemove);
        
        // Remove out-of-bounds bullets
        bullets.removeIf(Bullet::isOutOfBounds);
        
        // Update and check collision with regular bullets
        for (Bullet bullet : bullets) {
            bullet.update();
            if (player.checkCollision(bullet)) {
                gameOver();
                return;
            }
        }

        // Update enemies and their bullets
        for (Enemy enemy : enemies) {
            enemy.update(player, enemyBullets);
        }
        
        // Update enemy bullets and check collision with player
        for (Bullet bullet : enemyBullets) {
            bullet.update();
            if (player.checkCollision(bullet)) {
                gameOver();
                return;
            }
        }
        
        // Remove out-of-bounds enemy bullets
        enemyBullets.removeIf(Bullet::isOutOfBounds);
        playerBullets.removeIf(Bullet::isOutOfBounds);
        
        repaint();
    }
    
    // ========================= PAINT COMPONENT =====================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGame(g);
    }

    private void drawGame(Graphics g) {
        g.setColor(new Color(28, 51, 92));
        g.fillRect(0, 0, getWidth(), getHeight());

        player.draw(g);
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
        for (Bullet bullet : playerBullets) {
            bullet.draw(g);
        }
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
        for (Bullet bullet : enemyBullets) {
            bullet.draw(g);
        }
    }
    
    // ========================= INPUT HANDLING =====================================================
    @Override
    public void mousePressed(MouseEvent e) {
        if (gameActive && e.getButton() == MouseEvent.BUTTON1) {
            player.shootBullet(e.getX(), e.getY(), playerBullets);
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
    
    // Required interface methods
    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
