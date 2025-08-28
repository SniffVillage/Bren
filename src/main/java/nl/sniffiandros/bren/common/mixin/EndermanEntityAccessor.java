package nl.sniffiandros.bren.common.mixin;

import net.minecraft.entity.mob.EndermanEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Accessor mixin to call the protected teleportRandomly() method.
 **/
@Mixin(EndermanEntity.class)
public interface EndermanEntityAccessor {
    @Invoker("teleportRandomly")
    boolean bren$teleportRandomly();
}