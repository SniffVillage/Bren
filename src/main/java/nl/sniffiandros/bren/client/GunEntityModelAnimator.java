package nl.sniffiandros.bren.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import nl.sniffiandros.bren.common.entity.IGunUser;
import nl.sniffiandros.bren.common.registry.custom.GunItem;
import nl.sniffiandros.bren.common.utils.GunHelper;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class GunEntityModelAnimator {
    public static void oneArm(LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch,
                              ModelPart leftArm, ModelPart rightArm, ModelPart head, float gunAmount) {
        if (livingEntity instanceof IGunUser gunUser && !livingEntity.isSleeping()) {
            boolean isLeftHanded = livingEntity.getMainArm().equals(Arm.LEFT);

            float h_pi = 1.570796F;
            float p = headPitch * 0.01745329F;
            float y = netHeadYaw * 0.01745329F;

            ModelPart arm = isLeftHanded ? leftArm : rightArm;

            arm.yaw = y;
            arm.pitch = p - h_pi;
        }
    }

    public static void revolver(LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch,
                              ModelPart leftArm, ModelPart rightArm, ModelPart head, float gunAmount) {
        if (livingEntity instanceof IGunUser gunUser && !livingEntity.isSleeping()) {
            boolean reloading = gunUser.getGunState().equals(GunHelper.GunStates.RELOADING);

            boolean isLeftHanded = livingEntity.getMainArm().equals(Arm.LEFT);
            ModelPart arm = isLeftHanded ? leftArm : rightArm;

            float rotX = 0;
            float rotY = 0;
            float f = 0;
            float f1 = 1.570796F;

            if (livingEntity instanceof PlayerEntity player) {
                MinecraftClient client = MinecraftClient.getInstance();

                f = player.getItemCooldownManager().getCooldownProgress(player.getMainHandStack().getItem(), client.getTickDelta());
            }

            float sin = reloading ? (float) Math.sin((f*2 - 0.5)*Math.PI) * 0.5F + 0.5F : 0;

            rotY = (float) (Math.cos(f*15)*0.08726646);
            rotX = (float) (Math.sin(f*15)*0.08726646) - sin;

            float p = headPitch * 0.01745329F;
            float y = netHeadYaw * 0.01745329F;

            arm.pitch = p - f1 + rotX;
            arm.yaw = y + rotY;
        }
    }

    public static void angles(LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch,
    ModelPart leftArm, ModelPart rightArm, ModelPart head, float gunAmount) {

        if (livingEntity instanceof IGunUser gunUser && !livingEntity.isSleeping()) {

            boolean reloading = gunUser.getGunState().equals(GunHelper.GunStates.RELOADING);
            float kick = 2.5F;

            gunAmount = Math.max(gunAmount - 0.15F, 0);
            float f = (((float)gunUser.getGunTicks()/16) + gunAmount)/2;
            float f1 = (float) (Math.sin(f)/Math.PI) * (kick/2);


            boolean isLeftHanded = livingEntity.getMainArm().equals(Arm.LEFT);
            ModelPart arm = isLeftHanded ? leftArm : rightArm;
            ModelPart other_arm = !isLeftHanded ? leftArm : rightArm;

            float l = isLeftHanded ? -1 : 1;

            float p = headPitch * 0.01745329F;
            float y = netHeadYaw * 0.01745329F;

            float f2 = f1*kick/2;

            float fr = ((float) Math.sin((gunAmount * 2 - 0.5) * Math.PI) * 0.5F + 0.5F);
            float f3 = reloading ? fr/4 : f2 ;
            float f4 = reloading ? (isLeftHanded ? -fr/4 : fr/4) : f2 * l;

            arm.yaw = isLeftHanded ? y + 0.7853982F : y - 0.7853982F;
            arm.pitch = 0.2181662F + p + f3/2;
            arm.roll += f4;

            other_arm.pitch = -0.6981317F + p/3 - f3/2 - (reloading ? fr:0);
            other_arm.yaw = (isLeftHanded ? -1.090831F - y : 1.090831F + y) + (p/2) * l + f3/3;

            head.yaw = y - 0.7853982F * l;

        }
    }
}
