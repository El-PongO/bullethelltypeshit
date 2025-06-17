import java.awt.*;
import javax.swing.*;
import players.Player;

public class MainPanel extends JPanel {
    private boolean fromPauseMenu = false;   
    private MenuPanel menuPanel;
    private GameplayPanel gameplayPanel;
    private FPScounter fpsCounter;
    private JPanel heroSelectPanel;  
    private WeaponSelectPanel weaponSelectPanel;
    private Player selectedHero;
    private String selectedWeaponName;
    
    private CardLayout cardLayout = new CardLayout();
    CursorManager cursormanager = new CursorManager();
    
    public MainPanel() throws Exception {
        setLayout(cardLayout);
        fpsCounter = new FPScounter("FPS:");        
        gameplayPanel = new GameplayPanel(fpsCounter);
        menuPanel = new MenuPanel(this::showHeroSelect, this::showHeroSelect); //hero select
        
        IHeroSelectPanel.HeroSelectListener heroSelectListener = heroName -> {
            switch (heroName) {
                case "Gunslinger":
                    selectedHero = new players.Gunslinger(500, 400);
                    break;
                case "Bomber":
                    selectedHero = new players.Bomber(500, 400);
                    break;
                case "Brute":
                    selectedHero = new players.Brute(500, 400);
                    break;
                case "Vampire":
                    selectedHero = new players.Vampire(500, 400);
                    break;
            }
            showWeaponSelect();
        };
        
        try {
            System.out.println("Attempting to create advanced hero select panel...");
            heroSelectPanel = new HeroSelectPanel(heroSelectListener);
            System.out.println("Successfully created advanced hero select panel");
        } catch (Exception e) {
            System.err.println("Error creating advanced hero select panel: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Falling back to simple hero select panel");
            heroSelectPanel = new SimpleHeroSelectPanel(heroSelectListener);
        }
        weaponSelectPanel = new WeaponSelectPanel(weaponName -> {
            selectedWeaponName = weaponName;
            assignWeaponToHero();
            startGameWithHero();
        });
        cursormanager();
        Button.setupGlowCursor(cursormanager, "pointer", this);
        cursormanager.setCursor(this, "cursor");
        add(menuPanel, "MENU");
        add(heroSelectPanel, "HEROSELECT");
        add(weaponSelectPanel, "WEAPONSELECT");
        add(gameplayPanel, "GAMEPLAY");
        
        cardLayout.show(this, "MENU");

        JCheckBox fpsCheckbox = menuPanel.getSettingMenu().getFpsCheckbox();
        fpsCheckbox.addActionListener(e -> {
            boolean visible = fpsCheckbox.isSelected();
            fpsCounter.setVisible(visible);
        });

        //untuk gameplaypanel
        gameplayPanel.pauseMenu.setButtonListeners(
            // resume
            () -> gameplayPanel.togglePause(),
            
            // setting
            () -> {
                fromPauseMenu = true; 
                cardLayout.show(this, "MENU");
                menuPanel.setMenuState(MenuPanel.MenuState.SETTINGS);
                menuPanel.getSettingMenu().setActiveTab("Video");
                cursormanager.setCursor(this, "cursor");
            },
            
            // leave
            () -> {
                fromPauseMenu = false;
                GameplayPanel.stopGame();
                gameplayPanel.resetGame();
                gameplayPanel.soundsfx.stopAll();
                cardLayout.show(this, "MENU");
                menuPanel.setMenuState(MenuPanel.MenuState.MAIN_MENU);
                cursormanager.setCursor(this, "crosshair");
            }
        );

        //pause
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
                        selectedHero.getWeapons().add(new weapons.Revolver());
                        selectedHero.setWeaponIndex(0);
                        weaponAdded = true;
                        System.out.println("Added Revolver to hero");
                        break;
                    case "Shotgun":
                        selectedHero.getWeapons().add(new weapons.Shotgun());
                        selectedHero.setWeaponIndex(0);
                        weaponAdded = true;
                        System.out.println("Added Shotgun to hero");
                        break;
                    default:
                        System.out.println("Warning: Unknown weapon selected: " + selectedWeaponName);
                        break;
                }
            } catch (Exception e) {
                System.out.println("Error assigning weapon to hero: " + e.getMessage());
            }
            
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
        //start
        gameplayPanel.startGame();
        
        Timer focusTimer = new Timer(100, e -> {
            gameplayPanel.requestFocusInWindow();
            ((Timer)e.getSource()).stop();
        });
        focusTimer.setRepeats(false);
        focusTimer.start();
        
        Timer gameStateChecker = new Timer(500, e -> {
            if (gameplayPanel.isGameOver()) {
                gameOver();
                ((Timer)e.getSource()).stop();
            }
        });
        gameStateChecker.start();
    }
    
    private void gameOver() {
        GameplayPanel.stopGame();
        cursormanager.setCursor(this, "cursor"); // ganti cursor ke default
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
