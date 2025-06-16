package players;

import java.awt.Color;

public class Bomber extends Player {
    public Bomber(int x, int y) {
        super(x, y);
        health = 150;
        maxHealth = 150;
        dashspeed = 8;
        dashDuration = 250;
        invincibilityDuration = 250;
        maxDashCharges = 2;
        currentDashCharges = maxDashCharges;
        dashChargeCooldown = 6000;
        
        skillCooldown = 20000; // 20 secs
        skillDuration = 1000; // 1 sec
    }

    @Override
    public void getPlayerImage() {
        try {
            up1 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Bomber/up1.png"));
            up2 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Bomber/up2.png")); 
            down1 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Bomber/down1.png"));
            down2 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Bomber/down2.png"));
            left1 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Bomber/left1.png"));
            left2 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Bomber/left2.png"));
            right1 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Bomber/right1.png"));
            right2 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Bomber/right2.png"));
            idledown = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Bomber/idledown.png"));
            idleleft = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Bomber/idleleft.png"));
            idleright = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Bomber/idleright.png"));
            idleup = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Bomber/idleup.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    

    DamageCircle bomberCircleSkill = null;

    @Override
    public void useSkill() {
        if (isSkillOnCooldown()) {
            return;
        }
        activateSkill();
        int centerX = getX() + getSize() / 2;
        int centerY = getY() + getSize() / 2;
        bomberCircleSkill = new DamageCircle(centerX, centerY, 150, 999, 1000, Color.RED);
        System.out.println("Bomber's explosion skill activated!");
    }
    public DamageCircle getCircleSkill() {
        return bomberCircleSkill != null && bomberCircleSkill.isActive() ? bomberCircleSkill : null;
    }
}
