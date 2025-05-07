
import javax.swing.JFrame;

public class App {
    public static void main(String[] args) throws Exception {
        JFrame window = new JFrame(); // JFrame itu yg buat bikin window. pelajari lebih lanjut di chatgpt
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // biar bisa di close 
        window.setResizable(false); // biar ga bisa di resize, klo gk rusak game nya
        window.setTitle("Bullet Hell"); // tittylenya
        window.setSize(1024,768); // window size

        GamePanel panel = new GamePanel();
        window.add(panel); //buat windownya

        window.setVisible(true); // biar bisa di liat windownya
        window.setLocationRelativeTo(null); // biar di tengah
    }
}
