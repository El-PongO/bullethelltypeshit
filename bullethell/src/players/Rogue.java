package players;

public class Rogue extends Player {
    public Rogue(int x, int y) {
        super(x, y);
        health = 90;
        maxHealth = 90;
        dashspeed = 20;
        dashDuration = 180;
        invincibilityDuration = 180;
        maxDashCharges = 4;
        currentDashCharges = maxDashCharges;
        dashChargeCooldown = 3000;
    }

    @Override
    public void getPlayerImage() {
        try {
            up1 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Rogue/up1.png"));
            up2 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Rogue/up2.png")); 
            down1 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Rogue/down1.png"));
            down2 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Rogue/down2.png"));
            left1 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Rogue/left1.png"));
            left2 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Rogue/left2.png"));
            right1 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Rogue/right1.png"));
            right2 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Rogue/right2.png"));
            idledown = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Rogue/idledown.png"));
            idleleft = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Rogue/idleleft.png"));
            idleright = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Rogue/idleright.png"));
            idleup = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Rogue/idleup.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
    public void useSkill() {
        // TODO: Implement Rogue's unique skill
    }
}
