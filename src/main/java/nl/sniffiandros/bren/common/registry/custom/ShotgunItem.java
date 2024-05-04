package nl.sniffiandros.bren.common.registry.custom;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.entity.IGunUser;
import nl.sniffiandros.bren.common.registry.EnchantmentReg;
import nl.sniffiandros.bren.common.registry.ItemReg;
import nl.sniffiandros.bren.common.registry.SoundReg;
import nl.sniffiandros.bren.common.utils.GunHelper;

public class ShotgunItem extends GunItem{
    public ShotgunItem(Settings settings, ToolMaterial material, GunProperties gunProperties) {
        super(settings, material, gunProperties);
    }

    @Override
    public int getContents(ItemStack stack) {
        if (stack.getNbt() != null) {
            return stack.getNbt().getInt("Contents");
        }
        return 0;
    }

    @Override
    public int getMaxCapacity(ItemStack stack) {
        return 8 * Math.round(Math.max(1, EnchantmentHelper.getLevel(EnchantmentReg.OVERFLOW, stack)/2));
    }

    public void addShell(ItemStack stack) {
        if (stack.getItem() instanceof ShotgunItem) {
            stack.getOrCreateNbt().putInt("Contents", getContents(stack) + 1);
        }
    }

    @Override
    public void useBullet(ItemStack stack) {
        if (stack.getItem() instanceof ShotgunItem) {
            stack.getOrCreateNbt().putInt("Contents", getContents(stack) - 1);
        }
    }

    @Override
    public void onReload(PlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        ItemCooldownManager cooldownManager = player.getItemCooldownManager();



        if (stack.getItem() instanceof ShotgunItem) {

            if (player instanceof IGunUser gunUser && !cooldownManager.isCoolingDown(stack.getItem())) {

                ItemStack bullets = Bren.getItemFromPlayer(player, ItemReg.SHELL);

                if (!gunUser.canReload() || bullets.isEmpty() || getContents(stack) >= getMaxCapacity(stack)) {
                    return;
                }
                gunUser.setCanReload(false);
                gunUser.setGunState(GunHelper.GunStates.RELOADING);
                cooldownManager.set(stack.getItem(), 10);

                player.getWorld().playSound(null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundReg.ITEM_SHOTGUN_SHELL_INSERT,
                        SoundCategory.PLAYERS, 3.0F, 1.0F - (player.getRandom().nextFloat() - 0.5F) / 4);
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

        if (entity instanceof IGunUser gunUser && entity instanceof PlayerEntity player) {
            ItemCooldownManager cooldownManager = player.getItemCooldownManager();
            if (selected) {

                if (gunUser.getGunState().equals(GunHelper.GunStates.RELOADING)) {
                    if (!cooldownManager.isCoolingDown(stack.getItem())) {

                        ItemStack bullets = Bren.getItemFromPlayer(player, ItemReg.SHELL);

                        bullets.decrement(1);
                        addShell(stack);

                        gunUser.setGunState(GunHelper.GunStates.NORMAL);
                        gunUser.setCanReload(true);
                    } else if (cooldownManager.getCooldownProgress(stack.getItem(), 1) == 0 && getContents(stack) == getMaxCapacity(stack) - 1) {
                        player.getWorld().playSound(null,
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                SoundReg.ITEM_SHOTGUN_RACK,
                                SoundCategory.PLAYERS, 3.0F, 1.0F - (player.getRandom().nextFloat() - 0.5F) / 4);
                    }
                }
            }
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public int bulletLifespan() {
        return 7;
    }

    @Override
    public float spread() {
        return 5.0F;
    }

    @Override
    public int bulletAmount() {
        return 5;
    }
}
