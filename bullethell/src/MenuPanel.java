import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MenuPanel extends JPanel implements MouseListener, MouseMotionListener { 
    // ========================= UI =====================================================                                                                              
    private JFrame window;
    private MainMenu mainMenu = new MainMenu();
    private Settingmenu settingmenu = null;
    private Game_over gameover = new Game_over();

    // ========================= STATE =====================================================
    public enum MenuState {
        MAIN_MENU, SETTINGS, GAME_OVER
    }

    private MenuState currentState = MenuState.MAIN_MENU;
 
    // ========================= BUTTON =====================================================
    private Button buttonStart = new Button("Play");
    private Button buttonOption = new Button("Option");
    private Button buttonExit = new Button("Exit");
    private Button buttonRestart = new Button("Restart");
    private Button buttonBack = new Button("Back");

    // ========================= CALLBACKS =====================================================
    private Runnable onStartGame;
    private Runnable onRestartGame;
    private Runnable backButtonListener;

    public MenuPanel(JFrame window) {// basically some static call
        this.window = window;
        this.settingmenu = GameplayPanel.getSettingMenu();
    }

    public MenuPanel(Runnable startGameCallback, Runnable restartGameCallback) {
        this.onStartGame = startGameCallback;
        this.onRestartGame = restartGameCallback;

        setLayout(null);
        initButtons();

        this.settingmenu = GameplayPanel.getSettingMenu();
        if (this.settingmenu != null) {
            callsettings();
        }

        addMouseListener(this);
        addMouseMotionListener(this);

        mainMenu.playMusic(); // Start playing music when the menu is initialized
    }

    private void initButtons() {
        buttonStart.addActionListener(e -> {
            if (onStartGame != null) {
                onStartGame.run();
                mainMenu.stopMusic();
            }
        });

        buttonOption.addActionListener(e -> {
            currentState = MenuState.SETTINGS;
            settingmenu.setActiveTab("Video");
            setButtonVisibility(MenuState.SETTINGS);
            repaint();
        });

        buttonExit.addActionListener(e -> System.exit(0));

        buttonRestart.addActionListener(e -> {
            if (onRestartGame != null) {
                onRestartGame.run();
            }
        });

        buttonBack.addActionListener(e -> {
            if (backButtonListener != null) {
                backButtonListener.run();
                settingmenu.setvisibleoption("quit");
                setButtonVisibility(MenuState.MAIN_MENU);
            }
        });

        add(buttonStart.new_button);
        add(buttonOption.new_button);
        add(buttonExit.new_button);
        add(buttonRestart.new_button);
        add(buttonBack.new_button);

        setButtonVisibility(MenuState.MAIN_MENU);
    }

    public void setMenuState(MenuState state) {
        this.currentState = state;
        setButtonVisibility(state);
        if (state == MenuState.MAIN_MENU) {
            mainMenu.playMusic();
        } else {
            mainMenu.stopMusic();
        }
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

        if (currentState == MenuState.MAIN_MENU) {
            int baseX = 20;
            int baseY = panelHeight - 3 * (buttonHeight + spacing) - 40;

            buttonStart.setBound(baseX, baseY, buttonWidth, buttonHeight);
            buttonOption.setBound(baseX, baseY + buttonHeight + spacing, buttonWidth, buttonHeight);
            buttonExit.setBound(baseX, baseY + 2 * (buttonHeight + spacing), buttonWidth, buttonHeight);
        }

        // setting
        else if (currentState == MenuState.SETTINGS) {
            buttonWidth = 160;
            buttonHeight = 50;
            int baseX = 20;
            int baseY = getHeight() - buttonHeight - 20; // Bottom left

            buttonBack.setBound(baseX, baseY, buttonWidth, buttonHeight);
        }

        else if (currentState == MenuState.GAME_OVER) {
            buttonRestart.setBound(centerX, panelHeight / 2, buttonWidth, buttonHeight);
            buttonBack.setBound(centerX, panelHeight / 2 + buttonHeight + spacing, buttonWidth, buttonHeight);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        positionButtons();

        switch (currentState) {
            case MAIN_MENU:
                mainMenu.draw(g, getWidth(), getHeight());
                break;
            case SETTINGS:
                if (settingmenu != null) {
                    settingmenu.draw(g, getWidth(), getHeight());
                } else {
                    settingmenu = GameplayPanel.getSettingMenu();
                    if (settingmenu != null) {
                        settingmenu.draw(g, getWidth(), getHeight());
                    } else {
                        g.setColor(Color.WHITE);
                        g.setFont(new Font("Arial", Font.BOLD, 18));
                        g.drawString("Settings not available", getWidth() / 2 - 100, getHeight() / 2);
                    }
                }
                break;

            case GAME_OVER:
                gameover.draw(g, getWidth(), getHeight());
                break;
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        positionButtons();
    }

    public void callsettings() {
        if (settingmenu == null) {
            // klo ga ada ambil dari gameplaypanel
            settingmenu = GameplayPanel.getSettingMenu();
            if (settingmenu == null) {
                return;
            }
        }

        // Video settings
        add(settingmenu.getResolutionDropdown());
        add(settingmenu.getResolutionLabel());
        add(settingmenu.getFullscreen());

        // Audio settings
        add(settingmenu.getMusicLabel());
        add(settingmenu.getMusicValueLabel());
        add(settingmenu.getMusicSlider());
        add(settingmenu.getMusicVolumeUpBtn());
        add(settingmenu.getMusicVolumeDownBtn());
        add(settingmenu.getSfxSlider());
        add(settingmenu.getSfxLabel());
        add(settingmenu.getSfxValueLabel());
        add(settingmenu.getSfxVolumeUpBtn());
        add(settingmenu.getSfxVolumeDownBtn());
        add(settingmenu.getMuteAudio());
        add(settingmenu.getDisableFade());

        // Control Settings
        add(settingmenu.getMoveLabel());
        add(settingmenu.getShootLabel());
        add(settingmenu.getDashLabel());
        add(settingmenu.getReloadLabel());
        add(settingmenu.getWeaponLabel());
        add(settingmenu.getPauseLabel());
        add(settingmenu.getSkillsLabel());
        // Others Settings
        add(settingmenu.getFpsCheckbox());
        add(settingmenu.getDevCheckbox());
        add(settingmenu.getReloadCheckBox());
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

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    public Settingmenu getSettingMenu() {
        return settingmenu;
    }

    public void setBackButtonListener(Runnable listener) {
        this.backButtonListener = listener;
    }
}
