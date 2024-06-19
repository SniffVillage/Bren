package nl.sniffiandros.bren.common.registry.custom.trade;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import nl.sniffiandros.bren.common.registry.EnchantmentReg;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class TradeRandomGunEnchantmentFactory implements TradeOffers.Factory {

    private static final Set<Enchantment> ENCHANTS = ImmutableList.of(
            EnchantmentReg.AUTOFILL,
            EnchantmentReg.OVERFLOW,
            EnchantmentReg.SILENCED,
            EnchantmentReg.STEADY_HANDS,
            EnchantmentReg.FIRE_LANCE,
            EnchantmentReg.MOUNTED
    ).stream().collect(ImmutableSet.toImmutableSet());

    private final int price;
    private final int exp;

    public TradeRandomGunEnchantmentFactory(int price, int exp) {
        this.price = price;
        this.exp = exp;
    }

    @Nullable
    @Override
    public TradeOffer create(Entity entity, Random random) {

        int i = random.nextInt(4) + 1;

        Enchantment enchantment = (Enchantment) ENCHANTS.toArray()[random.nextBetween(0, random.nextInt(ENCHANTS.size()-1))];

        ItemStack itemStack = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, i));

        return new TradeOffer(new ItemStack(Items.EMERALD,this.price + i + 1), new ItemStack(Items.BOOK), itemStack,12,this.exp,.23f);
    }
}
