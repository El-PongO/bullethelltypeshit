package players;

import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Brute extends Player {
    private int originalSpeed;
    private boolean skillBuffActive = false;
    
    // Shield effect variables
    private BufferedImage[] shieldEffectSprites;
    private static final int FRAME_COUNT = 6; // 0-5 frames
    private static final long FRAME_INTERVAL = 100; // Show shield effect every 500ms during skill
    private long lastShieldEffectTime = 0;
    private int currentShieldFrame = 0;

    public Brute(int x, int y) {
        super(x, y);
        health = 90;
        maxHealth = 90;
        dashspeed = 20;
        dashDuration = 180;
        invincibilityDuration = 180;
        maxDashCharges = 4;
        currentDashCharges = maxDashCharges;
        dashChargeCooldown = 3000;

        skillActive = false;
        skillStartTime = 0;
        skillDuration = 10000; // 10 seconds in milliseconds
        skillCooldown = 20000; // 20 seconds cooldown
        lastSkillUseTime = 0;
        
        // Load shield effect sprites
        String basePath = "/Assets/shieldsfx/";
        shieldEffectSprites = new BufferedImage[FRAME_COUNT];

        for (int i = 0; i < FRAME_COUNT; i++) {
            String filename = String.format("%sshield%03d.png", basePath, i);
            try {
                shieldEffectSprites[i] = ImageIO.read(getClass().getResource(filename));
            } catch (Exception e) {
                System.err.println("Failed to load: " + filename);
            }
        }
    }    @Override
    public void getPlayerImage() {
        try {
            up1 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Brute/up1.png"));
            up2 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Brute/up2.png")); 
            down1 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Brute/down1.png"));
            down2 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Brute/down2.png"));
            left1 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Brute/left1.png"));
            left2 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Brute/left2.png"));
            right1 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Brute/right1.png"));
            right2 = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Brute/right2.png"));
            idledown = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Brute/idledown.png"));
            idleleft = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Brute/idleleft.png"));
            idleright = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Brute/idleright.png"));
            idleup = javax.imageio.ImageIO.read(getClass().getResource("/Assets/Brute/idleup.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }      @Override
    public void useSkill() {
        if (isSkillOnCooldown()) {
            return;
        }
        
        activateSkill();
        System.out.println("Brute's skill activated! Damage reduction, speed boost, and heal!");
        heal(20, true);
        if (!skillBuffActive) {
            originalSpeed = speed;
            speed += 1;
            skillBuffActive = true;
        }
        
        // Reset shield effect timing
        lastShieldEffectTime = System.currentTimeMillis();
        currentShieldFrame = 0;
    }    @Override
    public void takeDamage(int damage) {
        if (skillActive) {
            damage = (int)Math.ceil(damage * 0.2); //takes x0.2 damage
            lastShieldEffectTime = System.currentTimeMillis();
        }
        super.takeDamage(damage);
    }

    @Override
    public void updateSkill() {
        boolean wasActive = skillActive;
        super.updateSkill();
        
        if (skillBuffActive && wasActive && !skillActive) {
            speed = originalSpeed;
            skillBuffActive = false;
        }
        
        if (skillActive) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShieldEffectTime > FRAME_INTERVAL) {
                lastShieldEffectTime = currentTime;
                currentShieldFrame = (currentShieldFrame + 1) % FRAME_COUNT;
            }
        }
    }
    @Override
    public void draw(Graphics2D g, int px, int py, int zoom) {
        super.draw(g, px, py, zoom);
        
        if (skillActive && shieldEffectSprites != null) {
            BufferedImage shieldImage = shieldEffectSprites[currentShieldFrame];
            if (shieldImage != null) {
                Composite oldComp = g.getComposite();
                
                int shieldSize = size * zoom + 10;
                int shieldX = px - 5;
                int shieldY = py - 5;
                
                g.drawImage(shieldImage, shieldX, shieldY, shieldSize, shieldSize, null);
                
                g.setComposite(oldComp);
            }
        }
    }
}
