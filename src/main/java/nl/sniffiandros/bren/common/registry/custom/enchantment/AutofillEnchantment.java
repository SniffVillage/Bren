package nl.sniffiandros.bren.common.registry.custom.enchantment;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.registry.EnchantmentReg;
import nl.sniffiandros.bren.common.registry.ItemReg;
import nl.sniffiandros.bren.common.registry.custom.MagazineItem;

public class AutofillEnchantment extends MagazineEnchantment {
    public AutofillEnchantment(Rarity weight) {
        super(weight);
    }

    public int getMinPower(int level) {
        return 0;
    }

    public int getMaxPower(int level) {
        return 4;
    }

    public boolean isTreasure() {
        return true;
    }

    public int getMaxLevel() {
        return 4;
    }

    public static void insert(ItemStack mag, PlayerEntity player) {

        if (player.getRandom().nextFloat() > 0.1) {
            return;
        }

        float calculatedChance = ((float) EnchantmentHelper.getLevel(EnchantmentReg.AUTOFILL, mag) / 40)*2.5F;

        if (player.getRandom().nextFloat() <= calculatedChance)

        if (mag.getItem() instanceof MagazineItem && !MagazineItem.isFull(mag)) {
            ItemStack bullet = Bren.getItemFromPlayer(player, ItemReg.BULLET);
            if (!bullet.isEmpty()) {
                if (!player.getWorld().isClient()) {
                    MagazineItem.fillMagazine(mag, 1);
                    bullet.decrement(1);
                }
            }
        }
    }
}
