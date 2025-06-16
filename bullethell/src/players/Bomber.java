package players;

public class Bomber extends Player {
    public Bomber(int x, int y) {
        super(x, y);
        health = 150;
        maxHealth = 150;
        dashspeed = 8;
        dashDuration = 250;
        maxDashCharges = 2;
        currentDashCharges = maxDashCharges;
        dashChargeCooldown = 6000;
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
    public void useSkill() {
        // TODO: Implement Bomber's unique skill
    }
}
