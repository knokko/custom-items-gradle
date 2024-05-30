package nl.knokko.customitems.serialization;

import nl.knokko.customitems.block.drop.CustomBlockDrop;
import nl.knokko.customitems.block.drop.RequiredItems;
import nl.knokko.customitems.drops.*;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.KciProjectile;
import nl.knokko.customitems.recipe.KciCookingRecipe;
import nl.knokko.customitems.recipe.KciShapelessRecipe;
import nl.knokko.customitems.recipe.KciSmithingRecipe;
import nl.knokko.customitems.recipe.ingredient.CopiedIngredient;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.recipe.ingredient.constraint.IngredientConstraints;
import nl.knokko.customitems.recipe.result.CustomItemResult;
import nl.knokko.customitems.recipe.result.KciResult;
import nl.knokko.customitems.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.settings.ExportSettings;
import nl.knokko.customitems.worldgen.TreeGenerator;
import nl.knokko.customitems.worldgen.VTreeType;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static nl.knokko.customitems.serialization.BackwardHelper.listOf;
import static nl.knokko.customitems.serialization.BackwardHelper.loadItemSet;
import static nl.knokko.customitems.serialization.TestBackward10.*;
import static nl.knokko.customitems.serialization.TestBackward11.testEnergyTypesOld11;
import static nl.knokko.customitems.serialization.TestBackward11.testSoundsOld11;
import static nl.knokko.customitems.serialization.TestBackward12.*;
import static nl.knokko.customitems.serialization.TestBackward6.testProjectileCoversOld6;
import static nl.knokko.customitems.serialization.TestBackward8.testArmorTexturesOld8;
import static nl.knokko.customitems.serialization.TestBackward8.testFuelRegistriesOld8;
import static nl.knokko.customitems.serialization.TestBackward9.testTexturesNew9;
import static org.junit.jupiter.api.Assertions.*;

public class TestBackward13 {

    @Test
    public void testBackwardCompatibility13() {
        ItemSet[] oldPair = loadItemSet("backward13old", true);
        for (ItemSet old13 : oldPair) {
            testExportSettings13Old(old13);
            testTexturesOld10(old13, 4);
            testArmorTexturesOld8(old13, 1);
            testItemsOld13(old13, 52);
            testEquipmentSetsOld12(old13, 2);
            testDamageSourcesOld12(old13, 2);
            testUpgradesOld12(old13, 1);
            testRecipesOld13(old13, 11);
            testBlockDropsOld13(old13, 5);
            testMobDropsOld13(old13, 4);
            testProjectileCoversOld6(old13, 2);
            testProjectilesOld13(old13, 5);
            testFuelRegistriesOld8(old13, 1);
            testContainersOld12(old13, 6);
            testEnergyTypesOld11(old13, 1);
            testSoundsOld11(old13, 1);
            testCombinedResourcepacksOld12(old13, 1);
        }

        ItemSet[] newPair = loadItemSet("backward13new", true);
        for (ItemSet newSet : newPair) {
            testTexturesNew9(newSet, 2);
            testItemsNew13(newSet, 10);
            testRecipesNew10(newSet, 2);
            testCookingRecipesNew13(newSet, 1);
            testContainersNew13(newSet, 2);
            testBlocksNew12(newSet, 4);
            testOreVeinsNew12(newSet, 2);
            testTreesNew12(newSet, 2);
        }

        ItemSet[] fancyPair = loadItemSet("backward13fancy", true);
        for (ItemSet fancySet : fancyPair) {
            testFancyPantsTextures12(fancySet, 2);
            testItemsFancy13(fancySet, 3);
            testSmithingRecipesFancy13(fancySet, 1);
            testTreesFancy13(fancySet, 1);
            testCombinedResourcepacksFancy13(fancySet, 1);
            testExportSettingsFancy13(fancySet);
        }
    }

    static void testCombinedResourcepacksFancy13(ItemSet itemSet, int numPacks) {
        if (itemSet.getSide() == ItemSet.Side.PLUGIN) return;

        assertEquals(numPacks, itemSet.combinedResourcepacks.size());

        assertTrue(itemSet.combinedResourcepacks.get("testpack").get().isGeyser());
    }

