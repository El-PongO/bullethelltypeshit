package players;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


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
    protected long invincibilityDuration = 200; // Invincibility duration (same as dash duration)
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
    public BufferedImage changewp;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        getPlayerImage();
        direction="down";
        idling=true;
    }

    // Make this method abstract so subclasses must implement their own image loading
    public abstract void getPlayerImage();

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
        }

        int drawX = px, drawY = py, drawW = size * zoom, drawH = size * zoom;
        
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
        
        // Draw relative to camera position
        g.drawImage(bimage, px, py, size*zoom, size*zoom, null);
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;//gae movement e player receiver wasd ne
    }
    
    public Bullet shoot(int targetX, int targetY) {

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
        weapon.useAmmo();
        weapon.recordShot();

        double angle = Math.atan2(targetY - (y + Player.getSize()/2), 
                                targetX - (x + Player.getSize()/2));
        int dx = (int)(Math.cos(angle) * 3) * bulletSpeed;
        int dy = (int)(Math.sin(angle) * 3) * bulletSpeed;
        return new Bullet(x + Player.getSize()/2, y + Player.getSize()/2, dx, dy, weapon.getBulletSprite());
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
    }

    public void updateDash() {
        long currentTime = System.currentTimeMillis();

        // End dash after the duration
        if (isDashing && currentTime - dashStartTime >= dashDuration) {
            isDashing = false;
        }

        // End invincibility after the duration
        if (isInvincible && currentTime - dashStartTime >= invincibilityDuration) {
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
    }

    public void takeDamage(int damage) {
        if (!isInvincible) { // Only take damage if not invincible
            health -= damage;
            if (health < 0) {
                health = 0; // Prevent health from going negative
            }
        }
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
    }

    public boolean isInvincible() {
        return isInvincible; // Return the invincibility status
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