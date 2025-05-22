import java.awt.*;
import javax.swing.*;

public class MainPanel extends JPanel {
    // Panels
    private MenuPanel menuPanel;
    private GameplayPanel gameplayPanel;
    
    // Panel visibility
    private CardLayout cardLayout = new CardLayout();
    CursorManager cursormanager = new CursorManager();
    
    public MainPanel() throws Exception {
        setLayout(cardLayout);
        
        // Initialize panels
        gameplayPanel = new GameplayPanel();
        menuPanel = new MenuPanel(this::startGame, this::startGame); // Same callback for both start and restart
        cursormanager();
        Button.setupGlowCursor(cursormanager, "pointer", this);
        cursormanager.setCursor(this, "cursor"); // Default cursor
        // Add panels to card layout
        add(menuPanel, "MENU");
        add(gameplayPanel, "GAMEPLAY");
        
        // Start with menu panel
        cardLayout.show(this, "MENU");
    }
    
    private void startGame() {
        // Switch to gameplay panel
        cardLayout.show(this, "GAMEPLAY");
        cursormanager.setCursor(this, "crosshair");
        // Start the game
        gameplayPanel.startGame();
        
        // Need to request focus after a slight delay to ensure it works correctly
        Timer focusTimer = new Timer(100, e -> {
            gameplayPanel.requestFocusInWindow();
            ((Timer)e.getSource()).stop();
        });
        focusTimer.setRepeats(false);
        focusTimer.start();
        
        // Start a timer to check if the game is over
        Timer gameStateChecker = new Timer(500, e -> {
            if (gameplayPanel.isGameOver()) {
                gameOver();
                ((Timer)e.getSource()).stop();
            }
        });
        gameStateChecker.start();
    }
    
    private void gameOver() {
        // Stop the gameplay
        gameplayPanel.stopGame();
        cursormanager.setCursor(this, "cursor"); // ganti cursor ke default
        // Show the game over screen in the menu panel
        menuPanel.setMenuState(MenuPanel.MenuState.GAME_OVER);
        cardLayout.show(this, "MENU");
    }

    public void cursormanager() { // aku gak bisa nemu crosshair yang lebih bagus dari ini, kalo ketemu boleh coba pasang
        cursormanager.loadInvisibleCursor("cursor");
        cursormanager.loadCursor("cursor", "/custom/cursor.png", new Point(0, 0), 32, 32);
        cursormanager.loadCursor("pointer", "/custom/pointer.png", new Point(5, 2), 32, 32);
        cursormanager.loadCursor("crosshair", "/custom/crosshair.png", new Point(16, 16), 32, 32);
    }
}
