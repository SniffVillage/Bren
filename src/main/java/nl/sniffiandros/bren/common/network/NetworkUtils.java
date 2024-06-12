package nl.sniffiandros.bren.common.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import nl.sniffiandros.bren.common.registry.NetworkReg;

public class NetworkUtils {

    public static void sendShotEffect(PlayerEntity player, Vec3d origin, Vec3d direction, boolean ejectCasing) {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeFloat((float) origin.x);
        buf.writeFloat((float) origin.y);
        buf.writeFloat((float) origin.z);

        buf.writeFloat((float) direction.x);
        buf.writeFloat((float) direction.y);
        buf.writeFloat((float) direction.z);

        buf.writeBoolean(ejectCasing);

        sendDataToClient(player, NetworkReg.SHOOT_PACKET_ID, buf);
    }

    public static void sendDataToClient(PlayerEntity player, Identifier packet, PacketByteBuf buf) {
        MinecraftServer server = player.getServer();
        if (server != null) {
            PlayerManager manager = server.getPlayerManager();
            if (manager != null) {

                ServerPlayerEntity serverPlayer = manager.getPlayer(player.getUuid());
                if (serverPlayer != null) {
                    ServerPlayNetworking.send(serverPlayer, packet, buf);
                }
            }
        }
    }
}
