package nl.sniffiandros.bren.common.registry.custom;

import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import nl.sniffiandros.bren.common.entity.IGunUser;
import nl.sniffiandros.bren.common.registry.EnchantmentReg;
import nl.sniffiandros.bren.common.registry.ItemReg;
import nl.sniffiandros.bren.common.registry.SoundReg;
import nl.sniffiandros.bren.common.utils.GunHelper;

public class RevolverItem extends BulletOnlyGun {

    public RevolverItem(Settings settings, ToolMaterial material, GunProperties gunProperties) {
        super(settings, material, gunProperties);
    }

    @Override
    public int getMaxCapacity(ItemStack stack) {
        return 6 * Math.round(Math.max(1, EnchantmentHelper.getLevel(EnchantmentReg.OVERFLOW, stack)/2));
    }

    @Override
    public PoseType holdingPose() {
        return PoseType.REVOLVER;
    }

    @Override
    public int reloadSpeed() {
        return 16;
    }

    @Override
    public boolean renderOnBack() {
        return false;
    }

    @Override
    public boolean applyCustomMatrix(LivingEntity entity, GunHelper.GunStates state, MatrixStack matrices, ItemStack stack, float cooldownProgress, ModelTransformationMode renderMode, boolean leftHanded) {
        if (entity instanceof IGunUser gunUser && cooldownProgress > 0) {

            boolean reloading = gunUser.getGunState().equals(GunHelper.GunStates.RELOADING);

            float sin = (float) Math.sin((cooldownProgress * 2 - 0.5) * Math.PI) * 0.5F + 0.5F;

            if (renderMode.isFirstPerson()) {
                matrices.translate(0, 0.2, 0);
            }

            matrices.translate(0, (reloading ? sin / 2 : 0), 0);

            matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(leftHanded ? 90 + 25 : 65 +
                    (reloading && !renderMode.isFirstPerson() ? sin * 180 : 0)));
            matrices.multiply(RotationAxis.NEGATIVE_X.rotation(cooldownProgress * 15));
        }

        return true;
    }

    @Override
    public boolean hasGUIModel() {
        return false;
    }

    @Override
    public boolean ejectCasing() {
        return false;
    }

    @Override
    protected void afterInserted(ItemStack stack, PlayerEntity player) {
        player.getWorld().playSound(null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundReg.ITEM_REVOLVER_RELOAD,
                SoundCategory.PLAYERS, 1.0F, 1.0F - (player.getRandom().nextFloat() - 0.5F) / 4);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (entity instanceof IGunUser gunUser && entity instanceof PlayerEntity player) {
            ItemCooldownManager cooldownManager = player.getItemCooldownManager();
            if (selected) {
                float f = cooldownManager.getCooldownProgress(stack.getItem(), 1);

                if (f == .5F && gunUser.getGunState() == GunHelper.GunStates.RELOADING) {
                    player.getWorld().playSound(null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundReg.ITEM_REVOLVER_BULLET_INSERT,
                            SoundCategory.PLAYERS, 1.0F, 1.0F - (player.getRandom().nextFloat() - 0.5F) / 4);
                } else if (player.age % 5 == 0 && cooldownManager.isCoolingDown(stack.getItem())) {
                    player.getWorld().playSound(null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundReg.ITEM_REVOLVER_SPINNING,
                            SoundCategory.PLAYERS, 1.0F, 1.0F - (player.getRandom().nextFloat() - 0.5F) / 4);
                }
            }
        }
    }
}
