package nl.sniffiandros.bren.common.recipe;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import nl.sniffiandros.bren.common.Bren;
import nl.sniffiandros.bren.common.registry.ItemReg;
import nl.sniffiandros.bren.common.registry.custom.MagazineItem;

public class ClothedMagazineRecipe extends SpecialCraftingRecipe {
    public ClothedMagazineRecipe(Identifier identifier, CraftingRecipeCategory craftingRecipeCategory) {
        super(identifier, craftingRecipeCategory);
    }

    @Override
    public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
        if (recipeInputInventory.getWidth() < 2 || recipeInputInventory.getHeight() < 2) {
            return false;
        }

        boolean mag = false;
        boolean leather = false;

        for (int i = 0; i < recipeInputInventory.getWidth(); ++i) {
            for (int j = 0; j < recipeInputInventory.getHeight(); ++j) {
                ItemStack itemStack = recipeInputInventory.getStack(i + j * recipeInputInventory.getWidth());
                if (!itemStack.isEmpty()) {
                    if (itemStack.isOf(ItemReg.MAGAZINE)) {
                        if (mag) {
                            return false;
                        }
                        mag = true;
                    } else if (itemStack.isOf(Items.LEATHER)) {
                        if (leather) {
                            return false;
                        }
                        leather = true;
                    }
                }
            }
        }
        return mag && leather;
    }

    private ItemStack findMagazine(RecipeInputInventory recipeInputInventory) {
        for (int i = 0; i < recipeInputInventory.getWidth(); ++i) {
            for (int j = 0; j < recipeInputInventory.getHeight(); ++j) {
                ItemStack itemStack = recipeInputInventory.getStack(i + j * recipeInputInventory.getWidth());
                if (!itemStack.isEmpty()) {
                    if (itemStack.getItem() instanceof MagazineItem) {
                        return itemStack;
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
        ItemStack mag = findMagazine(recipeInputInventory);
        ItemStack clothed_mag = new ItemStack(ItemReg.CLOTHED_MAGAZINE);
        if (!mag.isEmpty()) {
            clothed_mag.setNbt(mag.getNbt());
        }
        return clothed_mag;
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Bren.CLOTHED_MAG;
    }
}
