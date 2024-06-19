package nl.sniffiandros.bren.common.registry;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.entity.IGunUser;
import nl.sniffiandros.bren.common.mixin.PlayerEntityMixin;
import nl.sniffiandros.bren.common.registry.custom.types.GunItem;
import nl.sniffiandros.bren.common.registry.custom.MagazineItem;
import nl.sniffiandros.bren.common.utils.GunUtils;

public class NetworkReg {
    public static final Identifier SHOOT_PACKET_ID = new Identifier(Bren.MODID, "shoot");
    public static final Identifier SHOOT_CLIENT_PACKET_ID = new Identifier(Bren.MODID, "shoot_client");
    public static final Identifier RECOIL_CLIENT_PACKET_ID = new Identifier(Bren.MODID, "recoil_client");
    public static final Identifier SHOOT_ANIMATION_PACKET_ID = new Identifier(Bren.MODID, "shoot_animation");
    public static final Identifier RELOAD_PACKET_ID = new Identifier(Bren.MODID, "reload");

    public static void reloadPacket() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkReg.RELOAD_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                ItemStack stack = player.getMainHandStack();

                if (stack.getItem() instanceof GunItem gunItem) {
                    gunItem.onReload(player);
                    ((IGunUser) player).setReloadingGun(stack);
                } else if (stack.getItem() instanceof MagazineItem) {
                    GunUtils.fillMagazine(stack, player);
                }
            });
        });
    }
}
