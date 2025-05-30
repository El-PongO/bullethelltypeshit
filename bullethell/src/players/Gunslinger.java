package players;

public class Gunslinger extends Player {
    public Gunslinger(int x, int y) {
        super(x, y);
        health = 120;
        maxHealth = 120;
        dashspeed = 15;
        dashDuration = 300;
        invincibilityDuration = 300;
        maxDashCharges = 3;
        currentDashCharges = maxDashCharges;
        dashChargeCooldown = 4000;
    }

    @Override
    public void getPlayerImage() {
        try {
            up1 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Hunter/up1.png"));
            up2 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Hunter/up2.png"));
            down1 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Hunter/down1.png"));
            down2 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Hunter/down2.png"));
            left1 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Hunter/left1.png"));
            left2 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Hunter/left2.png"));
            right1 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Hunter/right1.png"));
            right2 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Hunter/right2.png"));
            idledown = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Hunter/idledown.png"));
            idleleft = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Hunter/idleleft.png"));
            idleright = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Hunter/idleright.png"));
            idleup = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Hunter/idleup.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void useSkill() {
        // TODO: Implement Gunslinger's unique skill
    }

    @Override
    public Bullet shoot(int targetX, int targetY) {
        return super.shoot(targetX, targetY);
    }
}
