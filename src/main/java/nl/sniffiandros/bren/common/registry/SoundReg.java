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
    public static SoundEvent ITEM_DISTANT_GUNFIRE = register("item.distant_gunfire");
    public static SoundEvent ITEM_MAGAZINE_INSERT  = register("item.magazine_insert");
    public static SoundEvent ITEM_MAGAZINE_REMOVE  = register("item.magazine_remove");
    public static SoundEvent ITEM_REVOLVER_SHOOT = register("item.revolver.shoot");
    public static SoundEvent ITEM_REVOLVER_BULLET_INSERT = register("item.revolver.bullet_insert");
    public static SoundEvent ITEM_REVOLVER_RELOAD = register("item.revolver.reload");
    public static SoundEvent ITEM_REVOLVER_SPINNING = register("item.revolver.spinning");
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
