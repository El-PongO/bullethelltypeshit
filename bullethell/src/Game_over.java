import java.awt.*;
public class Game_over {
    public void draw(Graphics g, int width, int height){
        g.setColor(new Color(28, 51, 92));
        g.fillRect(0, 0, width, height); // gambar Game over
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("Game Over", 370, 330);
    }
}
