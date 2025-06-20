package players;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import weapons.Bullet;
import weapons.Weapon;

public abstract class Player {
    protected int x;
    protected int y;    
    protected int health = 100; // Player's health
    protected int maxHealth = 100; // Maximum health
    static int size = 20;
    static int speed = 4;
    static int bulletSpeed = 3; // Bullet speed
    protected int dashspeed = 10; // dash distance
    private boolean isDashing = false;
    private boolean isInvincible = false; // Invincibility flag
    protected long dashDuration = 200; // Dash duration in milliseconds
    protected long invincibilityDuration = 1500; // Invincibility duration (1.5 seconds)
    private long dashStartTime = 0; // When the current dash started
    protected int maxDashCharges = 2; // Maximum number of dash charges 
    protected int currentDashCharges = maxDashCharges; // Current number of dash charges ambil dari (maxDashCharges)
    protected int dashChargeCooldown = 5000; // Cooldown time for dash charges in milliseconds
    private long lastChargeTime = 0; // Last time a dash charge was used
    
    // Skill system variables
    protected boolean skillActive = false;
    protected long skillStartTime = 0;
    protected long skillDuration;
    protected long skillCooldown;
    protected long lastSkillUseTime = 0;
    
    public String direction;
    public boolean idling;
    public BufferedImage idledown, idleleft, idleright, idleup, up1, up2, down1, down2, left1, left2, right1, right2, weapon_pistol_1, ammo_pistol1, weapon_pistol_2, ammo_pistol2, weapon_rif_1, ammo_rif1;
    public int spritecounter=0;
    public int spritenum=1;
    private List<Weapon> weapons = new ArrayList<>();
    private int currentWeaponIndex = 0; // 0 = weapon 1, 1 = weapon 2, etc.
    public BufferedImage changewp, reloadbar, reloadbar2;
    private boolean dashInvincible = false;
    private boolean hitInvincible = false;
    private long hitInvincibleStartTime = 0;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        getPlayerImage();
        direction="down";
        idling=true;
    }

    public void getPlayerImage(){
        try {
            up1 = ImageIO.read(getClass().getResource("/Assets/Hunter/up1.png"));
            up2 = ImageIO.read(getClass().getResource("/Assets/Hunter/up2.png"));
            down1 = ImageIO.read(getClass().getResource("/Assets/Hunter/down1.png")); // FIXED
            down2 = ImageIO.read(getClass().getResource("/Assets/Hunter/down2.png"));
            left1 = ImageIO.read(getClass().getResource("/Assets/Hunter/left1.png"));
            left2 = ImageIO.read(getClass().getResource("/Assets/Hunter/left2.png"));
            right1 = ImageIO.read(getClass().getResource("/Assets/Hunter/right1.png"));
            right2 = ImageIO.read(getClass().getResource("/Assets/Hunter/right2.png")); // not "right.png"
            idledown = ImageIO.read(getClass().getResource("/Assets/Hunter/idledown.png"));
            idleleft = ImageIO.read(getClass().getResource("/Assets/Hunter/idleleft.png"));
            idleright = ImageIO.read(getClass().getResource("/Assets/Hunter/idleright.png"));
            idleup = ImageIO.read(getClass().getResource("/Assets/Hunter/idleup.png"));
            changewp = ImageIO.read(getClass().getResource("/Assets/player/change.png"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g, int px, int py, int zoom, Point mouse) {
        BufferedImage bimage = null;
        //////////////////////////////////////
            int barlength = size * zoom;
            g.setColor(Color.BLUE);
            g.fillRect(px, py-8, barlength, 4);
            g.setColor(Color.RED);
            double hp = barlength*((double)health/(double)maxHealth); // HP scaling
            g.fillRect(px, py-8, (int)hp, 4);
            g.setColor(Color.GRAY);
            g.fillOval(px, py-4, barlength, 4);
            g.setColor(Color.WHITE);
            double dashcount = barlength * ((double)currentDashCharges / (double)maxDashCharges);
            g.fillOval(px, py-4, (int)dashcount, 4);
        ///////////////////////////////////////
        if (idling){
            switch (direction) {
                case "up": bimage=idleup; break;
                case "down": bimage=idledown; break;
                case "left": bimage=idleleft; break;
                case "right": bimage=idleright; break;
                default: break;
            }
        }else{
            switch (direction) {
                case "up":
                    if (spritenum==1){
                        bimage=up1;
                    }else if (spritenum==2){
                        bimage=up2;
                    }
                    break;
                case "down":
                    if (spritenum==1){
                        bimage=down1;
                    } else if (spritenum==2){
                        bimage=down2;
                    }
                    break;
                case "left":
                    if (spritenum==1){
                        bimage=left1;
                    } else if (spritenum==2){
                        bimage=left2;
                    }
                    break;
                case "right":
                    if (spritenum==1){
                        bimage=right1;
                    } else if (spritenum==2){
                        bimage=right2;
                    }
                    break;
                default: break;
            }
        }        int drawX = px, drawY = py, drawW = size * zoom, drawH = size * zoom;
        
        if (isDashing){
            // Draw blurred "afterimages"
            Composite oldComp = g.getComposite();
            for (int i = 1; i <= 4; i++) {
                float alpha = 0.15f * (5 - i); // Fainter for further afterimages
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                int offset = i * 6;
                int blurX = drawX, blurY = drawY;
                switch (direction) {
                    case "up":    blurY += offset; break;
                    case "down":  blurY -= offset; break;
                    case "left":  blurX += offset; break;
                    case "right": blurX -= offset; break;
                }
                g.drawImage(bimage, blurX, blurY, drawW, drawH, null);
            }
            g.setComposite(oldComp);
        }
          // Visual effect for invincibility (blinking)
        Composite oldComp = g.getComposite();
        if (isInvincible() && !isDashing) {
            // Make player blink during invincibility (not during dash)
            long currentTime = System.currentTimeMillis();
            if ((currentTime / 150) % 2 == 0) { // Blink every 150ms
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            }
        }
        
        // Draw relative to camera position
        g.drawImage(bimage, px, py, size*zoom, size*zoom, null);
        
        // Reset composite if it was changed for invincibility effect
        if (isInvincible() && !isDashing) {
            g.setComposite(oldComp);
        }

        
        if (mouse != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            int playerCenterX = px + getSize() * zoom / 2;
            int playerCenterY = py + getSize() * zoom / 2;

            // Calculate angle to cursor
            double dx = mouse.x - playerCenterX;
            double dy = mouse.y - playerCenterY;
            double angle = Math.atan2(dy, dx);

            int radius = getSize() * zoom / 2 + 30;

            // Arrow tip position
            int arrowTipX = (int) (playerCenterX + Math.cos(angle) * radius);
            int arrowTipY = (int) (playerCenterY + Math.sin(angle) * radius);

            // Arrow base points (triangle)
            int arrowLength = 16;
            int arrowWidth = 16;
            double baseAngle1 = angle + Math.toRadians(150);
            double baseAngle2 = angle - Math.toRadians(150);

            int base1X = (int) (arrowTipX + Math.cos(baseAngle1) * arrowLength);
            int base1Y = (int) (arrowTipY + Math.sin(baseAngle1) * arrowLength);
            int base2X = (int) (arrowTipX + Math.cos(baseAngle2) * arrowLength);
            int base2Y = (int) (arrowTipY + Math.sin(baseAngle2) * arrowLength);

            int[] xPoints = {arrowTipX, base1X, base2X};
            int[] yPoints = {arrowTipY, base1Y, base2Y};

            g2d.setColor(Color.BLUE);
            g2d.fillPolygon(xPoints, yPoints, 3);
            g2d.setColor(Color.WHITE);
            g2d.drawPolygon(xPoints, yPoints, 3);

            g2d.dispose();
        }
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;//gae movement e player receiver wasd ne
    }

    public List<Bullet> shoot(int targetX, int targetY) {
        Weapon weapon = getCurrentWeapon();
        weapon.updateReload();

        if (weapon.isReloading()) {
            System.out.println(weapon.getName() + " is reloading!");
            return null;
        }
        if (!weapon.hasAmmo()) {
            System.out.println(weapon.getName() + " out of ammo! Press R to reload.");
            return null;
        }
        if (!weapon.canFire()) {
            return null;
        }
        // Pass player size to the weapon's shoot method
        return weapon.fire(x, y, targetX, targetY, size);
    }

    public void move(boolean upPressed,boolean downPressed, boolean leftPressed, boolean rightPressed,int[][] grid, int tileSize, boolean mouseHeld){       
        if (upPressed || downPressed || leftPressed || rightPressed){
            this.idling=false; // cek player kalau jalan berati tidak idle
            int dx = 0, dy = 0;
            if (upPressed) {
                dy -= speed;
            }
            if (downPressed){
                dy += speed;
            } 
            if (leftPressed){
                dx -= speed;
            } 
            if (rightPressed){
                dx += speed;
            } 
            if(!mouseHeld){
                if (dx > 0) direction = "right";
                else if (dx < 0) direction = "left";
                else if (dy > 0) direction = "down";
                else if (dy < 0) direction = "up";
            }

            int currentSpeed = isDashing ? dashspeed/3 : 1;
            int newX = x + dx*currentSpeed, newY = y + dy*currentSpeed;
            int gridX = (newX + size / 2) / tileSize;
            int gridY = (newY + size / 2) / tileSize;
            if (gridY >= 0 && gridY < grid.length && gridX >= 0 && gridX < grid[0].length && grid[gridY][gridX] == 0) {
            x = newX;
            y = newY;
        }

            this.spritecounter++; // delay buat ganti jenis sprite
            if (this.spritecounter > 12){ // di panggil 5x tiap jalan program (60/12)
                if (this.spritenum==1){
                    this.spritenum=2;
                }else if (this.spritenum==2){
                    this.spritenum=1;
                }
                this.spritecounter=0; // kalau sudah ganti varian set counter ke 0
            }
        }else{
            this.idling=true;
            this.spritenum=1; // set sprite ke 1
        }
    }    

    public void updateDash() {
        long currentTime = System.currentTimeMillis();
    
        // End dash and dash invincibility together
        if (isDashing && currentTime - dashStartTime >= dashDuration) {
            isDashing = false;
            dashInvincible = false;
        }
    
        // End hit invincibility after duration
        if (hitInvincible && (currentTime - hitInvincibleStartTime > invincibilityDuration)) {
            hitInvincible = false;
            System.out.println(currentTime - hitInvincibleStartTime + " ms since hit invincibility ended");
            System.out.println(invincibilityDuration + " ms invincibility duration");
        }
    
        // Recharge dash charges...s
        if (currentDashCharges < maxDashCharges && currentTime - lastChargeTime >= dashChargeCooldown) {
            currentDashCharges++;
            lastChargeTime = currentTime;
        }
    }

    public void dash() {
        long currentTime = System.currentTimeMillis();
        if (currentDashCharges > 0 && !isDashing) {
            isDashing = true;
            dashInvincible = true;
            dashStartTime = currentTime;
            currentDashCharges--;
        }
    }
    
    public void takeDamage(int damage) {
        if (!isInvincible) { // Only take damage if not invincible
            health -= damage;
            if (health < 0) {
                health = 0; // Prevent health from going negative
            }
            // Activate invincibility frame after taking damage
            activateInvincibility();
        }
    }

    // Activate invincibility for the player
    public void activateInvincibility() {
        hitInvincible = true;
        hitInvincibleStartTime = System.currentTimeMillis();
    }
    
    // Check if the player is currently invincible (either from dash or hit)
    public boolean isInvincible() {
        return dashInvincible || hitInvincible;
    }

    public void heal(int amount, Boolean isCapped) { // Nanti kalo dipake (pasti dipake sih)
        health += amount;
        if (health > maxHealth && isCapped) {
            health = maxHealth; // Cap health at maxHealth
        }
    }

    public void reloadCurrentWeapon() {
        Weapon weapon = getCurrentWeapon();
        weapon.startReload();
    }    
    public int getCurrentWeaponIndex() { return currentWeaponIndex; }
    public int getWeaponMinIndex() { return 0; }
    public int getWeaponMaxIndex() { return weapons.size() - 1; }
    public Weapon getCurrentWeapon() { 
        if (weapons.isEmpty()) {
            // If no weapons are available, return null or add a default weapon
            try {
                // Add a default weapon to prevent further exceptions
                weapons.add(new weapons.Revolver());
                System.out.println("Warning: No weapons available. Added default Revolver.");
                return weapons.get(0);
            } catch (Exception e) {
                System.out.println("Error creating default weapon: " + e.getMessage());
                return null;
            }
        }
        return weapons.get(currentWeaponIndex); 
    }
    public List<Weapon> getWeapons() { return weapons; }
    public void setWeaponIndex(int index) {
        if (index >= getWeaponMinIndex() && index <= getWeaponMaxIndex()) {
            int oldIndex = currentWeaponIndex;
            currentWeaponIndex = index;
            System.out.println("Switched weapon from " + weapons.get(oldIndex).getName() + " to " + weapons.get(currentWeaponIndex).getName());
        }
    }

    public void switchWeapon(int direction) {
        int newIndex = currentWeaponIndex + direction;
        int oldIndex = currentWeaponIndex;
        if (newIndex >= 0 && newIndex < weapons.size()) {
            currentWeaponIndex = newIndex;
            System.out.println("Switched weapon from " + weapons.get(oldIndex).getName() + " to " + weapons.get(currentWeaponIndex).getName());
        }
    }

    public String[] getWeaponNames() {
        return weapons.stream().map(Weapon::getName).toArray(String[]::new);
    }    
    
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        if (health > maxHealth) {
            health = maxHealth; // Adjust current health if it exceeds the new maxHealth
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCurrentDashCharges() {
        return currentDashCharges;
    }

    public int getMaxDashCharges() {
        return maxDashCharges;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public static int getSize() { return size; }
    public static int getSpeed() { return speed; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public BufferedImage getChangewp() {
        return changewp;
    }

    public void setChangewp(BufferedImage changewp) {
        this.changewp = changewp;
    }

    public boolean isSkillActive() { return skillActive; }
    public long getSkillStartTime() { return skillStartTime; }
    public long getSkillDuration() { return skillDuration; }
    public long getSkillCooldown() { return skillCooldown; }
    public long getLastSkillUseTime() { return lastSkillUseTime; }

    // Method to update the skill status
    public void updateSkill() {
        long currentTime = System.currentTimeMillis();
        
        // Check if skill is active and duration has expired
        if (skillActive && currentTime - skillStartTime >= skillDuration) {
            skillActive = false;
            System.out.println("Skill duration ended.");
        }
    }    
    // Abstract method for player skills - each player class must implement this
    public abstract void useSkill();

    // Helper method to check if skill is on cooldown
    public boolean isSkillOnCooldown() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSkillUseTime < skillCooldown) {
            long remainingCooldown = (lastSkillUseTime + skillCooldown - currentTime) / 1000;
            System.out.println("Skill on cooldown! " + remainingCooldown + " seconds remaining.");
            return true;
        }
        return false;
    }
    
    // Helper method to activate skill (set common flags)
    public void activateSkill() {
        skillActive = true;
        skillStartTime = System.currentTimeMillis();
        lastSkillUseTime = System.currentTimeMillis();
    }

    boolean speedBoostActive = false;
    int boostCount = 0;
    public void speedBoost(int duration, int amount) {
        boostCount++;
        if(!speedBoostActive) {
            speedBoostActive = true;
            speed += amount;
        }
        new Thread(() -> {
            int count = boostCount;
            try {
                Thread.sleep(duration);
                if(boostCount == count) {
                    speed -= amount;
                    speedBoostActive = false;
                    System.out.println("Speed boost ended. Current speed: " + speed);
                }
                System.out.println("Boost Continue : " + boostCount + " " + count);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}