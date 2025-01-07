package nl.knokko.customitems.serialization;

import nl.knokko.customitems.block.KciBlock;
import nl.knokko.customitems.block.drop.CustomBlockDrop;
import nl.knokko.customitems.block.drop.RequiredItems;
import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.block.model.BlockModel;
import nl.knokko.customitems.block.model.CustomBlockModel;
import nl.knokko.customitems.block.model.SidedBlockModel;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.slot.StorageSlot;
import nl.knokko.customitems.container.slot.display.SimpleVanillaDisplayItem;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.effect.*;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.command.ItemCommand;
import nl.knokko.customitems.item.command.ItemCommandEvent;
import nl.knokko.customitems.item.command.ItemCommandSystem;
import nl.knokko.customitems.item.enchantment.VEnchantmentType;
import nl.knokko.customitems.item.enchantment.LeveledEnchantment;
import nl.knokko.customitems.item.gun.IndirectGunAmmo;
import nl.knokko.customitems.item.model.GeyserCustomModel;
import nl.knokko.customitems.item.model.LegacyCustomItemModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.KciProjectile;
import nl.knokko.customitems.projectile.effect.*;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.customitems.recipe.KciShapelessRecipe;
import nl.knokko.customitems.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.recipe.ingredient.constraint.IngredientConstraints;
import nl.knokko.customitems.recipe.result.CustomItemResult;
import nl.knokko.customitems.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.sound.KciSound;
import nl.knokko.customitems.sound.VSoundType;
import nl.knokko.customitems.texture.BowTextureEntry;
import nl.knokko.customitems.texture.CrossbowTexture;
import nl.knokko.customitems.util.Chance;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static nl.knokko.customitems.serialization.TestBackward1.assertNoTexture;
import static nl.knokko.customitems.serialization.TestBackward1.testExportSettings1;
import static nl.knokko.customitems.serialization.TestBackward10.*;
import static nl.knokko.customitems.serialization.TestBackward3.testTextures3;
import static nl.knokko.customitems.serialization.TestBackward6.*;
import static nl.knokko.customitems.serialization.TestBackward8.*;
import static nl.knokko.customitems.serialization.BackwardHelper.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestBackward9 {

    @Test
    public void testBackwardCompatibility9() {
        // Due to a stupid oversight, I introduced an extra encoding between Editor 9.0 and Editor 9.12
        for (ItemSet[] oldPair : new ItemSet[][] {
                loadItemSet("backward9old", false, true),
                loadItemSet("backward9_12old", false, true)
        }) {
            for (ItemSet old9 : oldPair) {
                testExportSettings1(old9);
                testTextures3(old9, 3, true);
                testArmorTexturesOld8(old9, 1, true);
                testItemsOld9(old9, 33);
                testRecipesOld9(old9, 5);
                testBlockDropsOld8(old9, 2);
                testMobDropsOld8(old9, 2);
                testProjectileCoversOld6(old9, 2, true);
                testProjectilesOld9(old9, 2);
                testFuelRegistriesOld8(old9, 1);
                testContainersOld9(old9, 3);
            }
        }

        for (ItemSet new9 : loadItemSet("backward9new", false, true)) {
            testTexturesNew9(new9, 2, true);
            testItemsNew9(new9, 4);
            testRecipesNew6(new9, 1);
            testBlocksNew9(new9, 1);
        }
    }

    static void testBlocksNew9(ItemSet set, int numBlocks) {
        assertEquals(numBlocks, set.blocks.size());

        KciBlock block1 = set.blocks.get(1).get();
        assertEquals("block1", block1.getName());
        assertEquals(1, block1.getDrops().size());

        CustomBlockDrop drop = block1.getDrops().iterator().next();
        assertEquals(OutputTable.createQuick(listOf(
                OutputTable.Entry.createQuick(SimpleVanillaResult.createQuick(VMaterial.COBBLESTONE, 2), 50)
        )), drop.getDrop().getOutputTable());
        assertEquals(SilkTouchRequirement.FORBIDDEN, drop.getSilkTouchRequirement());

        RequiredItems requiredItems = drop.getDrop().getRequiredHeldItems();
        assertTrue(requiredItems.isEnabled());
        assertFalse(requiredItems.isInverted());
        assertEquals(listOf(
                set.items.getReference("trident_one")
        ), requiredItems.getCustomItems());
        assertEquals(listOf(
                RequiredItems.VanillaEntry.createQuick(VMaterial.STONE_PICKAXE, false)
        ), requiredItems.getVanillaItems());

        if (set.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("quick_wand", block1.getModel().getPrimaryTexture().get().getName());
        } else {
            assertNoBlockModel(block1.getModel());
        }
    }

    static void assertNoBlockModel(BlockModel model) {
        if (model != null) assertNoTexture(model.getPrimaryTexture());
        if (model instanceof SidedBlockModel) {
            for (SidedBlockModel.TexturePair pair : ((SidedBlockModel) model).getTexturePairs()) {
                assertNoTexture(pair.texture);
            }
        }
        if (model instanceof CustomBlockModel) {
            CustomBlockModel customModel = (CustomBlockModel) model;
            assertNoModel(customModel.getItemModel());
            assertNoGeyserModel(customModel.getGeyserModel());
        }
    }

    static void assertNoGeyserModel(GeyserCustomModel model) {
        if (model == null) return;
        assertNull(model.modelFile);
        assertNull(model.attachableFile);
        assertNull(model.animationFile);
    }

    static void testTexturesNew9(ItemSet set, int numTextures, boolean skipPlugin) {
        if (set.getSide() == ItemSet.Side.PLUGIN && skipPlugin) {
            assertEquals(0, set.textures.size());
            return;
        }

        testTexturesNew6(set, numTextures, skipPlugin);

        CrossbowTexture crossbowTextures = (CrossbowTexture) set.textures.get("crossbow_texture").get();
        assertEquals("crossbow_texture", crossbowTextures.getName());
        if (set.getSide() == ItemSet.Side.EDITOR) {
            assertImageEqual(loadImage("gun1"), crossbowTextures.getImage());
            assertImageEqual(loadImage("test1"), crossbowTextures.getArrowImage());
            assertImageEqual(loadImage("test1"), crossbowTextures.getFireworkImage());
        }

        assertEquals(3, crossbowTextures.getPullTextures().size());
        BowTextureEntry pull1 = crossbowTextures.getPullTextures().get(0);
        assertEquals(0.0, pull1.getPull(), 0.0);
        if (set.getSide() == ItemSet.Side.EDITOR) assertImageEqual(loadImage("gun1"), pull1.getImage());
        BowTextureEntry pull2 = crossbowTextures.getPullTextures().get(1);
        assertEquals(0.5, pull2.getPull(), 0.0);
        if (set.getSide() == ItemSet.Side.EDITOR) assertImageEqual(loadImage("gun1"), pull2.getImage());
        BowTextureEntry pull3 = crossbowTextures.getPullTextures().get(2);
        assertEquals(0.75, pull3.getPull(), 0.0);
        if (set.getSide() == ItemSet.Side.EDITOR) assertImageEqual(loadImage("test1"), pull3.getImage());
    }

    static void testItemsNew9(ItemSet set, int numItems) {
        testItemsNew8(set, numItems);

        testTridentDefault9((KciTrident) set.items.get("trident2").get());

        testCrossbow1((KciCrossbow) set.items.get("crossbow1").get(), set);
        testBlockItem1((KciBlockItem) set.items.get("block_item1").get(), set);
    }

    static void testBlockItem1(KciBlockItem item, ItemSet set) {
        assertEquals("block_item1", item.getName());
        assertEquals(KciItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("bi1", item.getAlias());
        assertEquals("Block 1", item.getDisplayName());
        assertEquals(listOf(
                "This is not an actual block",
                "Just the item that places it!"
        ), item.getLore());
        assertEquals(listOf(
                KciAttributeModifier.createQuick(
                        KciAttributeModifier.Attribute.ATTACK_SPEED,
                        KciAttributeModifier.Slot.MAINHAND,
                        KciAttributeModifier.Operation.ADD,
                        5.0
                )
        ), item.getAttributeModifiers());
        assertEquals(listOf(
                LeveledEnchantment.createQuick(VEnchantmentType.DURABILITY, 2)
        ), item.getDefaultEnchantments());
        assertEquals(listOf(
                false, false, true, true, false, false, false, false, false, false
        ), item.getItemFlags());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.REGENERATION, 100, 1, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.POISON, 100, 2, Chance.percentage(100))
        ), item.getOnHitTargetEffects());
        ItemCommandSystem killAllSystem = new ItemCommandSystem(true);
        killAllSystem.setCommandsFor(ItemCommandEvent.RIGHT_CLICK_GENERAL, listOf(ItemCommand.createFromLegacy("kill @a")));
        assertEquals(killAllSystem, item.getCommandSystem());
        assertEquals(ReplacementConditionEntry.ConditionOperation.NONE, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionEntry.createQuick(
                        ReplacementConditionEntry.ReplacementCondition.ISBROKEN,
                        set.items.getReference("trident2"),
                        ReplacementConditionEntry.ReplacementOperation.EXACTLY,
                        1,
                        set.items.getReference("crossbow1")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffect.createQuick(VEffectType.FAST_DIGGING, 3, KciAttributeModifier.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(listOf("{\"block\":1}"), item.getExtraNbt());
        assertEquals(0.25, item.getAttackRange(), 0.0);
        assertEquals("block1", item.getBlock().getName());
        assertEquals(15, item.getMaxStacksize());
    }

    static void testCrossbow1(KciCrossbow item, ItemSet itemSet) {
        assertEquals("crossbow1", item.getName());
        assertEquals(KciItemType.CROSSBOW, item.getItemType());
        assertEquals("cb1", item.getAlias());
        assertEquals("Test Crossbow", item.getDisplayName());
        assertEquals(listOf(
                "We finally have crossbows"
        ), item.getLore());
        assertEquals(listOf(
                KciAttributeModifier.createQuick(
                        KciAttributeModifier.Attribute.MOVEMENT_SPEED,
                        KciAttributeModifier.Slot.OFFHAND,
                        KciAttributeModifier.Operation.MULTIPLY,
                        0.8
                )
        ), item.getAttributeModifiers());
        assertEquals(listOf(
                LeveledEnchantment.createQuick(VEnchantmentType.PIERCING, 2)
        ), item.getDefaultEnchantments());
        assertEquals(listOf(
                true, true, true, true, true, true, false, false, false, false
        ), item.getItemFlags());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("crossbow_texture", item.getTexture().getName());
        } else {
            assertNoTexture(item.getTextureReference());
        }
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.SPEED, 20, 1, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.WITHER, 50, 2, Chance.percentage(100))
        ), item.getOnHitTargetEffects());
        ItemCommandSystem nightVisionSystem = new ItemCommandSystem(true);
        nightVisionSystem.setCommandsFor(ItemCommandEvent.RIGHT_CLICK_GENERAL, listOf(ItemCommand.createFromLegacy(
                "effect @p night_vision 5"
        )));
        assertEquals(nightVisionSystem, item.getCommandSystem());
        assertEquals(ReplacementConditionEntry.ConditionOperation.OR, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionEntry.createQuick(
                        ReplacementConditionEntry.ReplacementCondition.HASITEM,
                        itemSet.items.getReference("trident2"),
                        ReplacementConditionEntry.ReplacementOperation.EXACTLY,
                        1,
                        itemSet.items.getReference("trident_one")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffect.createQuick(VEffectType.INVISIBILITY, 1, KciAttributeModifier.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(listOf("{\"cross\":\"bow\"}"), item.getExtraNbt());
        assertEquals(0.5, item.getAttackRange(), 0.0);
        assertFalse(item.allowEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(567, (long) item.getMaxDurabilityNew());
        assertEquals(SimpleVanillaIngredient.createQuick(
                VMaterial.WHITE_WOOL, 2, SimpleVanillaResult.createQuick(VMaterial.BLACK_WOOL, 5),
                new IngredientConstraints(true)
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

        KciContainer container3 = set.containers.get("container3").get();
        assertEquals(StorageSlot.createQuick(SlotDisplay.createQuick(
                SimpleVanillaDisplayItem.createQuick(VMaterial.IRON_BARDING), "safe storage", new ArrayList<>(), 1
        )), container3.getSlot(0, 0));
        for (int x = 1; x < 9; x++) {
            assertEquals(StorageSlot.createQuick(null), container3.getSlot(x, 0));
        }
    }

    static void testProjectilesOld9(ItemSet set, int numProjectiles) {
        testProjectilesOld6(set, numProjectiles);

        KciProjectile crazy2 = set.projectiles.get("crazy2").get();
        assertEquals(1, crazy2.getInFlightEffects().size());
        ProjectileEffects inFlightEffects = crazy2.getInFlightEffects().iterator().next();
        assertEquals(listOf(
                PEPushOrPull.createQuick(0.5f, 1.5f),
                PEShowFireworks.createQuick(listOf(
                        PEShowFireworks.EffectValues.createQuick(true, false, PEShowFireworks.EffectType.CREEPER, listOf(
                                new Color(200, 100, 150)
                        ), listOf(
                                new Color(0, 50, 150)
                        ))
                ))
        ), inFlightEffects.getEffects());

        assertEquals(listOf(
                PEPlaySound.createQuick(KciSound.createQuick(VSoundType.BLOCK_ANVIL_HIT, 1.25f, 1.75f)),
                PEPotionAura.createQuick(3f, listOf(
                        KciPotionEffect.createQuick(VEffectType.FAST_DIGGING, 100, 2)
                ))
        ), crazy2.getImpactEffects());

        assertEquals(1f, crazy2.getLaunchKnockback(), 0.0);
        assertEquals(2f, crazy2.getImpactKnockback(), 0.0);

        assertEquals(listOf(
                KciPotionEffect.createQuick(VEffectType.INCREASE_DAMAGE, 100, 2)
        ), crazy2.getImpactPotionEffects());
    }

    static void testRecipesOld9(ItemSet set, int numRecipes) {
        testRecipesOld8(set, numRecipes);

        assertTrue(set.craftingRecipes.stream().anyMatch(recipe -> recipe.equals(createShapelessRecipe2(set))));
    }

    static KciShapelessRecipe createShapelessRecipe2(ItemSet set) {
        List<KciIngredient> ingredients = listOf(
                CustomItemIngredient.createQuick(
                        set.items.getReference("simple1"), 3,
                        CustomItemResult.createQuick(
                                set.items.getReference("simple2"), 2
                        ), new IngredientConstraints(true)
                )
        );
        return KciShapelessRecipe.createQuick(ingredients, SimpleVanillaResult.createQuick(VMaterial.CHORUS_PLANT, 8));
    }

    static void testItemsOld9(ItemSet set, int numItems) {
        testItemsOld8(set, numItems);

        testWandDefault9((KciWand) set.items.get("wand2").get());
        testSimpleDefault9((KciSimpleItem) set.items.get("simple3").get());
        testToolDefault9((KciTool) set.items.get("shovel2").get());
        testHoeDefault9((KciHoe) set.items.get("hoe3").get());
        testShearsDefault9((KciShears) set.items.get("shears3").get());
        test3dHelmetDefault9((Kci3dHelmet) set.items.get("3dhelmet1").get());
        testBowDefault9((KciBow) set.items.get("bow3").get());
        testArmorDefault9((KciArmor) set.items.get("chestplate2").get());
        testShieldDefault9((KciShield) set.items.get("shield2").get());

        testGun1((KciGun) set.items.get("gun1").get(), set);
        testPocketContainer1((KciPocketContainer) set.items.get("pocket_container1").get(), set);
        testFood1((KciFood) set.items.get("food1").get(), set);
    }

    static void testGun1(KciGun item, ItemSet set) {
        assertEquals("gun1", item.getName());
        assertEquals(KciItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("g1", item.getAlias());
        assertEquals("The first gun", item.getDisplayName());
        assertEquals(listOf(
                "It's like a wand,",
                "but requires ammunition."
        ), item.getLore());
        assertEquals(listOf(
                KciAttributeModifier.createQuick(
                        KciAttributeModifier.Attribute.KNOCKBACK_RESISTANCE,
                        KciAttributeModifier.Slot.OFFHAND,
                        KciAttributeModifier.Operation.ADD,
                        0.4
                )
        ), item.getAttributeModifiers());
        assertEquals(listOf(
                LeveledEnchantment.createQuick(VEnchantmentType.SILK_TOUCH, 1)
        ), item.getDefaultEnchantments());
        assertEquals(listOf(
                false, true, true, false, false, false, false, false, false, false
        ), item.getItemFlags());
        if (set.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("gun1", item.getTexture().getName());
        } else {
            assertNoTexture(item.getTextureReference());
        }
        TestBackward6.assertNoModel(item.getModel());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.SPEED, 10, 1, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.SLOW, 20, 1, Chance.percentage(100))
        ), item.getOnHitTargetEffects());
        ItemCommandSystem arrowSystem = new ItemCommandSystem(true);
        arrowSystem.setCommandsFor(ItemCommandEvent.RIGHT_CLICK_GENERAL, listOf(ItemCommand.createFromLegacy("summon arrow")));
        assertEquals(arrowSystem, item.getCommandSystem());
        assertEquals(ReplacementConditionEntry.ConditionOperation.OR, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionEntry.createQuick(
                        ReplacementConditionEntry.ReplacementCondition.HASITEM,
                        set.items.getReference("simple2"),
                        ReplacementConditionEntry.ReplacementOperation.ATMOST,
                        5,
                        set.items.getReference("simple1")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffect.createQuick(VEffectType.NIGHT_VISION, 1, KciAttributeModifier.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(listOf("{\"the\":\"gun\"}"), item.getExtraNbt());
        assertEquals(1.25, item.getAttackRange(), 0.0);
        assertEquals("crazy1", item.getProjectile().getName());
        IndirectGunAmmo ammo = (IndirectGunAmmo) item.getAmmo();
        assertEquals(CustomItemIngredient.createQuick(set.items.getReference("simple1"), 1), ammo.getReloadItem());
        assertEquals(15, ammo.getCooldown());
        assertEquals(35, ammo.getStoredAmmo());
        assertEquals(25, ammo.getReloadTime());
        assertNull(ammo.getStartReloadSound());
        assertEquals(KciSound.createQuick(VSoundType.ENTITY_SKELETON_HORSE_HURT, 1f, 1f), ammo.getEndReloadSound());
        assertEquals(2, item.getAmountPerShot());
    }

    static void testPocketContainer1(KciPocketContainer item, ItemSet set) {
        assertEquals("pocket_container1", item.getName());
        assertEquals(KciItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("pc1", item.getAlias());
        assertEquals("Pocket Container", item.getDisplayName());
        assertEquals(listOf(
                "You can carry containers around.",
                "Isn't that great?"
        ), item.getLore());
        assertEquals(listOf(
                KciAttributeModifier.createQuick(
                        KciAttributeModifier.Attribute.ATTACK_DAMAGE,
                        KciAttributeModifier.Slot.OFFHAND,
                        KciAttributeModifier.Operation.ADD,
                        5.0
                )
        ), item.getAttributeModifiers());
        assertEquals(listOf(
                LeveledEnchantment.createQuick(VEnchantmentType.DAMAGE_ARTHROPODS, 2)
        ), item.getDefaultEnchantments());
        assertEquals(listOf(false, false, false, false, false, false, false, false, false, false), item.getItemFlags());
        if (set.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("test1", item.getTexture().getName());
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/spear_diamond.json", ((LegacyCustomItemModel) item.getModel()).getRawModel());
        } else {
            assertNoTexture(item.getTextureReference());
            assertNoModel(item.getModel());
        }
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.HEAL, 1, 1, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.HARM, 1, 1, Chance.percentage(100))
        ), item.getOnHitTargetEffects());
        ItemCommandSystem stoneSystem = new ItemCommandSystem(true);
        stoneSystem.setCommandsFor(ItemCommandEvent.RIGHT_CLICK_GENERAL, listOf(ItemCommand.createFromLegacy("setblock ~ ~ ~ stone")));
        assertEquals(stoneSystem, item.getCommandSystem());
        assertEquals(ReplacementConditionEntry.ConditionOperation.NONE, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionEntry.createQuick(
                        ReplacementConditionEntry.ReplacementCondition.ISBROKEN,
                        set.items.getReference("sword1"),
                        ReplacementConditionEntry.ReplacementOperation.ATLEAST,
                        5,
                        set.items.getReference("simple1")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffect.createQuick(VEffectType.SLOW_DIGGING, 2, KciAttributeModifier.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(listOf("{\"rank\":1}"), item.getExtraNbt());
        assertEquals(0.75, item.getAttackRange(), 0.0);
        assertEquals(new HashSet<>(listOf(
                set.containers.getReference("container2")
        )), item.getContainerReferences());
    }

    static void testFood1(KciFood item, ItemSet set) {
        assertEquals("food1", item.getName());
        assertEquals(KciItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("f1", item.getAlias());
        assertEquals("Food!!", item.getDisplayName());
        assertEquals(listOf(
                "Food!",
                "Food!!"
        ), item.getLore());
        assertEquals(listOf(
                KciAttributeModifier.createQuick(
                        KciAttributeModifier.Attribute.LUCK,
                        KciAttributeModifier.Slot.OFFHAND,
                        KciAttributeModifier.Operation.ADD_FACTOR,
                        1.0
                )
        ), item.getAttributeModifiers());
        assertEquals(listOf(
                LeveledEnchantment.createQuick(VEnchantmentType.LOOT_BONUS_BLOCKS, 2)
        ), item.getDefaultEnchantments());
        assertEquals(listOf(
                false, false, true, true, true, true, false, false, false, false
        ), item.getItemFlags());
        if (set.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("test1", item.getTexture().getName());
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/blue_crossbow.json", ((LegacyCustomItemModel) item.getModel()).getRawModel());
        } else {
            assertNoTexture(item.getTextureReference());
            assertNoModel(item.getModel());
        }
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.SATURATION, 100, 1, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.HUNGER, 100, 3, Chance.percentage(100))
        ), item.getOnHitTargetEffects());
        ItemCommandSystem clearSystem = new ItemCommandSystem(true);
        clearSystem.setCommandsFor(ItemCommandEvent.RIGHT_CLICK_GENERAL, listOf(ItemCommand.createFromLegacy("effect @p clear")));
        assertEquals(clearSystem, item.getCommandSystem());
        assertEquals(ReplacementConditionEntry.ConditionOperation.AND, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionEntry.createQuick(
                        ReplacementConditionEntry.ReplacementCondition.MISSINGITEM,
                        set.items.getReference("simple1"),
                        ReplacementConditionEntry.ReplacementOperation.EXACTLY,
                        3,
                        set.items.getReference("simple2")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffect.createQuick(VEffectType.JUMP, 1, KciAttributeModifier.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(listOf("{\"food\":{\"is\":\"jum jum\"}}"), item.getExtraNbt());
        assertEquals(1.5, item.getAttackRange(), 0.0);
        assertEquals(6, item.getFoodValue());
        assertEquals(listOf(
                KciPotionEffect.createQuick(VEffectType.WATER_BREATHING, 300, 1)
        ), item.getEatEffects());
        assertEquals(37, item.getEatTime());
        assertEquals(VSoundType.ENTITY_WITCH_DRINK, item.getEatSound().getVanillaSound());
        assertEquals(1.25, item.getEatSound().getVolume(), 0.0);
        assertEquals(0.75, item.getEatSound().getPitch(), 0.0);
        assertEquals(7, item.getSoundPeriod());
        assertEquals(61, item.getMaxStacksize());
    }

    static void testBaseDefault9(KciItem item) {
        testBaseDefault10(item);
    }

    static void testSimpleDefault9(KciSimpleItem item) {
        testBaseDefault9(item);
        testSimpleDefault10(item);
    }

    static void testToolDefault9(KciTool item) {
        testBaseDefault9(item);
        testToolDefault10(item);
    }

    static void testArmorDefault9(KciArmor item) {
        testToolDefault9(item);
        testArmorDefault10(item);
    }

    static void testHoeDefault9(KciHoe item) {
        testToolDefault9(item);
        testHoeDefault10(item);
    }

    static void testShearsDefault9(KciShears item) {
        testToolDefault9(item);
        testShearsDefault10(item);
    }

    static void testBowDefault9(KciBow item) {
        testToolDefault9(item);
        testBowDefault10(item);
    }

    static void testShieldDefault9(KciShield item) {
        testToolDefault9(item);
        testShieldDefault10(item);
    }
    static void testWandDefault9(KciWand item) {
        testBaseDefault9(item);
        testWandDefault10(item);
    }

    static void test3dHelmetDefault9(Kci3dHelmet item) {
        testArmorDefault9(item);
        test3dHelmetDefault10(item);
    }

    static void testTridentDefault9(KciTrident item) {
        testToolDefault9(item);
        testTridentDefault10(item);
    }
}
