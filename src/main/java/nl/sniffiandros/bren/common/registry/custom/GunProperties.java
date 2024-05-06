package nl.sniffiandros.bren.common.registry.custom;

import net.minecraft.sound.SoundEvent;

public class GunProperties {

    public int recoil;
    float rangeDamage;
    int fireRate;
    SoundEvent sound;
    MagazineItem magazineItem;
    SoundEvent silentSound;

    public GunProperties() {}

    public GunProperties rangeDamage(float damage) {
        this.rangeDamage = damage;
        return this;
    }
    public GunProperties fireRate(int rate) {
        this.fireRate = rate;
        return this;
    }
    public GunProperties recoil(int recoil) {
        this.recoil = recoil;
        return this;
    }
    public GunProperties shootSound(SoundEvent sound, SoundEvent silent) {
        this.sound = sound;
        this.silentSound = silent;
        return this;
    }
    public GunProperties magazine(MagazineItem magazineItem) {
        this.magazineItem = magazineItem;
        return this;
    }
}
