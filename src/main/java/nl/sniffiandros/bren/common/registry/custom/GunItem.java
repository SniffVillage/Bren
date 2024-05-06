package nl.sniffiandros.bren.common.registry.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.config.MConfig;
import nl.sniffiandros.bren.common.entity.IGunUser;
import nl.sniffiandros.bren.common.registry.EnchantmentReg;
import nl.sniffiandros.bren.common.registry.ParticleReg;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GunItem extends ToolItem implements Vanishable {
    private final float rangeDamage;
    private final int fireRate;
    private final SoundEvent shootSound;
    private final SoundEvent silentShootSound;
    private final int recoil;


    public GunItem(Settings settings, ToolMaterial material, GunProperties gunProperties) {
        super(material, settings.maxDamageIfAbsent((int) (material.getDurability() * 1.5)));
        this.rangeDamage = gunProperties.rangeDamage;
        this.fireRate = gunProperties.fireRate;
        this.shootSound = gunProperties.sound;
        this.silentShootSound = gunProperties.silentSound;
        this.recoil = gunProperties.recoil;
    }

    public float getRecoil(ItemStack stack) {
        float reduction = ((float) EnchantmentHelper.getLevel(EnchantmentReg.STEADY_HANDS, stack) * 2.6F / 10) + 1;
        return this.recoil * MConfig.recoilMultiplier.get() / reduction;
    }


    public int getMaxCapacity(ItemStack stack) {
        return 0;
    }

    public SoundEvent getShootSound() {
        return this.shootSound;
    }

    public SoundEvent getSilentShootSound() {
        return this.silentShootSound;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        Bren.buildToolTip(tooltip, stack);

        super.appendTooltip(stack, world, tooltip, context);
    }

    public int getFireRate() {
        return this.fireRate;
    }


    public void onReload(PlayerEntity player) {

    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (hand == Hand.OFF_HAND) {
            return TypedActionResult.pass(stack);
        }

        if (user instanceof IGunUser) {
            user.setCurrentHand(hand);
        }

        return super.use(world, user, hand);
    }

    public static float rangeDamage(ItemStack stack) {
        if (stack.getItem() instanceof GunItem machineGun) {
            return machineGun.rangeDamage;
        }
        return 0;
    }

    public boolean isEmpty(ItemStack stack) {
        return getContents(stack) <= 0;
    }

    public int getContents(ItemStack stack) {
        return 0;
    }


    public void useBullet(ItemStack stack) {

    }

    public static void shotParticles(World world, Vec3d origin, Vec3d direction, Random random) {

        for (int i = 0; i != 8; ++i) {
            double t = Math.pow(random.nextFloat(), 1.5);

            Vec3d p = origin.add(direction.multiply(0.8 + t));
            Vec3d v = direction.multiply(0.2 * (1 - t));
            world.addParticle(ParticleReg.MUZZLE_SMOKE_PARTICLE, p.x, p.y, p.z, v.x, v.y, v.z);

        }
    }

    public static void ejectCasingParticle(World world, Vec3d origin, Vec3d direction, Random random) {
        Vec3d rotated = direction.rotateY((float) (-Math.PI/2));

        Vec3d p = origin.add(direction.multiply(.3f)).add(rotated.multiply(.26));
        Vec3d v = rotated.multiply(.15f).add(0,.5f + world.getRandom().nextFloat() * .1f,0);
        world.addParticle(ParticleReg.CASING_PARTICLE, p.x, p.y, p.z, v.x, v.y, v.z);
    }


    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    public int bulletLifespan() {
        return 35;
    }

    public float spread() {
        return 0.0f;
    }

    public int bulletAmount() {
        return 1;
    }
}
