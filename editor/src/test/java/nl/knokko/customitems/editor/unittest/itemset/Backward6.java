package nl.knokko.customitems.editor.unittest.itemset;

import nl.knokko.customitems.drops.*;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.*;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.projectile.cover.CustomProjectileCover;
import nl.knokko.customitems.editor.set.projectile.cover.SphereProjectileCover;
import nl.knokko.customitems.editor.set.recipe.ShapedRecipe;
import nl.knokko.customitems.editor.set.recipe.ShapelessRecipe;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.editor.set.recipe.result.CustomItemResult;
import nl.knokko.customitems.editor.set.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.effect.EffectType;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.particle.CIParticle;
import nl.knokko.customitems.projectile.CIProjectile;
import nl.knokko.customitems.projectile.effects.*;
import nl.knokko.customitems.recipe.OutputTable;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static nl.knokko.customitems.editor.unittest.itemset.Backward1.testRecipes1;
import static nl.knokko.customitems.editor.unittest.itemset.Backward3.testTextures3;
import static nl.knokko.customitems.editor.unittest.itemset.Backward5.testItems5;
import static nl.knokko.customitems.editor.unittest.itemset.Backward7.*;
import static nl.knokko.customitems.editor.unittest.itemset.BackwardHelper.*;
import static org.junit.Assert.*;

public class Backward6 {

    @Test
    public void testBackwardCompatibility6() {
        ItemSet oldSet = loadItemSet("backward6old");
        testTextures3(oldSet, 3);
        testItemsOld6(oldSet, 21);
        testRecipesOld6(oldSet, 3);
        testBlockDropsOld6(oldSet, 1);
        testMobDropsOld6(oldSet, 2);
        testProjectileCoversOld6(oldSet, 2);
        testProjectilesOld6(oldSet, 1);

        ItemSet newSet = loadItemSet("backward6new");
        testTexturesNew6(newSet, 1);
        testItemsNew6(newSet, 1);
        testRecipesNew6(newSet, 1);
    }

    static void testTexturesNew6(ItemSet set, int numTextures) {
        assertEquals(numTextures, set.getBackingTextures().size());

        assertImageEqual(loadImage("quick_wand"), set.getTextureByName("quick_wand").getImage());
    }

    static void testItemsNew6(ItemSet set, int numItems) {
        assertEquals(numItems, set.getBackingItems().size());

        CustomTrident trident1 = (CustomTrident) set.getCustomItemByName("trident_one");
        assertEquals("trident_one", trident1.getName());
        assertEquals(CustomItemType.TRIDENT, trident1.getItemType());
        assertEquals("Cold Trident", trident1.getDisplayName());
        assertArrayEquals(new String[] {
                "Slows down enemies"
        }, trident1.getLore());
        assertArrayEquals(new AttributeModifier[] {
                new AttributeModifier(
                        AttributeModifier.Attribute.ATTACK_DAMAGE,
                        AttributeModifier.Slot.MAINHAND,
                        AttributeModifier.Operation.ADD,
                        8.0
                )
        }, trident1.getAttributes());
        assertArrayEquals(new Enchantment[] {
                new Enchantment(EnchantmentType.DAMAGE_ARTHROPODS, 2)
        }, trident1.getDefaultEnchantments());
        assertArrayEquals(new boolean[] {
                false, false, true, false, false, false
        }, trident1.getItemFlags());
        assertEquals("quick_wand", trident1.getTexture().getName());
        assertResourceEquals("backward/itemset/model/spear_diamond.json", trident1.getCustomModel());
        assertEquals(0, trident1.getPlayerEffects().size());
        assertEquals(listOf(
                new PotionEffect(EffectType.SLOW, 40, 3)
        ), trident1.getTargetEffects());
        assertEquals(0, trident1.getCommands().length);
        assertStringResourceEquals("backward/itemset/model/blue_crossbow.json", trident1.customInHandModel);
        assertNull(trident1.customThrowingModel);
        assertFalse(trident1.allowEnchanting());
        assertFalse(trident1.allowAnvilActions());
        assertEquals(432, trident1.getDurability());
        assertEquals(new SimpleVanillaIngredient(CIMaterial.ACACIA_LOG, (byte) 1, null), trident1.getRepairItem());
        assertEquals(5, trident1.getEntityHitDurabilityLoss());
        assertEquals(6, trident1.getBlockBreakDurabilityLoss());
        assertEquals(7, trident1.throwDurabilityLoss);
        assertEquals(1.5, trident1.throwDamageMultiplier, DELTA);
        assertEquals(0.5, trident1.speedMultiplier, DELTA);
    }

