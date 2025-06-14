import java.awt.*;
import javax.swing.*;
import players.Player;

public class MainPanel extends JPanel {
    // Add this field
    private boolean fromPauseMenu = false;
    
    // Panels
    private MenuPanel menuPanel;
    private GameplayPanel gameplayPanel;
    private FPScounter fpsCounter;
    private HeroSelectPanel heroSelectPanel;
    private WeaponSelectPanel weaponSelectPanel;
    private Player selectedHero;
    private String selectedWeaponName;
    
    // Panel visibility
    private CardLayout cardLayout = new CardLayout();
    CursorManager cursormanager = new CursorManager();
    
    public MainPanel() throws Exception {
        setLayout(cardLayout);
        fpsCounter = new FPScounter("FPS:");

        // Initialize panels
        gameplayPanel = new GameplayPanel(fpsCounter);
        menuPanel = new MenuPanel(this::showHeroSelect, this::showHeroSelect); // Go to hero select on start/restart
        heroSelectPanel = new HeroSelectPanel(heroName -> {
            switch (heroName) {
                case "Gunslinger":
                    selectedHero = new players.Gunslinger(500, 400);
                    break;
                case "Bomber":
                    selectedHero = new players.Bomber(500, 400);
                    break;
                case "Rogue":
                    selectedHero = new players.Rogue(500, 400);
                    break;
                case "Vampire":
                    selectedHero = new players.Vampire(500, 400);
                    break;
            }
            showWeaponSelect();
        });
        weaponSelectPanel = new WeaponSelectPanel(weaponName -> {
            selectedWeaponName = weaponName;
            assignWeaponToHero();
            startGameWithHero();
        });
        cursormanager();
        Button.setupGlowCursor(cursormanager, "pointer", this);
        cursormanager.setCursor(this, "cursor"); // Default cursor
        // Add panels to card layout
        add(menuPanel, "MENU");
        add(heroSelectPanel, "HEROSELECT");
        add(weaponSelectPanel, "WEAPONSELECT");
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
                menuPanel.getSettingMenu().setActiveTab("Video");
                cursormanager.setCursor(this, "cursor");
            },
            
            // Leave button
            () -> {
                fromPauseMenu = false; // Reset the flag
                GameplayPanel.stopGame();
                gameplayPanel.resetGame();
                cardLayout.show(this, "MENU");
                menuPanel.setMenuState(MenuPanel.MenuState.MAIN_MENU);
                cursormanager.setCursor(this, "crosshair");
            }
        );

        // Add listener for the settings back button
        menuPanel.setBackButtonListener(() -> {
            if (fromPauseMenu) {
                fromPauseMenu = false; // Reset flag
                cardLayout.show(this, "GAMEPLAY");
                cursormanager.setCursor(this, "cursor");
                gameplayPanel.requestFocusInWindow();
            } else {
                menuPanel.setMenuState(MenuPanel.MenuState.MAIN_MENU);
            }
        });
    }
    
    private void showHeroSelect() {
        cardLayout.show(this, "HEROSELECT");
        cursormanager.setCursor(this, "pointer");
    }
    
    private void showWeaponSelect() {
        cardLayout.show(this, "WEAPONSELECT");
        cursormanager.setCursor(this, "pointer");
    }    private void assignWeaponToHero() {
        if (selectedHero != null && selectedWeaponName != null) {
            System.out.println("Assigning weapon: " + selectedWeaponName + " to hero");
            selectedHero.getWeapons().clear();
            boolean weaponAdded = false;
            
            try {
                switch (selectedWeaponName) {
                    case "Revolver":
                        // Add Revolver - uses the concrete implementation
                        selectedHero.getWeapons().add(new weapons.Revolver());
                        weaponAdded = true;
                        System.out.println("Added Revolver to hero");
                        break;
                    case "Shotgun":
                        // Add Shotgun - uses the concrete implementation
                        selectedHero.getWeapons().add(new weapons.Shotgun());
                        weaponAdded = true;
                        System.out.println("Added Shotgun to hero");
                        break;
                    default:
                        System.out.println("Warning: Unknown weapon selected: " + selectedWeaponName);
                        break;
                }
            } catch (Exception e) {
                System.out.println("Error assigning weapon to hero: " + e.getMessage());
                // Don't print stack trace to keep console clean
            }
            
            // Make sure hero has at least one weapon
            if (!weaponAdded || selectedHero.getWeapons().isEmpty()) {
                try {
                    System.out.println("Adding default Revolver as fallback");
                    selectedHero.getWeapons().add(new weapons.Revolver());
                } catch (Exception e) {
                    System.out.println("Error adding fallback weapon: " + e.getMessage());
                }
            }
            
            System.out.println("Hero now has " + selectedHero.getWeapons().size() + " weapons");
        } else {
            System.out.println("Cannot assign weapon: selectedHero=" + (selectedHero != null) + ", selectedWeaponName=" + selectedWeaponName);
        }
    }
    
    private void startGameWithHero() {
        gameplayPanel.setPlayer(selectedHero);
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
