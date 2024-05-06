package nl.sniffiandros.bren.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.registry.custom.GunItem;
import nl.sniffiandros.bren.common.registry.custom.GunWithMagItem;

public class HudOverlay implements HudRenderCallback {
    private static final Identifier BULLET_ICONS = new Identifier(Bren.MODID,
            "textures/gui/bullet_icons.png");




    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        PlayerEntity player = null;

        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();

        int x = width / 2;
        int y = height/ 2;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            player = client.player;
        }

        if (player == null) {
            return;
        }

        ItemStack gun = player.getMainHandStack();
        int i;
        int u;
        int max;
        if (gun.getItem() instanceof GunItem gunItem) {
            i = gunItem.getContents(gun);
            u = gunItem.bulletAmount() > 1 ? 12 : 0;
            max = gunItem.getMaxCapacity(gun);

            if (gunItem instanceof GunWithMagItem) {
                if (!GunWithMagItem.hasMagazine(gun)) {
                    return;
                }
            }

        } else {
            return;
        }




        client.getProfiler().push("machine_gun_bullets");

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        int rows = 2;

        for (int n = 0; n < max; ++n) {

            int ri = rows * 10;

            int row = (int) Math.floor(n/ri);

            int y1 = n * 6 - row*ri*6;
            int x1 = 15*row + 15;

            int u1 = n < i ? 0 : 24;

            addBulletIcon(context,x1, y1, u + u1, 0);
        }

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        client.getProfiler().pop();
    }

    public void addBulletIcon(DrawContext context, int x, int y,int u, int v) {
        context.drawTexture(BULLET_ICONS, x, y, u, v, 12, 12, 48, 12);
    }
}
