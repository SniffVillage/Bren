package nl.sniffiandros.bren.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.DyeableItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import nl.sniffiandros.bren.client.features.GunBackFeatureRenderer;
import nl.sniffiandros.bren.client.renderer.WeaponTickHolder;
import nl.sniffiandros.bren.client.renderer.BulletRenderer;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.config.MConfig;
import nl.sniffiandros.bren.common.entity.BulletEntity;
import nl.sniffiandros.bren.client.particle.AirRingParticle;
import nl.sniffiandros.bren.client.particle.MuzzleSmokeParticle;
import nl.sniffiandros.bren.client.particle.CasingParticle;
import nl.sniffiandros.bren.common.registry.*;
import nl.sniffiandros.bren.common.registry.custom.GunWithMagItem;
import nl.sniffiandros.bren.common.utils.ModModelPredicateProvider;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

import static nl.sniffiandros.bren.common.Bren.MODID;

public class ClientBren implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        var particleRegistry = ParticleFactoryRegistry.getInstance();

        particleRegistry.register(ParticleReg.MUZZLE_SMOKE_PARTICLE, MuzzleSmokeParticle.Factory::new);
        particleRegistry.register(ParticleReg.AIR_RING_PARTICLE, AirRingParticle.Factory::new);
        particleRegistry.register(ParticleReg.CASING_PARTICLE, CasingParticle.Factory::new);

        BlockRenderLayerMap.INSTANCE.putBlock(BlockReg.WORKBENCH, RenderLayer.getCutout());


        ClientNetworkReg.shootPacket();
        ClientNetworkReg.clientShootPacket();
        ClientNetworkReg.shootAnimationPacket();
        ClientNetworkReg.recoilPacket();
        EntityRendererRegistry.register(Bren.BULLET, BulletRenderer::new);
        KeyBindingReg.reg();
        ModModelPredicateProvider.regModels();

        if (MConfig.showAmmoGui.get()) {
            HudRenderCallback.EVENT.register(new HudOverlay());
        }

        List<ModelIdentifier> modelIdentifierList = new ArrayList<>();
        registerGUIModels(Registries.ITEM.getId(ItemReg.MACHINE_GUN), modelIdentifierList, true, true);
        registerGUIModels(Registries.ITEM.getId(ItemReg.AUTO_GUN), modelIdentifierList, true, true);
        registerGUIModels(Registries.ITEM.getId(ItemReg.RIFLE), modelIdentifierList, false, true);
        registerGUIModels(Registries.ITEM.getId(ItemReg.SHOTGUN), modelIdentifierList, false, false);
        registerGUIModels(Registries.ITEM.getId(ItemReg.NETHERITE_MACHINE_GUN), modelIdentifierList, true, true);
        registerGUIModels(Registries.ITEM.getId(ItemReg.NETHERITE_AUTO_GUN), modelIdentifierList, true, true);
        registerGUIModels(Registries.ITEM.getId(ItemReg.NETHERITE_RIFLE), modelIdentifierList, false, true);
        registerGUIModels(Registries.ITEM.getId(ItemReg.NETHERITE_SHOTGUN), modelIdentifierList, false, false);

        ModelLoadingPlugin.register(
                (manager) -> manager.addModels(
                        modelIdentifierList)
        );

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : ((DyeableItem)(stack.getItem())).getColor(stack),
                ItemReg.CLOTHED_MAGAZINE);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : ((GunWithMagItem)(stack.getItem())).getColor(stack),
                ItemReg.MACHINE_GUN, ItemReg.AUTO_GUN, ItemReg.NETHERITE_MACHINE_GUN, ItemReg.NETHERITE_AUTO_GUN);

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((t, r, e, c) -> {
            if (r instanceof PlayerEntityRenderer && MConfig.renderGunOnBack.get()) {
                e.register(new GunBackFeatureRenderer(r, c.getItemRenderer()));
            }});

        ClientTickEvents.END_CLIENT_TICK.register(WeaponTickHolder::tick);
        ClientTickEvents.END_CLIENT_TICK.register(WeaponTickHolder::tick);
    }

    public static List<ModelIdentifier> registerGUIModels(Identifier id, List<ModelIdentifier> modelIdentifierList, boolean clothed, boolean hasMagazine) {
        modelIdentifierList.add(new ModelIdentifier(MODID, id.getPath() + "_" + "gui", "inventory"));
        if (hasMagazine) {
            modelIdentifierList.add(new ModelIdentifier(MODID, id.getPath() + "_with_magazine_" + "gui", "inventory"));
        }
        if (clothed) {
            modelIdentifierList.add(new ModelIdentifier(MODID, id.getPath() + "_with_clothed_magazine_" + "gui", "inventory"));
        }
        return modelIdentifierList;
    }

    // Copied from my wild west mod

    public static void renderImage(Identifier icon, BulletEntity bullet, MatrixStack matrices, EntityRenderDispatcher dispatcher) {

        double d = dispatcher.getSquaredDistanceToCamera(bullet);
        if (d > 4096.0) {
            return;
        }

        matrices.push();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        float scale = 0.040F;

        matrices.translate(0, bullet.getBoundingBox().getYLength()/2, 0);
        matrices.multiply(dispatcher.getRotation());
        matrices.scale(-scale, -scale, scale);

        renderImage(icon, matrices, -8, -8, 16, 16, 0, 1, 0, 1, 1);

        matrices.pop();
    }


    public static void renderImage(Identifier icon, MatrixStack matrixStack, int x, int y, int w, int h, float u0, float u1, float v0, float v1, float alpha) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        RenderSystem.enableDepthTest();
        MinecraftClient.getInstance().getTextureManager().getTexture(icon).setFilter(false, false);
        RenderSystem.setShaderTexture(0, icon);

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferbuilder.vertex(matrix, (float)x,			(float)(y + h),	0).texture(u0, v1).color(1.0f, 1.0f, 1.0f, alpha).next();
        bufferbuilder.vertex(matrix, (float)(x + w),	(float)(y + h),	0).texture(u1, v1).color(1.0f, 1.0f, 1.0f, alpha).next();
        bufferbuilder.vertex(matrix, (float)(x + w),	(float)y,		0).texture(u1, v0).color(1.0f, 1.0f, 1.0f, alpha).next();
        bufferbuilder.vertex(matrix, (float)x,			(float)y,		0).texture(u0, v0).color(1.0f, 1.0f, 1.0f, alpha).next();
        BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
        RenderSystem.disableDepthTest();
    }
}
