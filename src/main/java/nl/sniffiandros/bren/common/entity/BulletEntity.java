package nl.sniffiandros.bren.common.entity;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
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

public class BulletEntity extends ProjectileEntity {
    private static final TrackedData<Integer> LIFESPAN = DataTracker.registerData(BulletEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private float damage;

    public BulletEntity(EntityType<? extends BulletEntity> entityType, World world) {
        super(entityType, world);
    }

    public BulletEntity(World world, float damage, int lifespan, LivingEntity owner) {
        super(Bren.BULLET, world);
        this.damage = damage;
        this.setLifespan(lifespan);
        this.setNoGravity(true);
        this.setOwner(owner);
    }

    protected void setLifespan(int lifespan) {
        this.dataTracker.set(LIFESPAN, lifespan);
    }

    public int getLifespan() {
        return this.dataTracker.get(LIFESPAN);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(LIFESPAN, 0);
    }

    public void tick() {
        float h;
        super.tick();
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        boolean bl = false;
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
            BlockState blockState = this.getWorld().getBlockState(blockPos);
            if (blockState.isOf(Blocks.NETHER_PORTAL)) {
                this.setInNetherPortal(blockPos);
                bl = true;
            } else if (blockState.isOf(Blocks.END_GATEWAY)) {
                BlockEntity blockEntity = this.getWorld().getBlockEntity(blockPos);
                if (blockEntity instanceof EndGatewayBlockEntity && EndGatewayBlockEntity.canTeleport(this)) {
                    EndGatewayBlockEntity.tryTeleportingEntity(this.getWorld(), blockPos, blockState, this, (EndGatewayBlockEntity)blockEntity);
                }
                bl = true;
            }
        }
        if (hitResult.getType() != HitResult.Type.MISS && !bl) {
            this.onCollision(hitResult);
        }
        this.checkBlockCollision();
        Vec3d vec3d = this.getVelocity();
        double d = this.getX() + vec3d.x;
        double e = this.getY() + vec3d.y;
        double f = this.getZ() + vec3d.z;
        this.updateRotation();
        if (this.isTouchingWater()) {
            for (int i = 0; i < 4; ++i) {
                float g = 0.25f;
                this.getWorld().addParticle(ParticleTypes.BUBBLE, d - vec3d.x * g, e - vec3d.y * g, f - vec3d.z * g, vec3d.x, vec3d.y, vec3d.z);
            }
            h = 0.8f;
        } else {
            h = 0.99f;
        }
        this.setVelocity(vec3d.multiply(h));
        if (!this.hasNoGravity()) {
            Vec3d vec3d2 = this.getVelocity();
            this.setVelocity(vec3d2.x, vec3d2.y - (double)this.getGravity(), vec3d2.z);
        }
        this.setPosition(d, e, f);

        double l = this.getVelocity().length();

        if (Math.ceil(l) == 0) {
            this.discard();
        }

        if (this.age >= this.getLifespan()) {
            this.discard();
        }

        if (this.getWorld().isClient() && this.age >= 2 && this.age % 3 == 0) {
            this.getWorld().addParticle(ParticleReg.AIR_RING_PARTICLE, this.getX(), this.getY() + this.getHeight()/2, this.getZ(), 0, 0, 0);
        }
    }

    protected float getGravity() {
        return 0.03f;
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();

        // This is to fix bullets not being able to destroy End Crystals
        if (entity.getType() == EntityType.END_CRYSTAL) {
            DamageSource damageSource = DamageTypeReg.shot(this.getWorld(), this, this.getOwner());
            entity.damage(damageSource, 1f); // Makes End Crystals explode when hit
        }

        // Fixes Ender Dragon not taking damage from bullets
        if (entity.getType() == EntityType.ENDER_DRAGON) {
            DamageSource damageSource = DamageTypeReg.shot(this.getWorld(), this, this.getOwner());

            entity.damage(damageSource, this.damage);
            entity.damage(damageSource, this.damage * 0.5f); // Apply an additional damage instance because "livingEntity.timeUntilRegen = 0;" isn't compatible with "EntityType.ENDER_DRAGON"
        }

        if (entity.equals(this.getOwner())) {
            return;
        }

        if (entity instanceof EndermanEntity enderman) {
            DamageSource damageSource = DamageTypeReg.shot(this.getWorld(), this, this.getOwner());
            enderman.damage(damageSource, this.damage);

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

        if (!state.isAir() && state.isSolid() && this.age > 1) {

            if ((state.isIn(ConventionalBlockTags.GLASS_BLOCKS) || state.isIn(ConventionalBlockTags.GLASS_PANES)) && MConfig.bulletsBreakGlass.get()) {
                if (this.getWorld().isClient()) { return;}
                this.getWorld().breakBlock(pos, false, this.getOwner());
            } else {
                this.getWorld().playSound(null,vec3d.x,vec3d.y,vec3d.z,state.getSoundGroup().getBreakSound(), SoundCategory.BLOCKS, 1.0F, 3.0F);

                boolean isAirAbove = this.getWorld().getBlockState(pos.up()).isAir();
                boolean hitGround = !this.getWorld().getBlockState(pos).isAir();

                if (this.getWorld().isClient()) {
                    for (int i = 0; i < 4; ++i) {

                        float x = this.random.nextFloat() - .5f;
                        float y = this.random.nextFloat() - .5f;
                        float z = this.random.nextFloat() - .5f;

                        this.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, state), vec3d.x,vec3d.y,vec3d.z, x, y, z);
                    }
                } else if (hitGround && isAirAbove && this.isOnFire()){
                    this.getWorld().setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
                }

                this.discard();
            }
        }
    }
}