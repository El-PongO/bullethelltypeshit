import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import java.io.BufferedReader;
import java.io.FileReader;

public class Settingmenu extends JPanel {
    private Rectangle[] categoryBounds; // Array to hold the bounding rectangles for each category (used for hover/click
                                        // detection)
    private int hoveredIndex = -1; // cek tab yang di hover
    private int selectedIndex = -1; // current active tab
    private String[] categories = { "Video", "Audio", "Controls", "Others" }; // list isinya

    private JCheckBox fpsCheckbox; // Checkbox for FPS counter
    private JCheckBox devmodeCheckbox; // Checkbox for dev mode
    private JCheckBox reloadCheckbox; // Checkbox for automatic reload
    private JLabel musicLabel, sfxLabel;
    private float musicVolume = 1.0f;
    private float sfxVolume = 1.0f;
    private JSlider musicSlider, sfxSlider;
    private JButton musicVolumeDownBtn, musicVolumeUpBtn, sfxVolumeDownBtn, sfxVolumeUpBtn;
    private JLabel musicValueLabel, sfxValueLabel;
    private JCheckBox muteAudio, disableFade;
    private JComboBox<VideoSettings> resolutionDropdown;
    private JLabel resolutionLabel;
    private JCheckBox fullscreen;
    private JFrame mainWindow;
    boolean isFullscreen = false;
    private players.Player player;
    protected JLabel moveLabel, shootLabel, dashLabel, reloadLabel, weaponLabel, pause, skills;

    public Settingmenu(JFrame mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(null);
        categoryBounds = new Rectangle[categories.length];
        Create_Setting_Video(); // Create video controls
        Create_Setting_Audio(); // Create audio controls
        Create_Settings_Others();
        Create_Setting_Controls();
    }

    public void draw(Graphics g, int panelWidth, int panelHeight) {
        Graphics2D g2d = (Graphics2D) g;
        // Background color
        g2d.setColor(new Color(28, 51, 92));
        g2d.fillRect(0, 0, panelWidth, panelHeight);

        // Title text
        String title = "Settings";
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        g2d.setColor(Color.WHITE);

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(title);
        int textHeight = fm.getAscent();
        int y = (panelHeight / 5) - 80;

        int x = (panelWidth - textWidth) / 2;
        g2d.drawString(title, x, y);

        // Draw category buttons
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        FontMetrics catMetrics = g2d.getFontMetrics();

        int numCols = categories.length;
        int colWidth = panelWidth / numCols;
        int rowY = y + 50;

        for (int i = 0; i < numCols; i++) {
            int catTextWidth = catMetrics.stringWidth(categories[i]);
            int catX = i * colWidth + (colWidth - catTextWidth) / 2;

            // Store bounds for mouse detection
            int boxX = i * colWidth;
            int boxY = rowY - 25;
            int boxWidth = colWidth;
            int boxHeight = 40;

            categoryBounds[i] = new Rectangle(boxX, boxY, boxWidth, boxHeight);

            // Highlight on hover
            if (i == hoveredIndex) {
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.fillRect(boxX, boxY, boxWidth, boxHeight);
            }

            // Draw selected underline
            if (i == selectedIndex) {
                g2d.setColor(Color.YELLOW);
                g2d.fillRect(boxX + 10, boxY + boxHeight - 5, boxWidth - 20, 3);
            }

            g2d.setColor(Color.WHITE);
            g2d.drawString(categories[i], catX, rowY);

            // Optional dividers
            if (i < numCols - 1) {
                int lineX = (i + 1) * colWidth;
                g2d.drawLine(lineX, rowY - 20, lineX, rowY + 10);
            }
        }

        // Border lines
        g2d.drawLine(0, rowY + 15, panelWidth, rowY + 15);
        g2d.drawLine(0, rowY - 25, panelWidth, rowY - 25);

        int checkboxY = rowY + 50;
        if (selectedIndex == 0) {
            setvisibleoption("Video");
            fullscreen.setBounds(20, checkboxY + 50, 200, 30); // Fullscreen checkbox
        } else if (selectedIndex == 1) {
            setvisibleoption("Audio");
            muteAudio.setBounds(20, (checkboxY * 2) + y, 200, 30);
            disableFade.setBounds(20, ((checkboxY * 2) + y) + 30, 200, 30);
        } else if (selectedIndex == 2) {
            setvisibleoption("Controls");
            moveLabel.setBounds(20, checkboxY, 300, 30);
            shootLabel.setBounds(20, checkboxY + 40, 300, 30);
            dashLabel.setBounds(20, checkboxY + 80, 300, 30);
            reloadLabel.setBounds(20, checkboxY + 120, 300, 30);
            weaponLabel.setBounds(20, checkboxY + 160, 300, 30);
            skills.setBounds(20, checkboxY + 200, 300, 30);
            pause.setBounds(20, checkboxY + 240, 300, 30);
        } else if (selectedIndex == 3) {
            setvisibleoption("Others");
            // Draw the FPS checkbox
            fpsCheckbox.setBounds(20, checkboxY, 200, 30);

            // Draw the dev mode checkbox
            devmodeCheckbox.setBounds(20, checkboxY + 30, 200, 30); 

            // Draw the reload checkbox
            reloadCheckbox.setBounds(20, checkboxY + 60, 400, 30);
        }
    }

