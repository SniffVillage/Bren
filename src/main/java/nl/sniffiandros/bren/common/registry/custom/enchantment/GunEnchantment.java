package nl.sniffiandros.bren.common.registry.custom.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import nl.sniffiandros.bren.common.registry.custom.GunItem;

public class GunEnchantment extends Enchantment {
    public GunEnchantment(Rarity weight) {
        super(weight, EnchantmentTarget.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND,EquipmentSlot.OFFHAND});
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof GunItem;
    }
}
