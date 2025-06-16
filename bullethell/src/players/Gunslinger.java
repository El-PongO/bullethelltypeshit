package players;

import java.util.List;
import weapons.Bullet;
import weapons.Weapon;

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

        skillActive = false;
        skillStartTime = 0;
        skillDuration = 10000; // 10 seconds in milliseconds
        skillCooldown = 20000; // 20 seconds cooldown
        lastSkillUseTime = 0;
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
    
    @Override
    public void useSkill() {
        // Check if the skill is on cooldown
        if (isSkillOnCooldown()) {
            return;
        }
        
        // Activate skill
        activateSkill();
        System.out.println("Gunslinger's skill activated! Unlimited ammo for 10 seconds!");
    }
    
    @Override
    public List<Bullet> shoot(int targetX, int targetY) {
        Weapon weapon = getCurrentWeapon();
        weapon.updateReload();

        if (weapon.isReloading()) {
            System.out.println(weapon.getName() + " is reloading!");
            return null;
        }
        
        if (!weapon.canFire()) {
            return null;
        }
        
        if (!skillActive && !weapon.hasAmmo()) {
            System.out.println(weapon.getName() + " out of ammo! Press R to reload.");
            return null;
        }
        
        List<Bullet> bullets = null;
        
        if (skillActive) {
            int savedAmmo = weapon.getCurrentAmmo();
            
            bullets = weapon.fire(x, y, targetX, targetY, size);
            
            if (bullets != null && !bullets.isEmpty()) {
                while (weapon.getCurrentAmmo() < savedAmmo) {
                    weapon.setCurrentAmmo(savedAmmo);
                }
                System.out.println("Unlimited ammo active! Ammo: " + weapon.getCurrentAmmo() + "/" + weapon.getMaxAmmo());
            }
        } else {
            bullets = weapon.fire(x, y, targetX, targetY, size);
            if (bullets != null && !bullets.isEmpty()) {
                System.out.println("Ammo used: " + weapon.getCurrentAmmo() + "/" + weapon.getMaxAmmo());
            }
        }
        
        return bullets;
    }
}
