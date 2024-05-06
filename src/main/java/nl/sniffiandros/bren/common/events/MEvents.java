package nl.sniffiandros.bren.common.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class MEvents {

    /**
     * An event that is called when a gun is fired.
     *
     * This is fired from {@link nl.sniffiandros.bren.common.mixin.PlayerEntityMixin#handleShooting)}
     *
     */
    public static final Event<MEvents.GunFired> GUN_FIRED_EVENT = EventFactory.createArrayBacked(MEvents.GunFired.class, callbacks -> (player, stack) -> {
        for (MEvents.GunFired callback : callbacks) {
            callback.gunFired(player, stack);
        }
    });

    @FunctionalInterface
    public interface GunFired {
        /**
         * Called when a player shoots a gun.
         *
         * @param player the player that fired the gun
         * @param stack the gun item stack
         */
        void gunFired(PlayerEntity player, ItemStack stack);
    }
}
