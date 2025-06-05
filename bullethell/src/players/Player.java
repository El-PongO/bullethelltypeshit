package players;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;


public abstract class Player {
    protected int x;
    protected int y;    protected int health = 100; // Player's health
    protected int maxHealth = 100; // Maximum health
    static int size = 20;
    static int speed = 4;
    static int bulletSpeed = 3; // Bullet speed
    protected int dashspeed = 10; // dash distance
    private boolean isDashing = false;
    private boolean isInvincible = false; // Invincibility flag
    protected long dashDuration = 200; // Dash duration in milliseconds
    protected long invincibilityDuration = 1000; // Invincibility duration (1 second)
    private long invincibilityStartTime = 0; // When invincibility started
    private long dashStartTime = 0; // When the current dash started
    protected int maxDashCharges = 2; // Maximum number of dash charges 
    protected int currentDashCharges = maxDashCharges; // Current number of dash charges ambil dari (maxDashCharges)
    protected int dashChargeCooldown = 5000; // Cooldown time for dash charges in milliseconds
    private long lastChargeTime = 0; // Last time a dash charge was used
    public String direction;
    public boolean idling;
    public BufferedImage idledown, idleleft, idleright, idleup, up1, up2, down1, down2, left1, left2, right1, right2, weapon_pistol_1, ammo_pistol1, weapon_pistol_2, ammo_pistol2, weapon_rif_1, ammo_rif1;
    public int spritecounter=0;
    public int spritenum=1;

    private final String[] weaponNames = {"Weapon 1", "Weapon 2", "Weapon 3"}; // Add more if needed
    private List<Weapon> weapons = new ArrayList<>();
    private int currentWeaponIndex = 0; // 0 = weapon 1, 1 = weapon 2, etc.
    public BufferedImage changewp, reloadbar, reloadbar2;

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
            // Load weapons
            weapons.clear();
            weapons.add(new Weapon("Weapon 1",
                ImageIO.read(getClass().getResource("/Assets/player/Guns/revolver.png")),
                ImageIO.read(getClass().getResource("/Assets/player/bullets/revolver/Bullet2.png")),
                6, 1200, 400, false // Revolver: 6 ammo, 1.2s reload, 400ms fire rate, semi-auto
            ));
            weapons.add(new Weapon("Weapon 2",
                ImageIO.read(getClass().getResource("/Assets/player/Guns/glock.png")),
                ImageIO.read(getClass().getResource("/Assets/player/bullets/glock/Bullet2.png")),
                18, 1500, 150, false // Glock: 18 ammo, 1.5s reload, 150ms fire rate, semi-auto
            ));
            weapons.add(new Weapon("Weapon 3",
                ImageIO.read(getClass().getResource("/Assets/player/Guns/smg1.png")),
                ImageIO.read(getClass().getResource("/Assets/player/bullets/smg1/Bullet2.png")),
                30, 2000, 80, true // SMG1: 30 ammo, 2s reload, 60ms fire rate, full-auto
            ));
            weapons.add(new Weapon("Weapon 4",
                ImageIO.read(getClass().getResource("/Assets/player/Guns/shotgun.png")),
                ImageIO.read(getClass().getResource("/Assets/player/bullets/smg1/Bullet2.png")),
                8, 3000, 800, false // Shotgun: 8 ammo, 3s reload, 600ms fire rate, semi-auto
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g, int px, int py, int zoom) {
        BufferedImage bimage = null;
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
        }        List<Bullet> bullets = new ArrayList<>();
    
        // Shotgun logic: check by weapon name instead of index
        if (weapon.getName().equalsIgnoreCase("Shotgun")) {
            int pellets = 8;
            double spread = Math.toRadians(45); // 45 degree spread
            double angle = Math.atan2(targetY - (y + Player.getSize()/2), targetX - (x + Player.getSize()/2));
            Random rand = new Random();
            if (weapon.getCurrentAmmo() > 0) {
                for (int i = 0; i < pellets; i++) {
                    double minAngle = angle - spread / 2;
                    double segment = spread / (pellets - 1);
                    double jitter = (rand.nextDouble() - 0.5) * (segment * 0.4); // 40% of segment width
                    double pelletAngle = minAngle + i * segment + jitter;
                    int dx = (int)(Math.cos(pelletAngle) * 3) * bulletSpeed;
                    int dy = (int)(Math.sin(pelletAngle) * 3) * bulletSpeed;
                    bullets.add(new Bullet(x + Player.getSize()/2, y + Player.getSize()/2, dx, dy, null));
                }
                weapon.useAmmo(); // Only use 1 ammo per shot
                weapon.recordShot();
            }
        } else {
            weapon.useAmmo();
            weapon.recordShot();
            double angle = Math.atan2(targetY - (y + Player.getSize()/2), targetX - (x + Player.getSize()/2));
            int dx = (int)(Math.cos(angle) * 3) * bulletSpeed;
            int dy = (int)(Math.sin(angle) * 3) * bulletSpeed;
            bullets.add(new Bullet(x + Player.getSize()/2, y + Player.getSize()/2, dx, dy, null));
        }
        return bullets;
    }

    public void move(boolean upPressed,boolean downPressed, boolean leftPressed, boolean rightPressed,int[][] grid, int tileSize){       
        if (upPressed || downPressed || leftPressed || rightPressed){
            this.idling=false; // cek player kalau jalan berati tidak idle
            int dx = 0, dy = 0;
            if (upPressed) {
                dy -= speed;
                this.direction="up";
            }
            if (downPressed){
                dy += speed;
                this.direction="down";
            } 
            if (leftPressed){
                dx -= speed;
                this.direction="left";
            } 
            if (rightPressed){
                dx += speed;
                this.direction="right";
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
    }    public void updateDash() {
        long currentTime = System.currentTimeMillis();

        // End dash after the duration
        if (isDashing && currentTime - dashStartTime >= dashDuration) {
            isDashing = false;
        }

        // End invincibility after the duration (only for dash-based invincibility)
        // Regular invincibility from taking damage is handled in isInvincible()
        if (isInvincible && isDashing && currentTime - dashStartTime >= invincibilityDuration) {
            isInvincible = false;
        }

        // Recharge dash charges
        if (currentDashCharges < maxDashCharges && currentTime - lastChargeTime >= dashChargeCooldown) {
            currentDashCharges++;
            lastChargeTime = currentTime; // Reset recharge timer
        }
    }
    public void dash() {
        long currentTime = System.currentTimeMillis();
        if (currentDashCharges > 0 && !isDashing) {
            isDashing = true;
            isInvincible = true; // Set invincibility when dashing
            dashStartTime = currentTime;
            currentDashCharges--; // Consume one dash charge
        }
    }    public void takeDamage(int damage) {
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
        isInvincible = true;
        invincibilityStartTime = System.currentTimeMillis();
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
    public Weapon getCurrentWeapon() { return weapons.get(currentWeaponIndex); }
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
    }    public boolean isInvincible() {
        // Check if invincibility has expired
        if (isInvincible && System.currentTimeMillis() - invincibilityStartTime > invincibilityDuration) {
            isInvincible = false;
        }
        return isInvincible; // Return the updated invincibility status
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
}