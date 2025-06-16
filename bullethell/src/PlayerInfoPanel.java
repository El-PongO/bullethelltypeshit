import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * A UI panel that displays player information including their face/avatar and name
 * with a dark grey-bluish background.
 */
public class PlayerInfoPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(40, 44, 52); // Dark grey-bluish
    private static final Color TEXT_COLOR = new Color(220, 223, 228); // Light grey/white for text
    private static final Font NAME_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font STATS_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final int AVATAR_SIZE = 120; // Size of avatar display
    private static final int BORDER_SIZE = 15; // Border padding
    
    private String playerName;
    private BufferedImage playerAvatar;
    private int health;
    private int maxHealth;
    private int dashCharges;
    private int maxDashCharges;
    private long skillCooldown;
    private String playerDescription;
    
    /**
     * Creates a new player info panel
     * 
     * @param playerType The type of player ("Gunslinger", "Bomber", "Vampire", "Brute")
     */
    public PlayerInfoPanel(String playerType) {
        this.playerName = playerType;
        setPreferredSize(new Dimension(200, 280));
        setBorder(new EmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
        setBackground(BACKGROUND_COLOR);
        setOpaque(true);
        loadPlayerAvatar(playerType);
        
        // Set default stats based on player type
        switch (playerType) {
            case "Bomber":
                health = 150;
                maxHealth = 150;
                dashCharges = 2;
                maxDashCharges = 2;
                skillCooldown = 20000;
                playerDescription = "Creates explosive areas";
                break;
            case "Brute":
                health = 200;
                maxHealth = 200;
                dashCharges = 2;
                maxDashCharges = 2;
                skillCooldown = 15000;
                playerDescription = "High health, powerful melee";
                break;
            case "Vampire":
                health = 100;
                maxHealth = 100;
                dashCharges = 3;
                maxDashCharges = 3;
                skillCooldown = 12000;
                playerDescription = "Steals health from enemies";
                break;
            case "Gunslinger":
            default:
                health = 100;
                maxHealth = 100;
                dashCharges = 2;
                maxDashCharges = 2;
                skillCooldown = 10000;
                playerDescription = "Versatile weapons expert";
                break;
        }
    }
      /**
     * Load the player's avatar image based on player type
     */    private void loadPlayerAvatar(String playerType) {
        // Create fallback image immediately so we have something to display regardless of errors
        playerAvatar = createFallbackAvatar(playerType);
        System.out.println("Created fallback avatar for " + playerType);
        
        // Map player type to the correct asset folder name (Hunter for Gunslinger)
        String assetFolder = getAssetFolderName(playerType);
        
        try {
            // First try to load the Faceset.png which is better for avatar display
            java.net.URL imageUrl = getClass().getResource("/Assets/" + assetFolder + "/Faceset.png");            // If Faceset.png doesn't work, try alternate paths for Faceset.png
            if (imageUrl == null) {
                System.out.println("Trying alternate path for " + assetFolder + " Faceset");
                // Try without the leading slash
                imageUrl = getClass().getResource("Assets/" + assetFolder + "/Faceset.png");
            }
              
            if (imageUrl == null) {
                System.out.println("Trying src path for " + assetFolder + " Faceset");
                // Try with src prefix
                imageUrl = getClass().getResource("/src/Assets/" + assetFolder + "/Faceset.png");
            }
            
            if (imageUrl == null) {
                System.out.println("Trying bullethell path for " + assetFolder + " Faceset");
                // Try with bullethell prefix
                imageUrl = getClass().getResource("/bullethell/src/Assets/" + assetFolder + "/Faceset.png");
            }
              if (imageUrl == null) {
                // Try a lowercase version of assetFolder as folder name
                System.out.println("Trying lowercase path for " + assetFolder + " Faceset");
                String lowerCase = assetFolder.toLowerCase();
                imageUrl = getClass().getResource("/Assets/" + lowerCase + "/Faceset.png");
                if (imageUrl == null) {
                    imageUrl = getClass().getResource("Assets/" + lowerCase + "/Faceset.png");
                }
            }
              // If we still don't have an image, fall back to idledown.png
            if (imageUrl == null) {                System.out.println("Faceset not found, falling back to idledown.png for " + assetFolder);
                imageUrl = getClass().getResource("/Assets/" + assetFolder + "/idledown.png");
                
                if (imageUrl == null) {
                    imageUrl = getClass().getResource("Assets/" + assetFolder + "/idledown.png");
                }
                  if (imageUrl == null) {
                    imageUrl = getClass().getResource("/src/Assets/" + assetFolder + "/idledown.png"); 
                }
                
                if (imageUrl == null) {
                    imageUrl = getClass().getResource("/bullethell/src/Assets/" + assetFolder + "/idledown.png");
                }
                
                if (imageUrl == null) {
                    imageUrl = getClass().getResource("/Assets/player/" + assetFolder + "/idledown.png");
                }
                
                if (imageUrl == null) {
                    imageUrl = getClass().getResource("Assets/player/" + assetFolder + "/idledown.png");
                }
            }
              if (imageUrl == null) {                // Try using File directly for local development with multiple paths
                String[] possiblePaths = {
                    "src/Assets/" + assetFolder + "/Faceset.png",
                    "bullethell/src/Assets/" + assetFolder + "/Faceset.png",
                    "src/Assets/" + assetFolder.toLowerCase() + "/Faceset.png",
                    "bullethell/src/Assets/" + assetFolder.toLowerCase() + "/Faceset.png",
                    "src/Assets/" + assetFolder + "/idledown.png",
                    "bullethell/src/Assets/" + assetFolder + "/idledown.png",
                    "src/Assets/" + assetFolder.toLowerCase() + "/idledown.png",
                    "bullethell/src/Assets/" + assetFolder.toLowerCase() + "/idledown.png",
                    // Try with player folder name
                    "src/Assets/player/" + assetFolder + "/idledown.png",
                    "bullethell/src/Assets/player/" + assetFolder + "/idledown.png",
                    // Try with the first letter lowercase
                    "src/Assets/" + assetFolder.substring(0, 1).toLowerCase() + assetFolder.substring(1) + "/idledown.png",
                    "bullethell/src/Assets/" + assetFolder.substring(0, 1).toLowerCase() + assetFolder.substring(1) + "/idledown.png"
                };
                
                for (String path : possiblePaths) {
                    File file = new File(path);
                    if (file.exists()) {
                        System.out.println("Loading from file: " + file.getAbsolutePath());
                        BufferedImage loadedImage = ImageIO.read(file);
                        if (loadedImage != null) {
                            playerAvatar = loadedImage;
                            return;
                        }
                    }
                }
                  System.err.println("Could not find avatar for " + playerType + " in any location");
                // We already created a fallback avatar at the start
                return;
            }
            
            // Load the image from the URL
            if (imageUrl != null) {
                System.out.println("Found image URL: " + imageUrl);
                BufferedImage loadedImage = ImageIO.read(imageUrl);
                if (loadedImage != null) {
                    playerAvatar = loadedImage;
                }
            }        } catch (Exception e) {
            System.err.println("Error loading avatar for " + playerType + ": " + e.getMessage());
            e.printStackTrace();
            System.out.println("Using fallback avatar for " + playerType);
            // We already created a fallback avatar at the start of this method, no need to create again
        }
    }
    
    /**
     * Create a fallback avatar image if the player image can't be loaded
     */    private BufferedImage createFallbackAvatar(String playerType) {
        // Create a 64x64 image with a distinct color for each player type
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Different color for each hero type
        Color bgColor;
        Color textColor = Color.WHITE;
        
        switch (playerType) {
            case "Bomber":
                bgColor = new Color(220, 60, 60); // Red
                break;
            case "Brute":
                bgColor = new Color(60, 150, 80); // Green
                break;
            case "Vampire":
                bgColor = new Color(130, 50, 160); // Purple
                break;
            case "Gunslinger":
                bgColor = new Color(60, 100, 200); // Blue
                break;
            default:
                bgColor = new Color(80, 90, 120); // Default blue-grey
        }
        
        // Draw background with gradient
        GradientPaint gradient = new GradientPaint(
            0, 0, bgColor.darker(),
            64, 64, bgColor
        );
        g2d.setPaint(gradient);
        g2d.fillRoundRect(0, 0, 64, 64, 10, 10);
        
        // Draw a border
        g2d.setStroke(new BasicStroke(2f));
        g2d.setColor(bgColor.brighter());
        g2d.drawRoundRect(2, 2, 60, 60, 8, 8);
        
        // Draw the first letter of the player type
        g2d.setColor(textColor);
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        FontMetrics fm = g2d.getFontMetrics();
        String letter = playerType.substring(0, 1);
        g2d.drawString(letter, (64 - fm.stringWidth(letter)) / 2, 42);
        
        // Draw a simple face
        int eyeSize = 5;
        int mouthWidth = 20;
        
        // Draw eyes
        g2d.fillOval(22 - eyeSize/2, 28 - eyeSize/2, eyeSize, eyeSize);
        g2d.fillOval(42 - eyeSize/2, 28 - eyeSize/2, eyeSize, eyeSize);
        
        // Draw smile
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawArc(32 - mouthWidth/2, 35, mouthWidth, 15, 0, -180);
        
        g2d.dispose();
        System.out.println("Created fallback avatar for " + playerType);
        return img;
    }
    
    /**
     * Update the panel with current player stats
     */
    public void updateStats(int health, int maxHealth, int dashCharges, int maxDashCharges) {
        this.health = health;
        this.maxHealth = maxHealth;
        this.dashCharges = dashCharges;
        this.maxDashCharges = maxDashCharges;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smoother rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        
        // Draw panel background with slight gradient
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(45, 49, 58),
            0, getHeight(), BACKGROUND_COLOR);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, getHeight());
          // Draw the avatar in the center top portion
        if (playerAvatar != null) {
            int avatarX = (width - AVATAR_SIZE) / 2;
            int avatarY = BORDER_SIZE;
            
            // Draw a slightly lighter background behind the avatar
            g2d.setColor(new Color(50, 54, 62));
            g2d.fillRoundRect(avatarX - 5, avatarY - 5, AVATAR_SIZE + 10, AVATAR_SIZE + 10, 10, 10);
            
            // Check if this is a Faceset image (usually square and contains just the face)
            boolean isFaceset = playerAvatar.getWidth() == playerAvatar.getHeight() || 
                               (playerAvatar.getWidth() > 50 && playerAvatar.getWidth() < 100);
                
            if (isFaceset) {
                // Draw the avatar directly - Facesets are already cropped to the face
                g2d.drawImage(playerAvatar, avatarX, avatarY, AVATAR_SIZE, AVATAR_SIZE, null);
            } else {
                // For sprite sheets like idledown.png, try to focus on the face by cropping
                // Typically the face is in the upper portion of the sprite
                int srcY = playerAvatar.getHeight() / 4; // Start 1/4 from the top to focus on face
                int srcHeight = playerAvatar.getHeight() / 2; // Take middle half for face
                
                // Draw just the face portion
                g2d.drawImage(playerAvatar, 
                              avatarX, avatarY, avatarX + AVATAR_SIZE, avatarY + AVATAR_SIZE,
                              0, srcY, playerAvatar.getWidth(), srcY + srcHeight, 
                              null);
            }
        }
        
        // Draw player name
        g2d.setFont(NAME_FONT);
        g2d.setColor(TEXT_COLOR);
        FontMetrics fm = g2d.getFontMetrics();
        int nameWidth = fm.stringWidth(playerName);
        g2d.drawString(playerName, (width - nameWidth) / 2, BORDER_SIZE + AVATAR_SIZE + 25);
        
        // Draw player description
        g2d.setFont(STATS_FONT);
        nameWidth = fm.stringWidth(playerDescription);
        g2d.drawString(playerDescription, (width - nameWidth) / 2, BORDER_SIZE + AVATAR_SIZE + 45);
        
        // Draw health bar
        int barWidth = width - (BORDER_SIZE * 2);
        int barHeight = 15;
        int barY = BORDER_SIZE + AVATAR_SIZE + 60;
        
        // Health bar background
        g2d.setColor(new Color(60, 63, 65));
        g2d.fillRoundRect(BORDER_SIZE, barY, barWidth, barHeight, 5, 5);
        
        // Health bar fill
        g2d.setColor(new Color(76, 175, 80)); // Green
        int fillWidth = (int)(((double)health / maxHealth) * barWidth);
        g2d.fillRoundRect(BORDER_SIZE, barY, fillWidth, barHeight, 5, 5);
        
        // Health text
        g2d.setFont(STATS_FONT);
        g2d.setColor(TEXT_COLOR);
        String healthText = "HP: " + health + "/" + maxHealth;
        g2d.drawString(healthText, BORDER_SIZE, barY + barHeight + 15);
        
        // Draw dash charges
        String dashText = "Dash: " + dashCharges + "/" + maxDashCharges;
        g2d.drawString(dashText, BORDER_SIZE, barY + barHeight + 35);
        
        // Draw skill cooldown
        String skillText = "Skill CD: " + (skillCooldown / 1000) + "s";
        g2d.drawString(skillText, BORDER_SIZE, barY + barHeight + 55);
    }
    
    /**
     * Create a standalone test frame to display the PlayerInfoPanel
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Player Info Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            JPanel container = new JPanel(new GridLayout(2, 2, 10, 10));
            container.setBackground(new Color(30, 34, 42)); // Even darker background for the container
            container.setBorder(new EmptyBorder(10, 10, 10, 10));
            
            container.add(new PlayerInfoPanel("Gunslinger"));
            container.add(new PlayerInfoPanel("Bomber"));
            container.add(new PlayerInfoPanel("Vampire"));
            container.add(new PlayerInfoPanel("Brute"));
            
            frame.getContentPane().add(container);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
    
    /**
     * Maps player types to their asset folder names
     */
    private String getAssetFolderName(String playerType) {
        // Hunter is the folder name for Gunslinger assets
        if ("Gunslinger".equals(playerType)) {
            System.out.println("Mapping Gunslinger to Hunter folder for assets");
            return "Hunter";
        }
        return playerType;
    }
}