    static void testRecipesNew6(ItemSet set, int numRecipes) {
        assertEquals(numRecipes, set.getBackingRecipes().size());

        assertTrue(set.getBackingRecipes().contains(new ShapelessRecipe(
                new SimpleVanillaResult(CIMaterial.BRAIN_CORAL, (byte) 3),
                new Ingredient[] { new SimpleVanillaIngredient(CIMaterial.ACACIA_PLANKS, (byte) 1, null)}
        )));
    }

    static void testItemsOld6(ItemSet set, int numItems) {
        testItems5(set, numItems);

        testHoeDefault6((CustomHoe) set.getCustomItemByName("hoe_two"));
        testShearsDefault6((CustomShears) set.getCustomItemByName("shears_two"));
        testBowDefault6((CustomBow) set.getCustomItemByName("bow_two"));
        testArmorDefault6((CustomArmor) set.getCustomItemByName("helmet_two"));

        testShield1((CustomShield) set.getCustomItemByName("shield_one"));
        testWand1((CustomWand) set.getCustomItemByName("wand_one"));
    }

    static void testRecipesOld6(ItemSet set, int numRecipes) {
        testRecipes1(set, numRecipes);

        assertTrue(set.getBackingRecipes().contains(createShapedRecipe2()));
    }

    static ShapedRecipe createShapedRecipe2() {
        Ingredient[] ingredients = {
                new NoIngredient(), new SimpleVanillaIngredient(CIMaterial.COAL, (byte) 1, null), new NoIngredient(),
                new NoIngredient(), new NoIngredient(), new NoIngredient(),
                new NoIngredient(), new NoIngredient(), new NoIngredient()
        };
        return new ShapedRecipe(ingredients, new SimpleVanillaResult(CIMaterial.TORCH, (byte) 3));
    }

    static void testBlockDropsOld6(ItemSet set, int numDrops) {
        Collection<BlockDrop> drops = set.getBackingBlockDrops();
        assertEquals(numDrops, drops.size());

        BlockDrop blockDrop = drops.stream().findFirst().get();
        assertEquals(BlockType.STONE, blockDrop.getBlock());
        Drop drop = blockDrop.getDrop();
        assertTrue(drop.cancelNormalDrop());
        OutputTable dropTable = drop.getDropTable();
        assertEquals(90, dropTable.getNothingChance());
        assertEquals(4, dropTable.getEntries().size());
        CustomItem simple1 = set.getCustomItemByName("simple1");
        assertTrue(dropTable.getEntries().contains(new OutputTable.Entry(
                new CustomItemResult(simple1, (byte) 2), 2
        )));
        assertTrue(dropTable.getEntries().contains(new OutputTable.Entry(
                new CustomItemResult(simple1, (byte) 3), 2
        )));
        assertTrue(dropTable.getEntries().contains(new OutputTable.Entry(
                new CustomItemResult(simple1, (byte) 4), 2
        )));
        assertTrue(dropTable.getEntries().contains(new OutputTable.Entry(
                new CustomItemResult(simple1, (byte) 5), 4
        )));
    }

