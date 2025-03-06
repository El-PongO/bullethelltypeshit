import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements MouseMotionListener, MouseListener {

    private Player player;
    private ArrayList<Bullet> bullets;
    private Random rand;
    private int spawnDelay = 1000;
    private Timer spawnTimer, gameLoop;

    private enum GameState { MENU, PLAYING, GAME_OVER }
    private GameState gameState = GameState.MENU;
    private MainMenu mainMenu = new MainMenu();

    private double bulletSpeedMultiplier = 1.0;


    public GamePanel() {
        this.player = new Player(500, 400); 
        this.bullets = new ArrayList<>();
        this.rand = new Random();

        addMouseMotionListener(this);
        addMouseListener(this);

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
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (gameState == GameState.PLAYING) {
            player.updatePosition(e.getX(), e.getY());
            repaint();
        }
    }

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
}
