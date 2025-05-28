package nl.sniffiandros.bren.common.entity;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.entity.projectile.ProjectileUtil;
import nl.sniffiandros.bren.common.config.MConfig;
import nl.sniffiandros.bren.common.registry.DamageTypeReg;
import nl.sniffiandros.bren.common.registry.ParticleReg;
import nl.sniffiandros.bren.common.Bren;

public class BulletEntity extends PersistentProjectileEntity {
    private static final TrackedData<Integer> LIFESPAN = DataTracker.registerData(BulletEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private float damage;

    public BulletEntity(EntityType<? extends BulletEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
        this.pickupType = PickupPermission.DISALLOWED;
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
        // Store current (pre-tick) position
        Vec3d oldPos = this.getPos();

        // Apply vanilla movement (updates position, velocity, etc.)
        super.tick();

        // Compute new position after movement
        Vec3d newPos = this.getPos();

        // Raytrace for blocks
        BlockHitResult blockHit = this.getWorld().raycast(new RaycastContext(
                oldPos, newPos,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                this
        ));

        // Set up for minimum distance check
        double minDist = Double.MAX_VALUE;
        HitResult hitResult = null;

        // If block hit, store it and its distance
        if (blockHit.getType() != HitResult.Type.MISS) {
            minDist = oldPos.squaredDistanceTo(blockHit.getPos());
            hitResult = blockHit;
        }

        // Raytrace for entities
        Box boundingBox = this.getBoundingBox().stretch(this.getVelocity()).expand(1.0D);
        EntityHitResult entityHit = ProjectileUtil.getEntityCollision(
                this.getWorld(),
                this,
                oldPos,
                newPos,
                boundingBox,
                (entity) -> entity != this.getOwner() && super.canHit(entity)
        );
        if (entityHit != null) {
            double entityDist = oldPos.squaredDistanceTo(entityHit.getPos());
            if (entityDist < minDist) {
                minDist = entityDist;
                hitResult = entityHit;
            }
        }

        // Handle collision if one occurred
        if (hitResult != null) {
            // Set bullet to the hit position
            Vec3d hitPos = hitResult instanceof EntityHitResult
                    ? ((EntityHitResult) hitResult).getPos()
                    : ((BlockHitResult) hitResult).getPos();
            this.setPos(hitPos.x, hitPos.y, hitPos.z);

            // Call the appropriate handler and return
            if (hitResult instanceof EntityHitResult) {
                this.onEntityHit((EntityHitResult) hitResult);
            } else if (hitResult instanceof BlockHitResult) {
                this.onBlockHit((BlockHitResult) hitResult);
            }
            return; // Stop further movement/processing this tick
        }

        // Air particle effect
        if (this.getWorld().isClient() && this.age >= 2 && this.age % 3 == 0) {
            this.getWorld().addParticle(
                    ParticleReg.AIR_RING_PARTICLE,
                    this.getX(), this.getY() + this.getHeight() / 2, this.getZ(),
                    0, 0, 0
            );
        }

        // Lifespan check
        if (this.age >= this.getLifespan()) {
            this.discard();
        }
    }

    @Override
    protected boolean canHit(Entity entity) {
        // Never hit the shooter, but allow hitting anything else immediately
        return entity != this.getOwner() && super.canHit(entity);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (entity.equals(this.getOwner())) return;

        if (entity instanceof EndermanEntity) {
            super.onEntityHit(entityHitResult);
        } else if (entity instanceof LivingEntity livingEntity) {
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
        BlockState state = this.getWorld().getBlockState(pos);
        Vec3d vec3d = blockHitResult.getPos();

        if (!state.isAir() && state.isSolid()) {
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
                } else if (hitGround && isAirAbove && this.isOnFire()) {
                    this.getWorld().setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
                }
            }
            this.discard();
        }
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.BLOCK_WOOL_HIT;
    }
}