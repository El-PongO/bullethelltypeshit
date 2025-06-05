import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class create_placeholder_images {
    public static void main(String[] args) {
        // Shooter enemy - purple colored
        Color mainColor = new Color(102, 51, 153); // Purple color
        Color shadowColor = new Color(76, 38, 115); // Darker purple
        Color highlightColor = new Color(146, 87, 204); // Lighter purple
        Color gunColor = new Color(150, 150, 150); // Gray for gun

        // Create directories if they don't exist
        String directory = "c:\\Users\\benny\\OneDrive\\Documents\\GitHub\\bullethelltypeshit\\bullethell\\src\\Assets\\ShooterEnemy";
        new File(directory).mkdirs();

        // Create different orientation images
        createShooterImage(directory + "\\up1.png", mainColor, shadowColor, highlightColor, gunColor, "up", 1);
        createShooterImage(directory + "\\up2.png", mainColor, shadowColor, highlightColor, gunColor, "up", 2);
        createShooterImage(directory + "\\down1.png", mainColor, shadowColor, highlightColor, gunColor, "down", 1);
        createShooterImage(directory + "\\down2.png", mainColor, shadowColor, highlightColor, gunColor, "down", 2);
        createShooterImage(directory + "\\left1.png", mainColor, shadowColor, highlightColor, gunColor, "left", 1);
        createShooterImage(directory + "\\left2.png", mainColor, shadowColor, highlightColor, gunColor, "left", 2);
        createShooterImage(directory + "\\right1.png", mainColor, shadowColor, highlightColor, gunColor, "right", 1);
        createShooterImage(directory + "\\right2.png", mainColor, shadowColor, highlightColor, gunColor, "right", 2);
        
        // Create idle images
        createShooterImage(directory + "\\idleup.png", mainColor, shadowColor, highlightColor, gunColor, "up", 0);
        createShooterImage(directory + "\\idledown.png", mainColor, shadowColor, highlightColor, gunColor, "down", 0);
        createShooterImage(directory + "\\idleleft.png", mainColor, shadowColor, highlightColor, gunColor, "left", 0);
        createShooterImage(directory + "\\idleright.png", mainColor, shadowColor, highlightColor, gunColor, "right", 0);
        
        System.out.println("All ShooterEnemy placeholder images created successfully!");
    }

    private static void createShooterImage(String fileName, Color mainColor, Color shadowColor, Color highlightColor, Color gunColor, String direction, int animationFrame) {
        // Create a 32x32 image for the shooter enemy
        BufferedImage image = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Set rendering hints for better quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Clear the image with transparent background
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, 40, 40);
        g2d.setComposite(AlphaComposite.SrcOver);
        
        // Draw the main body
        g2d.setColor(mainColor);
        g2d.fillOval(5, 5, 30, 30);
        
        // Add some depth/shadow
        g2d.setColor(shadowColor);
        g2d.fillOval(8, 8, 24, 24);
        
        // Add highlight
        g2d.setColor(highlightColor);
        g2d.fillOval(12, 12, 16, 16);
        
        // Add gun based on direction
        g2d.setColor(gunColor);
        switch (direction) {
            case "up":
                g2d.fillRect(18, 0, 4, 14);
                break;
            case "down":
                g2d.fillRect(18, 26, 4, 14);
                break;
            case "left":
                g2d.fillRect(0, 18, 14, 4);
                break;
            case "right":
                g2d.fillRect(26, 18, 14, 4);
                break;
        }
        
        // Add animation effects based on frame
        if (animationFrame > 0) {
            // For walking animation frames
            int offset = (animationFrame == 1) ? -2 : 2;
            
            switch (direction) {
                case "up":
                case "down":
                    // Wobble sideways
                    g2d.setColor(highlightColor);
                    g2d.fillOval(12 + offset, 12, 16, 16);
                    break;
                case "left":
                case "right":
                    // Wobble vertically
                    g2d.setColor(highlightColor);
                    g2d.fillOval(12, 12 + offset, 16, 16);
                    break;
            }
        }
        
        g2d.dispose();
        
        try {
            ImageIO.write(image, "png", new File(fileName));
            System.out.println("Created " + fileName);
        } catch (IOException e) {
            System.err.println("Error creating image " + fileName + ": " + e.getMessage());
        }
    }
}
