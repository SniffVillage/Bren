package nl.sniffiandros.bren.common.entity;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.config.MConfig;
import nl.sniffiandros.bren.common.registry.DamageTypeReg;
import nl.sniffiandros.bren.common.registry.ParticleReg;

public class BulletEntity extends PersistentProjectileEntity {
    private static final TrackedData<Integer> LIFESPAN = DataTracker.registerData(BulletEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private float damage;

    public BulletEntity(EntityType<? extends BulletEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
        this.pickupType = PickupPermission.DISALLOWED; // Bullets should not be picked up
    }

    public BulletEntity(World world, float damage, int lifespan, LivingEntity owner) {
        super(Bren.BULLET, world);
        this.damage = damage;
        this.setLifespan(lifespan);
        this.setNoGravity(true);
        this.setOwner(owner);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    protected void setLifespan(int lifespan) {
        this.dataTracker.set(LIFESPAN, lifespan);
    }

    public int getLifespan() {
        return this.dataTracker.get(LIFESPAN);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(LIFESPAN, 0);
    }

    @Override
    public void tick() {
        super.tick();

        // Custom air particle
        if (this.getWorld().isClient() && this.age >= 2 && this.age % 3 == 0) {
            this.getWorld().addParticle(
                    ParticleReg.AIR_RING_PARTICLE,
                    this.getX(), this.getY() + this.getHeight()/2, this.getZ(),
                    0, 0, 0
            );
        }

        // Lifespan discard
        if (this.age >= this.getLifespan()) {
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();

        if (entity instanceof EndermanEntity) {
            return; // Prevent damage towards Enderman
        }

        if (entity.equals(this.getOwner())) return;

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.timeUntilRegen = 0;
            DamageSource damageSource = DamageTypeReg.shot(this.getWorld(), this, this.getOwner());
            livingEntity.damage(damageSource, this.damage);

            if (this.isOnFire()) {
                entity.setOnFireFor(4);
            }
        }
        this.discard();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        BlockPos pos = blockHitResult.getBlockPos();
        var state = this.getWorld().getBlockState(pos);
        Vec3d vec3d = blockHitResult.getPos();

        if (!state.isAir() && state.isSolid() && this.age > 1) {
            if ((state.isIn(ConventionalBlockTags.GLASS_BLOCKS) || state.isIn(ConventionalBlockTags.GLASS_PANES)) && MConfig.bulletsBreakGlass.get()) {
                if (!this.getWorld().isClient()) {
                    this.getWorld().breakBlock(pos, false, this.getOwner());
                }
            } else {
                this.getWorld().playSound(null, vec3d.x, vec3d.y, vec3d.z, state.getSoundGroup().getBreakSound(), SoundCategory.BLOCKS, 1.0F, 3.0F);

                boolean isAirAbove = this.getWorld().getBlockState(pos.up()).isAir();
                boolean hitGround = !this.getWorld().getBlockState(pos).isAir();

                if (this.getWorld().isClient()) {
                    for (int i = 0; i < 4; ++i) {
                        float x = this.random.nextFloat() - .5f;
                        float y = this.random.nextFloat() - .5f;
                        float z = this.random.nextFloat() - .5f;
                        this.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, state), vec3d.x, vec3d.y, vec3d.z, x, y, z);
                    }
                } else if (hitGround && isAirAbove && this.isOnFire()){
                    this.getWorld().setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
                }
            }
            this.discard();
        }
    }

    @Override
    protected net.minecraft.item.ItemStack asItemStack() {
        // If you don't want bullets to drop as items, return ItemStack.EMPTY
        return net.minecraft.item.ItemStack.EMPTY;
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.BLOCK_WOOL_HIT; // to prevent arrow "thunk" from playing
    }
}
