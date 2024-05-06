package nl.sniffiandros.bren.common.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.registry.custom.*;

public class ItemReg {

    public static final int MACHINE_GUN_RECOIL = 15;
    public static final int AUTO_GUN_RECOIL = 10;
    public static final int RIFLE_RECOIL = 30;
    public static final int SHOTGUN_RECOIL = 33;

    public static final Item MACHINE_GUN = registerItem("machine_gun", new GunWithMagItem(
            new FabricItemSettings(), ToolMaterials.IRON, TagReg.MEDIUM_MAGAZINES, new GunProperties().rangeDamage(4.5F).fireRate(3).recoil(MACHINE_GUN_RECOIL)
            .shootSound(SoundReg.ITEM_MACHINE_GUN_SHOOT, SoundReg.ITEM_MACHINE_GUN_SHOOT_SILENCED)));

    public static final Item NETHERITE_MACHINE_GUN = registerItem("netherite_machine_gun", new GunWithMagItem(
            new FabricItemSettings().fireproof(), ToolMaterials.NETHERITE, TagReg.MEDIUM_MAGAZINES, new GunProperties().rangeDamage(4.75F).fireRate(3).recoil(MACHINE_GUN_RECOIL)
            .shootSound(SoundReg.ITEM_MACHINE_GUN_SHOOT, SoundReg.ITEM_MACHINE_GUN_SHOOT_SILENCED)));

    public static final Item AUTO_GUN = registerItem("auto_gun", new GunWithMagItem(
            new FabricItemSettings(), ToolMaterials.IRON, TagReg.MEDIUM_MAGAZINES, new GunProperties().rangeDamage(5.5F).fireRate(4).recoil(AUTO_GUN_RECOIL)
            .shootSound(SoundReg.ITEM_AUTO_GUN_SHOOT, SoundReg.ITEM_AUTO_GUN_SHOOT_SILENCED)));

    public static final Item NETHERITE_AUTO_GUN = registerItem("netherite_auto_gun", new GunWithMagItem(
            new FabricItemSettings().fireproof(), ToolMaterials.NETHERITE, TagReg.MEDIUM_MAGAZINES, new GunProperties().rangeDamage(5.75F).fireRate(4).recoil(AUTO_GUN_RECOIL)
            .shootSound(SoundReg.ITEM_AUTO_GUN_SHOOT, SoundReg.ITEM_AUTO_GUN_SHOOT_SILENCED)));

    public static final Item RIFLE = registerItem("rifle", new GunWithMagItem(
            new FabricItemSettings(), ToolMaterials.IRON, TagReg.SHORT_MAGAZINES, new GunProperties().rangeDamage(10.0F).fireRate(20).recoil(RIFLE_RECOIL)
            .shootSound(SoundReg.ITEM_RIFLE_SHOOT, SoundReg.ITEM_RIFLE_SHOOT_SILENCED)));

    public static final Item NETHERITE_RIFLE = registerItem("netherite_rifle", new GunWithMagItem(
            new FabricItemSettings().fireproof(), ToolMaterials.NETHERITE, TagReg.SHORT_MAGAZINES, new GunProperties().rangeDamage(11.0F).fireRate(20).recoil(RIFLE_RECOIL)
            .shootSound(SoundReg.ITEM_RIFLE_SHOOT, SoundReg.ITEM_RIFLE_SHOOT_SILENCED)));

    public static final Item SHOTGUN = registerItem("shotgun", new ShotgunItem(
            new FabricItemSettings(), ToolMaterials.IRON, new GunProperties().rangeDamage(3.75F).fireRate(20).recoil(SHOTGUN_RECOIL)
            .shootSound(SoundReg.ITEM_SHOTGUN_SHOOT, null)));

    public static final Item NETHERITE_SHOTGUN = registerItem("netherite_shotgun", new ShotgunItem(
            new FabricItemSettings().fireproof(), ToolMaterials.NETHERITE, new GunProperties().rangeDamage(4.0F).fireRate(20).recoil(SHOTGUN_RECOIL)
            .shootSound(SoundReg.ITEM_SHOTGUN_SHOOT, null)));

    public static final Item MAGAZINE = registerItem("magazine", new MagazineItem(new FabricItemSettings(), 20));
    public static final Item CLOTHED_MAGAZINE = registerItem("clothed_magazine", new ColorableMagazineItem(new FabricItemSettings(), 20));

    public static final Item SHORT_MAGAZINE = registerItem("short_magazine", new MagazineItem(new FabricItemSettings(), 6));

    public static final Item BULLET = registerItem("bullet", new Item(new FabricItemSettings()));
    public static final Item SHELL = registerItem("shell", new Item(new FabricItemSettings()));
    public static final Item AUTO_LOADER_CONTRAPTION = registerItem("auto_loader_contraption", new Item(new FabricItemSettings()));
    public static final Item METAL_TUBE = registerItem("metal_tube", new Item(new FabricItemSettings()));

    public static final Item WORKBENCH = registerItem("workbench", new BlockItem(BlockReg.WORKBENCH, new FabricItemSettings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(Bren.MODID, name), item);
    }

    public static void reg(){}
}
