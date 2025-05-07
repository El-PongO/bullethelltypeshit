import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Button {
    public JButton new_button;
    String nama;

    public Button(String nama){
        this.nama=nama;
        new_button = new JButton(nama);
        new_button.setBackground(Color.DARK_GRAY);
        new_button.setForeground(Color.WHITE); // text color
        new_button.setFont(new Font("Arial", Font.BOLD, 18)); 
        new_button.setFocusPainted(false); // ilangin focus border
        new_button.setBorder(BorderFactory.createLineBorder(Color.WHITE)); // kasi border putih
    }

    public void setBound(int x, int y, int width, int height){
        new_button.setBounds(x, y, width, height);
    }

    public void setInvisible(){
        new_button.setVisible(false);
    }
    
    public void setVisible(){
        new_button.setVisible(true);
    }
    
    public void repaint(){
        repaint();
    }
    
    public void remove(){
        new_button.remove(new_button);
    }

    public void addActionListener(ActionListener listener){
        new_button.addActionListener(listener);
    }

}

