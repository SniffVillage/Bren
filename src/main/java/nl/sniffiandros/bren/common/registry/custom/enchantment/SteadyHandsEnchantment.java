package nl.sniffiandros.bren.common.registry.custom.enchantment;

import net.minecraft.item.ItemStack;
import nl.sniffiandros.bren.common.registry.custom.types.MachineGunItem;

public class SteadyHandsEnchantment extends GunEnchantment {
    public SteadyHandsEnchantment(Rarity weight) {
        super(weight);
    }

    public int getMinPower(int level) {
        return 1;
    }

    public int getMaxPower(int level) {
        return level * 2;
    }

    public boolean isTreasure() {
        return false;
    }

    public int getMaxLevel() {
        return 2;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
         return super.isAcceptableItem(stack) && !(stack.getItem() instanceof MachineGunItem);
    }
}
