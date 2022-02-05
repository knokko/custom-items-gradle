package nl.knokko.customitems.serialization;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.block.drop.CustomBlockDropValues;
import nl.knokko.customitems.block.drop.RequiredItemValues;
import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.slot.StorageSlotValues;
import nl.knokko.customitems.container.slot.display.SimpleVanillaDisplayItemValues;
import nl.knokko.customitems.container.slot.display.SlotDisplayValues;
import nl.knokko.customitems.effect.*;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.command.ItemCommand;
import nl.knokko.customitems.item.command.ItemCommandEvent;
import nl.knokko.customitems.item.command.ItemCommandSystem;
import nl.knokko.customitems.item.gun.IndirectGunAmmoValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.projectile.effect.*;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.ShapelessRecipeValues;
import nl.knokko.customitems.recipe.ingredient.CustomItemIngredientValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredientValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.customitems.recipe.result.SimpleVanillaResultValues;
import nl.knokko.customitems.sound.CISound;
import nl.knokko.customitems.texture.BowTextureEntry;
import nl.knokko.customitems.texture.CrossbowTextureValues;
import org.junit.Test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static nl.knokko.customitems.serialization.Backward3.testTextures3;
import static nl.knokko.customitems.serialization.Backward6.*;
import static nl.knokko.customitems.serialization.Backward8.*;
import static nl.knokko.customitems.serialization.BackwardHelper.*;
import static org.junit.Assert.*;

public class Backward9 {

    @Test
    public void testBackwardCompatibility9() {
        // Due to a stupid oversight, I introduced an extra encoding between Editor 9.0 and Editor 9.12
        for (ItemSet[] oldPair : new ItemSet[][] { loadItemSet("backward9old"), loadItemSet("backward9_12old") }) {
            for (ItemSet old9 : oldPair) {
                testTextures3(old9, 3);
                testArmorTexturesOld8(old9, 1);
                testItemsOld9(old9, 33);
                testRecipesOld9(old9, 5);
                testBlockDropsOld8(old9, 2);
                testMobDropsOld8(old9, 2);
                testProjectileCoversOld6(old9, 2);
                testProjectilesOld9(old9, 2);
                testFuelRegistriesOld8(old9, 1);
                testContainersOld9(old9, 3);
            }
        }

        for (ItemSet new9 : loadItemSet("backward9new")) {
            testTexturesNew9(new9, 2);
            testItemsNew9(new9, 4);
            testRecipesNew6(new9, 1);
            testBlocksNew9(new9, 1);
        }
    }

    static void testBlocksNew9(ItemSet set, int numBlocks) {
        assertEquals(numBlocks, set.getBlocks().size());

        CustomBlockValues block1 = set.getBlock(1).get();
        assertEquals("block1", block1.getName());
        assertEquals(1, block1.getDrops().size());

        CustomBlockDropValues drop = block1.getDrops().iterator().next();
        assertEquals(OutputTableValues.createQuick(listOf(
                OutputTableValues.Entry.createQuick(SimpleVanillaResultValues.createQuick(CIMaterial.COBBLESTONE, 2), 50)
        )), drop.getItemsToDrop());
        assertEquals(SilkTouchRequirement.FORBIDDEN, drop.getSilkTouchRequirement());

        RequiredItemValues requiredItems = drop.getRequiredItems();
        assertTrue(requiredItems.isEnabled());
        assertFalse(requiredItems.isInverted());
        assertEquals(listOf(
                set.getItemReference("trident_one")
        ), requiredItems.getCustomItems());
        assertEquals(listOf(
                RequiredItemValues.VanillaEntry.createQuick(CIMaterial.STONE_PICKAXE, false)
        ), requiredItems.getVanillaItems());

        if (set.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("quick_wand", block1.getTexture().getName());
        } else {
            assertNull(block1.getTextureReference());
        }
    }

