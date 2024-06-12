package nl.sniffiandros.bren.common.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import nl.sniffiandros.bren.common.registry.AttributeReg;
import nl.sniffiandros.bren.common.registry.EnchantmentReg;
import nl.sniffiandros.bren.common.registry.custom.GunItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.text.DecimalFormat;
import java.util.UUID;

/*
 * Credits : WHY-HAVE-YOU-SUMMONED-ME
 * From : https://github.com/WHY-HAVE-YOU-SUMMONED-ME/Bren/blob/main/src/main/java/nl/sniffiandros/bren/common/mixin/CustomFirearmTooltips.java
 *
 * He/She knows much more about mixins than me. That's for sure!
 */


@Mixin(ItemStack.class)
public abstract class TooltipsMixin {
    @Shadow
    private Item item;

    @ModifyVariable(method = "getTooltip", at = @At("STORE"), ordinal = 0)
    private boolean overrideFormatting(boolean original) {
        return original || item instanceof GunItem;
    }

    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/text/DecimalFormat;format(D)Ljava/lang/String;", ordinal = 0))
    private String modifyTooltipContents(DecimalFormat formatter, double value, @Local EntityAttributeModifier attribute, @Local @Nullable PlayerEntity player) {
        String insertion = "";
        UUID attributeID = attribute.getId();
        if (attributeID == AttributeReg.RANGED_DAMAGE_MODIFIER_ID) {

            if (item instanceof GunItem gunItem) {
                if (gunItem.bulletAmount() > 1) {
                    insertion = "x" + formatter.format(gunItem.bulletAmount());
                }
            }

            if (player != null) {
                value += player.getAttributeBaseValue(AttributeReg.RANGED_DAMAGE);
            }

        } else if (attributeID == AttributeReg.FIRE_RATE_MODIFIER_ID) {
            insertion = "t";

            if (player != null) {
                value += player.getAttributeBaseValue(AttributeReg.FIRE_RATE);
            }

        } else if (attributeID == AttributeReg.RECOIL_MODIFIER_ID) {
            insertion = "°";
            value *= 1 - Math.min(EnchantmentHelper.getLevel(EnchantmentReg.STEADY_HANDS, (ItemStack)(Object)this) * 0.2d, 1.0d);

            if (player != null) {
                value += player.getAttributeBaseValue(AttributeReg.RECOIL);
            }

        }
        return formatter.format(value) + insertion;
    }
}
