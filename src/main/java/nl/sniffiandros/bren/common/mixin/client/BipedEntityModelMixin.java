package nl.sniffiandros.bren.common.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import nl.sniffiandros.bren.client.GunEntityModelAnimator;
import nl.sniffiandros.bren.common.entity.IGunUser;
import nl.sniffiandros.bren.common.registry.custom.GunItem;
import nl.sniffiandros.bren.common.utils.GunHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead {

    @Shadow public abstract ModelPart getHead();

    @Shadow @Final public ModelPart leftArm;

    @Shadow @Final public ModelPart rightArm;

    @Shadow protected abstract void animateArms(T entity, float animationProgress);

    @Shadow @Final public ModelPart hat;

    @Shadow @Final public ModelPart body;


    @Inject(at = @At("TAIL"), method = "setAngles*")
    private void angles(T livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        float delta = minecraftClient.getTickDelta();

        ItemStack s = livingEntity.getMainHandStack();
        if (s != null) {
            if (s.getItem() instanceof GunItem && livingEntity instanceof PlayerEntity player) {

                ItemCooldownManager cooldownManager = player.getItemCooldownManager();

                ItemStack mainHandItem = player.getMainHandStack();

                float c = cooldownManager.getCooldownProgress(mainHandItem.getItem(),delta);

                GunEntityModelAnimator.angles(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch,
                        this.leftArm, this.rightArm, this.getHead(), c);

                this.hat.copyTransform(this.getHead());
                this.animateArms(livingEntity, ageInTicks);
            }
        }
    }
}
