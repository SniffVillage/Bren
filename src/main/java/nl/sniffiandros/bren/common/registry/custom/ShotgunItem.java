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
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.entity.IGunUser;
import nl.sniffiandros.bren.common.registry.EnchantmentReg;
import nl.sniffiandros.bren.common.registry.ItemReg;
import nl.sniffiandros.bren.common.registry.SoundReg;
import nl.sniffiandros.bren.common.utils.GunHelper;

public class ShotgunItem extends BulletOnlyGun {

    public ShotgunItem(Settings settings, ToolMaterial material, GunProperties gunProperties) {
        super(settings, material, gunProperties);
    }

    @Override
    protected void onInsert(ItemStack stack, PlayerEntity player) {
        player.getWorld().playSound(null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundReg.ITEM_SHOTGUN_SHELL_INSERT,
                SoundCategory.PLAYERS, 1.0F, 1.0F - (player.getRandom().nextFloat() - 0.5F) / 4);
    }

    @Override
    protected void onFullyLoaded(ItemStack stack, PlayerEntity player) {
        player.getWorld().playSound(null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundReg.ITEM_SHOTGUN_RACK,
                SoundCategory.PLAYERS, 1.0F, 1.0F - (player.getRandom().nextFloat() - 0.5F) / 4);
    }

    @Override
    public int getMaxCapacity(ItemStack stack) {
        return 8 * Math.round(Math.max(1, EnchantmentHelper.getLevel(EnchantmentReg.OVERFLOW, stack)/2));
    }

    @Override
    public boolean applyCustomMatrix(LivingEntity entity, GunHelper.GunStates state, MatrixStack matrixStack, ItemStack stack, float cooldownProgress, ModelTransformationMode renderMode, boolean leftHanded) {

        if (state == GunHelper.GunStates.NORMAL && renderMode.isFirstPerson()) {

            float f = -Math.min(cooldownProgress - .5F, 0) * 2;
            float f1 = -Math.min(cooldownProgress - .3333333333333333F, 0) * 3;

            float sin1 = (float) Math.abs(Math.sin(f * Math.PI));
            float sin2 = (float) Math.abs(Math.sin(f1 * Math.PI));

            matrixStack.translate(0, sin1 * .3 - sin2 * .7, -0.2F * sin1);
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotation(sin1 * 1.047198F));
        }

        return false;
    }

    @Override
    public int bulletLifespan() {
        return 9;
    }

    @Override
    public float spread() {
        return 5.0F;
    }

    @Override
    public int bulletAmount() {
        return 5;
    }

    @Override
    public Item compatibleBullet() {return ItemReg.SHELL;}
}
