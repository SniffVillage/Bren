package nl.sniffiandros.bren.common.registry.custom.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import nl.sniffiandros.bren.common.registry.custom.ShotgunItem;

public class OverflowEnchantment extends MagazineEnchantment {
    public OverflowEnchantment(Enchantment.Rarity weight) {
        super(weight);
    }

    public int getMinPower(int level) {
        return level * 2;
    }

    public int getMaxPower(int level) {
        return 8;
    }

    public boolean isTreasure() {
        return false;
    }

    public int getMaxLevel() {
        return 4;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack) || stack.getItem() instanceof ShotgunItem;
    }
}
