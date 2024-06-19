package nl.sniffiandros.bren.client.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class RecoilSys {

    private static float cameraRecoil = 0;
    private static float sideRecoil = 0;
    private static float recoil = 0;
    private static int cameraRecoilProgress = 0;
    private static int lastCameraRecoilProgress = 0;

    public static void shotEvent(PlayerEntity player, float cam_recoil) {
        cameraRecoil = cam_recoil;
        sideRecoil = (player.getRandom().nextFloat() - .5F) / 2;
        cameraRecoilProgress = 2;
        recoil = 0;
    }

    public static void render(MinecraftClient client) {
        PlayerEntity player = client.player;

        if (player == null) { return;}

        float progress = MathHelper.lerp(client.getTickDelta(), lastCameraRecoilProgress, cameraRecoilProgress);

        float pitch = player.getPitch();
        float yaw = player.getYaw();

        recoil = progress * cameraRecoil * client.getLastFrameDuration();

        player.setPitch(pitch - (Float.isNaN(recoil) ? .0F : recoil));
        player.setYaw(yaw - (Float.isNaN(recoil * sideRecoil) ? .0F : recoil * sideRecoil));
        player.prevPitch = pitch;
    }

    public static void tick(MinecraftClient client) {
        lastCameraRecoilProgress = cameraRecoilProgress;
        cameraRecoilProgress = Math.max(0, --cameraRecoilProgress);
    }
}
