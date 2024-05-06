package nl.sniffiandros.bren.common.entity;

import net.minecraft.item.ItemStack;
import nl.sniffiandros.bren.common.utils.GunHelper;

import java.util.function.Predicate;

public interface IGunUser {

    void setShooting(boolean b);

    boolean isShooting();

    boolean canShoot(Predicate<ItemStack> predicate);

    void setGunTicks(int t);

    int shootingDuration();

    int getGunTicks();

    void setCanReload(boolean b);

    boolean canReload();

    ItemStack getLastGun();

    GunHelper.GunStates getGunState();

    void setGunState(GunHelper.GunStates state);
}
