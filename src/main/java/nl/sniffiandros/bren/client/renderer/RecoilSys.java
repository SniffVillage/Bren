package nl.sniffiandros.bren.client.renderer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import nl.sniffiandros.bren.common.events.MEvents;
import nl.sniffiandros.bren.common.registry.custom.GunItem;

public class RecoilSys {

    private static float cameraRecoil = 0;
    private static float sideRecoil = 0;
    private static float recoil = 0;
    private static float cameraRecoilProgress = 0;

    public static void shotEvent(PlayerEntity player, float cam_recoil) {
        cameraRecoil = cam_recoil;
        sideRecoil = (player.getRandom().nextFloat() - .5F) / 2;
        cameraRecoilProgress = 1;
        recoil = 0;
    }

    public static void tick(MinecraftClient client) {
       // MinecraftClient client = MinecraftClient.getInstance();

        PlayerEntity player = client.player;

        if (player == null) { return;}

        float pitch = player.getPitch();
        float yaw = player.getYaw();

        cameraRecoilProgress = Math.max(cameraRecoilProgress - .15F, 0.0F);

        recoil = MathHelper.lerp(cameraRecoilProgress, recoil * client.getTickDelta(), cameraRecoil) * .3F;

        player.setPitch(pitch - (Float.isNaN(recoil) ? .0F : recoil));
        player.setYaw(yaw - (Float.isNaN(recoil * sideRecoil) ? .0F : recoil * sideRecoil));
        player.prevPitch = pitch;
    }
}
