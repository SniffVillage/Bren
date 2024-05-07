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


    public static void shootAnimationPacket() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkReg.SHOOT_ANIMATION_PACKET_ID, (client, handler, buf, responseSender) -> {

            client.execute(() -> {
                WeaponTickHolder.setTicks(16);
            });
        });
    }

    public static void recoilPacket() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkReg.RECOIL_CLIENT_PACKET_ID, (client, handler, buf, responseSender) -> {
            float recoil = buf.readFloat();

            client.execute(() -> {
                if (client.player ==  null) {return;}
                RecoilSys.shotEvent(client.player, recoil);
            });
        });
    }

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

    public static void clientShootPacket() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkReg.SHOOT_CLIENT_PACKET_ID, (client, handler, buf, responseSender) -> {
            float volume = buf.readFloat();

            client.execute(() -> {



                World world = client.world;
                if (world != null) {

                    SoundInstance soundInstance = PositionedSoundInstance.master(SoundReg.ITEM_MACHINE_GUN_DISTANT_SHOOT, 1.0F - (world.getRandom().nextFloat() - 0.5F)/8, volume);
                    client.getSoundManager().play(soundInstance);
                }
            });
        });
    }

    public static void shootPacket() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkReg.SHOOT_PACKET_ID, (client, handler, buf, responseSender) -> {
            float originX = buf.readFloat();
            float originY = buf.readFloat();
            float originZ = buf.readFloat();

            float directionX = buf.readFloat();
            float directionY = buf.readFloat();
            float directionZ = buf.readFloat();

            client.execute(() -> {
                Vec3d origin = new Vec3d(originX, originY, originZ);
                Vec3d direction = new Vec3d(directionX, directionY, directionZ);

                World world = client.world;
                if (world != null) {
                    GunItem.shotParticles(client.world, origin, direction, world.getRandom());
                    if (MConfig.spawnCasingParticles.get()) {
                        GunItem.ejectCasingParticle(client.world, origin, direction, world.getRandom());
                    }
                }
            });
        });
    }
}