    static void testMobDropsOld6(ItemSet set, int numDrops) {
        assertEquals(set.getBackingMobDrops().size(), numDrops);

        EntityDrop swordMobDrop = set.getBackingMobDrops().stream().filter(drop -> drop.getEntityType() == CIEntityType.ZOMBIE).findFirst().get();
        EntityDrop axeMobDrop = set.getBackingMobDrops().stream().filter(drop -> drop.getEntityType() == CIEntityType.SKELETON).findFirst().get();

        assertNull(swordMobDrop.getRequiredName());
        Drop swordDrop = swordMobDrop.getDrop();
        assertFalse(swordDrop.cancelNormalDrop());
        OutputTable swordTable = swordDrop.getDropTable();
        assertEquals(1, swordTable.getEntries().size());
        assertEquals(90, swordTable.getNothingChance());
        OutputTable.Entry swordEntry = swordTable.getEntries().get(0);
        assertEquals(10, swordEntry.getChance());
        CustomItem sword1 = set.getCustomItemByName("sword1");
        assertEquals(new CustomItemResult(sword1, (byte) 1), swordEntry.getResult());

        assertEquals("skelly", axeMobDrop.getRequiredName());
        Drop axeDrop = axeMobDrop.getDrop();
        assertTrue(axeDrop.cancelNormalDrop());
        OutputTable axeTable = axeDrop.getDropTable();
        assertEquals(1, axeTable.getEntries().size());
        assertEquals(0, axeTable.getNothingChance());
        CustomItem axe1 = set.getCustomItemByName("axe1");
        OutputTable.Entry axeEntry = axeTable.getEntries().get(0);
        assertEquals(100, axeEntry.getChance());
        assertEquals(new CustomItemResult(axe1, (byte) 1), axeEntry.getResult());
    }

    static void testProjectileCoversOld6(ItemSet set, int numProjectileCovers) {
        assertEquals(numProjectileCovers, set.getBackingProjectileCovers().size());

        SphereProjectileCover sphere1 = (SphereProjectileCover) set.getProjectileCoverByName("sphere_one");
        assertEquals("sphere_one", sphere1.name);
        assertEquals(CustomItemType.DIAMOND_SHOVEL, sphere1.itemType);
        assertEquals(13, sphere1.slotsPerAxis);
        assertEquals(0.65, sphere1.scale, 0.0);
        assertEquals("test1", sphere1.texture.getName());

        CustomProjectileCover custom1 = (CustomProjectileCover) set.getProjectileCoverByName("custom_one");
        assertEquals("custom_one", custom1.name);
        assertEquals(CustomItemType.DIAMOND_SHOVEL, custom1.itemType);
        assertResourceEquals("backward/itemset/model/spear_diamond.json", custom1.model);
    }

    static void testProjectilesOld6(ItemSet set, int numProjectiles) {
        assertEquals(numProjectiles, set.getBackingProjectiles().size());

        CIProjectile crazy1 = set.getProjectileByName("crazy1");
        assertEquals("crazy1", crazy1.name);
        assertEquals(3.5, crazy1.damage, DELTA);
        assertEquals(1.6, crazy1.minLaunchAngle, DELTA);
        assertEquals(20.5, crazy1.maxLaunchAngle, DELTA);
        assertEquals(2.2, crazy1.minLaunchSpeed, DELTA);
        assertEquals(4.5, crazy1.maxLaunchSpeed, DELTA);
        assertEquals(300, crazy1.maxLifeTime);
        assertEquals(0.01, crazy1.gravity, DELTA);

        assertEquals(1, crazy1.inFlightEffects.size());
        ProjectileEffects flightEffects = crazy1.inFlightEffects.iterator().next();
        assertEquals(3, flightEffects.delay);
        assertEquals(10, flightEffects.period);
        assertEquals(6, flightEffects.effects.size());
        assertTrue(flightEffects.effects.contains(new ColoredRedstone(
                150, 50, 60, 250, 100, 90,
                0.01f, 0.25f, 30
        )));
        assertTrue(flightEffects.effects.contains(new ExecuteCommand(
                "summon chicken", ExecuteCommand.Executor.CONSOLE
        )));
        assertTrue(flightEffects.effects.contains(new Explosion(
                0.5f, false, true
        )));
        assertTrue(flightEffects.effects.contains(new RandomAccelleration(0.03f, 0.2f)));
        assertTrue(flightEffects.effects.contains(new StraightAccelleration(-0.1f, 0.3f)));
        assertTrue(flightEffects.effects.contains(new SimpleParticles(
                CIParticle.WATER_BUBBLE, 0.1f, 0.7f, 6
        )));

        assertEquals(1, crazy1.impactEffects.size());
        assertTrue(crazy1.impactEffects.contains(new SubProjectiles(
                crazy1, true, 1, 2, 30f
        )));
        assertEquals("sphere_one", crazy1.cover.name);
    }