    static void testExportSettingsFancy13(ItemSet itemSet) {
        ExportSettings settings = itemSet.getExportSettings();
        assertTrue(settings.shouldGenerateGeyserPack());
        assertTrue(settings.shouldSkipResourcepack());
    }

    static void testTreesFancy13(ItemSet itemSet, int numTreeGenerators) {
        assertEquals(numTreeGenerators, itemSet.treeGenerators.size());

        testCherryTree(itemSet.treeGenerators.stream().filter(generator -> generator.getTreeType() == VTreeType.CHERRY).findAny().get());
    }

    private static void testCherryTree(TreeGenerator tree) {
        assertEquals(5, tree.getMinimumDepth());
        assertEquals(10, tree.getMaximumDepth());
    }

    static void testSmithingRecipesFancy13(ItemSet itemSet, int numRecipes) {
        assertEquals(numRecipes, itemSet.smithingRecipes.size());

        testSmithingRecipeDiamond(itemSet.smithingRecipes.stream().filter(
                recipe -> recipe.getResult().equals(SimpleVanillaResult.createQuick(VMaterial.DIAMOND, 1))
        ).findAny().get());
    }

    private static void testSmithingRecipeDiamond(KciSmithingRecipe recipe) {
        assertEquals(SimpleVanillaIngredient.createQuick(VMaterial.IRON_INGOT, 2), recipe.getTemplate());
        assertEquals(SimpleVanillaIngredient.createQuick(
                VMaterial.IRON_PICKAXE, 1, SimpleVanillaResult.createQuick(VMaterial.STONE_PICKAXE, 1), new IngredientConstraints(false)
        ), recipe.getTool());
        assertEquals(SimpleVanillaIngredient.createQuick(
                VMaterial.REDSTONE, 2, SimpleVanillaResult.createQuick(VMaterial.COAL, 3), new IngredientConstraints(false)
        ), recipe.getMaterial());
        assertEquals("test", recipe.getRequiredPermission());
    }

    static void testItemsFancy13(ItemSet itemSet, int numItems) {
        testItemsFancy12(itemSet, numItems);

        assertEquals(listOf(
                false, false, true, false, false, false, true, true
        ), itemSet.items.get("flagged").get().getItemFlags());

        testArmorDefault13((KciArmor) itemSet.items.get("simple_helmet").get());
        testArmorDefault13((KciArmor) itemSet.items.get("shiny_boots").get());
    }

    static void testContainersNew13(ItemSet itemSet, int numContainers) {
        testContainersNew10(itemSet, numContainers);

        assertEquals(VEntityType.CREEPER, itemSet.containers.get("creeper_cache").get().getHost().getVanillaEntity());
    }

    static void testItemsNew13(ItemSet itemSet, int numItems) {
        testItemsNew12(itemSet, numItems);

        testSimple1((KciSimpleItem) itemSet.items.get("simple1").get());

        testArrowDefault13((KciArrow) itemSet.items.get("arrow1").get());
    }

    static void testCookingRecipesNew13(ItemSet itemSet, int numRecipes) {
        assertEquals(numRecipes, itemSet.cookingRecipes.size());

        testCookingRecipe1(itemSet, itemSet.cookingRecipes.stream().filter(recipe -> recipe.getCookTime() == 1234).findAny().get());
    }

    private static void testCookingRecipe1(ItemSet itemSet, KciCookingRecipe recipe) {
        assertEquals(0.5f, recipe.getExperience());
        assertEquals(SimpleVanillaIngredient.createQuick(VMaterial.BEDROCK, 2), recipe.getInput());
        assertEquals(CustomItemResult.createQuick(itemSet.items.getReference("trident_one"), 1), recipe.getResult());
        assertFalse(recipe.isFurnaceRecipe());
        assertTrue(recipe.isBlastFurnaceRecipe());
        assertTrue(recipe.isSmokerRecipe());
        assertFalse(recipe.isCampfireRecipe());
    }

