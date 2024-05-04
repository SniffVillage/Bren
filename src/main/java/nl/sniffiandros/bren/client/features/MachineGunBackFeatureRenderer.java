package nl.sniffiandros.bren.client.features;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import nl.sniffiandros.bren.common.entity.IGunUser;

public class MachineGunBackFeatureRenderer <T extends LivingEntity, M extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {

    private final ItemRenderer itemRenderer;

    public MachineGunBackFeatureRenderer(FeatureRendererContext<T, M> context, ItemRenderer itemRenderer) {
        super(context);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity instanceof IGunUser machineGunUser) {
            ItemStack machineGun = machineGunUser.getLastGun();
            if (!machineGun.isEmpty()) {
                this.renderItem(entity, machineGun, ModelTransformationMode.NONE, matrices, vertexConsumers, light);
            }
        }
    }

    protected void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode transformationMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (stack == null) return;

        if (!stack.isEmpty()) {

            matrices.push();

            ModelPart modelPart = this.getContextModel().body;
            modelPart.rotate(matrices);

            matrices.translate(0.1F,0.1F,0.25F);
            matrices.scale(1.65F,1.65F,1.0F);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(60 + 180));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-180));
            BakedModel bakedModel = this.itemRenderer.getModel(stack,entity.getWorld(),entity,entity.getId() + transformationMode.ordinal());
            this.itemRenderer.renderItem(stack,transformationMode,false,matrices,vertexConsumers,light, OverlayTexture.DEFAULT_UV,bakedModel);
            matrices.pop();
        }
    }
}
