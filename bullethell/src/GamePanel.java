import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements MouseMotionListener, MouseListener, KeyListener {

    private Player player;
    private ArrayList<Bullet> bullets;
    private Random rand;
    private int spawnDelay = 1000;
    private Timer spawnTimer, gameLoop;

    private enum GameState { MENU, PLAYING, GAME_OVER }
    private GameState gameState = GameState.MENU;
    private MainMenu mainMenu = new MainMenu();

    private double bulletSpeedMultiplier = 1.0;

    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private int mouseX, mouseY;//beberapa tambahan idk mousex sama mousey gae apa aku agak minta chatgpt soale:" )
    private ArrayList<Bullet> playerBullets = new ArrayList<>();

    public GamePanel() {
        this.player = new Player(500, 400); 
        this.bullets = new ArrayList<>();
        this.rand = new Random();

        addMouseMotionListener(this);
        addMouseListener(this);
        addKeyListener(this);
        setFocusable(true);//gae keyboard
        requestFocusInWindow();

        spawnTimer = new Timer(spawnDelay, e -> {
            spawnBullet();
            updateSpawnDelay();
        });
        spawnTimer.setRepeats(false);

        gameLoop = new Timer(16, e -> updateGame());
    }

    private void startGame() {
        gameState = GameState.PLAYING;
        player = new Player(500, 400); // Default spawn nya Player, 500 x 400 karena ukuran layar 1000 x 800, jadi di tengah
        bullets.clear();
        spawnDelay = 2000;
        spawnTimer.start();
        gameLoop.start();
    }

    private void updateSpawnDelay() {
        if (spawnDelay > 300) {
            spawnDelay -= 50;
        }
    }
    

    private void spawnBullet() {
        int baseSpeed = 2 + rand.nextInt(3); // speed bullet mulai dari 2 + random 1-3
        int speed = (int) (baseSpeed * bulletSpeedMultiplier);

        bullets.add(new Bullet(player.getX(), player.getY(), speed));

        if (bulletSpeedMultiplier < 2.0) {
            bulletSpeedMultiplier += 0.1; // ini, tiap 1 bullet spawn, speed akan di tambah 0.1, bisa jadi chaotic saat 10+ detik
        }

        if (spawnDelay > 300) {
            spawnDelay -= 50; // ini juga buat biar game nya lebih susah, setiap 1 bullet spawn, delay nya akan berkurang 50ms
        }

        spawnTimer.setInitialDelay(spawnDelay);
        spawnTimer.restart();
    }

    private void updateGame() {
        if (gameState == GameState.PLAYING) {
            if (upPressed) player.move(0, -5);//gae wasd
            if (downPressed) player.move(0, 5);
            if (leftPressed) player.move(-5, 0);
            if (rightPressed) player.move(5, 0);
            for (Bullet bullet : playerBullets) {
                bullet.update();
            }
            ArrayList<Bullet> bulletsToRemove = new ArrayList<>();//dari sini{
            ArrayList<Bullet> playerBulletsToRemove = new ArrayList<>();

            for (Bullet pBullet : playerBullets) {
                for (Bullet eBullet : bullets) {
                    double distance = Math.sqrt(Math.pow(pBullet.getX() - eBullet.getX(), 2) + Math.pow(pBullet.getY() - eBullet.getY(), 2));
                    if (distance < (pBullet.getHitboxSize() / 2 + eBullet.getHitboxSize() / 2)) {
                        bulletsToRemove.add(eBullet);
                        playerBulletsToRemove.add(pBullet);
                    }
                }
            }

            bullets.removeAll(bulletsToRemove);
            playerBullets.removeAll(playerBulletsToRemove);//sampai sini} itu gae peluru musuh ngilang lek kene tembak
            bullets.removeIf(Bullet::isOutOfBounds); // fungsi untuk menghapus bullet yang keluar layar (agak sulit jelasinya, apalahi di pahami)
            for (Bullet bullet : bullets) {
                bullet.update();
                if (player.checkCollision(bullet)) {
                    gameOver(); // cek collsion
                    return;
                }
            }
            repaint();
        }
    }

    private void gameOver() {
        gameState = GameState.GAME_OVER;
        spawnTimer.stop();
        gameLoop.stop();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameState == GameState.MENU) {
            drawMenu(g);
        } else if (gameState == GameState.PLAYING) {
            drawGame(g);
        } else if (gameState == GameState.GAME_OVER) {
            drawGameOver(g);
        }
    }

    private void drawMenu(Graphics g) {
        mainMenu.draw(g, getWidth(), getHeight()); //untuk mempelajari lebih lanjut liat bro code di YT: https://www.youtube.com/watch?v=KcEvHq8Pqs0
    }                                               // (ak gk di sponsor untuk bilang ini)           

    private void drawGame(Graphics g) {
        g.setColor(new Color(28, 51, 92));
        g.fillRect(0, 0, getWidth(), getHeight());

        player.draw(g);
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
        for (Bullet bullet : playerBullets) {
            bullet.draw(g);//pelurue kene
        }
    }

    private void drawGameOver(Graphics g) {
        g.setColor(new Color(28, 51, 92));
        g.fillRect(0, 0, getWidth(), getHeight()); // gambar Game over
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("Game Over", 380, 300);
        g.setFont(new Font("Arial", Font.PLAIN, 30));
        g.drawString("Click to Restart", 420, 400);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (gameState == GameState.MENU || gameState == GameState.GAME_OVER) {
            startGame();
        } else if (gameState == GameState.PLAYING && e.getButton() == MouseEvent.BUTTON1) { // Left click
            shootBullet(e.getX(), e.getY()); //ya tau lah iki apa dari nama function
        }
    }
    private void shootBullet(int targetX, int targetY) {
        Bullet b = new Bullet(player.getX(), player.getY(), targetX, targetY, 10);
        b.setColor(Color.GREEN);//gae buat warna bullet seng ditembak player hijau
        playerBullets.add(b);
    }
    


    // @Override
    // public void mouseMoved(MouseEvent e) {
    //     if (gameState == GameState.PLAYING) {
            
    //         repaint();
    //     } iki tak comment soal ga dipakai sempet keapus so ye im sorry but iki gae movement pakai mouse tadi tp ws tak ganti pakai keyboard
    // }

    @Override
    public void mouseDragged(MouseEvent e) {} //gerakkan mouse

    @Override
    public void mouseClicked(MouseEvent e) {} // click mouse (buat main menu dan gameover)
    @Override
    public void mouseReleased(MouseEvent e) {} // lepas mouse
    @Override
    public void mouseEntered(MouseEvent e) {} // masuk mouse ke dalam window
    @Override
    public void mouseExited(MouseEvent e) {} // ya bisa di baca sendiri lah km ws tua berjembut

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameState != GameState.PLAYING) return;
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) upPressed = true;
        if (code == KeyEvent.VK_S) downPressed = true;
        if (code == KeyEvent.VK_A) leftPressed = true;
        if (code == KeyEvent.VK_D) rightPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameState != GameState.PLAYING) return;
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) upPressed = false;
        if (code == KeyEvent.VK_S) downPressed = false;
        if (code == KeyEvent.VK_A) leftPressed = false;
        if (code == KeyEvent.VK_D) rightPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {} // unused

}
