package nl.sniffiandros.bren.common.registry.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ColorableMagazineItem extends MagazineItem implements DyeableItem {
    public ColorableMagazineItem(Settings settings, int capacity) {
        super(settings, capacity);
    }

}
