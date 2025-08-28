package nl.sniffiandros.bren.common;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import nl.sniffiandros.bren.common.config.MConfig;
import nl.sniffiandros.bren.common.entity.BulletEntity;
import nl.sniffiandros.bren.common.registry.*;
import nl.sniffiandros.bren.common.registry.custom.MagazineItem;
import nl.sniffiandros.bren.common.registry.custom.criterion.LongShootingCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bren implements ModInitializer {
	public static final String MODID = "bren";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	public static final int UNIVERSAL_AMMO_COLOR = 0xFFAE00;

	public static final EntityType<BulletEntity> BULLET = Registry.register(Registries.ENTITY_TYPE, new Identifier(MODID,
			"bullet"), FabricEntityTypeBuilder.<BulletEntity>create(SpawnGroup.MISC, BulletEntity::new).trackRangeChunks(10)
			.dimensions(EntityDimensions.fixed(0.35f, 0.35f)).disableSaving().build());

	public static LongShootingCriterion LONG_SHOOTING = Criteria.register(new LongShootingCriterion());


	@Override
	public void onInitialize() {
		AttributeReg.reg();
		BlockReg.reg();
		SoundReg.reg();
		ParticleReg.reg();
		EnchantmentReg.reg();

		MConfig.init();

		ItemReg.reg(); // Has to be AFTER "MConfig.init();" in order for the damage inside the config file to also apply

		ServerLifecycleEvents.SERVER_STARTING.register(StructureRegistry::registerJigsaws);

		NetworkReg.reloadPacket();

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> {
			content.addAfter(Items.GUNPOWDER, ItemReg.AUTO_LOADER_CONTRAPTION);
			content.addAfter(Items.STICK, ItemReg.METAL_TUBE);
		});

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
			content.addAfter(Items.CRAFTING_TABLE, ItemReg.WORKBENCH);
		});

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
			MagazineItem m = (MagazineItem) ItemReg.MAGAZINE;
			MagazineItem cm = (MagazineItem) ItemReg.CLOTHED_MAGAZINE;
			MagazineItem sm = (MagazineItem) ItemReg.SHORT_MAGAZINE;
			ItemStack mag = mag(m);
			ItemStack clothed_mag = mag(cm);
			ItemStack short_mag = mag(sm);

			content.addAfter(Items.CROSSBOW, ItemReg.MACHINE_GUN);
			content.addAfter(ItemReg.MACHINE_GUN, ItemReg.AUTO_GUN);
			content.addAfter(ItemReg.AUTO_GUN, ItemReg.SHOTGUN);
			content.addAfter(ItemReg.SHOTGUN, ItemReg.RIFLE);
			content.addAfter(ItemReg.RIFLE, ItemReg.REVOLVER);
			content.addAfter(ItemReg.REVOLVER, ItemReg.NETHERITE_MACHINE_GUN);
			content.addAfter(ItemReg.NETHERITE_MACHINE_GUN, ItemReg.NETHERITE_AUTO_GUN);
			content.addAfter(ItemReg.NETHERITE_AUTO_GUN, ItemReg.NETHERITE_SHOTGUN);
			content.addAfter(ItemReg.NETHERITE_SHOTGUN, ItemReg.NETHERITE_RIFLE);
			content.addAfter(ItemReg.NETHERITE_RIFLE, ItemReg.NETHERITE_REVOLVER);
			content.addAfter(Items.DIAMOND_HORSE_ARMOR, ItemReg.MAGAZINE);
			content.addAfter(ItemReg.MAGAZINE, mag);
			content.addAfter(mag, ItemReg.CLOTHED_MAGAZINE);
			content.addAfter(ItemReg.CLOTHED_MAGAZINE, clothed_mag);
			content.addAfter(clothed_mag, ItemReg.SHORT_MAGAZINE);
			content.addAfter(ItemReg.SHORT_MAGAZINE, short_mag);
			content.addAfter(Items.ARROW, ItemReg.BULLET);
			content.addAfter(ItemReg.BULLET, ItemReg.SHELL);
		});

		VillagerRegistry.reg();
		VillagerRegistry.regTrades();

		LOGGER.info(String.format("BAM! %s is done loading!", MODID));
	}

	private static ItemStack mag(MagazineItem m) {
		ItemStack mag = m.getDefaultStack();
		MagazineItem.fillMagazine(mag, MagazineItem.getMaxCapacity(mag));
		return mag;
	}

	public static ItemStack getMagazineFromPlayer(PlayerEntity player, TagKey<Item> magTag) {
		Inventory inventory = player.getInventory();

		ItemStack fullestMag = ItemStack.EMPTY;

		if (player.getOffHandStack().isIn(magTag)) {
			if (!MagazineItem.isEmpty(player.getOffHandStack())) return player.getOffHandStack();
		}
		for(int i = 0; i < inventory.size(); ++i) {
			ItemStack itemStack = inventory.getStack(i);
			if (itemStack.isIn(magTag)) {
				if (!MagazineItem.isEmpty(itemStack) && MagazineItem.getContents(itemStack) > MagazineItem.getContents(fullestMag)) {
					fullestMag = itemStack;
				};
			}
		}
		return fullestMag;
	}

	public static ItemStack getItemFromPlayer(PlayerEntity player, Item item) {
		Inventory inventory = player.getInventory();

		if (player.getOffHandStack().isOf(item)) {
			return player.getOffHandStack();
		}
		for(int i = 0; i < inventory.size(); ++i) {
			ItemStack itemStack = inventory.getStack(i);
			if (itemStack.isOf(item)) {
				return itemStack;
			}
		}
		return ItemStack.EMPTY;
	}
}
