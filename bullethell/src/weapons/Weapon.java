package weapons;
import java.awt.image.BufferedImage;
import java.util.List;

public abstract class Weapon {
    private String name;
    private BufferedImage sprite;
    private BufferedImage bulletSprite;
    private int maxAmmo;
    private int currentAmmo;
    private int reloadDelayMs; // in milliseconds
    private boolean reloading = false;
    private long reloadStartTime = 0;
    private int fireRateMs; // Minimum ms between shots
    private long lastShotTime = 0;
    private boolean fullAuto; // true = hold to fire, false = semi-auto
    private int damage; // Damage dealt by the weapon

    public Weapon(String name, BufferedImage sprite, BufferedImage bulletSprite, int maxAmmo, int reloadDelayMs, int fireRateMs, boolean fullAuto, int damage) {
        this.name = name;
        this.sprite = sprite;
        this.bulletSprite = bulletSprite;
        this.maxAmmo = maxAmmo;
        this.currentAmmo = maxAmmo;
        this.reloadDelayMs = reloadDelayMs;
        this.fireRateMs = fireRateMs;
        this.fullAuto = fullAuto;
        this.damage = damage;
    }

    public String getName() {
        return name;
    }

    public BufferedImage getSprite() {
        return sprite;
    }

    public BufferedImage getBulletSprite() {
        return bulletSprite;
    }

    public boolean canFire() {
        return !reloading && hasAmmo() && (System.currentTimeMillis() - lastShotTime >= fireRateMs);
    }

    public void recordShot() {
        lastShotTime = System.currentTimeMillis();
    }

    public boolean isFullAuto() {
        return fullAuto;
    }

    public int getWeaponDamage() {
        return damage;
    }
    
    public int getMaxAmmo() { return maxAmmo; }
    public int getCurrentAmmo() { return currentAmmo; }
    public boolean isReloading() { return reloading; }
    public int getReloadDelayMs() { return reloadDelayMs; }
    public boolean hasAmmo() { return currentAmmo > 0; }
    public void useAmmo() {
        if (currentAmmo > 0) currentAmmo--;
    }
    public void reload() {
        currentAmmo = maxAmmo;
    }

    public void startReload() {
        if (!reloading && currentAmmo < maxAmmo) {
            reloading = true;
            reloadStartTime = System.currentTimeMillis();
        }
    }

    public void updateReload() {
        if (reloading && System.currentTimeMillis() - reloadStartTime >= reloadDelayMs) {
            currentAmmo = maxAmmo;
            reloading = false;
        }
    }

    public abstract List<Bullet> fire(int x, int y, int targetX, int targetY, int playerSize);

    public void setCurrentAmmo(int currentAmmo) {
        this.currentAmmo = currentAmmo;
    }
}
