package nl.sniffiandros.bren.common.registry;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nl.sniffiandros.bren.client.renderer.RecoilSys;
import nl.sniffiandros.bren.client.renderer.WeaponTickHolder;
import nl.sniffiandros.bren.common.config.MConfig;
import nl.sniffiandros.bren.common.registry.NetworkReg;
import nl.sniffiandros.bren.common.registry.SoundReg;
import nl.sniffiandros.bren.common.registry.custom.GunItem;

public class ClientNetworkReg {
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

    public static void clientShootPacket() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkReg.SHOOT_CLIENT_PACKET_ID, (client, handler, buf, responseSender) -> {
            float volume = buf.readFloat();

            client.execute(() -> {

                World world = client.world;
                if (world != null) {

                    SoundInstance soundInstance = PositionedSoundInstance.master(SoundReg.ITEM_DISTANT_GUNFIRE, 1.0F - (world.getRandom().nextFloat() - 0.5F)/8, volume);
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

            boolean casing = buf.readBoolean();

            client.execute(() -> {
                Vec3d origin = new Vec3d(originX, originY, originZ);
                Vec3d direction = new Vec3d(directionX, directionY, directionZ);

                World world = client.world;
                if (world != null) {
                    GunItem.shotParticles(client.world, origin, direction, world.getRandom());
                    if (MConfig.spawnCasingParticles.get() && casing) {
                        GunItem.ejectCasingParticle(client.world, origin, direction, world.getRandom());
                    }
                }
            });
        });
    }
}
