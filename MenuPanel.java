import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.omg.CORBA.Current;

public class MenuPanel extends JPanel implements MouseListener, MouseMotionListener {
    // ========================= UI =====================================================
    private MainMenu mainMenu = new MainMenu();
    private Settingmenu settingmenu = new Settingmenu();
    private Game_over gameover = new Game_over();
    
    // ========================= STATE =====================================================
    public enum MenuState { MAIN_MENU, SETTINGS, GAME_OVER }
    private MenuState currentState = MenuState.MAIN_MENU;
    
    // ========================= BUTTON =====================================================
    private Button buttonStart = new Button("Play");
    private Button buttonOption = new Button("Option");
    private Button buttonExit = new Button("Exit");
    private Button buttonRestart = new Button("Restart");
    private Button buttonBack = new Button("Back");
    
    // ========================= LAIN-LAIN =====================================================
    private JCheckBox fpsCheckbox; // Checkbox for FPS counter
    private JCheckBox devCheckBox; // Checkbox for developer mode
    private JCheckBox holdweaponCheckbox; // Checkbox for hold weapon
    private JCheckBox disableupgradeCheckbox; // Checkbox for disable upgrade

    // ========================= CALLBACKS =====================================================
    private Runnable onStartGame;
    private Runnable onRestartGame;
    
    public MenuPanel(Runnable startGameCallback, Runnable restartGameCallback) {
        this.onStartGame = startGameCallback;
        this.onRestartGame = restartGameCallback;
        
        setLayout(null);
        initButtons();
        callsettings();
        
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    private void initButtons() {
        // Start Button
        buttonStart.addActionListener(e -> {
            if (onStartGame != null) {
                onStartGame.run();
            }
        });
        
        // Option Button
        buttonOption.addActionListener(e -> {
            currentState = MenuState.SETTINGS;
            settingmenu.setActiveTab("video");
            setButtonVisibility(MenuState.SETTINGS);
            repaint();
        });
        
        // Exit Button
        buttonExit.addActionListener(e -> System.exit(0));
        
        // Restart Button
        buttonRestart.addActionListener(e -> {
            if (onRestartGame != null) {
                onRestartGame.run();
            }
        });
        
        // Back Button
        buttonBack.addActionListener(e -> {
            if (currentState == MenuState.SETTINGS) {
                settingmenu.setvisibleoption("quit");
            }
            currentState = MenuState.MAIN_MENU;
            setButtonVisibility(MenuState.MAIN_MENU);
            repaint();
        });
        
        add(buttonStart.new_button);
        add(buttonOption.new_button);
        add(buttonExit.new_button);
        add(buttonRestart.new_button);
        add(buttonBack.new_button);
        
        // Set initial button visibility
        setButtonVisibility(MenuState.MAIN_MENU);
    }
    
    public void setMenuState(MenuState state) {
        this.currentState = state;
        setButtonVisibility(state);
        repaint();
    }
    
    private void setButtonVisibility(MenuState state) {
        switch (state) {
            case MAIN_MENU:
                buttonStart.setVisible();
                buttonOption.setVisible();
                buttonExit.setVisible();
                buttonRestart.setInvisible();
                buttonBack.setInvisible();
                break;
                
            case SETTINGS:
                buttonStart.setInvisible();
                buttonOption.setInvisible();
                buttonExit.setInvisible();
                buttonRestart.setInvisible();
                buttonBack.setVisible();
                break;
                
            case GAME_OVER:
                buttonStart.setInvisible();
                buttonOption.setInvisible();
                buttonExit.setInvisible();
                buttonRestart.setVisible();
                buttonBack.setVisible();
                break;
        }
    }
    
    private void positionButtons() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        
        int buttonWidth = 160;
        int buttonHeight = 50;
        int centerX = (panelWidth - buttonWidth) / 2;
        int spacing = 10;
        
        // For MAIN_MENU
        if (currentState == MenuState.MAIN_MENU) {
            int baseX = 20; // Slightly to the right from true bottom-left
            int baseY = panelHeight - 3 * (buttonHeight + spacing) - 40; // Slightly upward from bottom
            
            buttonStart.setBound(baseX, baseY, buttonWidth, buttonHeight);
            buttonOption.setBound(baseX, baseY + buttonHeight + spacing, buttonWidth, buttonHeight);
            buttonExit.setBound(baseX, baseY + 2 * (buttonHeight + spacing), buttonWidth, buttonHeight);
        }
        
        // For SETTINGS
        else if (currentState == MenuState.SETTINGS) {
            buttonWidth = 160;
            buttonHeight = 50;
            int baseX = 20;
            int baseY = getHeight() - buttonHeight - 20; // Bottom left
            
            buttonBack.setBound(baseX, baseY, buttonWidth, buttonHeight);
        }
        
        // For GAME_OVER
        else if (currentState == MenuState.GAME_OVER) {
            buttonRestart.setBound(centerX, panelHeight / 2, buttonWidth, buttonHeight);
            buttonBack.setBound(centerX, panelHeight / 2 + buttonHeight + spacing, buttonWidth, buttonHeight);
        }
    }
      @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Position buttons before drawing
        positionButtons();
        
        switch (currentState) {
            case MAIN_MENU:
                mainMenu.draw(g, getWidth(), getHeight());
                break;
                
            case SETTINGS:
                settingmenu.draw(g, getWidth(), getHeight());
                break;
                
            case GAME_OVER:
                gameover.draw(g, getWidth(), getHeight());
                break;
        }
    }
    
    // Called when the panel is resized
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        positionButtons();
    }
    
    public void callsettings(){
        fpsCheckbox = settingmenu.getFpsCheckbox();
        add(settingmenu.getFpsCheckbox());

        devCheckBox = settingmenu.getDevCheckbox();
        add(settingmenu.getDevCheckbox());

        holdweaponCheckbox = settingmenu.getHoldWeaponCheckbox();
        add(settingmenu.getHoldWeaponCheckbox());

        disableupgradeCheckbox = settingmenu.getDisableUpgradeCheckbox();
        add(settingmenu.getDisableUpgradeCheckbox());
    }

    // ========================= MOUSE EVENTS =====================================================
    @Override
    public void mouseClicked(MouseEvent e) {
        if (currentState == MenuState.SETTINGS) {
            settingmenu.handleMouseClicked(e.getX(), e.getY());
            repaint();
        } else if (currentState == MenuState.MAIN_MENU) {
            mainMenu.handleMouseClick(e);
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        if (currentState == MenuState.SETTINGS) {
            settingmenu.handleMouseMoved(e.getX(), e.getY());
            repaint();
        }
    }
    
    // Required interface methods
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}

    public Settingmenu getSettingMenu() {
        return settingmenu;
    }
}
