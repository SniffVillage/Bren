package nl.sniffiandros.bren.common.registry;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.registry.custom.enchantment.*;

public class EnchantmentReg {
    public static Enchantment OVERFLOW = register("overflow", new OverflowEnchantment(Enchantment.Rarity.RARE));
    public static Enchantment AUTOFILL = register("autofill", new AutofillEnchantment(Enchantment.Rarity.VERY_RARE));
    public static Enchantment SILENCED = register("silenced", new SilencedEnchantment(Enchantment.Rarity.UNCOMMON));
    public static Enchantment STEADY_HANDS = register("steady_hands", new SteadyHandsEnchantment(Enchantment.Rarity.RARE));
    public static Enchantment FIRE_LANCE = register("fire_lance", new FireLanceEnchantment(Enchantment.Rarity.RARE));
    public static Enchantment MOUNTED = register("mounted", new MountedEnchantment(Enchantment.Rarity.RARE));

    private static Enchantment register(String name, Enchantment enchantment) {
        return Registry.register(Registries.ENCHANTMENT, new Identifier(Bren.MODID, name), enchantment);
    }

    public static void reg() {}
}
