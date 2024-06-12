package nl.sniffiandros.bren.common.registry;

import net.minecraft.block.Block;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import nl.sniffiandros.bren.common.Bren;

import java.util.UUID;

public class AttributeReg {
    public static final EntityAttribute RANGED_DAMAGE = new ClampedEntityAttribute("attribute.name.ranged_damage", 0d, 0d, 2048d).setTracked(true);
    public static final EntityAttribute FIRE_RATE = new ClampedEntityAttribute("attribute.name.fire_rate", 0d, 0d, 2048d).setTracked(true);
    public static final EntityAttribute RECOIL = new ClampedEntityAttribute("attribute.name.recoil", 0d, -360d, 360d).setTracked(true);

    public static final UUID RANGED_DAMAGE_MODIFIER_ID = UUID.fromString("EF1BE063-D502-1F12-9E55-7D827281DB27");
    public static final UUID FIRE_RATE_MODIFIER_ID = UUID.fromString("C8E578CC-5986-417E-B78D-CD4F6F3535CD");
    public static final UUID RECOIL_MODIFIER_ID = UUID.fromString("EA3C78D6-F93E-45CC-B683-4EBF2E5DE456");

    public static void reg() {
        Registry.register(Registries.ATTRIBUTE, new Identifier(Bren.MODID, "ranged_damage"), RANGED_DAMAGE);

        Registry.register(Registries.ATTRIBUTE, new Identifier(Bren.MODID, "fire_rate"), FIRE_RATE);

        Registry.register(Registries.ATTRIBUTE, new Identifier(Bren.MODID, "recoil"), RECOIL);
    }
}
