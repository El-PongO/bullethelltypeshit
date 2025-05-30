
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class HeroSelectPanel extends JPanel {
    public interface HeroSelectListener {
        void onHeroSelected(String heroName);
    }

    public HeroSelectPanel(HeroSelectListener listener) {
        setLayout(new GridLayout(2, 2, 20, 20));
        JButton gunslingerBtn = new JButton("Gunslinger");
        JButton bomberBtn = new JButton("Bomber");
        JButton rogueBtn = new JButton("Rogue");
        JButton vampireBtn = new JButton("Vampire");

        ActionListener buttonListener = e -> {
            JButton src = (JButton) e.getSource();
            listener.onHeroSelected(src.getText());
        };
        gunslingerBtn.addActionListener(buttonListener);
        bomberBtn.addActionListener(buttonListener);
        rogueBtn.addActionListener(buttonListener);
        vampireBtn.addActionListener(buttonListener);

        add(gunslingerBtn);
        add(bomberBtn);
        add(rogueBtn);
        add(vampireBtn);
    }
}
