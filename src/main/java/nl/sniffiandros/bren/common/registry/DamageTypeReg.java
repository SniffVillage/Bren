package nl.sniffiandros.bren.common.registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import nl.sniffiandros.bren.common.Bren;
import org.jetbrains.annotations.Nullable;

public class DamageTypeReg {
    public static final RegistryKey<DamageType> BULLET_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Bren.MODID, "bullet_type"));

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }

    public static DamageSource shot(World world, @Nullable Entity source,@Nullable Entity attacker) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(BULLET_TYPE), source, attacker);
    }
}