    // Set the active tab by matching a given name
    public void setActiveTab(String tabName) {
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equalsIgnoreCase(tabName)) {
                selectedIndex = i;
                setvisibleoption(tabName);
                repaint();
                return;
            }
        }
    }

    public void setvisibleoption(String tabname) {
        switch (tabname) {
            case "Video":
                // Invisible
                setAudioControlsVisible(false);
                fpsCheckbox.setVisible(false);
                devmodeCheckbox.setVisible(false);
                setControlLabelsVisible(false);
                reloadCheckbox.setVisible(false);
                // Visible
                resolutionDropdown.setVisible(true);
                resolutionLabel.setVisible(true);
                fullscreen.setVisible(true);
                break;
            case "Audio":
                // Invisible
                resolutionDropdown.setVisible(false);
                resolutionLabel.setVisible(false);
                fpsCheckbox.setVisible(false);
                devmodeCheckbox.setVisible(false);
                fullscreen.setVisible(false);
                setControlLabelsVisible(false);
                reloadCheckbox.setVisible(false);
                // Visible
                setAudioControlsVisible(true);
                break;
            case "Controls":
                // Invisible
                resolutionDropdown.setVisible(false);
                resolutionLabel.setVisible(false);
                setAudioControlsVisible(false);
                fpsCheckbox.setVisible(false);
                devmodeCheckbox.setVisible(false);
                fullscreen.setVisible(false);
                reloadCheckbox.setVisible(false);
                // Visible
                setControlLabelsVisible(true);
                break;
            case "Others":
                // Invisible
                resolutionDropdown.setVisible(false);
                resolutionLabel.setVisible(false);
                setAudioControlsVisible(false);
                fullscreen.setVisible(false);
                setControlLabelsVisible(false);
                // Visible
                fpsCheckbox.setVisible(true);
                devmodeCheckbox.setVisible(true);
                reloadCheckbox.setVisible(true);
                break;
            case "quit":
                resolutionDropdown.setVisible(false);
                resolutionLabel.setVisible(false);
                setAudioControlsVisible(false);
                fpsCheckbox.setVisible(false);
                devmodeCheckbox.setVisible(false);
                reloadCheckbox.setVisible(false);
                fullscreen.setVisible(false);
                setControlLabelsVisible(false);
                break;
        }
        revalidate();
        repaint();
    }

    // Update the hovered index when the mouse moves
    public void handleMouseMoved(int mouseX, int mouseY) {
        hoveredIndex = -1;
        for (int i = 0; i < categoryBounds.length; i++) {
            if (categoryBounds[i].contains(mouseX, mouseY)) {
                hoveredIndex = i;
                break;
            }
        }
    }

    public void handleMouseClicked(int mouseX, int mouseY) {
        for (int i = 0; i < categoryBounds.length; i++) {
            if (categoryBounds[i].contains(mouseX, mouseY)) {
                selectedIndex = i;
                break;
            }
        }
    }

    // Create the settings for the "Video" category
    protected enum VideoSettings {
        RES_1024x768(1024, 768), // Default resolution
        RES_1280x720(1280, 720),
        RES_1366x768(1366, 768),
        RES_1920x1080(1920, 1080);

        protected final int width;
        protected final int height;

        VideoSettings(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getEnumWidth() {
            return width;
        }

        public int getEnumHeight() {
            return height;
        }

        @Override
        public String toString() {
            return width + "x" + height;
        }
    }

    public void Create_Setting_Video() {
        resolutionDropdown = new JComboBox<>(VideoSettings.values());
        resolutionDropdown.setFont(new Font("Arial", Font.PLAIN, 20));
        resolutionDropdown.setBounds(20, 180, 200, 30);
        // Read current resolution from config.cfg
        int currentWidth = 1024, currentHeight = 768; // default
        try (BufferedReader br = new BufferedReader(new FileReader("config.cfg"))) {
            String w = br.readLine();
            String h = br.readLine();
            String fs = br.readLine();
            if (w != null && h != null) {
                currentWidth = Integer.parseInt(w.trim());
                currentHeight = Integer.parseInt(h.trim());
            }

            if (fs != null) {
                isFullscreen = fs.trim().equals("1");
            }
        } catch (Exception ignored) {
        }

        // Set the selected item in the dropdown
        for (int i = 0; i < resolutionDropdown.getItemCount(); i++) {
            VideoSettings vs = resolutionDropdown.getItemAt(i);
            if (vs.getEnumWidth() == currentWidth && vs.getEnumHeight() == currentHeight) {
                resolutionDropdown.setSelectedIndex(i);
                break;
            }
        }

        resolutionDropdown.addActionListener(e -> {
            VideoSettings selectedResolution = (VideoSettings) resolutionDropdown.getSelectedItem();
            if (selectedResolution != null) {
                // Set the game and restart resolution to the selected value
                System.out.println("Selected resolution: " + selectedResolution);
                restartWithResolution(selectedResolution.getEnumWidth(), selectedResolution.getEnumHeight(),
                        fullscreen.isSelected());
            }
        });
        add(resolutionDropdown);
        resolutionLabel = new JLabel("Resolution:");
        resolutionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        resolutionLabel.setForeground(Color.WHITE);
        resolutionLabel.setBounds(20, 150, 200, 30);
        add(resolutionLabel);
        Create_fullscreenCheckbox();
        add(fullscreen);
        resolutionDropdown.setVisible(false);
        resolutionLabel.setVisible(false);
    }

    public JComboBox<VideoSettings> getResolutionDropdown() {
        return resolutionDropdown;
    }

    public JLabel getResolutionLabel() {
        return resolutionLabel;
    }

    public void restartWithResolution(int width, int height, boolean fullscreen) {
        // Save resolution to config file
        System.out.println("restartWithResolution CALLED with: " + width + "x" + height);
        System.out.println("Attempting to write config.cfg to: " + new File("config.cfg").getAbsolutePath());
        try (PrintWriter out = new PrintWriter("config.cfg")) {
            out.println(width);
            out.println(height);
            out.println(fullscreen ? "1" : "0");
            System.out.println("Successfully wrote config.cfg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Restart the app
        try {
            String javaBin = System.getProperty("java.home") + "/bin/java";
            String jarPath = new File(Settingmenu.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .getPath();
            ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", jarPath, "App");
            builder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public void Create_fullscreenCheckbox() {
        fullscreen = new Customcheckbox("Fullscreen");
        fullscreen.setFont(new Font("Arial", Font.PLAIN, 20));
        fullscreen.setForeground(Color.WHITE);
        fullscreen.setBackground(new Color(28, 51, 92)); // Match the background color
        fullscreen.setFocusPainted(false);
        fullscreen.setVisible(false); // Initially hidden
        if (isFullscreen) {
            fullscreen.setSelected(true);
        } else {
            fullscreen.setSelected(false);
        }
        fullscreen.addActionListener(e -> {
            VideoSettings selectedResolution = (VideoSettings) resolutionDropdown.getSelectedItem();
            int width = selectedResolution != null ? selectedResolution.getEnumWidth() : 1024;
            int height = selectedResolution != null ? selectedResolution.getEnumHeight() : 768;
            restartWithResolution(width, height, fullscreen.isSelected());
            isFullscreen = fullscreen.isSelected();
        });
    }

    private void Create_Setting_Audio() {
        // Font & Color
        Font labelFont = new Font("Arial", Font.PLAIN, 18);
        Color labelColor = Color.WHITE;
        Color sliderBg = new Color(40, 40, 40);
        Color sliderFg = new Color(100, 255, 100);

        // --- MUSIC CONTROLS ---

        // Music Label
        musicLabel = new JLabel("Music Volume:");
        musicLabel.setForeground(labelColor);
        musicLabel.setFont(labelFont);
        musicLabel.setBounds(20, 150, 200, 30);
        add(musicLabel);

        // Music Slider
        musicSlider = new JSlider(0, 100, 100);
        musicSlider.setBounds(20, 180, 200, 40);
        musicSlider.setBackground(sliderBg);
        musicSlider.setForeground(sliderFg);
        musicSlider.setOpaque(true);
        musicSlider.addChangeListener(e -> {
            musicVolume = musicSlider.getValue() / 100f;
            MusicManager.setGlobalVolume(musicVolume);
            musicValueLabel.setText(musicSlider.getValue() + "%");
        });
        musicSlider.setBorder(new EmptyBorder(0, 10, 0, 10));
        add(musicSlider);

        // Music Value Label
        musicValueLabel = new JLabel("100%");
        musicValueLabel.setForeground(labelColor);
        musicValueLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        musicValueLabel.setBounds(225, 180, 50, 30);
        add(musicValueLabel);

        // Music Buttons
        Button musicDownBtn = new Button("-");
        musicDownBtn.setGlowEnabled(false);
        musicVolumeDownBtn = musicDownBtn.new_button;
        musicVolumeDownBtn.setBounds(20, 220, 50, 30);
        musicVolumeDownBtn.addActionListener(e -> {
            int newValue = Math.max(0, musicSlider.getValue() - 10);
            musicSlider.setValue(newValue); // Slider listener handles volume update
        });
        add(musicVolumeDownBtn);

        Button musicUpBtn = new Button("+");
        musicUpBtn.setGlowEnabled(false);
        musicVolumeUpBtn = musicUpBtn.new_button;
        musicVolumeUpBtn.setBounds(170, 220, 50, 30);
        musicVolumeUpBtn.addActionListener(e -> {
            int newValue = Math.min(100, musicSlider.getValue() + 10);
            musicSlider.setValue(newValue);
        });
        add(musicVolumeUpBtn);

        // --- SFX CONTROLS ---

        // SFX Label
        sfxLabel = new JLabel("SFX Volume:");
        sfxLabel.setForeground(labelColor);
        sfxLabel.setFont(labelFont);
        sfxLabel.setBounds(20, 270, 200, 30);
        add(sfxLabel);

        // SFX Slider
        sfxSlider = new JSlider(0, 100, 100);
        sfxSlider.setBounds(20, 300, 200, 40);
        sfxSlider.setBackground(sliderBg);
        sfxSlider.setForeground(sliderFg);
        sfxSlider.setOpaque(true);
        sfxSlider.addChangeListener(e -> {
            sfxVolume = sfxSlider.getValue() / 100f;
            Sfx.setGlobalVolume(sfxVolume);
            sfxValueLabel.setText(sfxSlider.getValue() + "%");
        });
        sfxSlider.setBorder(new EmptyBorder(0, 10, 0, 10));
        add(sfxSlider);

        // SFX Value Label
        sfxValueLabel = new JLabel("100%");
        sfxValueLabel.setForeground(labelColor);
        sfxValueLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        sfxValueLabel.setBounds(225, 300, 50, 30);
        add(sfxValueLabel);

        // SFX Buttons
        Button sfxDownBtn = new Button("-");
        sfxDownBtn.setGlowEnabled(false);
        sfxVolumeDownBtn = sfxDownBtn.new_button;
        sfxVolumeDownBtn.setBounds(20, 340, 50, 30);
        sfxVolumeDownBtn.addActionListener(e -> {
            int newValue = Math.max(0, sfxSlider.getValue() - 10);
            sfxSlider.setValue(newValue);
        });
        add(sfxVolumeDownBtn);

        Button sfxUpBtn = new Button("+");
        sfxUpBtn.setGlowEnabled(false);
        sfxVolumeUpBtn = sfxUpBtn.new_button;
        sfxVolumeUpBtn.setBounds(170, 340, 50, 30);
        sfxVolumeUpBtn.addActionListener(e -> {
            int newValue = Math.min(100, sfxSlider.getValue() + 10);
            sfxSlider.setValue(newValue);
        });
        add(sfxVolumeUpBtn);

        // --- Additional UI ---
        Create_disFadeCheckbox();
        Create_muteAudioCheckbox();

        // Hide by default
        setAudioControlsVisible(false);
    }

    private void setAudioControlsVisible(boolean visible) {
        musicLabel.setVisible(visible);
        musicValueLabel.setVisible(visible);
        sfxLabel.setVisible(visible);
        sfxValueLabel.setVisible(visible);

        musicVolumeUpBtn.setVisible(visible);
        musicVolumeDownBtn.setVisible(visible);
        sfxVolumeUpBtn.setVisible(visible);
        sfxVolumeDownBtn.setVisible(visible);

        musicSlider.setVisible(visible);
        sfxSlider.setVisible(visible);

        muteAudio.setVisible(visible);
        disableFade.setVisible(visible);
    }

    public void Create_muteAudioCheckbox() { // Unused method
        muteAudio = new Customcheckbox("Mute All Audio");
        muteAudio.setFont(new Font("Arial", Font.PLAIN, 20));
        muteAudio.setForeground(Color.WHITE);
        muteAudio.setBackground(new Color(28, 51, 92)); // Match the background color
        muteAudio.setFocusPainted(false);
        muteAudio.setVisible(false); // Initially hidden
        muteAudio.setSelected(false); // Default state
        muteAudio.addActionListener(e -> {
            boolean isMuted = muteAudio.isSelected();
            if (isMuted) {
                musicSlider.setValue(0);
                sfxSlider.setValue(0);
                MusicManager.setGlobalVolume(0);
                Sfx.setGlobalVolume(0);
            } else {
                musicSlider.setValue(100);
                sfxSlider.setValue(100);
                MusicManager.setGlobalVolume(musicVolume);
                Sfx.setGlobalVolume(sfxVolume);
            }
        });
    }

    public void Create_disFadeCheckbox() { // Unused method
        disableFade = new Customcheckbox("Disable Audio Fade");
        disableFade.setFont(new Font("Arial", Font.PLAIN, 20));
        disableFade.setForeground(Color.WHITE);
        disableFade.setBackground(new Color(28, 51, 92)); // Match the background color
        disableFade.setFocusPainted(false);
        disableFade.setVisible(false); // Initially hidden
        disableFade.setSelected(false); // Default state
    }

    // Create the settings for the "Controls" category
    public void Create_Setting_Controls() {
        moveLabel = createControlLabel("Movement: WASD");
        shootLabel = createControlLabel("Shoot: M1");
        dashLabel = createControlLabel("Dash: Shift");
        reloadLabel = createControlLabel("Reload: R");
        weaponLabel = createControlLabel("Switch Weapon: QE");
        pause = createControlLabel("Pause Game: Esc");
        skills = createControlLabel("Skills: F");
    }

    private JLabel createControlLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
        label.setBackground(new Color(40, 40, 40));
        label.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        label.setPreferredSize(new Dimension(300, 100));
        return label;
    }

    public void setControlLabelsVisible(boolean visible) {
        moveLabel.setVisible(visible);
        shootLabel.setVisible(visible);
        dashLabel.setVisible(visible);
        reloadLabel.setVisible(visible);
        weaponLabel.setVisible(visible);
        pause.setVisible(visible);
        skills.setVisible(visible);
    }

    public JLabel getMoveLabel() {
        return moveLabel;
    }

    public JLabel getShootLabel() {
        return shootLabel;
    }

    public JLabel getDashLabel() {
        return dashLabel;
    }

    public JLabel getReloadLabel() {
        return reloadLabel;
    }

    public JLabel getWeaponLabel() {
        return weaponLabel;
    }

    public JLabel getPauseLabel() {
        return pause;
    }

    public JLabel getSkillsLabel() {
        return skills;
    }

    // Create the settings for the "Others" category
    public void Create_Settings_Others() {
        Create_fpsCheckbox();
        Create_devmodeCheckbox();
        Create_reloadCheckbox();
        add(fpsCheckbox);
        add(devmodeCheckbox);
        add(reloadCheckbox);
        loadReloadCheckboxState(); // Load the state of the reload checkbox from config
    }

    public void Create_fpsCheckbox() {
        fpsCheckbox = new Customcheckbox("Show FPS Counter");
        fpsCheckbox.setFont(new Font("Arial", Font.PLAIN, 20));
        fpsCheckbox.setForeground(Color.WHITE);
        fpsCheckbox.setBackground(new Color(28, 51, 92)); // Match the background color
        fpsCheckbox.setFocusPainted(false);
        fpsCheckbox.setVisible(false); // Initially hidden
        fpsCheckbox.setSelected(false); // Default state
    }

    public void Create_devmodeCheckbox() {
        devmodeCheckbox = new Customcheckbox("Cheats :)");
        devmodeCheckbox.setFont(new Font("Arial", Font.PLAIN, 20));
        devmodeCheckbox.setForeground(Color.WHITE);
        devmodeCheckbox.setBackground(new Color(28, 51, 92));
        devmodeCheckbox.setFocusPainted(false);
        devmodeCheckbox.setVisible(false);
        devmodeCheckbox.setSelected(false);

        try {
            File configFile = new File("config.cfg");
            if (configFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(configFile));
                reader.readLine(); // width
                reader.readLine(); // height
                reader.readLine(); // fullscreen
                String devState = reader.readLine(); // devmode state
                devmodeCheckbox.setSelected(devState != null && devState.equals("1"));
                reader.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        devmodeCheckbox.addActionListener(e -> {
            boolean isEnabled = devmodeCheckbox.isSelected();
            // Save all states to config file
            try {
                // First read existing values
                int width = 1024, height = 768;
                boolean fullscreenEnabled = false;
                File configFile = new File("config.cfg");
                if (configFile.exists()) {
                    BufferedReader reader = new BufferedReader(new FileReader(configFile));
                    width = Integer.parseInt(reader.readLine().trim());
                    height = Integer.parseInt(reader.readLine().trim());
                    fullscreenEnabled = reader.readLine().trim().equals("1");
                    reader.close();
                }

                // Write all values back including devmode
                PrintWriter writer = new PrintWriter("config.cfg");
                writer.println(width);
                writer.println(height);
                writer.println(fullscreenEnabled ? "1" : "0");
                writer.println(isEnabled ? "1" : "0");
                writer.close();

                System.out.println("Dev mode " + (isEnabled ? "enabled" : "disabled"));
                if (isEnabled && player != null) {
                    System.out.println("Applying cheats to player...");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public boolean isDevModeEnabled() {
        try {
            File configFile = new File("config.cfg");
            if (configFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(configFile));
                reader.readLine(); // width
                reader.readLine(); // height
                reader.readLine(); // fullscreen
                String state = reader.readLine(); // devmode state
                reader.close();
                return state != null && state.equals("1");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void Create_reloadCheckbox() { // masih belum ada fungsionalitas
        reloadCheckbox = new Customcheckbox("Automaticly Reload when out of ammo");
        reloadCheckbox.setFont(new Font("Arial", Font.PLAIN, 20));
        reloadCheckbox.setForeground(Color.WHITE);
        reloadCheckbox.setBackground(new Color(28, 51, 92)); // Match the background color
        reloadCheckbox.setFocusPainted(false);
        reloadCheckbox.setVisible(false); // Initially hidden
        reloadCheckbox.setSelected(false); // Default state

        reloadCheckbox.addActionListener(e -> {
            saveReloadCheckboxState();
        });
    }

    private void saveReloadCheckboxState() {
        try {
            File configFile = new File("config.cfg");
            List<String> lines = new ArrayList<>();
            // Read existing lines
            if (configFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(configFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                reader.close();
            }
            // Ensure at least 5 lines (add empty if missing)
            while (lines.size() < 5) lines.add("");
            // Line 4 (index 4) is for reload checkbox
            lines.set(4, reloadCheckbox.isSelected() ? "1" : "0");
            PrintWriter writer = new PrintWriter(configFile);
            for (String l : lines) writer.println(l);
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isAutoReloadEnabled() {
        try {
            File configFile = new File("config.cfg");
            if (configFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(configFile));
                for (int i = 0; i < 5; i++) {
                    String line = reader.readLine();
                    if (i == 4 && line != null) {
                        reader.close();
                        return line.trim().equals("1");
                    }
                }
                reader.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void loadReloadCheckboxState() {
        reloadCheckbox.setSelected(isAutoReloadEnabled());
    }

    public JCheckBox getFpsCheckbox() {
        return fpsCheckbox;
    }

    public JCheckBox getDevCheckbox() {
        return devmodeCheckbox;
    }

    public JCheckBox getReloadCheckBox() {
        return reloadCheckbox;
    }

    public void setPlayer(players.Player player) {
        this.player = player;
    }

    public JLabel getMusicLabel() {
        return musicLabel;
    }

    public JLabel getSfxLabel() {
        return sfxLabel;
    }

    public JLabel getMusicValueLabel() {
        return musicValueLabel;
    }

    public JLabel getSfxValueLabel() {
        return sfxValueLabel;
    }

    public JSlider getMusicSlider() {
        return musicSlider;
    }

    public JSlider getSfxSlider() {
        return sfxSlider;
    }

    public JButton getMusicVolumeUpBtn() {
        return musicVolumeUpBtn;
    }

    public JButton getMusicVolumeDownBtn() {
        return musicVolumeDownBtn;
    }

    public JButton getSfxVolumeUpBtn() {
        return sfxVolumeUpBtn;
    }

    public JButton getSfxVolumeDownBtn() {
        return sfxVolumeDownBtn;
    }

    public JCheckBox getMuteAudio() {
        return muteAudio;
    }

    public JCheckBox getDisableFade() {
        return disableFade;
    }

    public JCheckBox getFullscreen() {
        return fullscreen;
    }
}
