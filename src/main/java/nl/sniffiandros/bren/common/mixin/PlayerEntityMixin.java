package nl.sniffiandros.bren.common.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.entity.IGunUser;
import nl.sniffiandros.bren.common.events.MEvents;
import nl.sniffiandros.bren.common.registry.AttributeReg;
import nl.sniffiandros.bren.common.registry.custom.types.GunItem;
import nl.sniffiandros.bren.common.utils.GunHelper;
import nl.sniffiandros.bren.common.utils.GunUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements IGunUser {
    @Shadow public abstract ItemCooldownManager getItemCooldownManager();

    private static final TrackedData<NbtCompound> LAST_GUN_NBT = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
    private static final TrackedData<Integer> GUN_TICKS = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private boolean canReload = true;
    private ItemStack reloadingGun = ItemStack.EMPTY;
    private ItemStack lastGun = ItemStack.EMPTY;
    private ItemStack lastEquippedGun = ItemStack.EMPTY;
    private boolean lastGunLoaded = false;
    private int shootingDur = 0;

    public PlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(EntityType.PLAYER, world);
    }

    @Override
    public boolean isShooting() {
        return this.getActiveItem().getItem() instanceof GunItem;
    }

    @Override
    public int shootingDuration() {
        return this.shootingDur;
    }

    @Override
    public boolean canShoot(Predicate<ItemStack> predicate) {
        return predicate.test(this.getMainHandStack());
    }

    @Override
    public void setReloadingGun(ItemStack reloadingGun) {
        this.reloadingGun = reloadingGun;
    }

    public void reloadTick() {

        if (this.getWorld().isClient()) return;

        PlayerEntity player = (PlayerEntity)(Object)this;

        ItemCooldownManager cooldownManager = this.getItemCooldownManager();

        if (this.getMainHandStack().getItem() instanceof GunItem gunItem && this.getGunState().equals(GunHelper.GunStates.RELOADING)) {
            gunItem.reloadTick(this.reloadingGun, this.getWorld(), player, (IGunUser) player);
        }

        if (this.getGunState().equals(GunHelper.GunStates.RELOADING) && this.getMainHandStack() != this.reloadingGun) {
            cooldownManager.remove(this.reloadingGun.getItem());
            this.setGunState(GunHelper.GunStates.NORMAL);
            this.setCanReload(true);
        }

        if (this.getGunState().equals(GunHelper.GunStates.NORMAL) && !this.reloadingGun.isEmpty()) {
            cooldownManager.remove(this.reloadingGun.getItem());
            this.reloadingGun = ItemStack.EMPTY;
        }
    }

    public void handleShooting() {

        ItemCooldownManager c = this.getItemCooldownManager();


        if (this.canShoot((stack -> {
            boolean b = true;

            if (stack.getItem() instanceof GunItem gunItem) {
                 b = !gunItem.isEmpty(stack);
            }

            return !c.isCoolingDown(stack.getItem()) && b;
        })) && this.isShooting()) {
            this.setGunTicks(16);

            int fireRate = GunUtils.fire(this);
            if (fireRate == 0) { return;}

            c.set(this.getMainHandStack().getItem(), fireRate);

            PlayerEntity player = (PlayerEntity) (Object) this;

            MEvents.GUN_FIRED_EVENT.invoker().gunFired(player, this.getMainHandStack());

            GunUtils.sendAnimationPacket(player);
        }
    }

    @Inject(at = @At("RETURN"), method = "createPlayerAttributes()Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;")
    private static void createPlayerAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.getReturnValue()
                .add(AttributeReg.RANGED_DAMAGE, 0d)
                .add(AttributeReg.FIRE_RATE, 0d)
                .add(AttributeReg.RECOIL, 0d);
    }

    @Override
    public boolean canReload() {
        return this.canReload;
    }

    @Override
    public void setCanReload(boolean canReload) {
        this.canReload = canReload;
    }

    @Override
    public ItemStack getLastGun() {
        return this.lastGun;
    }

    @Override
    public int getGunTicks() {
        return this.dataTracker.get(GUN_TICKS);
    }

    @Override
    public void setGunTicks(int ticks) {
        this.dataTracker.set(GUN_TICKS, Math.max(ticks, 0));
    }

    @Override
    public GunHelper.GunStates getGunState() {
        return GunHelper.getGunState(this.dataTracker);
    }

    @Override
    public void setGunState(GunHelper.GunStates state) {
        GunHelper.setGunState(state, this.dataTracker);
    }

    @Inject(at = @At("TAIL"), method = "tickMovement")
    public void tickMovement(CallbackInfo ci) {
        this.setGunTicks(this.getGunTicks() - 1);
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo ci) {

        if (this.lastEquippedGun.isEmpty() && !this.dataTracker.get(LAST_GUN_NBT).isEmpty() && !this.lastGunLoaded) {
            this.buildLastGun(this.dataTracker.get(LAST_GUN_NBT));
            this.lastGunLoaded = true;
        }

        ItemStack handItem = this.getMainHandStack();

        if (handItem != null) {
            if (handItem.getItem() instanceof GunItem gunItem) {
                if (gunItem.renderOnBack()) {
                    this.lastEquippedGun = handItem;
                }
            }


            if (!handItem.equals(this.lastEquippedGun)) {
                this.lastGun = this.lastEquippedGun;
            } else {
                this.lastGun = ItemStack.EMPTY;
            }
        }
        this.handleShooting();

        if (this.isShooting()) {
            ++this.shootingDur;
            if (!this.getWorld().isClient()) {
                Bren.LONG_SHOOTING.trigger((ServerPlayerEntity) (Object) this, this.getActiveItem());
            }
        } else {
            this.shootingDur = 0;
        }

        this.reloadTick();
    }

    private void buildLastGun(NbtCompound itemNbt) {
        String s = itemNbt.getString("id");
        Identifier id = new Identifier(s);
        Item item = Registries.ITEM.get(id);

        ItemStack stack = new ItemStack(item);
        stack.setNbt(itemNbt.getCompound("tag"));
        this.lastEquippedGun = stack;
    }

    @Inject(at = @At("TAIL"), method = "initDataTracker")
    private void dataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(GUN_TICKS, 0);
        this.dataTracker.startTracking(LAST_GUN_NBT, new NbtCompound());
        GunHelper.dataTracker(this.dataTracker);
    }

    @Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound itemNbt = nbt.getCompound("LastGun");

        if (!itemNbt.isEmpty()) {
            this.dataTracker.set(LAST_GUN_NBT, itemNbt);
        }
        GunHelper.readCustomDataFromNbt(nbt, this.dataTracker);
    }

    @Inject(at = @At("TAIL"), method = "writeCustomDataToNbt")
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        if (this.getLastGun().hasNbt()) {
            NbtCompound nbtCompound = new NbtCompound();
            this.getLastGun().writeNbt(nbtCompound);

            nbt.put("LastGun", nbtCompound);
        }

        GunHelper.writeCustomDataToNbt(nbt, this.dataTracker);
    }
}
