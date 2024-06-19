package nl.sniffiandros.bren.common.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.registry.custom.types.GunItem;
import nl.sniffiandros.bren.common.registry.custom.types.GunWithMagItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value= EnvType.CLIENT)
@Mixin(DrawContext.class)
public abstract class DrawContextMixin {

    @Shadow public abstract void fill(RenderLayer layer, int x1, int y1, int x2, int y2, int color);

    @Shadow @Final private MatrixStack matrices;
    //private static final Identifier CONTENT_TEXTURE = new Identifier(Bren.MODID, "test");

    @Inject(at = @At("HEAD"), method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
    private void editGuiItem(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {
        if (!stack.isEmpty()) {
            this.matrices.push();
            if (stack.getItem() instanceof GunItem gunItem) {

                if (showBar(stack)) {
                    int m = gunItem.getMaxCapacity(stack);
                    int i = m > 0 ? Math.round(((float) gunItem.getContents(stack) / m) * 13) : 0;
                    int j = Bren.UNIVERSAL_AMMO_COLOR;
                    int k = x + 2;
                    int l = y + 11;
                    this.fill(RenderLayer.getGuiOverlay(), k, l, k + 13, l + 2, -16777216);
                    this.fill(RenderLayer.getGuiOverlay(), k, l, k + i, l + 1, j | -16777216);
                }
            }
            this.matrices.pop();
        }
    }

    private static boolean showBar(ItemStack stack) {
        if (stack.getItem() instanceof GunWithMagItem) {
            return GunWithMagItem.hasMagazine(stack);
        } else if (stack.getItem() instanceof GunItem gunItem) {
            return gunItem.getContents(stack) > 0;
        }
        return true;
    }
}
