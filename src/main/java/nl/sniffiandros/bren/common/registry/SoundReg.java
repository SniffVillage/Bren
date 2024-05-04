package nl.sniffiandros.bren.common.registry;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import nl.sniffiandros.bren.common.Bren;

public class SoundReg {
    public static SoundEvent ITEM_MACHINE_GUN_SHOOT = register("item.machine_gun.shoot");
    public static SoundEvent ITEM_AUTO_GUN_SHOOT = register("item.auto_gun.shoot");
    public static SoundEvent ITEM_RIFLE_SHOOT = register("item.rifle.shoot");
    public static SoundEvent ITEM_RIFLE_SHOOT_SILENCED = register("item.rifle.shoot_silenced");
    public static SoundEvent ITEM_SHOTGUN_SHOOT = register("item.shotgun.shoot");
    public static SoundEvent ITEM_SHOTGUN_SHELL_INSERT = register("item.shotgun.shell_insert");
    public static SoundEvent ITEM_SHOTGUN_RACK = register("item.shotgun.rack");
    public static SoundEvent ITEM_MACHINE_GUN_SHOOT_SILENCED = register("item.machine_gun.shoot_silenced");
    public static SoundEvent ITEM_AUTO_GUN_SHOOT_SILENCED = register("item.auto_gun.shoot_silenced");
    public static SoundEvent ITEM_MACHINE_GUN_DISTANT_SHOOT = register("item.machine_gun.distant_shoot");
    public static SoundEvent ITEM_MACHINE_GUN_MAGAZINE_INSERT = register("item.machine_gun.magazine_insert");
    public static SoundEvent ITEM_MACHINE_GUN_MAGAZINE_REMOVE = register("item.machine_gun.magazine_remove");
    public static SoundEvent PARTICLE_CASING_BOUNCE = register("particle.casing.bounce");

    public static SoundEvent ENTITY_VILLAGER_WORK_GUNSMITH = register("entity.villager.work_gunsmith");

    private static SoundEvent register(String name) {
        Identifier id = new Identifier(Bren.MODID, name);
        SoundEvent sound = SoundEvent.of(id);
        Registry.register(Registries.SOUND_EVENT, id, sound);
        return sound;
    }

    public static void reg(){}
}
