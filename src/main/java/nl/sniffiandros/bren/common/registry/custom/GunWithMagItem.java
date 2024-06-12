package nl.sniffiandros.bren.common.registry.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.entity.IGunUser;
import nl.sniffiandros.bren.common.registry.SoundReg;
import nl.sniffiandros.bren.common.utils.GunHelper;
import nl.sniffiandros.bren.common.utils.GunUtils;

public class GunWithMagItem extends GunItem{

    private final TagKey<Item> compatibleMagazines;

    public GunWithMagItem(Settings settings, ToolMaterial material, TagKey<Item> compatibleMagazines, GunProperties gunProperties) {
        super(settings, material, gunProperties);
        this.compatibleMagazines = compatibleMagazines;
    }

    @Override
    public void onReload(PlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        ItemCooldownManager cooldownManager = player.getItemCooldownManager();



        if (stack.getItem() instanceof GunWithMagItem gunItem) {

            if (player instanceof IGunUser gunUser && !cooldownManager.isCoolingDown(stack.getItem())) {

                ItemStack mag = Bren.getMagazineFromPlayer(player, gunItem.compatibleMagazines());

                if (!GunWithMagItem.hasMagazine(stack) && mag.isEmpty()) {
                    return;
                }

                if (!gunUser.canReload()) {
                    return;
                }
                gunUser.setCanReload(false);
                gunUser.setGunState(GunHelper.GunStates.RELOADING);
                cooldownManager.set(stack.getItem(), 20);
            }
        }
    }

    public static void putMagazine(ItemStack stack, ItemStack mag) {
        if (!(mag.getItem() instanceof MagazineItem)) {
            return;
        }

        NbtCompound magNBT = new NbtCompound();

        mag.writeNbt(magNBT);
        magNBT.remove("Count");

        stack.getOrCreateNbt().putInt("MaxMagazineCapacity", MagazineItem.getMaxCapacity(mag));
        stack.getOrCreateNbt().put("Magazine", magNBT);
    }

    public static NbtCompound getMagazineNBT(ItemStack stack) {
        if (stack.getNbt() != null) {
            return stack.getNbt().getCompound("Magazine");
        }
        return new NbtCompound();
    }

    @Override
    public int getMaxCapacity(ItemStack stack) {
        if (stack.getNbt() != null) {
            return stack.getNbt().getInt("MaxMagazineCapacity");
        }
        return 0;
    }

    @Override
    public int getContents(ItemStack stack) {
        if (stack.getNbt() != null && hasMagazine(stack)) {
            NbtCompound nbtCompound = getMagazineNBT(stack);
            return MagazineItem.getContentsNBT(nbtCompound);
        }
        return 0;
    }

    public static boolean hasMagazine(ItemStack stack){
        if (stack.getNbt() != null) {
            NbtCompound nbtCompound = getMagazineNBT(stack);
            if (nbtCompound == null) return false;

            return !nbtCompound.isEmpty();
        }
        return false;
    }

    public static boolean hasColorableMagazine(ItemStack stack) {
        NbtCompound getMagNBT = getMagazineNBT(stack);
        if (!getMagNBT.isEmpty()) {
            NbtCompound getSubNBT = getMagNBT.getCompound("display");
            if (!getSubNBT.isEmpty()) {
                return getSubNBT.contains("color", 99);
            }

            String id = getMagNBT.getString("id");
            Identifier identifier = new Identifier(id);

            ItemStack mag = new ItemStack(Registries.ITEM.get(identifier));
            return mag.getItem() instanceof ColorableMagazineItem;
        }


        return false;
    }

    public static int getMagazineColor(ItemStack stack) {
        NbtCompound getMagNBT = getMagazineNBT(stack);
        if (!getMagNBT.isEmpty()) {
            NbtCompound getSubNBT = getMagNBT.getCompound("tag").getCompound("display");
            if (!getSubNBT.isEmpty()) {
                return getSubNBT.contains("color", 99) ? getSubNBT.getInt("color") : 10511680;
            }
        }
        return hasColorableMagazine(stack) ? 10511680 : -1;
    }

    public int getColor(ItemStack stack) {
        return getMagazineColor(stack);
    }

    public static ItemStack getMagazine(ItemStack stack) {
        boolean hasMag = hasMagazine(stack);
        if (hasMag) {

            NbtCompound nbtCompound = getMagazineNBT(stack);
            if (!nbtCompound.isEmpty()) {

                String id = nbtCompound.getString("id");
                Identifier identifier = new Identifier(id);

                ItemStack mag = new ItemStack(Registries.ITEM.get(identifier));
                mag.setNbt(nbtCompound.getCompound("tag"));
                return mag;
            }
        }
        return ItemStack.EMPTY;
    }


    public static ItemStack unloadMagazine(ItemStack stack, PlayerEntity player) {
        if (!(stack.getItem() instanceof GunItem)) {
            return ItemStack.EMPTY;
        }

        ItemStack mag = getMagazine(stack);
        if (!mag.isEmpty()) {

            int empty_slot = player.getInventory().getEmptySlot();
            if (empty_slot != -1) {
                player.getInventory().setStack(empty_slot, mag);
            } else {
                ItemEntity itemEntity = new ItemEntity(player.getWorld(), player.getX(), player.getEyePos().getY(), player.getZ(), mag);
                itemEntity.setThrower(player.getUuid());
                player.getWorld().spawnEntity(itemEntity);
            }

            stack.getNbt().put("Magazine", new NbtCompound());

            return mag;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isEmpty(ItemStack stack) {
        boolean b = false;

        if (stack.getItem() instanceof GunItem gunItem) {
            b = gunItem.getContents(stack) <= 0;
        }

        return b || !hasMagazine(stack);
    }
    @Override
    public void useBullet(ItemStack stack) {
        if (stack.getNbt() != null) {
            NbtCompound nbtCompound = getMagazineNBT(stack);
            MagazineItem.removeOneContent(nbtCompound);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

        if (entity instanceof IGunUser gunUser && entity instanceof PlayerEntity player) {
            ItemCooldownManager cooldownManager = player.getItemCooldownManager();
            if (selected) {

                if (gunUser.getGunState().equals(GunHelper.GunStates.RELOADING)) {
                    if (!cooldownManager.isCoolingDown(stack.getItem())) {
                        if (GunWithMagItem.hasMagazine(stack)) {
                            GunWithMagItem.unloadMagazine(stack, player);
                            world.playSound(null,
                                    player.getX(),
                                    player.getY(),
                                    player.getZ(),
                                    SoundReg.ITEM_MAGAZINE_REMOVE,
                                    SoundCategory.PLAYERS, 1.0F, 1.0F - (player.getRandom().nextFloat() - 0.5F) / 4);

                        } else {
                            ItemStack mag = Bren.getMagazineFromPlayer(player, ((GunWithMagItem) stack.getItem()).compatibleMagazines());
                            GunWithMagItem.putMagazine(stack, mag);
                            mag.decrement(1);
                        }
                        gunUser.setGunState(GunHelper.GunStates.NORMAL);
                        gunUser.setCanReload(true);
                    } else if (cooldownManager.getCooldownProgress(stack.getItem(),1) == 0.75F && !GunWithMagItem.hasMagazine(stack)) {
                        world.playSound(null,
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                SoundReg.ITEM_MAGAZINE_INSERT,
                                SoundCategory.PLAYERS, 1.0F, 1.0F - (player.getRandom().nextFloat() - 0.5F) / 4);
                    }

                }

            }
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    public TagKey<Item> compatibleMagazines() {
        return this.compatibleMagazines;
    }
}