    private static void testSimple1(KciSimpleItem item) {
        Collection<TranslationEntry> translations = item.getTranslations();
        assertEquals(2, translations.size());

        TranslationEntry english = translations.stream().filter(entry -> entry.getLanguage().equals("en_us")).findAny().get();
        assertEquals("Simple", english.getDisplayName());
        assertEquals(listOf("This is simple."), english.getLore());

        TranslationEntry dutch = translations.stream().filter(entry -> entry.getLanguage().equals("nl_nl")).findAny().get();
        assertEquals("Simpel", dutch.getDisplayName());
        assertEquals(listOf("Dit is simpel."), dutch.getLore());
    }

    private static void testExportSettings13Old(ItemSet itemSet) {
        testExportSettings12Old(itemSet);
        ExportSettings ex = itemSet.getExportSettings();
        assertEquals("http://localhost:21002/", ex.getHostAddress());
        assertFalse(ex.shouldGenerateGeyserPack());
        assertTrue(ex.shouldSkipResourcepack());
    }

    private static void testItemsOld13(ItemSet itemSet, int numItems) {
        testItemsOld12(itemSet, numItems);

        testWand5((KciWand) itemSet.items.get("wand5").get());
        testThrowable1((KciThrowable) itemSet.items.get("throwable1").get());

        testArmorDefault13((KciArmor) itemSet.items.get("boots2").get());
    }

    static void testRecipesOld13(ItemSet itemSet, int numRecipes) {
        testRecipesOld12(itemSet, numRecipes);

        KciShapelessRecipe bottleRecipe = (KciShapelessRecipe) itemSet.craftingRecipes.stream().filter(recipe -> {
            KciResult result = recipe.getResult();
            if (result instanceof SimpleVanillaResult) {
                return ((SimpleVanillaResult) result).getMaterial() == VMaterial.GLASS_BOTTLE;
            } else return false;
        }).findAny().get();

        String expectedCode = "eiaaaaaafegigfejhegfgnfdhegbgdgldkakcacadndndkcagphcghcogchfglglgjhecogjgohggfgohegphc" +
                "hjcoejhegfgnfdhegbgdglakcacahehjhagfdkcaehemebfdfdfpecepfefeemefak";
        CopiedIngredient ingredient = (CopiedIngredient) bottleRecipe.getIngredients().get(0);
        assertEquals(expectedCode, ingredient.getEncodedItem());
        assertEquals(5, ingredient.getAmount());
    }

    static void testBlockDropsOld13(ItemSet itemSet, int numDrops) {
        testBlockDropsOld12(itemSet, numDrops);

        BlockDrop drop = itemSet.blockDrops.stream().filter(blockDrop -> blockDrop.getBlockType() == VBlockType.SAND).findAny().get();
        RequiredItems ri = drop.getDrop().getRequiredHeldItems();
        assertTrue(ri.isEnabled());
        assertFalse(ri.isInverted());
        assertEquals(listOf(itemSet.items.getReference("simple2")), ri.getCustomItems());
        assertEquals(listOf(RequiredItems.VanillaEntry.createQuick(VMaterial.SAND, true)), ri.getVanillaItems());
    }

    static void testMobDropsOld13(ItemSet itemSet, int numDrops) {
        testMobDropsOld10(itemSet, numDrops);

        MobDrop drop = itemSet.mobDrops.stream().filter(mobDrop -> mobDrop.getEntityType() == VEntityType.MULE).findAny().get();
        RequiredItems ri = drop.getDrop().getRequiredHeldItems();
        assertTrue(ri.isEnabled());
        assertFalse(ri.isInverted());
        assertEquals(listOf(itemSet.items.getReference("simple2")), ri.getCustomItems());
        assertEquals(listOf(RequiredItems.VanillaEntry.createQuick(VMaterial.SAND, true)), ri.getVanillaItems());
    }

    static void testProjectilesOld13(ItemSet itemSet, int numProjectiles) {
        testProjectilesOld12(itemSet, numProjectiles);

        KciProjectile pierce = itemSet.projectiles.get("pierce").get();
        assertEquals(5, pierce.getMaxPiercedEntities());
        assertTrue(pierce.shouldApplyImpactEffectsAtExpiration());
        assertTrue(pierce.shouldApplyImpactEffectsAtPierce());
    }

