package nl.sniffiandros.bren.common.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EndermanEntity;
import nl.sniffiandros.bren.common.entity.BulletEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to make all Enderman dodge custom BulletEntity projectiles globally.
 **/
@Mixin(EndermanEntity.class)
public class EndermanEntityMixin {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void bren$onCustomProjectileDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity projectile = source.getSource();
        if (projectile instanceof BulletEntity) {
            EndermanEntity enderman = (EndermanEntity) (Object) this;
            boolean teleported = false;
            for (int i = 0; i < 64 && !teleported; ++i) {
                teleported = ((EndermanEntityAccessor) enderman).bren$teleportRandomly();
            }
            cir.setReturnValue(false); // Cancel the damage, as Endermen normally do
        }
    }
}