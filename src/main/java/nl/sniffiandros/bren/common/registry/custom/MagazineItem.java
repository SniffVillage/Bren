package nl.sniffiandros.bren.common.registry.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.registry.EnchantmentReg;
import nl.sniffiandros.bren.common.registry.custom.enchantment.AutofillEnchantment;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MagazineItem extends Item {
    private final int capacity;

    public MagazineItem(Settings settings, int capacity) {
        super(settings.maxCount(1));
        this.capacity = capacity;

    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        Formatting formatting = Formatting.GRAY;

        tooltip.add(Text.translatable(String.format("desc.%s.item.magazine.content",Bren.MODID))
                .append(Text.literal(" " + getContents(stack) + "/" + getMaxCapacity(stack))).formatted(formatting));

        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

        if (EnchantmentHelper.getLevel(EnchantmentReg.AUTOFILL, stack) > 0 && entity instanceof PlayerEntity player) {
            AutofillEnchantment.insert(stack, player);
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    public static int getMaxCapacity(ItemStack stack) {
        if (stack.getItem() instanceof MagazineItem magazineItem) {
            return magazineItem.capacity * Math.round(Math.max(1, EnchantmentHelper.getLevel(EnchantmentReg.OVERFLOW, stack)/2));
        }
        return 10;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        if (stack.getItem() instanceof MagazineItem) {
            return Math.round(getContents(stack) * 13.0F / (float) getMaxCapacity(stack));
        }
        return 0;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return Bren.UNIVERSAL_AMMO_COLOR;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return getContents(stack) > 0;
    }


    public static int getContentsNBT(NbtCompound nbtCompound) {
        if (!nbtCompound.isEmpty()) {
            return nbtCompound.getCompound("tag").getInt("Contents");
        }
        return 0;
    }

    public static int getContents(ItemStack itemStack) {
        if (itemStack.getNbt() != null) {
            return itemStack.getNbt().getInt("Contents");
        }
        return 0;
    }

    public static boolean isEmpty(ItemStack stack) {
        if (stack.getNbt() != null) {
            return stack.getNbt().getInt("Contents") <= 0;
        }
        return true;
    }

    public static void removeOneContent(NbtCompound nbtCompound) {
        if (!nbtCompound.isEmpty()) {
            nbtCompound.getCompound("tag").putInt("Contents", Math.max(nbtCompound.getCompound("tag").getInt("Contents") - 1, 0));
        }
    }

    public static boolean isFull(ItemStack mag) {
        if (mag.getItem() instanceof MagazineItem) {
            return MagazineItem.getContents(mag) >= MagazineItem.getMaxCapacity(mag);
        }
        return false;
    }

    public static int fillMagazine(ItemStack mag, int amount) {
        if (mag.getItem() instanceof MagazineItem) {
            int original = getContents(mag);

            int i = Math.min(getContents(mag) + amount, getMaxCapacity(mag));
            mag.getOrCreateNbt().putInt("Contents", i);

            return getMaxCapacity(mag) - original;
        }
        return 0;
    }
}
