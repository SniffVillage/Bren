package nl.sniffiandros.bren.common.registry.custom.enchantment;

import net.minecraft.item.ItemStack;
import nl.sniffiandros.bren.common.registry.custom.ShotgunItem;

public class SilencedEnchantment extends GunEnchantment {
    public SilencedEnchantment(Rarity weight) {
        super(weight);
    }

    public int getMinPower(int level) {
        return 0;
    }

    public int getMaxPower(int level) {
        return 1;
    }

    public boolean isTreasure() {
        return false;
    }

    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack) && !(stack.getItem() instanceof ShotgunItem);
    }
}
