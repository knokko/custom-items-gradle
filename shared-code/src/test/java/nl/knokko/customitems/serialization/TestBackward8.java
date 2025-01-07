package nl.knokko.customitems.serialization;

import nl.knokko.customitems.block.drop.RequiredItems;
import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.container.ContainerRecipe;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.fuel.ContainerFuelEntry;
import nl.knokko.customitems.container.fuel.ContainerFuelRegistry;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.container.slot.display.*;
import nl.knokko.customitems.damage.VDamageSource;
import nl.knokko.customitems.drops.*;
import nl.knokko.customitems.effect.*;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.command.ItemCommand;
import nl.knokko.customitems.item.command.ItemCommandEvent;
import nl.knokko.customitems.item.command.ItemCommandSystem;
import nl.knokko.customitems.item.enchantment.VEnchantmentType;
import nl.knokko.customitems.item.enchantment.LeveledEnchantment;
import nl.knokko.customitems.item.model.DefaultItemModel;
import nl.knokko.customitems.item.model.LegacyCustomItemModel;
import nl.knokko.customitems.itemset.ArmorTextureReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.customitems.recipe.KciShapedRecipe;
import nl.knokko.customitems.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.customitems.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.recipe.result.CopiedResult;
import nl.knokko.customitems.recipe.result.CustomItemResult;
import nl.knokko.customitems.recipe.result.DataVanillaResult;
import nl.knokko.customitems.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.texture.ArmorTexture;
import nl.knokko.customitems.util.Chance;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;

