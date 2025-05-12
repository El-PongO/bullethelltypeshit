import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements MouseMotionListener, MouseListener, KeyListener {

    // ========================= PLAYER =====================================================
    private Player player;

    // ========================= ENEMY =====================================================
    private ArrayList<Enemy> enemies = new ArrayList<>();

    // ========================= BULLET =====================================================
    private ArrayList<Bullet> bullets;
    private ArrayList<Bullet> playerBullets = new ArrayList<>();
    private ArrayList<Bullet> enemyBullets = new ArrayList<>();

    // ========================= LOGIC =====================================================
    private Random rand;
    private int spawnDelay = 1000;
    private Timer spawnTimer, gameLoop;
    private enum GameState { MENU, PLAYING, GAME_OVER, SETTING }
    private GameState gameState = GameState.MENU;
    private double bulletSpeedMultiplier = 1.0;

    // ========================= UI =====================================================
    private MainMenu mainMenu = new MainMenu();
    private Settingmenu settingmenu = new Settingmenu();
    private Game_over gameover = new Game_over();

    // ========================= KEY MOVEMENT =====================================================
    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private int mouseX, mouseY;//beberapa tambahan idk mousex sama mousey gae apa aku agak minta chatgpt soale:" )
    
    
    // ========================= BUTTON =====================================================
    Button buttonstart = new Button("Play");
    Button buttonoption = new Button("Option");
    Button buttonexit = new Button("Exit");
    Button buttonrestart = new Button("Restart");
    Button buttonback = new Button("Back");

    // ========================= MAIN =====================================================
    public GamePanel() {
        this.player = new Player(500, 400); 
        this.bullets = new ArrayList<>();
        this.rand = new Random();

        addMouseMotionListener(this);
        addMouseListener(this);
        addKeyListener(this);
        setFocusable(true);//gae keyboard
        requestFocusInWindow();
        innitmenubutton(); // buat button

        spawnTimer = new Timer(spawnDelay, e -> {
            // spawnBullet();
            spawnEnemy();
            updateSpawnDelay();
        });
        spawnTimer.setRepeats(false);

        gameLoop = new Timer(16, e -> updateGame());
    }

    private void startGame() {
        gameState = GameState.PLAYING;
        player = new Player(500, 400); // Default spawn nya Player, 500 x 400 karena ukuran layar 1000 x 800, jadi di tengah
        bullets.clear();// skrg cuman buat bullet player
        enemies.clear();//spawn enemy sama pelurunya
        enemyBullets.clear();
        spawnDelay = 2000;
        spawnTimer.start();
        gameLoop.start();
        
        // set button to invisible when game start
        Setbuttonvisibility(2);
    }

    private void gameOver() {
        gameState = GameState.GAME_OVER;
        spawnTimer.stop();
        gameLoop.stop();

        repaint();
    }

    // ========================= SPAWN =====================================================
    private void spawnEnemy() {
        int spawnX = rand.nextInt(getWidth());
        int spawnY = rand.nextInt(getHeight());
        enemies.add(new Enemy(spawnX, spawnY));
        spawnTimer.setInitialDelay(spawnDelay);
        spawnTimer.restart();
    }
    

    // private void spawnBullet() {
    //     int baseSpeed = 2 + rand.nextInt(3); // speed bullet mulai dari 2 + random 1-3
    //     int speed = (int) (baseSpeed * bulletSpeedMultiplier);

    //     bullets.add(new Bullet(player.getX(), player.getY(), speed));

    //     if (bulletSpeedMultiplier < 2.0) {
    //         bulletSpeedMultiplier += 0.1; // ini, tiap 1 bullet spawn, speed akan di tambah 0.1, bisa jadi chaotic saat 10+ detik
    //     }

    //     if (spawnDelay > 300) {
    //         spawnDelay -= 50; // ini juga buat biar game nya lebih susah, setiap 1 bullet spawn, delay nya akan berkurang 50ms
    //     }

    //     spawnTimer.setInitialDelay(spawnDelay);
    //     spawnTimer.restart();
    // }

    // ========================= GAME UPDATE =====================================================
    private void updateSpawnDelay() {
        if (spawnDelay > 300) {
            spawnDelay -= 50;
        }
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
            ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
            ArrayList<Bullet> playerBulletsToRemove = new ArrayList<>();
            
            // for (Bullet pBullet : playerBullets) { //iki bullet seng lama idk like again chatgpt makes the bullet of the enemy into a new variable
            //     for (Bullet eBullet : bullets) {
            //         double distance = Math.sqrt(Math.pow(pBullet.getX() - eBullet.getX(), 2) + Math.pow(pBullet.getY() - eBullet.getY(), 2));
            //         if (distance < (pBullet.getHitboxSize() / 2 + eBullet.getHitboxSize() / 2)) {
            //             bulletsToRemove.add(eBullet);
            //             playerBulletsToRemove.add(pBullet);
            //         }
            //     }
            // }
            // bullets.removeAll(bulletsToRemove);
            // playerBullets.removeAll(playerBulletsToRemove);

            for (Bullet pBullet : playerBullets) {//dari sini{
                for (Bullet eBullet : enemyBullets) {
                    double distance = Math.sqrt(Math.pow(pBullet.getX() - eBullet.getX(), 2) + Math.pow(pBullet.getY() - eBullet.getY(), 2));
                    if (distance < (pBullet.getHitboxSize() / 2 + eBullet.getHitboxSize() / 2)) {
                        bulletsToRemove.add(eBullet);
                        playerBulletsToRemove.add(pBullet);
                    }
                }
            }
            enemyBullets.removeAll(bulletsToRemove);
            playerBullets.removeAll(playerBulletsToRemove);//sampai sini} itu gae peluru musuh ngilang lek kene tembak

            ArrayList<Enemy> enemiesToRemove = new ArrayList<>();
            for (Bullet pBullet : playerBullets) {
                for (Enemy enemy : enemies) {
                    if (enemy.checkCollision(pBullet)) {
                        enemiesToRemove.add(enemy);
                        playerBulletsToRemove.add(pBullet); // this line removes the bullet
                        break; // optional: to avoid 1 bullet hitting multiple enemies
                    }
                }
            }

            enemies.removeAll(enemiesToRemove);
            playerBullets.removeAll(playerBulletsToRemove);
            //sini untuk remove musuh

            bullets.removeIf(Bullet::isOutOfBounds); // fungsi untuk menghapus bullet yang keluar layar (agak sulit jelasinya, apalahi di pahami)
            for (Bullet bullet : bullets) {
                bullet.update();
                if (player.checkCollision(bullet)) {
                    gameOver(); // cek collsion
                    return;
                }
            }

            for (Enemy enemy : enemies) {//gae musuh bisa nembak
                enemy.update(player, enemyBullets);
            }
            for (Bullet bullet : enemyBullets) {
                bullet.update();
                if (player.checkCollision(bullet)) {
                    gameOver();
                    return;
                }
            }
            enemyBullets.removeIf(Bullet::isOutOfBounds);
            //gae bullet e musuh idk why chatgpt literally makes it another new variable tp haruse bullet isa dewek so idk
            repaint();
        }
    }

    
    // ========================= PAINT COMPONENT =====================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameState == GameState.MENU) {
            positionMenuButtons();
            drawMenu(g);
        } else if (gameState == GameState.PLAYING) {
            drawGame(g);
        } else if (gameState == GameState.GAME_OVER) {
            positionMenuButtons();
            drawGameOver(g);
        }else if (gameState == GameState.SETTING){
            positionMenuButtons();
            drawOptionSetting(g);
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
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
        for (Bullet bullet : enemyBullets) {
            bullet.draw(g);
        }
    }

    private void drawGameOver(Graphics g) {
        setLayout(null);
        gameover.draw(g, getWidth(), getHeight());
        Setbuttonvisibility(3);
    }

    private void drawOptionSetting(Graphics g){
        setLayout(null);
        settingmenu.draw(g, getWidth(), getHeight());
    }

    // ========================= LOGIC =====================================================
    private void shootBullet(int targetX, int targetY) {
        Bullet b = new Bullet(player.getX(), player.getY(), targetX, targetY, 10);
        b.setColor(Color.GREEN);//gae buat warna bullet seng ditembak player hijau
        playerBullets.add(b);
    }
    
    private void innitmenubutton(){ // buat fungsi button
        setLayout(null);
        // Start Button
        buttonstart.addActionListener(e -> startGame());
        // Option Button
        buttonoption.addActionListener(e -> {
            gameState = GameState.SETTING;
            settingmenu.setActiveTab("video"); // untuk setting aku default ke video
            Setbuttonvisibility(4);
            repaint();
        });
        // Exit Button
        buttonexit.addActionListener(e -> System.exit(0));
        // Restart Button
        buttonrestart.addActionListener(e -> startGame());
        // Back Button
        buttonback.addActionListener(e -> {
            gameState = GameState.MENU;
            Setbuttonvisibility(1);
            repaint();
        });

        add(buttonstart.new_button);
        add(buttonoption.new_button);
        add(buttonexit.new_button);
        add(buttonrestart.new_button);
        add(buttonback.new_button);
    }

    private void positionMenuButtons() { // set position untuk button 
        int panelWidth = getWidth();
        int panelHeight = getHeight(); 
    
        int buttonWidth = 160;
        int buttonHeight = 50;
        int centerX = (panelWidth - buttonWidth) / 2; // ambil koordinat tengah dari x
    
        // Space between buttons
        int spacing = 10;
    
        // For MENU
        if (gameState == GameState.MENU) {
            setLayout(null);
            int baseX = 20; // Slightly to the right from true bottom-left
            int baseY = panelHeight - 3 * (buttonHeight + spacing) - 40; // Slightly upward from bottom

            buttonstart.setBound(baseX, baseY, buttonWidth, buttonHeight); // inisial untuk setboundnya X, Y, width, height
            buttonoption.setBound(baseX, baseY + buttonHeight + spacing, buttonWidth, buttonHeight);
            buttonexit.setBound(baseX, baseY + 2 * (buttonHeight + spacing), buttonWidth, buttonHeight);
        }
        
        // For SETTINGS
        if (gameState == GameState.SETTING) {
            buttonWidth = 160;
            buttonHeight = 50;
            int baseX = 20;
            int baseY = getHeight() - buttonHeight - 20; // Bottom left
        
            buttonback.setBound(baseX, baseY, buttonWidth, buttonHeight);
        }

        // For GAME_OVER
        if (gameState == GameState.GAME_OVER) {
            buttonrestart.setBound(centerX, panelHeight / 2, buttonWidth, buttonHeight); 
            buttonback.setBound(centerX, panelHeight / 2 + buttonHeight + spacing, buttonWidth, buttonHeight);
        }
    }
    

    public void Setbuttonvisibility(int i){
        switch (i) { 
            case 1: // 1 pindah ke menu
                buttonstart.setVisible();
                buttonoption.setVisible();
                buttonexit.setVisible();
                buttonrestart.setInvisible();
                buttonback.setInvisible();
                break;
            case 2: // 2 buat start game
                buttonstart.setInvisible();
                buttonoption.setInvisible();
                buttonexit.setInvisible();
                buttonback.setInvisible();
                buttonrestart.setInvisible();
                break;
            case 3: // game over
                buttonrestart.setVisible();
                buttonback.setVisible();
                buttonstart.setInvisible();
                buttonoption.setInvisible();
                buttonexit.setInvisible();
                break;
            case 4: // settings
                buttonstart.setInvisible();
                buttonoption.setInvisible();
                buttonexit.setInvisible();
                buttonrestart.setInvisible();
                buttonback.setVisible();
                break;
            default:
                break;
        }
    }
    // ========================= FUNCTION =====================================================
    @Override
    public void mouseMoved(MouseEvent e) {
        if (gameState == GameState.SETTING) { // buat cek hover mouse
            settingmenu.handleMouseMoved(e.getX(), e.getY());
            repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // if (gameState == GameState.MENU || gameState == GameState.GAME_OVER) {
        //     startGame();
        // } else if (gameState == GameState.PLAYING && e.getButton() == MouseEvent.BUTTON1) { // Left click
        //     shootBullet(e.getX(), e.getY()); //ya tau lah iki apa dari nama function
        // }

        if (gameState == GameState.PLAYING && e.getButton() == MouseEvent.BUTTON1){
            shootBullet(e.getX(), e.getY()); //ya tau lah iki apa dari nama function
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {} //gerakkan mouse

    @Override
    public void mouseClicked(MouseEvent e) { // buat deteksi klik
        if (gameState == GameState.SETTING) {
            settingmenu.handleMouseClicked(e.getX(), e.getY()); 
            repaint();
        }else if (gameState == GameState.MENU){
            mainMenu.handleMouseClick(e);
        }
    } // click mouse (buat main menu dan gameover)

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