    private static void testWand5(KciWand wand) {
        assertEquals(10, wand.getManaCost());
        assertEquals(listOf("fire", "bolt"), wand.getMagicSpells());
        assertEquals(100, wand.getFurnaceBurnTime());
        assertEquals(listOf("{\"a\": \"b\"}"), wand.getExtraNbt());
    }

    private static void testThrowable1(KciThrowable throwable) {
        assertEquals("soundbolt", throwable.getProjectile().getName());
        assertEquals(60, throwable.getMaxStacksize());
        assertEquals(2, throwable.getAmountPerShot());
        assertEquals(123, throwable.getCooldown());
        assertTrue(throwable.shouldRequirePermission());
    }

    static void testDefaultDrop13(KciDrop drop) {
        assertEquals(0, drop.getRequiredHeldItems().getVanillaItems().size());
        // TODO Call testDefaultDrop14
    }

    static void testDefaultCustomBlockDrop13(CustomBlockDrop blockDrop) {
        assertFalse(blockDrop.getDrop().shouldCancelNormalDrops());
        assertEquals(new AllowedBiomes(false), blockDrop.getDrop().getAllowedBiomes());
        // TODO Call testDefaultBlockDrop14
    }

    static void testBaseDefault13(KciItem item) {
        assertEquals(0, item.getFurnaceBurnTime());
        assertEquals(0, item.getTranslations().size());
        assertEquals(8, item.getItemFlags().size());
        assertFalse(item.getItemFlags().get(6));
        assertFalse(item.getItemFlags().get(7));
        // TODO Call testBaseDefault14
    }

    static void testSimpleDefault13(KciSimpleItem simpleItem) {
        testBaseDefault13(simpleItem);
        // TODO Call testSimpleDefault14
    }

    static void testToolDefault13(KciTool tool) {
        testBaseDefault13(tool);
        // TODO Call testToolDefault14
    }

    static void testArmorDefault13(KciArmor armor) {
        testToolDefault13(armor);
        // TODO Call testArmorDefault14
    }

    static void testHoeDefault13(KciHoe hoe) {
        testToolDefault13(hoe);
        // TODO Call testHoeDefault14
    }

    static void testShearsDefault13(KciShears shears) {
        testToolDefault13(shears);
        // TODO Call testShearsDefault14
    }

    static void testBowDefault13(KciBow bow) {
        testToolDefault13(bow);
        // TODO Call testBowDefault14
    }

    static void testShieldDefault13(KciShield shield) {
        testToolDefault13(shield);
        // TODO Call testShieldDefault14
    }

    static void testWandDefault13(KciWand wand) {
        testBaseDefault13(wand);

        assertEquals(0, wand.getManaCost());
        assertEquals(0, wand.getMagicSpells().size());

        // TODO Call testWandDefault14
    }
    static void testGunDefault13(KciGun gun) {
        testBaseDefault13(gun);
        // TODO call testGunDefault14
    }

    static void testFoodDefault13(KciFood food) {
        testBaseDefault13(food);
        // TODO Call testFoodDefault14
    }
    static void testPocketContainerDefault13(KciPocketContainer pocketContainer) {
        testBaseDefault13(pocketContainer);
        // TODO Call testPocketContainerDefault14
    }
    static void test3dHelmetDefault13(Kci3dHelmet helmet) {
        testArmorDefault13(helmet);
        // TODO call test3dHelmetDefault14
    }
    static void testTridentDefault13(KciTrident trident) {
        testToolDefault13(trident);
        // TODO Call testTridentDefault14
    }
    static void testCrossbowDefault13(KciCrossbow crossbow) {
        testToolDefault13(crossbow);
        // TODO Call testCrossbowDefault14
    }
    static void testBlockItemDefault13(KciBlockItem blockItem) {
        testBaseDefault13(blockItem);
        // TODO Call testBlockItemDefault14
    }
    static void testElytraDefault13(KciElytra elytra) {
        testArmorDefault13(elytra);
        // TODO Call testElytraDefault14
    }

    static void testMusicDiscDefault13(KciMusicDisc disc) {
        testBaseDefault13(disc);
        // TODO Call testMusicDiscDefault14
    }

    static void testArrowDefault13(KciArrow arrow) {
        testBaseDefault13(arrow);
        // TODO Call testArrowDefault14
    }
}
