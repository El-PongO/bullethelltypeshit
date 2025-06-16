import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.JFrame; 
public class App {
    public static void main(String[] args) throws Exception {
        int width = 1024, height = 768; // default width and height
        boolean fullscreen = false;
        File config = new File("config.cfg");
        if (config.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(config))) {
                width = Integer.parseInt(br.readLine());
                height = Integer.parseInt(br.readLine());
                String fs = br.readLine();
                fullscreen = fs != null && fs.trim().equals("1");
            } catch (Exception ignored) {}
        }
        JFrame window = new JFrame(); // JFrame itu yg buat bikin window. pelajari lebih lanjut di chatgpt
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // biar bisa di close 
        window.setResizable(false); // biar ga bisa di resize, klo gk rusak game nya
        window.setTitle("Bullet Hell"); // tittylenya
        if (fullscreen) {
            window.dispose();
            window.setUndecorated(true);
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }else{
            window.setSize(width, height); // window size
        }
            // Initialize the SettingMenu before creating MainPanel
        GameplayPanel.initializeSettingMenu(window);
        
        // Make sure the SettingMenu is properly initialized
        if (GameplayPanel.getSettingMenu() == null) {
            System.out.println("Warning: SettingMenu was not properly initialized.");
        }
        
        // Use the new MainPanel instead of GamePanel
        MainPanel panel = new MainPanel();
        window.add(panel); //buat windownya
        window.setVisible(true); // biar bisa di liat windownya
        window.setLocationRelativeTo(null); // biar di tengah
    }
}
