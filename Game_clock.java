import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Game_clock {
    public JLabel label;
    public int minutes, seconds;
    public Timer timer;

    public Game_clock (){
        minutes = 0;
        seconds = 0;
        label = new JLabel(formatTime(minutes, seconds));
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                seconds++;
                if (seconds == 60) {
                    seconds = 0;
                    minutes++;
                }
                label.setText(formatTime(minutes, seconds));
            }
        });

        label.setFont(new Font("Arial", Font.PLAIN, 20));
        label.setForeground(Color.WHITE);
    }

    public String formatTime(int minutes, int seconds) {
        return String.format("<html>Time:<br>%02d : %02d</html>", minutes, seconds);
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    public void reset() {
        timer.stop();
        minutes=0;
        seconds=0;
        label.setText(formatTime(minutes, seconds));
    }
    
    public void setVisible(boolean visible){
        label.setVisible(visible);
    }

    public void setPosition(int x, int y, int width, int height) {
        label.setBounds(x, y, width, height);
    }
    
    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}
