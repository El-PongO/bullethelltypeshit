import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import java.io.BufferedReader;
import java.io.FileReader;

public class Settingmenu extends JPanel{
    private Rectangle[] categoryBounds; // Array to hold the bounding rectangles for each category (used for hover/click detection)
    private int hoveredIndex = -1; // cek tab yang di hover
    private int selectedIndex = -1; // current active tab
    private String[] categories = {"Video", "Audio", "Controls", "Others"}; // list isinya
    
    private JCheckBox fpsCheckbox; // Checkbox for FPS counter
    private JCheckBox devmodeCheckbox; // Checkbox for dev mode
    private JCheckBox holdweaponCheckbox; // Checkbox for hold weapon
    private JCheckBox disableupgradeCheckbox; // Checkbox for disable upgrade

    private JButton musicVolumeUpBtn, musicVolumeDownBtn;
    private JButton sfxVolumeUpBtn, sfxVolumeDownBtn;
    private JProgressBar musicVolumeBar;
    private JProgressBar sfxVolumeBar;
    private float musicVolume = 1.0f;
    private float sfxVolume = 1.0f;
    private JLabel musicLabel;
    private JLabel sfxLabel;
    private JCheckBox disableFade;
    private JCheckBox muteAudio;
    private JComboBox<VideoSettings> resolutionDropdown;
    private JLabel resolutionLabel;
    private JCheckBox fullscreen;
    private JFrame mainWindow;
    boolean isFullscreen = false;

