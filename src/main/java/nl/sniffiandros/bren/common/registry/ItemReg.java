package nl.sniffiandros.bren.common.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.config.MConfig;
import nl.sniffiandros.bren.common.registry.custom.*;
import nl.sniffiandros.bren.common.registry.custom.types.*;

public class ItemReg {

    // Machine Gun
    public static final float MACHINE_GUN_RECOIL = 9f;
    public static final float MACHINE_GUN_DAMAGE = MConfig.machineGunDamage.get();
    public static final float N_MACHINE_GUN_DAMAGE = MConfig.netheriteMachineGunDamage.get();

    // Auto-Gun
    public static final float AUTO_GUN_RECOIL = 12f;
    public static final float AUTO_GUN_DAMAGE = MConfig.autoGunDamage.get();
    public static final float N_AUTO_GUN_DAMAGE = MConfig.netheriteAutoGunDamage.get();

    // Rifle
    public static final float RIFLE_RECOIL = 22f;
    public static final float RIFLE_DAMAGE = MConfig.rifleDamage.get();
    public static final float N_RIFLE_DAMAGE = MConfig.netheriteRifleDamage.get();

    // Shotgun
    public static final float SHOTGUN_RECOIL = 25f;
    public static final float SHOTGUN_DAMAGE = MConfig.shotgunDamage.get();
    public static final float N_SHOTGUN_DAMAGE = MConfig.netheriteShotgunDamage.get();

    // Revolver
    public static final float REVOLVER_RECOIL = 15f;
    public static final float REVOLVER_DAMAGE = MConfig.revolverDamage.get();
    public static final float N_REVOLVER_DAMAGE = MConfig.netheriteRevolverDamage.get();

    public static final Item MACHINE_GUN = registerItem("machine_gun", new MachineGunItem(
            new FabricItemSettings(), ToolMaterials.IRON, TagReg.MEDIUM_MAGAZINES, new GunProperties().rangedDamage(MACHINE_GUN_DAMAGE).fireRate(3).recoil(MACHINE_GUN_RECOIL)
            .shootSound(SoundReg.ITEM_MACHINE_GUN_SHOOT, SoundReg.ITEM_MACHINE_GUN_SHOOT_SILENCED)));

    public static final Item NETHERITE_MACHINE_GUN = registerItem("netherite_machine_gun", new MachineGunItem(
            new FabricItemSettings().fireproof(), ToolMaterials.NETHERITE, TagReg.MEDIUM_MAGAZINES, new GunProperties().rangedDamage(N_MACHINE_GUN_DAMAGE).fireRate(3).recoil(MACHINE_GUN_RECOIL)
            .shootSound(SoundReg.ITEM_MACHINE_GUN_SHOOT, SoundReg.ITEM_MACHINE_GUN_SHOOT_SILENCED)));

    public static final Item AUTO_GUN = registerItem("auto_gun", new GunWithMagItem(
            new FabricItemSettings(), ToolMaterials.IRON, TagReg.MEDIUM_MAGAZINES, new GunProperties().rangedDamage(AUTO_GUN_DAMAGE).fireRate(5).recoil(AUTO_GUN_RECOIL)
            .shootSound(SoundReg.ITEM_AUTO_GUN_SHOOT, SoundReg.ITEM_AUTO_GUN_SHOOT_SILENCED)));

    public static final Item NETHERITE_AUTO_GUN = registerItem("netherite_auto_gun", new GunWithMagItem(
            new FabricItemSettings().fireproof(), ToolMaterials.NETHERITE, TagReg.MEDIUM_MAGAZINES, new GunProperties().rangedDamage(N_AUTO_GUN_DAMAGE).fireRate(4).recoil(AUTO_GUN_RECOIL)
            .shootSound(SoundReg.ITEM_AUTO_GUN_SHOOT, SoundReg.ITEM_AUTO_GUN_SHOOT_SILENCED)));

    public static final Item RIFLE = registerItem("rifle", new GunWithMagItem(
            new FabricItemSettings(), ToolMaterials.IRON, TagReg.SHORT_MAGAZINES, new GunProperties().rangedDamage(RIFLE_DAMAGE).fireRate(20).recoil(RIFLE_RECOIL)
            .shootSound(SoundReg.ITEM_RIFLE_SHOOT, SoundReg.ITEM_RIFLE_SHOOT_SILENCED)));

    public static final Item NETHERITE_RIFLE = registerItem("netherite_rifle", new GunWithMagItem(
            new FabricItemSettings().fireproof(), ToolMaterials.NETHERITE, TagReg.SHORT_MAGAZINES, new GunProperties().rangedDamage(N_RIFLE_DAMAGE).fireRate(20).recoil(RIFLE_RECOIL)
            .shootSound(SoundReg.ITEM_RIFLE_SHOOT, SoundReg.ITEM_RIFLE_SHOOT_SILENCED)));

    public static final Item SHOTGUN = registerItem("shotgun", new ShotgunItem(
            new FabricItemSettings(), ToolMaterials.IRON, new GunProperties().rangedDamage(SHOTGUN_DAMAGE).fireRate(20).recoil(SHOTGUN_RECOIL).bulletSpeed(1.8F)
            .shootSound(SoundReg.ITEM_SHOTGUN_SHOOT, null)));

    public static final Item NETHERITE_SHOTGUN = registerItem("netherite_shotgun", new ShotgunItem(
            new FabricItemSettings().fireproof(), ToolMaterials.NETHERITE, new GunProperties().rangedDamage(N_SHOTGUN_DAMAGE).fireRate(20).recoil(SHOTGUN_RECOIL).bulletSpeed(1.5F)
            .shootSound(SoundReg.ITEM_SHOTGUN_SHOOT, null)));

    public static final Item REVOLVER = registerItem("revolver", new RevolverItem(
            new FabricItemSettings(), ToolMaterials.IRON, new GunProperties().rangedDamage(REVOLVER_DAMAGE).fireRate(15).recoil(REVOLVER_RECOIL)
            .shootSound(SoundReg.ITEM_REVOLVER_SHOOT, null)));

    public static final Item NETHERITE_REVOLVER = registerItem("netherite_revolver", new RevolverItem(
            new FabricItemSettings().fireproof(), ToolMaterials.NETHERITE, new GunProperties().rangedDamage(N_REVOLVER_DAMAGE).fireRate(15).recoil(REVOLVER_RECOIL)
            .shootSound(SoundReg.ITEM_REVOLVER_SHOOT, null)));

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
