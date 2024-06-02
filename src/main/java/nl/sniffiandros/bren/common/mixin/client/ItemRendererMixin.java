package nl.sniffiandros.bren.common.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import nl.sniffiandros.bren.client.renderer.WeaponTickHolder;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.entity.IGunUser;
import nl.sniffiandros.bren.common.registry.custom.GunItem;
import nl.sniffiandros.bren.common.registry.custom.GunWithMagItem;
import nl.sniffiandros.bren.common.utils.GunHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    private static final Identifier CONTENT_TEXTURE = new Identifier(Bren.MODID, "test");

    @Shadow @Final private ItemModels models;

    @ModifyVariable(at = @At("HEAD"), method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", argsOnly = true)
    private BakedModel editGuiModel(BakedModel defaultModel, ItemStack stack, ModelTransformationMode renderMode) {

        if (renderMode == ModelTransformationMode.GUI || renderMode == ModelTransformationMode.FIXED || renderMode == ModelTransformationMode.GROUND) {
            if (stack.getItem() instanceof GunItem) {
                return bakeGuiModel(stack);
            }

        }

        return defaultModel;
    }

    private BakedModel bakeGuiModel(ItemStack stack) {
        Identifier identifier = Registries.ITEM.getId(stack.getItem());
        String itemName = identifier.getPath();
        String formattedName = itemName.toLowerCase().replace(' ', '_');
        BakedModel bakedModel;
        if (!GunWithMagItem.hasMagazine(stack)) {
            bakedModel = this.models.getModelManager().getModel(new ModelIdentifier(Bren.MODID, formattedName + "_gui", "inventory"));
        } else {
            bakedModel = !GunWithMagItem.hasColorableMagazine(stack) ?
                    this.models.getModelManager().getModel(new ModelIdentifier(Bren.MODID, formattedName + "_with_magazine_gui", "inventory")) :
                    this.models.getModelManager().getModel(new ModelIdentifier(Bren.MODID, formattedName + "_with_clothed_magazine_gui", "inventory"));

        }
        return bakedModel;
    }


    @Inject(at = @At("HEAD"), method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V")
    private void render(LivingEntity entity, ItemStack item, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int light, int overlay, int seed, CallbackInfo ci) {
        if (entity != null) {

            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            float delta = minecraftClient.getTickDelta();

            if (item.getItem() instanceof GunItem) {

                if (entity instanceof IGunUser gunUser) {

                    float f1 = 0;

                    if (entity instanceof PlayerEntity player) {
                        ItemCooldownManager cooldownManager = player.getItemCooldownManager();
                        f1 = cooldownManager.getCooldownProgress(item.getItem(), delta);
                        f1 = Math.max(f1 - 0.15F, 0);
                    }


                    float f = 1 - WeaponTickHolder.getAnimationTicks(delta)/16;
                    boolean reloading = gunUser.getGunState().equals(GunHelper.GunStates.RELOADING);

                    float kick = !reloading ? Math.max(GunItem.rangeDamage(item), 6) / 6 : 1;

                    if (renderMode.isFirstPerson()) {

                        float sin = (float) Math.sin((f * 2 - 0.5) * Math.PI) * 0.5F + 0.5F;
                        float sin2 = (float) Math.sin((f1 * 2 - 0.5) * Math.PI) * 0.5F + 0.5F;
                        float sin3 = reloading ? sin2 : (float) Math.sin(1 - f);

                        double d = (Math.sin(((float) entity.age + delta) / 2) * (reloading ? sin2 : f1)) * 30;

                        matrices.translate(0, 0, reloading ? 0 : sin / 2 + f1 / 4);
                        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) (leftHanded ? -15 + d : 15 + d)));
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((sin3 * 10) * kick));

                    } else {
                        float z = Math.max((1 - f + f1) / 2, 0);
                        float f2 = reloading ? ((float) Math.sin((f1 * 2 - 0.5) * Math.PI) * 0.5F + 0.5F) / 3 : z;
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(leftHanded ? 10 : -10));
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f2 * 30 + 45));

                        matrices.translate(0, -f2 / 4 + 0.25F, f2 / 8 - 0.25F);
                    }
                }
            }
        }
    }
}
