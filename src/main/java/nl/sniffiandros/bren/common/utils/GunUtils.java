package nl.sniffiandros.bren.common.utils;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.config.MConfig;
import nl.sniffiandros.bren.common.entity.BulletEntity;
import nl.sniffiandros.bren.common.entity.IGunUser;
import nl.sniffiandros.bren.common.network.NetworkUtils;
import nl.sniffiandros.bren.common.registry.AttributeReg;
import nl.sniffiandros.bren.common.registry.EnchantmentReg;
import nl.sniffiandros.bren.common.registry.ItemReg;
import nl.sniffiandros.bren.common.registry.NetworkReg;
import nl.sniffiandros.bren.common.registry.custom.types.GunItem;
import nl.sniffiandros.bren.common.registry.custom.MagazineItem;

import java.util.ArrayList;
import java.util.List;


public class GunUtils {

    public static int fire(LivingEntity user) {

        World world = user.getWorld();
        ItemStack stack = user.getMainHandStack();
        IGunUser gunUser = (IGunUser)user;

        if (!(stack.getItem() instanceof GunItem gunItem)) return 0;

        if (!gunUser.getGunState().equals(GunHelper.GunStates.NORMAL)) return 0;

        boolean silenced = EnchantmentHelper.getLevel(EnchantmentReg.SILENCED, stack) >= 1;

        world.playSound(null,
                user.getX(),
                user.getY(),
                user.getZ(),
                silenced ? gunItem.getSilentShootSound() : gunItem.getShootSound(),
                SoundCategory.PLAYERS, silenced ? 0.5F : 5.0F,1.0F - (user.getRandom().nextFloat() - 0.5F)/8);

        if (!silenced) {
            GunUtils.playDistantGunFire(world, user.getPos());
        }

        int fireRate = (int)Math.round(user.getAttributeValue(AttributeReg.FIRE_RATE));

        if (!world.isClient()) {

            if (user.isPlayer()) {
                stack.damage(1, user, (p) -> p.sendToolBreakStatus(Hand.MAIN_HAND));
            }

            List<Vec3d> position = GunUtils.calculatePositionBasedOnAngle(user);
            Vec3d origin = position.get(0);
            Vec3d front = position.get(1);
            Vec3d down = position.get(2);
            Vec3d side = position.get(3);

            for (PlayerEntity p : user.getWorld().getPlayers()) {
                NetworkUtils.sendShotEffect(p, origin.add(side.add(down).multiply(0.15)), front, gunItem.ejectCasing());
            }

            int level = EnchantmentHelper.getLevel(EnchantmentReg.FIRE_LANCE, stack);

            for (int i = 0; i < gunItem.bulletAmount(); ++i) {
                float x = (user.getRandom().nextFloat() - .5f) * 2 * gunItem.spread();
                float y = (user.getRandom().nextFloat() - .5f) * 2 * gunItem.spread();

                GunUtils.spawnBullet(user, origin, front, stack, new Vec2f(x,y), level * 6, gunItem.bulletSpeed(), gunItem.bulletLifespan());
            }
        }



        if (user instanceof PlayerEntity player) {
            PacketByteBuf buf = PacketByteBufs.create();

            double recoil = user.getAttributeValue(AttributeReg.RECOIL);
            recoil = CalculateRecoil(player, stack, recoil);
            buf.writeFloat((float)recoil);

            NetworkUtils.sendDataToClient(player, NetworkReg.RECOIL_CLIENT_PACKET_ID, buf);
        }

        gunItem.useBullet(stack);
        return fireRate;
    }

    public static double CalculateRecoil(PlayerEntity player, ItemStack stack, double baseRecoil) {
        baseRecoil *= MConfig.recoilMultiplier.get();
        baseRecoil /= ((EnchantmentHelper.getLevel(EnchantmentReg.STEADY_HANDS, stack) * 2.6d * 0.1d) + 1);
        baseRecoil = EnchantmentHelper.getLevel(EnchantmentReg.MOUNTED, stack) == 1 && player.isSneaking() ? baseRecoil / 2 : baseRecoil;
        return Math.round(baseRecoil * 2) / 2.0;
    }

    public static List<Vec3d> calculatePositionBasedOnAngle(LivingEntity entity) {
        Vec3d front = Vec3d.fromPolar(entity.getPitch(), entity.getYaw());
        Arm arm = entity.getMainArm();
        boolean isRightHand = arm == Arm.RIGHT;
        Vec3d side = Vec3d.fromPolar(0, entity.getYaw() + (isRightHand ? 90 : -90));
        Vec3d down = Vec3d.fromPolar(entity.getPitch() + 90, entity.getYaw());

        Vec3d origin = new Vec3d(entity.getX(), entity.getEyeY(), entity.getZ());

        List<Vec3d> positions = new ArrayList<>();
        positions.add(origin);
        positions.add(front);
        positions.add(down);
        positions.add(side);
        return positions;
    }

    public static void spawnBullet(LivingEntity entity, Vec3d origin, Vec3d front, ItemStack stack, Vec2f spread,
                                   int ticksOnFire, float speed, int lifespan) {

        World world = entity.getWorld();

        float rangedDamage = (float)entity.getAttributeValue(AttributeReg.RANGED_DAMAGE);

        BulletEntity bullet = new BulletEntity(world, rangedDamage, lifespan, entity);

        Vec3d bulletPos = origin.subtract(new Vec3d(0,0.2,0));

        bullet.setPos(bulletPos.getX(), bulletPos.getY() - 0.1, bulletPos.getZ());
        bullet.setVelocity(entity, entity.getPitch() + spread.y, entity.getHeadYaw() + spread.x, 0.0F, speed, 0.0F);

        bullet.velocityModified = true;
        bullet.velocityDirty = true;
        bullet.setFireTicks(ticksOnFire);

        world.spawnEntity(bullet);
    }

    public static void sendAnimationPacket(PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.empty();

        NetworkUtils.sendDataToClient(player, NetworkReg.SHOOT_ANIMATION_PACKET_ID, buf);
    }

    public static void playDistantGunFire(World world, Vec3d pos) {
        if (world.isClient()) {
            return;
        }

        world.getPlayers().forEach(player -> {
            double distance = player.squaredDistanceTo(pos);

            if (distance > 60) {

                float volume = (float) Math.max(1.0F - (distance / 400)/100, 0);

                if (volume > 0) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeFloat(volume);
                    NetworkUtils.sendDataToClient(player, NetworkReg.SHOOT_CLIENT_PACKET_ID, buf);
                }
            }
        });
    }


    public static void fillMagazine(ItemStack mag, PlayerEntity player) {
        while (mag.getItem() instanceof MagazineItem) {
            ItemStack bulletStack = Bren.getItemFromPlayer(player, ItemReg.BULLET);

            if (bulletStack.isEmpty()) break;
            if (MagazineItem.getContents(mag) >= MagazineItem.getMaxCapacity(mag)) {
                break;
            } else {
               int i = MagazineItem.fillMagazine(mag, bulletStack.getCount());
               bulletStack.decrement(i);
            }
        }
    }
}
