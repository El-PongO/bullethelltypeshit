import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class GameMap {
    private int[][] grid;
    private BufferedImage[] tilesSprite = new BufferedImage[9];
    private static final int TILE_SIZE = 32;
    private static final int VIEWPORT_WIDTH = 15;
    private static final int VIEWPORT_HEIGHT = 11;
    private static final int ZOOM = 2;

    public GameMap() {
        loadMap();
        loadSprites();
    }

    private void loadSprites() {
        try {
            for (int i = 0; i < 9; i++) {
                int idx;
                switch (i) {
                    case 0: idx = 23; break;
                    case 1: idx = 1; break;
                    case 2: idx = 22; break;
                    case 3: idx = 24; break;
                    case 4: idx = 45; break;
                    case 5: idx = 0; break;
                    case 6: idx = 2; break;
                    case 7: idx = 44; break;
                    default: idx = 46; break;
                }
                tilesSprite[i] = ImageIO.read(getClass().getResource("/Assets/tile/tile0" + String.format("%02d", idx) + ".png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMap() {
        try {
            grid = loadMapFromFile("bullethell/src/map.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g, Player player) {
        int vpw = VIEWPORT_WIDTH * TILE_SIZE;
        int vph = VIEWPORT_HEIGHT * TILE_SIZE;
        int cameraPixelX = player.getX() + player.getHitboxSize() / 2 - vpw / 2;
        int cameraPixelY = player.getY() + player.getHitboxSize() / 2 - vph / 2;

        int startY = cameraPixelY / TILE_SIZE;
        int startX = cameraPixelX / TILE_SIZE;
        int endY = (cameraPixelY + vph) / TILE_SIZE + 1;
        int endX = (cameraPixelX + vpw) / TILE_SIZE + 1;

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                int drawX = (x * TILE_SIZE - cameraPixelX) * ZOOM;
                int drawY = (y * TILE_SIZE - cameraPixelY) * ZOOM;
                if (y >= 0 && y < grid.length && x >= 0 && x < grid[0].length) {
                    g.drawImage(tilesSprite[grid[y][x]], drawX, drawY, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM, null);
                } else {
                    g.setColor(Color.BLACK);
                    g.fillRect(drawX, drawY, TILE_SIZE * ZOOM, TILE_SIZE * ZOOM);
                }
            }
        }
    }

    private int[][] loadMapFromFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        int rows = 0, cols = 0;
        while ((line = reader.readLine()) != null) {
            cols = line.length();
            rows++;
        }
        reader.close();

        int[][] grid = new int[rows][cols];
        reader = new BufferedReader(new FileReader(filePath));
        int row = 0;
        while ((line = reader.readLine()) != null) {
            for (int col = 0; col < line.length(); col++) {
                grid[row][col] = Character.getNumericValue(line.charAt(col));
            }
            row++;
        }
        reader.close();
        return grid;
    }

    public boolean isWalkable(int x, int y) {
        int gridX = x / TILE_SIZE;
        int gridY = y / TILE_SIZE;
        return gridY >= 0 && gridY < grid.length && 
               gridX >= 0 && gridX < grid[0].length && 
               grid[gridY][gridX] != 1;
    }
}
