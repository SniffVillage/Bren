package nl.sniffiandros.bren.common.registry;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nl.sniffiandros.bren.client.renderer.RecoilSys;
import nl.sniffiandros.bren.client.renderer.WeaponTickHolder;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.config.MConfig;
import nl.sniffiandros.bren.common.entity.IGunUser;
import nl.sniffiandros.bren.common.registry.custom.GunItem;
import nl.sniffiandros.bren.common.registry.custom.GunWithMagItem;
import nl.sniffiandros.bren.common.registry.custom.MagazineItem;
import nl.sniffiandros.bren.common.utils.GunHelper;
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
                } else if (stack.getItem() instanceof MagazineItem) {
                    GunUtils.fillMagazine(stack, player);
                }
            });
        });
    }
}