    static void testTexturesNew9(ItemSet set, int numTextures) {
        if (set.getSide() == ItemSet.Side.PLUGIN) {
            assertEquals(0, set.getTextures().size());
            return;
        }

        testTexturesNew6(set, numTextures);

        CrossbowTextureValues crossbowTextures = (CrossbowTextureValues) set.getTexture("crossbow_texture").get();
        assertEquals("crossbow_texture", crossbowTextures.getName());
        assertImageEqual(loadImage("gun1"), crossbowTextures.getImage());
        assertImageEqual(loadImage("test1"), crossbowTextures.getArrowImage());
        assertImageEqual(loadImage("test1"), crossbowTextures.getFireworkImage());

        assertEquals(3, crossbowTextures.getPullTextures().size());
        BowTextureEntry pull1 = crossbowTextures.getPullTextures().get(0);
        assertEquals(0.0, pull1.getPull(), 0.0);
        assertImageEqual(loadImage("gun1"), pull1.getImage());
        BowTextureEntry pull2 = crossbowTextures.getPullTextures().get(1);
        assertEquals(0.5, pull2.getPull(), 0.0);
        assertImageEqual(loadImage("gun1"), pull2.getImage());
        BowTextureEntry pull3 = crossbowTextures.getPullTextures().get(2);
        assertEquals(0.75, pull3.getPull(), 0.0);
        assertImageEqual(loadImage("test1"), pull3.getImage());
    }

    static void testItemsNew9(ItemSet set, int numItems) {
        testItemsNew8(set, numItems);

        testTridentDefault9((CustomTridentValues) set.getItem("trident2").get());

        testCrossbow1((CustomCrossbowValues) set.getItem("crossbow1").get(), set);
        testBlockItem1((CustomBlockItemValues) set.getItem("block_item1").get(), set);
    }

