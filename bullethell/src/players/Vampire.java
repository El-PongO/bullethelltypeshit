package players;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Vampire extends Player {
    // Life drain skill variables
    private double lifeStealPercent = 0.20; // 20% of damage
    
    private boolean showHealEffect = false;
    private long healEffectStartTime = 0;
    private static final long HEAL_EFFECT_DURATION = 1000; // 1 second
    private BufferedImage[] healEffectSprites; // Array to hold animation frames
    private static final int FRAME_COUNT = 10; // 0-9 frames
    
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

        skillActive = false;
        skillStartTime = 0;
        skillDuration = 5000; // 10 seconds in milliseconds
        skillCooldown = 15000; // 20 seconds cooldown
        lastSkillUseTime = 0;


        String basePath = "/Assets/sparksfx/";
        healEffectSprites = new BufferedImage[FRAME_COUNT];

        for (int i = 1; i <= FRAME_COUNT; i++) {
            String filename = String.format("%sspark%03d.png", basePath, i - 1);
            try {
                healEffectSprites[i - 1] = ImageIO.read(getClass().getResource(filename));
            } catch (Exception e) {
                System.err.println("Failed to load: " + filename);
            }
        }
    }
    
    @Override
    public void getPlayerImage() {
        try {
            up1 = ImageIO.read(getClass().getResource("/Assets/Vampire/up1.png"));
            up2 = ImageIO.read(getClass().getResource("/Assets/Vampire/up2.png"));
            down1 = ImageIO.read(getClass().getResource("/Assets/Vampire/down1.png"));
            down2 = ImageIO.read(getClass().getResource("/Assets/Vampire/down2.png"));
            left1 = ImageIO.read(getClass().getResource("/Assets/Vampire/left1.png"));
            left2 = ImageIO.read(getClass().getResource("/Assets/Vampire/left2.png"));
            right1 = ImageIO.read(getClass().getResource("/Assets/Vampire/right1.png"));
            right2 = ImageIO.read(getClass().getResource("/Assets/Vampire/right2.png"));
            idledown = ImageIO.read(getClass().getResource("/Assets/Vampire/idledown.png"));
            idleleft = ImageIO.read(getClass().getResource("/Assets/Vampire/idleleft.png"));
            idleright = ImageIO.read(getClass().getResource("/Assets/Vampire/idleright.png"));
            idleup = ImageIO.read(getClass().getResource("/Assets/Vampire/idleup.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void useSkill() {
        if (isSkillOnCooldown()) {
            return;
        }
        
        activateSkill();
        
        skillDuration = 5000;
        
        System.out.println("Vampire's skill activated! Life Drain for 5 seconds!");
    }
    

    public void stealHealth(int damage) {
        if (skillActive) {
            // Calculate health to steal (20% of damage)
            int healthStolen = (int)(damage * lifeStealPercent);
            if (healthStolen > 0) {
                heal(healthStolen, true);
                showHealEffect = true; // Activate healing effect visual
                healEffectStartTime = System.currentTimeMillis(); // Start time for healing effect
                System.out.println("Vampire drained " + healthStolen + " health!");
            }
        }
    }
    
    @Override
    public void updateSkill() {
        long currentTime = System.currentTimeMillis();
        
        if (skillActive && currentTime - skillStartTime >= skillDuration) {
            skillActive = false;
            skillDuration = 10000;
            System.out.println("Vampire's life drain ended.");
        }
    }

    @Override
    public void draw(Graphics2D g, int px, int py, int zoom) {
        super.draw(g, px, py, zoom);
        
        if (showHealEffect) {
            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - healEffectStartTime;
        
            if (elapsed < HEAL_EFFECT_DURATION) {
                float alpha = 1.0f - (float)elapsed / HEAL_EFFECT_DURATION;

                int frameIndex = (int)(elapsed / 100);
                if (healEffectSprites != null && frameIndex < FRAME_COUNT && healEffectSprites[frameIndex] != null) {
                    Composite oldComp = g.getComposite();
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

                    int spriteX = px + (size * zoom - healEffectSprites[frameIndex].getWidth()) / 2;
                    int spriteY = py + (size * zoom - healEffectSprites[frameIndex].getHeight()) / 2;

                    g.drawImage(healEffectSprites[frameIndex], spriteX, spriteY, size * zoom, size * zoom, null);

                    g.setComposite(oldComp);
                }
            } else {
                showHealEffect = false;
            }
        }   
    }
}
