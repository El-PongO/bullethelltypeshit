import java.awt.*;
import javax.swing.*;

public class MainPanel extends JPanel {
    // Add this field
    private boolean fromPauseMenu = false;
    
    // Panels
    private MenuPanel menuPanel;
    private GameplayPanel gameplayPanel;
    private FPScounter fpsCounter;
    
    // Panel visibility
    private CardLayout cardLayout = new CardLayout();
    CursorManager cursormanager = new CursorManager();
    
    public MainPanel() throws Exception {
        setLayout(cardLayout);
        
        fpsCounter = new FPScounter("FPS:");

        // Initialize panels
        gameplayPanel = new GameplayPanel(fpsCounter);
        menuPanel = new MenuPanel(this::startGame, this::startGame); // Same callback for both start and restart
        cursormanager();
        Button.setupGlowCursor(cursormanager, "pointer", this);
        cursormanager.setCursor(this, "cursor"); // Default cursor
        // Add panels to card layout
        add(menuPanel, "MENU");
        add(gameplayPanel, "GAMEPLAY");
        
        // Start with menu panel
        cardLayout.show(this, "MENU");

        // JCheckBox for FPS counter
        JCheckBox fpsCheckbox = menuPanel.getSettingMenu().getFpsCheckbox();
        fpsCheckbox.addActionListener(e -> {
            boolean visible = fpsCheckbox.isSelected();
            fpsCounter.setVisible(visible);
        });

        // Add this in the constructor after initializing gameplayPanel
        gameplayPanel.pauseMenu.setButtonListeners(
            // Continue button
            () -> gameplayPanel.togglePause(),
            
            // Settings button
            () -> {
                fromPauseMenu = true; // Set the flag
                cardLayout.show(this, "MENU");
                menuPanel.setMenuState(MenuPanel.MenuState.SETTINGS);
                cursormanager.setCursor(this, "cursor");
            },
            
            // Leave button
            () -> {
                fromPauseMenu = false; // Reset the flag
                GameplayPanel.stopGame();
                gameplayPanel.resetGame();
                cardLayout.show(this, "MENU");
                menuPanel.setMenuState(MenuPanel.MenuState.MAIN_MENU);
                cursormanager.setCursor(this, "cursor");
            }
        );

        // Add listener for the settings back button
        menuPanel.setBackButtonListener(() -> {
            if (fromPauseMenu) {
                fromPauseMenu = false; // Reset flag
                cardLayout.show(this, "GAMEPLAY");
                cursormanager.setCursor(this, "crosshair");
                gameplayPanel.requestFocusInWindow();
            } else {
                menuPanel.setMenuState(MenuPanel.MenuState.MAIN_MENU);
            }
        });
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
        GameplayPanel.stopGame();
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
