package nl.sniffiandros.bren.common.registry.custom.types;

import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.world.World;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.entity.IGunUser;
import nl.sniffiandros.bren.common.registry.ItemReg;
import nl.sniffiandros.bren.common.utils.GunHelper;

public class BulletOnlyGun extends GunItem{
    public BulletOnlyGun(Settings settings, ToolMaterial material, GunProperties gunProperties) {
        super(settings, material, gunProperties);
    }

    @Override
    public int getContents(ItemStack stack) {
        if (stack.getNbt() != null) {
            return stack.getNbt().getInt("Contents");
        }
        return 0;
    }

    public void addContent(ItemStack stack) {
        if (stack.getItem() instanceof BulletOnlyGun) {
            stack.getOrCreateNbt().putInt("Contents", getContents(stack) + 1);
        }
    }

    @Override
    public void useBullet(ItemStack stack) {
        if (stack.getItem() instanceof BulletOnlyGun) {
            stack.getOrCreateNbt().putInt("Contents", getContents(stack) - 1);
        }
    }

    @Override
    public void onReload(PlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        ItemCooldownManager cooldownManager = player.getItemCooldownManager();

        if (player instanceof IGunUser gunUser && !cooldownManager.isCoolingDown(stack.getItem())) {

            ItemStack bullets = Bren.getItemFromPlayer(player, compatibleBullet());

            gunUser.setGunState(GunHelper.GunStates.NORMAL);
            gunUser.setCanReload(true);

            if (bullets.isEmpty() || getContents(stack) >= getMaxCapacity(stack)) {
                return;
            }

            if (!gunUser.canReload()) {
                return;
            }
            gunUser.setCanReload(false);
            gunUser.setGunState(GunHelper.GunStates.RELOADING);
            cooldownManager.set(stack.getItem(), this.reloadSpeed());
            onInsert(stack, player);
        }
    }

    protected void onInsert(ItemStack stack, PlayerEntity player) {}

    protected void afterInserted(ItemStack stack, PlayerEntity player) {}

    protected void onFullyLoaded(ItemStack stack, PlayerEntity player) {}

    public Item compatibleBullet() {
        return ItemReg.BULLET;
    }


    @Override
    public void reloadTick(ItemStack stack, World world, PlayerEntity player, IGunUser gunUser) {

        ItemCooldownManager cooldownManager = player.getItemCooldownManager();

        if (!cooldownManager.isCoolingDown(stack.getItem()) && getContents(stack) < getMaxCapacity(stack)) {

            ItemStack bullets = Bren.getItemFromPlayer(player, compatibleBullet());

            bullets.decrement(1);
            addContent(stack);

            afterInserted(stack, player);

            gunUser.setGunState(GunHelper.GunStates.NORMAL);
            gunUser.setCanReload(true);
        } else if (cooldownManager.getCooldownProgress(stack.getItem(), 1) == 0 && getContents(stack) == getMaxCapacity(stack) - 1) {
            onFullyLoaded(stack, player);
        }
    }
}
