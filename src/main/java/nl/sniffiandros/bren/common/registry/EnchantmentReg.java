package nl.sniffiandros.bren.common.registry;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.registry.custom.enchantment.AutofillEnchantment;
import nl.sniffiandros.bren.common.registry.custom.enchantment.OverflowEnchantment;
import nl.sniffiandros.bren.common.registry.custom.enchantment.SilencedEnchantment;

public class EnchantmentReg {
    public static Enchantment OVERFLOW = register("overflow", new OverflowEnchantment(Enchantment.Rarity.RARE));
    public static Enchantment AUTOFILL = register("autofill", new AutofillEnchantment(Enchantment.Rarity.VERY_RARE));
    public static Enchantment SILENCED = register("silenced", new SilencedEnchantment(Enchantment.Rarity.UNCOMMON));

    private static Enchantment register(String name, Enchantment enchantment) {
        return Registry.register(Registries.ENCHANTMENT, new Identifier(Bren.MODID, name), enchantment);
    }

    public static void reg() {}
}
