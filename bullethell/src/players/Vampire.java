package players;

public class Vampire extends Player {
    public Vampire(int x, int y) {
        super(x, y);
        health = 110;
        maxHealth = 110;
        dashspeed = 12;
        dashDuration = 220;
        invincibilityDuration = 220;
        maxDashCharges = 2;
        currentDashCharges = maxDashCharges;
        dashChargeCooldown = 5000;
    }

    @Override
    public void getPlayerImage() {
        try {
            up1 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Vampire/up1.png"));
            up2 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Vampire/up2.png"));
            down1 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Vampire/down1.png"));
            down2 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Vampire/down2.png"));
            left1 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Vampire/left1.png"));
            left2 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Vampire/left2.png"));
            right1 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Vampire/right1.png"));
            right2 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Vampire/right2.png"));
            idledown = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Vampire/idledown.png"));
            idleleft = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Vampire/idleleft.png"));
            idleright = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Vampire/idleright.png"));
            idleup = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Vampire/idleup.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
    public void useSkill() {
        // TODO: Implement Vampire's unique skill
    }
}
