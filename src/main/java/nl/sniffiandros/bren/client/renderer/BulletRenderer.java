package nl.sniffiandros.bren.client.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import nl.sniffiandros.bren.client.ClientBren;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.entity.BulletEntity;

public class BulletRenderer<T extends BulletEntity> extends EntityRenderer<T> {

    public static final Identifier TEXTURE = new Identifier(Bren.MODID, "textures/entity/bullet.png");

    public BulletRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        if (entity.age > 2) {
            ClientBren.renderImage(
                    this.getTexture(entity),
                    entity,
                    matrices,
                    this.dispatcher);
        }

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(BulletEntity entity) {
        return TEXTURE;
    }
}