    static void testShield1(CustomShield item) {
        assertEquals("shield_one", item.getName());
        assertEquals(CustomItemType.SHIELD, item.getItemType());
        assertEquals("Spike Shield", item.getDisplayName());
        assertArrayEquals(new String[] {
                "Useful for both blocking",
                "and hitting!"
        }, item.getLore());
        assertArrayEquals(new AttributeModifier[] {
                new AttributeModifier(
                        AttributeModifier.Attribute.ATTACK_DAMAGE,
                        AttributeModifier.Slot.MAINHAND,
                        AttributeModifier.Operation.ADD,
                        6.0
                )
        }, item.getAttributes());
        assertArrayEquals(new Enchantment[] {
                new Enchantment(EnchantmentType.MENDING, 1)
        }, item.getDefaultEnchantments());
        assertArrayEquals(new boolean[] {
                true, false, true, false, false, false
        }, item.getItemFlags());
        assertEquals("gun1", item.getTexture().getName());
        assertResourceEquals("backward/itemset/model/spear_diamond.json", item.getCustomModel());
        assertEquals(listOf(
                new PotionEffect(EffectType.SPEED, 40, 1)
        ), item.getPlayerEffects());
        assertEquals(listOf(
                new PotionEffect(EffectType.INVISIBILITY, 30, 1)
        ), item.getTargetEffects());
        assertArrayEquals(new String[] {
                "summon bat"
        }, item.getCommands());
        assertStringResourceEquals("backward/itemset/model/blue_crossbow.json", item.getBlockingModel());
        assertFalse(item.allowEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(234, item.getDurability());
        assertEquals(new SimpleVanillaIngredient(CIMaterial.DIAMOND, (byte) 1, null), item.getRepairItem());
        assertEquals(1, item.getEntityHitDurabilityLoss());
        assertEquals(2, item.getBlockBreakDurabilityLoss());
        assertEquals(7.0, item.getThresholdDamage(), 0.0);
    }

    static void testWand1(CustomWand item) {
        assertEquals("wand_one", item.getName());
        assertEquals(CustomItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("Crazy Wand", item.getDisplayName());
        assertArrayEquals(new String[] {
                "Such a weird projectile!"
        }, item.getLore());
        assertEquals(0, item.getAttributes().length);
        assertEquals(0, item.getDefaultEnchantments().length);
        assertArrayEquals(new boolean[] {
                true, true, true, false, false, false
        }, item.getItemFlags());
        assertEquals("test1", item.getTexture().getName());
        assertResourceEquals("backward/itemset/model/spear_diamond.json", item.getCustomModel());
        assertEquals(listOf(
                new PotionEffect(EffectType.REGENERATION, 100, 1)
        ), item.getPlayerEffects());
        assertEquals(0, item.getTargetEffects().size());
        assertEquals(0, item.getCommands().length);
        assertEquals("crazy1", item.projectile.name);
        assertEquals(2, item.charges.maxCharges);
        assertEquals(30, item.charges.rechargeTime);
        assertEquals(2, item.amountPerShot);
        assertEquals(70, item.cooldown);
    }

    static void testBaseDefault6(CustomItem item) {
        assertNull(item.getCustomModel());
        assertEquals(0, item.getPlayerEffects().size());
        assertEquals(0, item.getTargetEffects().size());
        assertEquals(0, item.getCommands().length);

        testBaseDefault7(item);
    }

    static void testSimpleDefault6(SimpleCustomItem item) {
        testBaseDefault6(item);
        testSimpleDefault7(item);
    }

    static void testToolDefault6(CustomTool item) {
        testBaseDefault6(item);
        testToolDefault7(item);
    }

    static void testArmorDefault6(CustomArmor item) {
        testToolDefault6(item);
        testArmorDefault7(item);
    }

    static void testHoeDefault6(CustomHoe item) {
        testToolDefault6(item);
        testHoeDefault7(item);
    }

    static void testShearsDefault6(CustomShears item) {
        testToolDefault6(item);
        testShearsDefault7(item);
    }

    static void testBowDefault6(CustomBow item) {
        testToolDefault6(item);
        testBowDefault7(item);
    }
}