    public Settingmenu(JFrame mainWindow) {
        this.mainWindow = mainWindow; 
        setLayout(null);
        categoryBounds = new Rectangle[categories.length];
        Create_Setting_Video(); // Create video controls 
        Create_Setting_Audio();    // Create audio controls 
        Create_Settings_Others();   
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
        if (selectedIndex == 0){
            setvisibleoption("Video");
            fullscreen.setBounds(20, checkboxY + 50, 200, 30); // Fullscreen checkbox
        }else if (selectedIndex == 1){
            setvisibleoption("Audio");
            muteAudio.setBounds(20, (checkboxY * 2) + y, 200, 30); 
            disableFade.setBounds(20, ((checkboxY * 2) + y) + 30, 200, 30);
        }else if (selectedIndex == 2){
            setvisibleoption("Controls");
        }else if (selectedIndex == 3){
            setvisibleoption("Others");
            // Draw the FPS checkbox
            fpsCheckbox.setBounds(20, checkboxY, 200, 30); 

            // Draw the dev mode checkbox
            devmodeCheckbox.setBounds(20, checkboxY + 30, 200, 30); 

            // Draw the hold weapon checkbox
            holdweaponCheckbox.setBounds(20, checkboxY + 60, 350, 30); 

            // Draw the disable upgrade checkbox
            disableupgradeCheckbox.setBounds(20, checkboxY + 90, 200, 30); 
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

    public void setvisibleoption(String tabname){
        switch (tabname){
            case "Video":
                // Invisible
                setAudioControlsVisible(false);
                fpsCheckbox.setVisible(false);
                devmodeCheckbox.setVisible(false);
                holdweaponCheckbox.setVisible(false);
                disableupgradeCheckbox.setVisible(false);
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
                holdweaponCheckbox.setVisible(false);
                disableupgradeCheckbox.setVisible(false);
                fullscreen.setVisible(false);
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
                holdweaponCheckbox.setVisible(false);
                disableupgradeCheckbox.setVisible(false);
                fullscreen.setVisible(false);
                // Visible
                break;
            case "Others":
                // Invisible
                resolutionDropdown.setVisible(false);
                resolutionLabel.setVisible(false);
                setAudioControlsVisible(false);
                fullscreen.setVisible(false);
                // Visible
                fpsCheckbox.setVisible(true);
                devmodeCheckbox.setVisible(true);
                holdweaponCheckbox.setVisible(true);
                disableupgradeCheckbox.setVisible(true);
                break;
            case "quit":
                resolutionDropdown.setVisible(false);
                resolutionLabel.setVisible(false);
                setAudioControlsVisible(false);
                fpsCheckbox.setVisible(false);
                devmodeCheckbox.setVisible(false);
                holdweaponCheckbox.setVisible(false);
                disableupgradeCheckbox.setVisible(false);
                fullscreen.setVisible(false);
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

    public void Create_Setting_Video(){
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

            if (fs != null){
                isFullscreen = fs.trim().equals("1");
            }
        } catch (Exception ignored) {}

        // Set the selected item in the dropdown
        for (int i = 0; i < resolutionDropdown.getItemCount(); i++) {
            VideoSettings vs = resolutionDropdown.getItemAt(i);
            if (vs.getEnumWidth() == currentWidth && vs.getEnumHeight() == currentHeight) {
                resolutionDropdown.setSelectedIndex(i);
                break;
            }
        }

        resolutionDropdown.addActionListener(e -> {
            System.out.println("Dropdown action performed");
            VideoSettings selectedResolution = (VideoSettings) resolutionDropdown.getSelectedItem();
            if (selectedResolution != null) {
                // Set the game and restart resolution to the selected value
                System.out.println("Selected resolution: " + selectedResolution);
                restartWithResolution(selectedResolution.getEnumWidth(), selectedResolution.getEnumHeight(), fullscreen.isSelected());
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
            String jarPath = new File(Settingmenu.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
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
        if (isFullscreen){
            fullscreen.setSelected(true); 
        }else{
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

    // Create the settings for the "Audio" category
    public void Create_Setting_Audio() {
        // Music Controls
        musicLabel = new JLabel("Music Volume:");
        musicLabel.setForeground(Color.WHITE);
        musicLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        musicLabel.setBounds(20, 150, 200, 30);
        add(musicLabel);

        musicVolumeBar = new JProgressBar(0, 100);
        musicVolumeBar.setValue(100);
        musicVolumeBar.setForeground(Color.GREEN);
        musicVolumeBar.setBackground(new Color(60, 60, 60));
        musicVolumeBar.setBounds(20, 180, 200, 20);
        add(musicVolumeBar);

        musicVolumeDownBtn = new Button("-").new_button;
        musicVolumeDownBtn.setBounds(20, 210, 50, 30);
        musicVolumeDownBtn.addActionListener(e -> adjustMusicVolume(-0.1f));
        add(musicVolumeDownBtn);

        musicVolumeUpBtn = new Button("+").new_button;
        musicVolumeUpBtn.setBounds(170, 210, 50, 30);
        musicVolumeUpBtn.addActionListener(e -> adjustMusicVolume(0.1f));
        add(musicVolumeUpBtn);

        // SFX Controls
        sfxLabel = new JLabel("SFX Volume:");
        sfxLabel.setForeground(Color.WHITE);
        sfxLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        sfxLabel.setBounds(20, 260, 200, 30);
        add(sfxLabel);

        sfxVolumeBar = new JProgressBar(0, 100);
        sfxVolumeBar.setValue(100);
        sfxVolumeBar.setForeground(Color.GREEN);
        sfxVolumeBar.setBackground(new Color(60, 60, 60));
        sfxVolumeBar.setBounds(20, 290, 200, 20);
        add(sfxVolumeBar);

        sfxVolumeDownBtn = new Button("-").new_button;
        sfxVolumeDownBtn.setBounds(20, 320, 50, 30);
        sfxVolumeDownBtn.addActionListener(e -> adjustSfxVolume(-0.1f));
        add(sfxVolumeDownBtn);

        sfxVolumeUpBtn = new Button("+").new_button;
        sfxVolumeUpBtn.setBounds(170, 320, 50, 30);
        sfxVolumeUpBtn.addActionListener(e -> adjustSfxVolume(0.1f));
        add(sfxVolumeUpBtn);

        // Initially hide all controls
        Create_disFadeCheckbox();
        Create_muteAudioCheckbox();
        setAudioControlsVisible(false);
    }

    private void adjustMusicVolume(float delta) {
        musicVolume = Math.max(0, Math.min(1, musicVolume + delta));
        musicVolumeBar.setValue((int)(musicVolume * 100));
        
        // Update global music volume
        MusicManager.setGlobalVolume(musicVolume);
    }
    
    private void adjustSfxVolume(float delta) {
        sfxVolume = Math.max(0, Math.min(1, sfxVolume + delta));
        sfxVolumeBar.setValue((int)(sfxVolume * 100));
        // Update the actual SFX volume
        Sfx.setGlobalVolume(sfxVolume);
    }
    
    private void setAudioControlsVisible(boolean visible) {
        musicLabel.setVisible(visible);
        sfxLabel.setVisible(visible);
        musicVolumeUpBtn.setVisible(visible);
        musicVolumeDownBtn.setVisible(visible);
        sfxVolumeUpBtn.setVisible(visible);
        sfxVolumeDownBtn.setVisible(visible);
        musicVolumeBar.setVisible(visible);
        sfxVolumeBar.setVisible(visible);
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
                musicVolumeBar.setValue(0);
                sfxVolumeBar.setValue(0);
                MusicManager.setGlobalVolume(0);
                Sfx.setGlobalVolume(0);
            } else {
                musicVolumeBar.setValue((int)(musicVolume * 100));
                sfxVolumeBar.setValue((int)(sfxVolume * 100));
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
    public void Create_Setting_Controls(){

    }
    // Create the settings for the "Others" category
    public void Create_Settings_Others(){
        Create_fpsCheckbox();
        Create_devmodeCheckbox();
        Create_holdweaponCheckbox();
        Create_disableupgradeCheckbox();
        add(fpsCheckbox);
        add(devmodeCheckbox);
        add(holdweaponCheckbox);
        add(disableupgradeCheckbox);
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

    public void Create_devmodeCheckbox() { // Unused method
        devmodeCheckbox = new Customcheckbox("Cheats :)");
        devmodeCheckbox.setFont(new Font("Arial", Font.PLAIN, 20));
        devmodeCheckbox.setForeground(Color.WHITE);
        devmodeCheckbox.setBackground(new Color(28, 51, 92)); // Match the background color
        devmodeCheckbox.setFocusPainted(false);
        devmodeCheckbox.setVisible(false); // Initially hidden
        devmodeCheckbox.setSelected(false); // Default state
    }

    public void Create_holdweaponCheckbox() { // Unused method
        holdweaponCheckbox = new Customcheckbox("Enable hold (FIRE_KEY) to shoot");
        holdweaponCheckbox.setFont(new Font("Arial", Font.PLAIN, 20));
        holdweaponCheckbox.setForeground(Color.WHITE);
        holdweaponCheckbox.setBackground(new Color(28, 51, 92)); // Match the background color
        holdweaponCheckbox.setFocusPainted(false);
        holdweaponCheckbox.setVisible(false); // Initially hidden
        holdweaponCheckbox.setSelected(false); // Default state
    }

    public void Create_disableupgradeCheckbox() { // Unused method
        disableupgradeCheckbox = new Customcheckbox("Disable upgrade");
        disableupgradeCheckbox.setFont(new Font("Arial", Font.PLAIN, 20));
        disableupgradeCheckbox.setForeground(Color.WHITE);
        disableupgradeCheckbox.setBackground(new Color(28, 51, 92)); // Match the background color
        disableupgradeCheckbox.setFocusPainted(false);
        disableupgradeCheckbox.setVisible(false); // Initially hidden
        disableupgradeCheckbox.setSelected(false); // Default state
    }

    public JCheckBox getFpsCheckbox() {
        return fpsCheckbox;
    }

    public JCheckBox getDevCheckbox() {
        return devmodeCheckbox;
    }

    public JCheckBox getHoldWeaponCheckbox() {
        return holdweaponCheckbox;
    }

    public JCheckBox getDisableUpgradeCheckbox() {
        return disableupgradeCheckbox;
    }

    public JLabel getMusicLabel() { return musicLabel; }
    public JLabel getSfxLabel() { return sfxLabel; }
    public JProgressBar getMusicVolumeBar() { return musicVolumeBar; }
    public JProgressBar getSfxVolumeBar() { return sfxVolumeBar; }
    public JButton getMusicVolumeUpBtn() { return musicVolumeUpBtn; }
    public JButton getMusicVolumeDownBtn() { return musicVolumeDownBtn; }
    public JButton getSfxVolumeUpBtn() { return sfxVolumeUpBtn; }
    public JButton getSfxVolumeDownBtn() { return sfxVolumeDownBtn; }
    public JCheckBox getMuteAudio() { return muteAudio; }
    public JCheckBox getDisableFade() { return disableFade;}
    public JCheckBox getFullscreen() { return fullscreen;}
}
