import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class WeaponSelectPanel extends JPanel {
    public interface WeaponSelectListener {
        void onWeaponSelected(String weaponName);
    }

    public WeaponSelectPanel(WeaponSelectListener listener) {
        setLayout(new GridLayout(1, 3, 20, 20));
        JButton pistolBtn = new JButton("Pistol");
        JButton placeholder1Btn = new JButton("No weapon for now lol");
        JButton placeholder2Btn = new JButton("gedagedigedageda oh");

        ActionListener buttonListener = e -> {
            JButton src = (JButton) e.getSource();
            listener.onWeaponSelected(src.getText());
        };
        pistolBtn.addActionListener(buttonListener);
        placeholder1Btn.addActionListener(buttonListener);
        placeholder2Btn.addActionListener(buttonListener);

        add(pistolBtn);
        add(placeholder1Btn);
        add(placeholder2Btn);
    }
}
