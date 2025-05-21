import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
public class CursorManager {
    private final Map<String, Cursor> cursors = new HashMap<>();
    private final Toolkit toolkit = Toolkit.getDefaultToolkit();

    // hotspot is the point in the image that will be the "click" point of the cursor
    public void loadCursor(String name, String imagePath, Point hotspot, int width, int height) {
        try {
            java.io.InputStream stream = getClass().getResourceAsStream(imagePath);
            System.out.println("Resource stream: " + stream);
            if (stream == null) {
                System.err.println("Resource not found: " + imagePath);
                return;
            }
            Image image = ImageIO.read(stream);
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            Cursor cursor = toolkit.createCustomCursor(scaledImage, hotspot, name);
            cursors.put(name, cursor);
            System.out.println("Cursor loaded and stored: " + name);
        } catch (Exception e) {
            System.err.println("Failed to load cursor: " + name + " from " + imagePath);
            e.printStackTrace();
        }
    }
    

    public void loadInvisibleCursor(String name) { // Create a transparent image for the invisible cursor
        BufferedImage transparentImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor invisible = toolkit.createCustomCursor(transparentImg, new Point(0, 0), name);
        cursors.put(name, invisible);
    }

    public void setCursor(Component component, String name) { // Set the cursor for the given component
        Cursor cursor = cursors.get(name);
        if (cursor != null) {
            component.setCursor(cursor);
        } else {
            System.err.println("Cursor not found: " + name);
        }
    }

    public void resetToDefault(Component component) { // Reset the cursor to default
        System.out.println("Resetting cursor to default");
        component.setCursor(Cursor.getDefaultCursor());
    }
}