import static nl.knokko.customitems.serialization.TestBackward1.assertNoTexture;
import static nl.knokko.customitems.serialization.TestBackward1.testExportSettings1;
import static nl.knokko.customitems.serialization.TestBackward10.*;
import static nl.knokko.customitems.serialization.TestBackward3.testTextures3;
import static nl.knokko.customitems.serialization.TestBackward6.*;
import static nl.knokko.customitems.serialization.TestBackward7.testContainers7;
import static nl.knokko.customitems.serialization.BackwardHelper.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestBackward8 {

    @Test
    public void testBackwardCompatibility8() {
        for (ItemSet oldSet : loadItemSet("backward8old", false, true)) {
            testExportSettings1(oldSet);
            testTextures3(oldSet, 3, true);
            testArmorTexturesOld8(oldSet, 1, true);
            testItemsOld8(oldSet, 30);
            testRecipesOld8(oldSet, 4);
            testBlockDropsOld8(oldSet, 2);
            testMobDropsOld8(oldSet, 2);
            testProjectileCoversOld6(oldSet, 2, true);
            testProjectilesOld6(oldSet, 1);
            testFuelRegistriesOld8(oldSet, 1);
            testContainersOld8(oldSet, 2);
        }

        for (ItemSet newSet : loadItemSet("backward8new", false, true)) {
            testTexturesNew6(newSet, 1, true);
            testItemsNew8(newSet, 2);
            testRecipesNew6(newSet, 1);
        }
    }

    static String copiedFromServerString() {
        Scanner scanner = new Scanner(
                Objects.requireNonNull(TestBackward8.class.getClassLoader().getResourceAsStream(
                        "nl/knokko/customitems/serialization/copiedFromServer.txt"
                ))
        );
        String result = scanner.next();
        scanner.close();
        return result;
    }

    static void testItemsNew8(ItemSet set, int numItems) {
        testItemsNew6(set, numItems);

        testTridentDefault8((KciTrident) set.items.get("trident_one").get());

        KciTrident item = (KciTrident) set.items.get("trident2").get();
        assertEquals("trident2", item.getName());
        assertEquals(KciItemType.TRIDENT, item.getItemType());
        assertEquals("t2", item.getAlias());
        assertEquals("Second Test Trident", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, false, false, false, false, false, false
        ), item.getItemFlags());
        if (set.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("quick_wand", item.getTexture().getName());
        } else {
            assertNoTexture(item.getTextureReference());
        }
        assertTrue(item.getModel() instanceof DefaultItemModel);
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(0, item.getOnHitTargetEffects().size());
        assertEquals(new ItemCommandSystem(true), item.getCommandSystem());
        assertEquals(ReplacementConditionEntry.ConditionOperation.NONE, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionEntry.createQuick(
                        ReplacementConditionEntry.ReplacementCondition.HASITEM,
                        set.items.getReference( "trident_one"),
                        ReplacementConditionEntry.ReplacementOperation.ATLEAST,
                        1,
                        set.items.getReference("trident_one")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffect.createQuick(VEffectType.REGENERATION, 2, KciAttributeModifier.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(listOf("{\"trident\":\"t2\"}"), item.getExtraNbt());
        assertEquals(1.75f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, (long) item.getMaxDurabilityNew());
        assertEquals(new NoIngredient(), item.getRepairItem());
        assertEquals(1, item.getEntityHitDurabilityLoss());
        assertEquals(2, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getThrowDurabilityLoss());
        assertEquals(1.0, item.getThrowDamageMultiplier(), 0.0);
        assertEquals(1.0, item.getThrowSpeedMultiplier(), 0.0);
        assertTrue(item.getInHandModel() instanceof DefaultItemModel);
        assertTrue(item.getThrowingModel() instanceof DefaultItemModel);
    }

    static void testContainersOld8(ItemSet set, int numContainers) {
        testContainers7(set, numContainers);

        KciContainer container2 = set.containers.get("container2").get();
        ContainerRecipe recipe = container2.getRecipes().stream().findFirst().get();

        OutputTable output = recipe.getOutputs().values().stream().findFirst().get();
        assertEquals(listOf(
                OutputTable.Entry.createQuick(CustomItemResult.createQuick(set.items.getReference("simple1"), 1), 40),
                OutputTable.Entry.createQuick(SimpleVanillaResult.createQuick(VMaterial.DIAMOND, 2), 50)
        ), output.getEntries());

        OutputSlot outputSlot = (OutputSlot) container2.getSlot(0, 0);
        SlotDisplay outputPlaceholder = outputSlot.getPlaceholder();
        assertEquals("Simple Test", outputPlaceholder.getDisplayName());
        assertEquals(2, outputPlaceholder.getAmount());
        assertEquals(CustomDisplayItem.createQuick(set.items.getReference("simple1")), outputPlaceholder.getDisplayItem());
        assertEquals(0, outputPlaceholder.getLore().size());

        InputSlot inputSlot = (InputSlot) container2.getSlot(1, 0);
        SlotDisplay inputPlaceholder = inputSlot.getPlaceholder();
        assertEquals("", inputPlaceholder.getDisplayName());
        assertEquals(listOf(
                "test the lore"
        ), inputPlaceholder.getLore());
        assertEquals(1, inputPlaceholder.getAmount());
        assertEquals(SimpleVanillaDisplayItem.createQuick(VMaterial.ACACIA_FENCE), inputPlaceholder.getDisplayItem());

        FuelSlot fuelSlot = (FuelSlot) container2.getSlot(0, 1);
        SlotDisplay fuelPlaceholder = fuelSlot.getPlaceholder();
        assertEquals("", fuelPlaceholder.getDisplayName());
        assertEquals(0, fuelPlaceholder.getLore().size());
        assertEquals(10, fuelPlaceholder.getAmount());
        assertEquals(DataVanillaDisplayItem.createQuick(VMaterial.WOOL, (byte) 8), fuelPlaceholder.getDisplayItem());
    }

    static void testFuelRegistriesOld8(ItemSet set, int numFuelRegistries) {
        assertEquals(numFuelRegistries, set.fuelRegistries.size());

        ContainerFuelRegistry registry1 = set.fuelRegistries.get("registry1").get();
        assertEquals("registry1", registry1.getName());
        assertEquals(listOf(
                ContainerFuelEntry.createQuick(SimpleVanillaIngredient.createQuick(VMaterial.COAL, 1), 100),
                ContainerFuelEntry.createQuick(CustomItemIngredient.createQuick(set.items.getReference("simple1"), 1), 500)
        ), registry1.getEntries());
    }

    static void testMobDropsOld8(ItemSet set, int numBlockDrops) {
        testMobDropsOld6(set, numBlockDrops);

        Iterator<MobDrop> mobDropIterator = set.mobDrops.iterator();
        testDefaultMobDrop8(mobDropIterator.next());
        testDefaultMobDrop8(mobDropIterator.next());
    }

    static void testBlockDropsOld8(ItemSet set, int numBlockDrops) {
        testBlockDropsOld6(set, numBlockDrops, true);

        Iterator<BlockDrop> blockDropIterator = set.blockDrops.iterator();
        BlockDrop firstBlockDrop = blockDropIterator.next();
        testDefaultBlockDrop8(firstBlockDrop);

        BlockDrop blockDrop = blockDropIterator.next();
        assertEquals(VBlockType.STONE, blockDrop.getBlockType());
        assertEquals(SilkTouchRequirement.OPTIONAL, blockDrop.getSilkTouchRequirement());
        KciDrop drop = blockDrop.getDrop();

        assertFalse(drop.shouldCancelNormalDrops());
        RequiredItems expectedRequiredItems = new RequiredItems(true);
        expectedRequiredItems.setCustomItems(listOf(set.items.getReference("pickaxe1"), set.items.getReference("pickaxe_two")));
        assertEquals(expectedRequiredItems, drop.getRequiredHeldItems());

        OutputTable dropTable = drop.getOutputTable();
        assertEquals(Chance.percentage(35), dropTable.getNothingChance());
        assertEquals(listOf(
                OutputTable.Entry.createQuick(CustomItemResult.createQuick(set.items.getReference("simple1"), (byte) 1), 30),
                OutputTable.Entry.createQuick(SimpleVanillaResult.createQuick(VMaterial.ACACIA_DOOR, 2), 20),
                OutputTable.Entry.createQuick(DataVanillaResult.createQuick(VMaterial.WOOL, 3, 3), 10),
                OutputTable.Entry.createQuick(CopiedResult.createQuick(copiedFromServerString()), 5)
        ), dropTable.getEntries());
    }

    static void testDefaultBlockDrop8(BlockDrop blockDrop) {
        assertEquals(SilkTouchRequirement.FORBIDDEN, blockDrop.getSilkTouchRequirement());
        testDefaultDrop8(blockDrop.getDrop());
        testDefaultBlockDrop10(blockDrop);
    }

    static void testDefaultMobDrop8(MobDrop mobDrop) {
        testDefaultDrop8(mobDrop.getDrop());
        testDefaultMobDrop10(mobDrop);
    }

    static void testDefaultDrop8(KciDrop drop) {
        assertEquals(new RequiredItems(false), drop.getRequiredHeldItems());
        testDefaultDrop10(drop);
    }

    static void testRecipesOld8(ItemSet set, int numRecipes) {
        testRecipesOld6(set, numRecipes);

        assertTrue(set.craftingRecipes.stream().anyMatch(recipe -> recipe.equals(createShapedRecipe3(set))));
    }

    static KciShapedRecipe createShapedRecipe3(ItemSet set) {
        KciIngredient[] ingredients = {
                CustomItemIngredient.createQuick(set.items.getReference("simple1"), 1), new NoIngredient(), new NoIngredient(),
                new NoIngredient(), CustomItemIngredient.createQuick(set.items.getReference("simple2"), 1), new NoIngredient(),
                new NoIngredient(), new NoIngredient(), CustomItemIngredient.createQuick(set.items.getReference("simple3"), 1)
        };
        return KciShapedRecipe.createQuick(ingredients, CopiedResult.createQuick(copiedFromServerString()), false);
    }

    static void testArmorTexturesOld8(ItemSet set, int numArmorTextures, boolean skipPlugin) {
        if (set.getSide() == ItemSet.Side.PLUGIN && skipPlugin) {
            assertEquals(0, set.armorTextures.size());
            return;
        }

        assertEquals(numArmorTextures, set.armorTextures.size());

        ArmorTexture armorTexture1 = set.armorTextures.get("armor_texture1").get();
        assertEquals("armor_texture1", armorTexture1.getName());
        if (set.getSide() == ItemSet.Side.EDITOR) {
            assertImageEqual(loadImage("armor1layer1"), armorTexture1.getLayer1());
            assertImageEqual(loadImage("armor1layer2"), armorTexture1.getLayer2());
        }
    }

    static void testItemsOld8(ItemSet set, int numItems) {
        testItemsOld6(set, numItems);

        testShieldDefault8((KciShield) set.items.get("shield_one").get());
        testWandDefault8((KciWand) set.items.get("wand_one").get());

        testWand2((KciWand) set.items.get("wand2").get(), set);
        // Yeah... naming mistake...
        testSimple4((KciSimpleItem) set.items.get("simple3").get(), set);
        testShovel2((KciTool) set.items.get("shovel2").get(), set);
        testHoe3((KciHoe) set.items.get("hoe3").get(), set);
        testShears3((KciShears) set.items.get("shears3").get(), set);
        test3dHelmet1((Kci3dHelmet) set.items.get("3dhelmet1").get(), set);
        testBow3((KciBow) set.items.get("bow3").get(), set);
        testChestplate2((KciArmor) set.items.get("chestplate2").get(), set);
        testShield2((KciShield) set.items.get("shield2").get(), set);
    }

    static void testWand2(KciWand item, ItemSet itemSet) {
        assertEquals("wand2", item.getName());
        assertEquals(KciItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("wand2", item.getAlias());
        assertEquals("Wand 2", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, false, false, false, false, false, false
        ), item.getItemFlags());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("test1", item.getTexture().getName());
        } else {
            assertNoTexture(item.getTextureReference());
        }
        assertTrue(item.getModel() instanceof DefaultItemModel);
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(0, item.getOnHitTargetEffects().size());
        assertEquals(new ItemCommandSystem(false), item.getCommandSystem());
        assertEquals(ReplacementConditionEntry.ConditionOperation.AND, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionEntry.createQuick(
                        ReplacementConditionEntry.ReplacementCondition.HASITEM,
                        itemSet.items.getReference("simple1"),
                        ReplacementConditionEntry.ReplacementOperation.ATLEAST,
                        1,
                        itemSet.items.getReference("simple2")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffect.createQuick(
                        VEffectType.JUMP, 2, KciAttributeModifier.Slot.OFFHAND
                )
        ), item.getEquippedEffects());
        assertEquals(listOf("{\"test_int\":1}", "{\"parent\":{\"child\":{\"test_string\":\"2\"}}}"), item.getExtraNbt());
        assertEquals(1.5f, item.getAttackRange(), 0f);
        assertEquals("crazy1", item.getProjectile().getName());
        assertNull(item.getCharges());
        assertEquals(1, item.getAmountPerShot());
        assertEquals(40, item.getCooldown());
    }

    static void testSimple4(KciSimpleItem item, ItemSet itemSet) {
        assertEquals("simple3", item.getName());
        assertEquals(KciItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("sim3", item.getAlias());
        assertEquals("Third Simple", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, false, false, false, false, false, false
        ), item.getItemFlags());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("gun1", item.getTexture().getName());
        } else {
            assertNoTexture(item.getTextureReference());
        }
        assertNoModel(item.getModel());
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(0, item.getOnHitTargetEffects().size());
        assertEquals(new ItemCommandSystem(false), item.getCommandSystem());
        assertEquals(ReplacementConditionEntry.ConditionOperation.NONE, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionEntry.createQuick(
                            ReplacementConditionEntry.ReplacementCondition.MISSINGITEM,
                            itemSet.items.getReference("simple2"),
                            ReplacementConditionEntry.ReplacementOperation.EXACTLY,
                            1,
                            itemSet.items.getReference("simple1")
                        )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffect.createQuick(VEffectType.SLOW, 1, KciAttributeModifier.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(listOf("{\"type\":\"simple\"}"), item.getExtraNbt());
        assertEquals(0.5f, item.getAttackRange(), 0f);
        assertEquals(64, item.getMaxStacksize());
    }

    static void testShovel2(KciTool item, ItemSet itemSet) {
        assertEquals("shovel2", item.getName());
        assertEquals(KciItemType.IRON_SHOVEL, item.getItemType());
        assertEquals("shov2", item.getAlias());
        assertEquals("Second Shovel", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, false, false, false, false, false, false
        ), item.getItemFlags());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("gun1", item.getTexture().getName());
        } else {
            assertNoTexture(item.getTextureReference());
        }
        assertNoModel(item.getModel());
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(0, item.getOnHitTargetEffects().size());
        assertEquals(new ItemCommandSystem(false), item.getCommandSystem());
        assertEquals(ReplacementConditionEntry.ConditionOperation.OR, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionEntry.createQuick(
                        ReplacementConditionEntry.ReplacementCondition.ISBROKEN,
                        itemSet.items.getReference( "sword1"),
                        ReplacementConditionEntry.ReplacementOperation.EXACTLY,
                        1,
                        itemSet.items.getReference("simple2")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffect.createQuick(VEffectType.FAST_DIGGING, 1, KciAttributeModifier.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(listOf("{\"type\":\"shovel\"}"), item.getExtraNbt());
        assertEquals(3.5f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, (long) item.getMaxDurabilityNew());
        assertEquals(new NoIngredient(), item.getRepairItem());
        assertEquals(2, item.getEntityHitDurabilityLoss());
        assertEquals(1, item.getBlockBreakDurabilityLoss());
    }

    static void testHoe3(KciHoe item, ItemSet itemSet) {
        assertEquals("hoe3", item.getName());
        assertEquals(KciItemType.IRON_HOE, item.getItemType());
        assertEquals("h3", item.getAlias());
        assertEquals("Third Hoe", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, false, false, false, false, false, false
        ), item.getItemFlags());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("test1", item.getTexture().getName());
        } else {
            assertNoTexture(item.getTextureReference());
        }
        assertNoModel(item.getModel());
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(0, item.getOnHitTargetEffects().size());
        assertEquals(new ItemCommandSystem(false), item.getCommandSystem());
        assertEquals(ReplacementConditionEntry.ConditionOperation.NONE, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionEntry.createQuick(
                        ReplacementConditionEntry.ReplacementCondition.HASITEM,
                        itemSet.items.getReference("simple_three"),
                        ReplacementConditionEntry.ReplacementOperation.ATMOST,
                        10,
                        itemSet.items.getReference("simple2")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffect.createQuick(VEffectType.REGENERATION, 3, KciAttributeModifier.Slot.OFFHAND)
        ), item.getEquippedEffects());
        assertEquals(listOf("{\"int_type\":5}"), item.getExtraNbt());
        assertEquals(0.75f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, (long) item.getMaxDurabilityNew());
        assertEquals(new NoIngredient(), item.getRepairItem());
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getTillDurabilityLoss());
    }

    static void testShears3(KciShears item, ItemSet itemSet) {
        assertEquals("shears3", item.getName());
        assertEquals(KciItemType.SHEARS, item.getItemType());
        assertEquals("sh3", item.getAlias());
        assertEquals("Third Shears", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, false, false, false, false, false, false
        ), item.getItemFlags());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("test1", item.getTexture().getName());
        } else {
            assertNoTexture(item.getTextureReference());
        }
        assertNoModel(item.getModel());
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(0, item.getOnHitTargetEffects().size());
        assertEquals(new ItemCommandSystem(true), item.getCommandSystem());
        assertEquals(ReplacementConditionEntry.ConditionOperation.NONE, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionEntry.createQuick(
                        ReplacementConditionEntry.ReplacementCondition.HASITEM,
                        itemSet.items.getReference("pickaxe1"),
                        ReplacementConditionEntry.ReplacementOperation.EXACTLY,
                        1,
                        itemSet.items.getReference("sword1")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffect.createQuick(VEffectType.INVISIBILITY, 1, KciAttributeModifier.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(listOf("{\"int_type\":9}"), item.getExtraNbt());
        assertEquals(1.25f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, (long) item.getMaxDurabilityNew());
        assertEquals(new NoIngredient(), item.getRepairItem());
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(1, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getShearDurabilityLoss());
    }

    static void test3dHelmet1(Kci3dHelmet item, ItemSet itemSet) {
        assertEquals("3dhelmet1", item.getName());
        assertEquals(KciItemType.IRON_HOE, item.getItemType());
        assertEquals("3d1", item.getAlias());
        assertEquals("A 3D Helmet!", item.getDisplayName());
        assertEquals(listOf(
                "The only custom armor",
                "that works without Optifine"
        ), item.getLore());
        assertEquals(listOf(
                KciAttributeModifier.createQuick(
                        KciAttributeModifier.Attribute.ARMOR,
                        KciAttributeModifier.Slot.HEAD,
                        KciAttributeModifier.Operation.ADD,
                        6.0
                )
        ), item.getAttributeModifiers());
        assertEquals(listOf(
                LeveledEnchantment.createQuick(VEnchantmentType.THORNS, 2)
        ), item.getDefaultEnchantments());
        assertEquals(listOf(
                true, false, true, false, false, false, false, false, false, false
        ), item.getItemFlags());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("gun1", item.getTexture().getName());
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/blue_crossbow.json", ((LegacyCustomItemModel) item.getModel()).getRawModel());
        } else {
            assertNoTexture(item.getTextureReference());
            assertNoModel(item.getModel());
        }
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.INVISIBILITY, 30, 1, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.POISON, 100, 1, Chance.percentage(100))
        ), item.getOnHitTargetEffects());
        ItemCommandSystem summonSheepSystem = new ItemCommandSystem(true);
        summonSheepSystem.setCommandsFor(ItemCommandEvent.RIGHT_CLICK_GENERAL, listOf(ItemCommand.createFromLegacy("summon sheep")));
        assertEquals(summonSheepSystem, item.getCommandSystem());
        assertEquals(ReplacementConditionEntry.ConditionOperation.NONE, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionEntry.createQuick(
                        ReplacementConditionEntry.ReplacementCondition.HASITEM,
                        itemSet.items.getReference( "pickaxe1"),
                        ReplacementConditionEntry.ReplacementOperation.ATLEAST,
                        5,
                        itemSet.items.getReference("simple2")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffect.createQuick(VEffectType.SLOW, 1, KciAttributeModifier.Slot.HEAD)
        ), item.getEquippedEffects());
        assertEquals(listOf("{\"type\":\"3dhelmet\"}"), item.getExtraNbt());
        assertEquals(0.25f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertFalse(item.allowEnchanting());
        assertEquals(123, (long) item.getMaxDurabilityNew());
        assertEquals(new NoIngredient(), item.getRepairItem());
        assertEquals(3, item.getEntityHitDurabilityLoss());
        assertEquals(4, item.getBlockBreakDurabilityLoss());
        for (VDamageSource source : VDamageSource.values()) {
            if (source == VDamageSource.SUFFOCATION) {
                assertEquals(100, item.getDamageResistances().getResistance(source));
            } else {
                assertEquals(0, item.getDamageResistances().getResistance(source));
            }
        }
    }

    static void testBow3(KciBow item, ItemSet itemSet) {
        assertEquals("bow3", item.getName());
        assertEquals(KciItemType.BOW, item.getItemType());
        assertEquals("b3", item.getAlias());
        assertEquals("Third Bow", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, false, false, false, false, false, false
        ), item.getItemFlags());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("bow_one", item.getTexture().getName());
        } else {
            assertNoTexture(item.getTextureReference());
        }
        assertNull(item.getModel());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.NIGHT_VISION, 1000, 1, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffect.createQuick(VEffectType.WITHER, 100, 2, Chance.percentage(100))
        ), item.getOnHitTargetEffects());
        assertEquals(new ItemCommandSystem(true), item.getCommandSystem());
        assertEquals(ReplacementConditionEntry.ConditionOperation.NONE, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionEntry.createQuick(
                        ReplacementConditionEntry.ReplacementCondition.ISBROKEN,
                        itemSet.items.getReference( "sword1"),
                        ReplacementConditionEntry.ReplacementOperation.EXACTLY,
                        1,
                        itemSet.items.getReference("boots_one")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffect.createQuick(VEffectType.SPEED, 2, KciAttributeModifier.Slot.OFFHAND)
        ), item.getEquippedEffects());
        assertEquals(listOf("{\"type\":\"bow\"}"), item.getExtraNbt());
        assertEquals(1.75f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, (long) item.getMaxDurabilityNew());
        assertEquals(new NoIngredient(), item.getRepairItem());
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getShootDurabilityLoss());
        assertEquals(1.0, item.getDamageMultiplier(), 0.0);
        assertEquals(1.0, item.getSpeedMultiplier(), 0.0);
        assertEquals(0, item.getKnockbackStrength());
        assertTrue(item.hasGravity());
    }

    static void testChestplate2(KciArmor item, ItemSet itemSet) {
        assertEquals("chestplate2", item.getName());
        assertEquals(KciItemType.IRON_CHESTPLATE, item.getItemType());
        assertEquals("chess2", item.getAlias());
        assertEquals("Second Chestplate", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, false, false, false, false, false, false
        ), item.getItemFlags());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("gun1", item.getTexture().getName());
        } else {
            assertNoTexture(item.getTextureReference());
        }
        assertNoModel(item.getModel());
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(0, item.getOnHitTargetEffects().size());
        assertEquals(new ItemCommandSystem(false), item.getCommandSystem());
        assertEquals(ReplacementConditionEntry.ConditionOperation.NONE, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionEntry.createQuick(
                        ReplacementConditionEntry.ReplacementCondition.MISSINGITEM,
                        itemSet.items.getReference("simple1"),
                        ReplacementConditionEntry.ReplacementOperation.EXACTLY,
                        7,
                        itemSet.items.getReference("simple2")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffect.createQuick(VEffectType.DAMAGE_RESISTANCE, 1, KciAttributeModifier.Slot.CHEST)
        ), item.getEquippedEffects());
        assertEquals(listOf("{\"type\":\"chest\"}"), item.getExtraNbt());
        assertEquals(4.25f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, (long) item.getMaxDurabilityNew());
        assertEquals(new NoIngredient(), item.getRepairItem());
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        for (VDamageSource source : VDamageSource.values()) {
            assertEquals(0, item.getDamageResistances().getResistance(source));
        }
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("armor_texture1", item.getArmorTexture().getName());
        } else {
            assertNoArmorTexture(item.getArmorTextureReference());
        }
    }

    static void assertNoArmorTexture(ArmorTextureReference armorTextureReference) {
        if (armorTextureReference != null) {
            assertNull(armorTextureReference.get().getLayer1());
            assertNull(armorTextureReference.get().getLayer2());
        }
    }

    static void testShield2(KciShield item, ItemSet itemSet) {
        assertEquals("shield2", item.getName());
        assertEquals(KciItemType.SHIELD, item.getItemType());
        assertEquals("s2", item.getAlias());
        assertEquals("Second Shield", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, false, false, false, false, false, false
        ), item.getItemFlags());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("test1", item.getTexture().getName());
        } else {
            assertNoTexture(item.getTextureReference());
        }
        assertNoModel(item.getModel());
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(0, item.getOnHitTargetEffects().size());
        assertEquals(new ItemCommandSystem(true), item.getCommandSystem());
        assertEquals(ReplacementConditionEntry.ConditionOperation.AND, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionEntry.createQuick(
                        ReplacementConditionEntry.ReplacementCondition.ISBROKEN,
                        itemSet.items.getReference("pickaxe1"),
                        ReplacementConditionEntry.ReplacementOperation.ATMOST,
                        1,
                        itemSet.items.getReference("bow_one")
                )
        ), item.getReplacementConditions());
        assertEquals(0, item.getEquippedEffects().size());
        assertEquals(listOf("{\"test\":-5}"), item.getExtraNbt());
        assertEquals(1.5f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, (long) item.getMaxDurabilityNew());
        assertEquals(new NoIngredient(), item.getRepairItem());
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        assertEquals(4.0, item.getThresholdDamage(), 0.0);
        assertNoModel(item.getBlockingModel());
    }

    static void testBaseDefault8(KciItem item) {
        // Wands don't have empty string as default alias due to issue #124
        if (!(item instanceof KciWand)) {
            assertEquals("", item.getAlias());
        }
        assertEquals(0, item.getReplacementConditions().size());
        assertEquals(0, item.getEquippedEffects().size());
        assertEquals(0, item.getExtraNbt().size());
        assertEquals(1f, item.getAttackRange(), 0f);
        TestBackward9.testBaseDefault9(item);
    }

    static void testSimpleDefault8(KciSimpleItem item) {
        testBaseDefault8(item);
        TestBackward9.testSimpleDefault9(item);
    }

    static void testToolDefault8(KciTool item) {
        testBaseDefault8(item);
        TestBackward9.testToolDefault9(item);
    }

    static void testArmorDefault8(KciArmor item) {
        testToolDefault8(item);
        TestBackward9.testArmorDefault9(item);
    }

    static void testHoeDefault8(KciHoe item) {
        testToolDefault8(item);
        TestBackward9.testHoeDefault9(item);
    }

    static void testShearsDefault8(KciShears item) {
        testToolDefault8(item);
        TestBackward9.testShearsDefault9(item);
    }

    static void testBowDefault8(KciBow item) {
        testToolDefault8(item);
        TestBackward9.testBowDefault9(item);
    }

    static void testShieldDefault8(KciShield item) {
        testToolDefault8(item);
        TestBackward9.testShieldDefault9(item);
    }

    static void testWandDefault8(KciWand item) {
        testBaseDefault8(item);
        TestBackward9.testWandDefault9(item);
    }

    static void testTridentDefault8(KciTrident item) {
        testToolDefault8(item);
        TestBackward9.testTridentDefault9(item);
    }
}