    static void testBlockItem1(CustomBlockItemValues item, ItemSet set) {
        assertEquals("block_item1", item.getName());
        assertEquals(CustomItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("bi1", item.getAlias());
        assertEquals("Block 1", item.getDisplayName());
        assertEquals(listOf(
                "This is not an actual block",
                "Just the item that places it!"
        ), item.getLore());
        assertEquals(listOf(
                AttributeModifierValues.createQuick(
                        AttributeModifierValues.Attribute.ATTACK_SPEED,
                        AttributeModifierValues.Slot.MAINHAND,
                        AttributeModifierValues.Operation.ADD,
                        5.0
                )
        ), item.getAttributeModifiers());
        assertEquals(listOf(
                EnchantmentValues.createQuick(EnchantmentType.DURABILITY, 2)
        ), item.getDefaultEnchantments());
        assertEquals(listOf(
                false, false, true, true, false, false
        ), item.getItemFlags());
        assertEquals(listOf(
                PotionEffectValues.createQuick(EffectType.REGENERATION, 100, 1)
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                PotionEffectValues.createQuick(EffectType.POISON, 100, 2)
        ), item.getOnHitTargetEffects());
        ItemCommandSystem killAllSystem = new ItemCommandSystem(true);
        killAllSystem.setCommandsFor(ItemCommandEvent.RIGHT_CLICK_GENERAL, listOf(ItemCommand.createFromLegacy("kill @a")));
        assertEquals(killAllSystem, item.getCommandSystem());
        assertEquals(ReplacementConditionValues.ConditionOperation.NONE, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionValues.createQuick(
                        ReplacementConditionValues.ReplacementCondition.ISBROKEN,
                        set.getItemReference("trident2"),
                        ReplacementConditionValues.ReplacementOperation.EXACTLY,
                        1,
                        set.getItemReference("crossbow1")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffectValues.createQuick(EffectType.FAST_DIGGING, 3, AttributeModifierValues.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(ExtraItemNbtValues.createQuick(listOf(
                ExtraItemNbtValues.Entry.createQuick(listOf("block"), new ExtraItemNbtValues.Value(1))
        )), item.getExtraNbt());
        assertEquals(0.25, item.getAttackRange(), 0.0);
        assertEquals("block1", item.getBlock().getName());
        assertEquals(15, item.getMaxStacksize());
    }

    static void testCrossbow1(CustomCrossbowValues item, ItemSet itemSet) {
        assertEquals("crossbow1", item.getName());
        assertEquals(CustomItemType.CROSSBOW, item.getItemType());
        assertEquals("cb1", item.getAlias());
        assertEquals("Test Crossbow", item.getDisplayName());
        assertEquals(listOf(
                "We finally have crossbows"
        ), item.getLore());
        assertEquals(listOf(
                AttributeModifierValues.createQuick(
                        AttributeModifierValues.Attribute.MOVEMENT_SPEED,
                        AttributeModifierValues.Slot.OFFHAND,
                        AttributeModifierValues.Operation.MULTIPLY,
                        0.8
                )
        ), item.getAttributeModifiers());
        assertEquals(listOf(
                EnchantmentValues.createQuick(EnchantmentType.PIERCING, 2)
        ), item.getDefaultEnchantments());
        assertEquals(listOf(
                true, true, true, true, true, true
        ), item.getItemFlags());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("crossbow_texture", item.getTexture().getName());
        } else {
            assertNull(item.getTextureReference());
        }
        assertEquals(listOf(
                PotionEffectValues.createQuick(EffectType.SPEED, 20, 1)
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                PotionEffectValues.createQuick(EffectType.WITHER, 50, 2)
        ), item.getOnHitTargetEffects());
        ItemCommandSystem nightVisionSystem = new ItemCommandSystem(true);
        nightVisionSystem.setCommandsFor(ItemCommandEvent.RIGHT_CLICK_GENERAL, listOf(ItemCommand.createFromLegacy(
                "effect @p night_vision 5"
        )));
        assertEquals(nightVisionSystem, item.getCommandSystem());
        assertEquals(ReplacementConditionValues.ConditionOperation.OR, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionValues.createQuick(
                        ReplacementConditionValues.ReplacementCondition.HASITEM,
                        itemSet.getItemReference("trident2"),
                        ReplacementConditionValues.ReplacementOperation.EXACTLY,
                        1,
                        itemSet.getItemReference("trident_one")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffectValues.createQuick(EffectType.INVISIBILITY, 1, AttributeModifierValues.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(ExtraItemNbtValues.createQuick(listOf(
                ExtraItemNbtValues.Entry.createQuick(listOf("cross"), new ExtraItemNbtValues.Value("bow"))
        )), item.getExtraNbt());
        assertEquals(0.5, item.getAttackRange(), 0.0);
        assertFalse(item.allowEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(567, (long) item.getMaxDurabilityNew());
        assertEquals(SimpleVanillaIngredientValues.createQuick(
                CIMaterial.WHITE_WOOL, 2, SimpleVanillaResultValues.createQuick(CIMaterial.BLACK_WOOL, 5)
        ), item.getRepairItem());
        assertEquals(1, item.getEntityHitDurabilityLoss());
        assertEquals(2, item.getBlockBreakDurabilityLoss());
        assertEquals(0, item.getArrowDurabilityLoss());
        assertEquals(4, item.getFireworkDurabilityLoss());
        assertEquals(1.25, item.getArrowDamageMultiplier(), 0.0);
        assertEquals(0.75, item.getFireworkDamageMultiplier(), 0.0);
        assertEquals(1.5, item.getArrowSpeedMultiplier(), 0.0);
        assertEquals(1.75, item.getFireworkSpeedMultiplier(), 0.0);
        assertEquals(1, item.getArrowKnockbackStrength());
        assertFalse(item.hasArrowGravity());
    }

    static void testContainersOld9(ItemSet set, int numContainers) {
        testContainersOld8(set, numContainers);

        CustomContainerValues container3 = set.getContainer("container3").get();
        assertEquals(StorageSlotValues.createQuick(SlotDisplayValues.createQuick(
                SimpleVanillaDisplayItemValues.createQuick(CIMaterial.IRON_BARDING), "safe storage", new ArrayList<>(), 1
        )), container3.getSlot(0, 0));
        for (int x = 1; x < 9; x++) {
            assertEquals(StorageSlotValues.createQuick(null), container3.getSlot(x, 0));
        }
    }

    static void testProjectilesOld9(ItemSet set, int numProjectiles) {
        testProjectilesOld6(set, numProjectiles);

        CustomProjectileValues crazy2 = set.getProjectile("crazy2").get();
        assertEquals(1, crazy2.getInFlightEffects().size());
        ProjectileEffectsValues inFlightEffects = crazy2.getInFlightEffects().iterator().next();
        assertEquals(listOf(
                PushOrPullValues.createQuick(0.5f, 1.5f),
                ShowFireworkValues.createQuick(listOf(
                        ShowFireworkValues.EffectValues.createQuick(true, false, ShowFireworkValues.EffectType.CREEPER, listOf(
                                new Color(200, 100, 150)
                        ), listOf(
                                new Color(0, 50, 150)
                        ))
                ))
        ), inFlightEffects.getEffects());

        assertEquals(listOf(
                PlaySoundValues.createQuick(CISound.BLOCK_ANVIL_HIT, 1.25f, 1.75f),
                PotionAuraValues.createQuick(3f, listOf(
                        PotionEffectValues.createQuick(EffectType.FAST_DIGGING, 100, 2)
                ))
        ), crazy2.getImpactEffects());

        assertEquals(1f, crazy2.getLaunchKnockback(), 0.0);
        assertEquals(2f, crazy2.getImpactKnockback(), 0.0);

        assertEquals(listOf(
                PotionEffectValues.createQuick(EffectType.INCREASE_DAMAGE, 100, 2)
        ), crazy2.getImpactPotionEffects());
    }

    static void testRecipesOld9(ItemSet set, int numRecipes) {
        testRecipesOld8(set, numRecipes);

        assertTrue(set.getCraftingRecipes().stream().anyMatch(recipe -> recipe.equals(createShapelessRecipe2(set))));
    }

    static ShapelessRecipeValues createShapelessRecipe2(ItemSet set) {
        List<IngredientValues> ingredients = listOf(
                CustomItemIngredientValues.createQuick(
                        set.getItemReference("simple1"), 3,
                        CustomItemResultValues.createQuick(
                                set.getItemReference("simple2"), 2
                        )
                )
        );
        return ShapelessRecipeValues.createQuick(ingredients, SimpleVanillaResultValues.createQuick(CIMaterial.CHORUS_PLANT, 8));
    }

    static void testItemsOld9(ItemSet set, int numItems) {
        testItemsOld8(set, numItems);

        testWandDefault9((CustomWandValues) set.getItem("wand2").get());
        testSimpleDefault9((SimpleCustomItemValues) set.getItem("simple3").get());
        testToolDefault9((CustomToolValues) set.getItem("shovel2").get());
        testHoeDefault9((CustomHoeValues) set.getItem("hoe3").get());
        testShearsDefault9((CustomShearsValues) set.getItem("shears3").get());
        test3dHelmetDefault9((CustomHelmet3dValues) set.getItem("3dhelmet1").get());
        testBowDefault9((CustomBowValues) set.getItem("bow3").get());
        testArmorDefault9((CustomArmorValues) set.getItem("chestplate2").get());
        testShieldDefault9((CustomShieldValues) set.getItem("shield2").get());

        testGun1((CustomGunValues) set.getItem("gun1").get(), set);
        testPocketContainer1((CustomPocketContainerValues) set.getItem("pocket_container1").get(), set);
        testFood1((CustomFoodValues) set.getItem("food1").get(), set);
    }

    static void testGun1(CustomGunValues item, ItemSet set) {
        assertEquals("gun1", item.getName());
        assertEquals(CustomItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("g1", item.getAlias());
        assertEquals("The first gun", item.getDisplayName());
        assertEquals(listOf(
                "It's like a wand,",
                "but requires ammunition."
        ), item.getLore());
        assertEquals(listOf(
                AttributeModifierValues.createQuick(
                        AttributeModifierValues.Attribute.KNOCKBACK_RESISTANCE,
                        AttributeModifierValues.Slot.OFFHAND,
                        AttributeModifierValues.Operation.ADD,
                        0.4
                )
        ), item.getAttributeModifiers());
        assertEquals(listOf(
                EnchantmentValues.createQuick(EnchantmentType.SILK_TOUCH, 1)
        ), item.getDefaultEnchantments());
        assertEquals(listOf(
                false, true, true, false, false, false
        ), item.getItemFlags());
        if (set.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("gun1", item.getTexture().getName());
        } else {
            assertNull(item.getTextureReference());
        }
        assertNull(item.getCustomModel());
        assertEquals(listOf(
                PotionEffectValues.createQuick(EffectType.SPEED, 10, 1)
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                PotionEffectValues.createQuick(EffectType.SLOW, 20, 1)
        ), item.getOnHitTargetEffects());
        ItemCommandSystem arrowSystem = new ItemCommandSystem(true);
        arrowSystem.setCommandsFor(ItemCommandEvent.RIGHT_CLICK_GENERAL, listOf(ItemCommand.createFromLegacy("summon arrow")));
        assertEquals(arrowSystem, item.getCommandSystem());
        assertEquals(ReplacementConditionValues.ConditionOperation.OR, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionValues.createQuick(
                        ReplacementConditionValues.ReplacementCondition.HASITEM,
                        set.getItemReference("simple2"),
                        ReplacementConditionValues.ReplacementOperation.ATMOST,
                        5,
                        set.getItemReference("simple1")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffectValues.createQuick(EffectType.NIGHT_VISION, 1, AttributeModifierValues.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(ExtraItemNbtValues.createQuick(listOf(
                ExtraItemNbtValues.Entry.createQuick(listOf("the"), new ExtraItemNbtValues.Value("gun"))
        )), item.getExtraNbt());
        assertEquals(1.25, item.getAttackRange(), 0.0);
        assertEquals("crazy1", item.getProjectile().getName());
        IndirectGunAmmoValues ammo = (IndirectGunAmmoValues) item.getAmmo();
        assertEquals(CustomItemIngredientValues.createQuick(set.getItemReference("simple1"), 1, null), ammo.getReloadItem());
        assertEquals(15, ammo.getCooldown());
        assertEquals(35, ammo.getStoredAmmo());
        assertEquals(25, ammo.getReloadTime());
        assertNull(ammo.getStartReloadSound());
        assertEquals(CISound.ENTITY_SKELETON_HORSE_HURT, ammo.getEndReloadSound());
        assertEquals(2, item.getAmountPerShot());
    }

    static void testPocketContainer1(CustomPocketContainerValues item, ItemSet set) {
        assertEquals("pocket_container1", item.getName());
        assertEquals(CustomItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("pc1", item.getAlias());
        assertEquals("Pocket Container", item.getDisplayName());
        assertEquals(listOf(
                "You can carry containers around.",
                "Isn't that great?"
        ), item.getLore());
        assertEquals(listOf(
                AttributeModifierValues.createQuick(
                        AttributeModifierValues.Attribute.ATTACK_DAMAGE,
                        AttributeModifierValues.Slot.OFFHAND,
                        AttributeModifierValues.Operation.ADD,
                        5.0
                )
        ), item.getAttributeModifiers());
        assertEquals(listOf(
                EnchantmentValues.createQuick(EnchantmentType.DAMAGE_ARTHROPODS, 2)
        ), item.getDefaultEnchantments());
        assertEquals(listOf(false, false, false, false, false, false), item.getItemFlags());
        if (set.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("test1", item.getTexture().getName());
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/spear_diamond.json", item.getCustomModel());
        } else {
            assertNull(item.getTextureReference());
            assertNull(item.getCustomModel());
        }
        assertEquals(listOf(
                PotionEffectValues.createQuick(EffectType.HEAL, 1, 1)
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                PotionEffectValues.createQuick(EffectType.HARM, 1, 1)
        ), item.getOnHitTargetEffects());
        ItemCommandSystem stoneSystem = new ItemCommandSystem(true);
        stoneSystem.setCommandsFor(ItemCommandEvent.RIGHT_CLICK_GENERAL, listOf(ItemCommand.createFromLegacy("setblock ~ ~ ~ stone")));
        assertEquals(stoneSystem, item.getCommandSystem());
        assertEquals(ReplacementConditionValues.ConditionOperation.NONE, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionValues.createQuick(
                        ReplacementConditionValues.ReplacementCondition.ISBROKEN,
                        set.getItemReference("sword1"),
                        ReplacementConditionValues.ReplacementOperation.ATLEAST,
                        5,
                        set.getItemReference("simple1")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffectValues.createQuick(EffectType.SLOW_DIGGING, 2, AttributeModifierValues.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(ExtraItemNbtValues.createQuick(listOf(
                ExtraItemNbtValues.Entry.createQuick(listOf("rank"), new ExtraItemNbtValues.Value(1))
        )), item.getExtraNbt());
        assertEquals(0.75, item.getAttackRange(), 0.0);
        assertEquals(new HashSet<>(listOf(
                set.getContainerReference("container2")
        )), item.getContainerReferences());
    }

    static void testFood1(CustomFoodValues item, ItemSet set) {
        assertEquals("food1", item.getName());
        assertEquals(CustomItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("f1", item.getAlias());
        assertEquals("Food!!", item.getDisplayName());
        assertEquals(listOf(
                "Food!",
                "Food!!"
        ), item.getLore());
        assertEquals(listOf(
                AttributeModifierValues.createQuick(
                        AttributeModifierValues.Attribute.LUCK,
                        AttributeModifierValues.Slot.OFFHAND,
                        AttributeModifierValues.Operation.ADD_FACTOR,
                        1.0
                )
        ), item.getAttributeModifiers());
        assertEquals(listOf(
                EnchantmentValues.createQuick(EnchantmentType.LOOT_BONUS_BLOCKS, 2)
        ), item.getDefaultEnchantments());
        assertEquals(listOf(
                false, false, true, true, true, true
        ), item.getItemFlags());
        if (set.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("test1", item.getTexture().getName());
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/blue_crossbow.json", item.getCustomModel());
        } else {
            assertNull(item.getTextureReference());
            assertNull(item.getCustomModel());
        }
        assertEquals(listOf(
                PotionEffectValues.createQuick(EffectType.SATURATION, 100, 1)
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                PotionEffectValues.createQuick(EffectType.HUNGER, 100, 3)
        ), item.getOnHitTargetEffects());
        ItemCommandSystem clearSystem = new ItemCommandSystem(true);
        clearSystem.setCommandsFor(ItemCommandEvent.RIGHT_CLICK_GENERAL, listOf(ItemCommand.createFromLegacy("effect @p clear")));
        assertEquals(clearSystem, item.getCommandSystem());
        assertEquals(ReplacementConditionValues.ConditionOperation.AND, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionValues.createQuick(
                        ReplacementConditionValues.ReplacementCondition.MISSINGITEM,
                        set.getItemReference("simple1"),
                        ReplacementConditionValues.ReplacementOperation.EXACTLY,
                        3,
                        set.getItemReference("simple2")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffectValues.createQuick(EffectType.JUMP, 1, AttributeModifierValues.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(ExtraItemNbtValues.createQuick(listOf(
                ExtraItemNbtValues.Entry.createQuick(listOf("food", "is"), new ExtraItemNbtValues.Value("jum jum"))
        )), item.getExtraNbt());
        assertEquals(1.5, item.getAttackRange(), 0.0);
        assertEquals(6, item.getFoodValue());
        assertEquals(listOf(
                PotionEffectValues.createQuick(EffectType.WATER_BREATHING, 300, 1)
        ), item.getEatEffects());
        assertEquals(37, item.getEatTime());
        assertEquals(CISound.ENTITY_WITCH_DRINK, item.getEatSound());
        assertEquals(1.25, item.getSoundVolume(), 0.0);
        assertEquals(0.75, item.getSoundPitch(), 0.0);
        assertEquals(7, item.getSoundPeriod());
        assertEquals(61, item.getMaxStacksize());
    }

    static void testBaseDefault9(CustomItemValues item) {
        // TODO Call testBaseDefault10
    }

    static void testSimpleDefault9(SimpleCustomItemValues item) {
        testBaseDefault9(item);
        // TODO Call testSimpleDefault10
    }

    static void testToolDefault9(CustomToolValues item) {
        testBaseDefault9(item);
        // TODO Call testToolDefault10
    }

    static void testArmorDefault9(CustomArmorValues item) {
        testToolDefault9(item);
        // TODO Call testArmorDefault10
    }

    static void testHoeDefault9(CustomHoeValues item) {
        testToolDefault9(item);
        // TODO Call testHoeDefault10
    }

    static void testShearsDefault9(CustomShearsValues item) {
        testToolDefault9(item);
        // TODO Call testShearsDefault10
    }

    static void testBowDefault9(CustomBowValues item) {
        testToolDefault9(item);
        // TODO Call testBowDefault10
    }

    static void testShieldDefault9(CustomShieldValues item) {
        testToolDefault9(item);
        // TODO Call testShieldDefault10
    }
    static void testWandDefault9(CustomWandValues item) {
        testBaseDefault9(item);
        // TODO Call testWandDefault10
    }

    static void test3dHelmetDefault9(CustomHelmet3dValues item) {
        testArmorDefault9(item);
        // TODO Call test3dHelmetDefault10
    }

    static void testTridentDefault9(CustomTridentValues item) {
        testToolDefault9(item);
        // TODO Call testTridentDefault10
    }
}
