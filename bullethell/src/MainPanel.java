import java.awt.*;
import javax.swing.*;

public class MainPanel extends JPanel {
    // Game state management
    private enum GameState { MENU, PLAYING, GAME_OVER, SETTING }
    private GameState gameState = GameState.MENU;
    
    // Panels
    private MenuPanel menuPanel;
    private GameplayPanel gameplayPanel;
    
    // Panel visibility
    private CardLayout cardLayout = new CardLayout();
    
    public MainPanel() throws Exception {
        setLayout(cardLayout);
        
        // Initialize panels
        gameplayPanel = new GameplayPanel();
        menuPanel = new MenuPanel(this::startGame, this::startGame); // Same callback for both start and restart
        
        // Add panels to card layout
        add(menuPanel, "MENU");
        add(gameplayPanel, "GAMEPLAY");
        
        // Start with menu panel
        cardLayout.show(this, "MENU");
    }
    
    private void startGame() {
        // Switch to gameplay panel
        cardLayout.show(this, "GAMEPLAY");
        
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
        
        // Show the game over screen in the menu panel
        menuPanel.setMenuState(MenuPanel.MenuState.GAME_OVER);
        cardLayout.show(this, "MENU");
    }
}
