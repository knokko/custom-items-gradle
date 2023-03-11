package nl.knokko.customitems.serialization;

import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.container.ContainerRecipeValues;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.fuel.FuelEntryValues;
import nl.knokko.customitems.container.fuel.FuelRegistryValues;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.container.slot.display.*;
import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.drops.*;
import nl.knokko.customitems.effect.*;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.command.ItemCommand;
import nl.knokko.customitems.item.command.ItemCommandEvent;
import nl.knokko.customitems.item.command.ItemCommandSystem;
import nl.knokko.customitems.item.enchantment.EnchantmentType;
import nl.knokko.customitems.item.enchantment.EnchantmentValues;
import nl.knokko.customitems.item.model.DefaultItemModel;
import nl.knokko.customitems.item.model.LegacyCustomItemModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.customitems.recipe.ingredient.CustomItemIngredientValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredientValues;
import nl.knokko.customitems.recipe.result.CopiedResultValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.customitems.recipe.result.DataVanillaResultValues;
import nl.knokko.customitems.recipe.result.SimpleVanillaResultValues;
import nl.knokko.customitems.texture.ArmorTextureValues;
import nl.knokko.customitems.util.Chance;
import org.junit.Test;

import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;

import static nl.knokko.customitems.serialization.Backward10.*;
import static nl.knokko.customitems.serialization.Backward3.testTextures3;
import static nl.knokko.customitems.serialization.Backward6.*;
import static nl.knokko.customitems.serialization.Backward7.testContainers7;
import static nl.knokko.customitems.serialization.BackwardHelper.*;
import static org.junit.Assert.*;

public class Backward8 {

    @Test
    public void testBackwardCompatibility8() {
        for (ItemSet oldSet : loadItemSet("backward8old")) {
            testTextures3(oldSet, 3);
            testArmorTexturesOld8(oldSet, 1);
            testItemsOld8(oldSet, 30);
            testRecipesOld8(oldSet, 4);
            testBlockDropsOld8(oldSet, 2);
            testMobDropsOld8(oldSet, 2);
            testProjectileCoversOld6(oldSet, 2);
            testProjectilesOld6(oldSet, 1);
            testFuelRegistriesOld8(oldSet, 1);
            testContainersOld8(oldSet, 2);
        }

        for (ItemSet newSet : loadItemSet("backward8new")) {
            testTexturesNew6(newSet, 1);
            testItemsNew8(newSet, 2);
            testRecipesNew6(newSet, 1);
        }
    }

    static String copiedFromServerString() {
        Scanner scanner = new Scanner(
                Objects.requireNonNull(Backward8.class.getClassLoader().getResourceAsStream(
                        "nl/knokko/customitems/serialization/copiedFromServer.txt"
                ))
        );
        String result = scanner.next();
        scanner.close();
        return result;
    }

    static void testItemsNew8(ItemSet set, int numItems) {
        testItemsNew6(set, numItems);

        testTridentDefault8((CustomTridentValues) set.getItem("trident_one").get());

        CustomTridentValues item = (CustomTridentValues) set.getItem("trident2").get();
        assertEquals("trident2", item.getName());
        assertEquals(CustomItemType.TRIDENT, item.getItemType());
        assertEquals("t2", item.getAlias());
        assertEquals("Second Test Trident", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, false, false
        ), item.getItemFlags());
        if (set.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("quick_wand", item.getTexture().getName());
        } else {
            assertNull(item.getTextureReference());
        }
        assertTrue(item.getModel() instanceof DefaultItemModel);
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(0, item.getOnHitTargetEffects().size());
        assertEquals(new ItemCommandSystem(true), item.getCommandSystem());
        assertEquals(ReplacementConditionValues.ConditionOperation.NONE, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionValues.createQuick(
                        ReplacementConditionValues.ReplacementCondition.HASITEM,
                        set.getItemReference( "trident_one"),
                        ReplacementConditionValues.ReplacementOperation.ATLEAST,
                        1,
                        set.getItemReference("trident_one")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffectValues.createQuick(EffectType.REGENERATION, 2, AttributeModifierValues.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(ExtraItemNbtValues.createQuick(listOf(
                ExtraItemNbtValues.Entry.createQuick(listOf("trident"), new ExtraItemNbtValues.Value("t2"))
        )), item.getExtraNbt());
        assertEquals(1.75f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, (long) item.getMaxDurabilityNew());
        assertEquals(new NoIngredientValues(), item.getRepairItem());
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

        CustomContainerValues container2 = set.getContainer("container2").get();
        ContainerRecipeValues recipe = container2.getRecipes().stream().findFirst().get();

        OutputTableValues output = recipe.getOutputs().values().stream().findFirst().get();
        assertEquals(listOf(
                OutputTableValues.Entry.createQuick(CustomItemResultValues.createQuick(set.getItemReference("simple1"), 1), 40),
                OutputTableValues.Entry.createQuick(SimpleVanillaResultValues.createQuick(CIMaterial.DIAMOND, 2), 50)
        ), output.getEntries());

        OutputSlotValues outputSlot = (OutputSlotValues) container2.getSlot(0, 0);
        SlotDisplayValues outputPlaceholder = outputSlot.getPlaceholder();
        assertEquals("Simple Test", outputPlaceholder.getDisplayName());
        assertEquals(2, outputPlaceholder.getAmount());
        assertEquals(CustomDisplayItemValues.createQuick(set.getItemReference("simple1")), outputPlaceholder.getDisplayItem());
        assertEquals(0, outputPlaceholder.getLore().size());

        InputSlotValues inputSlot = (InputSlotValues) container2.getSlot(1, 0);
        SlotDisplayValues inputPlaceholder = inputSlot.getPlaceholder();
        assertEquals("", inputPlaceholder.getDisplayName());
        assertEquals(listOf(
                "test the lore"
        ), inputPlaceholder.getLore());
        assertEquals(1, inputPlaceholder.getAmount());
        assertEquals(SimpleVanillaDisplayItemValues.createQuick(CIMaterial.ACACIA_FENCE), inputPlaceholder.getDisplayItem());

        FuelSlotValues fuelSlot = (FuelSlotValues) container2.getSlot(0, 1);
        SlotDisplayValues fuelPlaceholder = fuelSlot.getPlaceholder();
        assertEquals("", fuelPlaceholder.getDisplayName());
        assertEquals(0, fuelPlaceholder.getLore().size());
        assertEquals(10, fuelPlaceholder.getAmount());
        assertEquals(DataVanillaDisplayItemValues.createQuick(CIMaterial.WOOL, (byte) 8), fuelPlaceholder.getDisplayItem());
    }

    static void testFuelRegistriesOld8(ItemSet set, int numFuelRegistries) {
        assertEquals(numFuelRegistries, set.getFuelRegistries().size());

        FuelRegistryValues registry1 = set.getFuelRegistry("registry1").get();
        assertEquals("registry1", registry1.getName());
        assertEquals(listOf(
                FuelEntryValues.createQuick(SimpleVanillaIngredientValues.createQuick(CIMaterial.COAL, 1), 100),
                FuelEntryValues.createQuick(CustomItemIngredientValues.createQuick(set.getItemReference("simple1"), 1), 500)
        ), registry1.getEntries());
    }

    static void testMobDropsOld8(ItemSet set, int numBlockDrops) {
        testMobDropsOld6(set, numBlockDrops);

        Iterator<MobDropValues> mobDropIterator = set.getMobDrops().iterator();
        testDefaultMobDrop8(mobDropIterator.next());
        testDefaultMobDrop8(mobDropIterator.next());
    }

    static void testBlockDropsOld8(ItemSet set, int numBlockDrops) {
        testBlockDropsOld6(set, numBlockDrops, true);

        Iterator<BlockDropValues> blockDropIterator = set.getBlockDrops().iterator();
        BlockDropValues firstBlockDrop = blockDropIterator.next();
        testDefaultBlockDrop8(firstBlockDrop);

        BlockDropValues blockDrop = blockDropIterator.next();
        assertEquals(BlockType.STONE, blockDrop.getBlockType());
        assertEquals(SilkTouchRequirement.OPTIONAL, blockDrop.getSilkTouchRequirement());
        DropValues drop = blockDrop.getDrop();

        assertFalse(drop.shouldCancelNormalDrops());
        assertEquals(listOf(
                set.getItemReference("pickaxe1"), set.getItemReference("pickaxe_two")
        ), drop.getRequiredHeldItems());

        OutputTableValues dropTable = drop.getOutputTable();
        assertEquals(Chance.percentage(35), dropTable.getNothingChance());
        assertEquals(listOf(
                OutputTableValues.Entry.createQuick(CustomItemResultValues.createQuick(set.getItemReference("simple1"), (byte) 1), 30),
                OutputTableValues.Entry.createQuick(SimpleVanillaResultValues.createQuick(CIMaterial.ACACIA_DOOR, 2), 20),
                OutputTableValues.Entry.createQuick(DataVanillaResultValues.createQuick(CIMaterial.WOOL, 3, 3), 10),
                OutputTableValues.Entry.createQuick(CopiedResultValues.createQuick(copiedFromServerString()), 5)
        ), dropTable.getEntries());
    }

    static void testDefaultBlockDrop8(BlockDropValues blockDrop) {
        assertEquals(SilkTouchRequirement.FORBIDDEN, blockDrop.getSilkTouchRequirement());
        testDefaultDrop8(blockDrop.getDrop());
        testDefaultBlockDrop10(blockDrop);
    }

    static void testDefaultMobDrop8(MobDropValues mobDrop) {
        testDefaultDrop8(mobDrop.getDrop());
        testDefaultMobDrop10(mobDrop);
    }

    static void testDefaultDrop8(DropValues drop) {
        assertEquals(0, drop.getRequiredHeldItems().size());
        testDefaultDrop10(drop);
    }

    static void testRecipesOld8(ItemSet set, int numRecipes) {
        testRecipesOld6(set, numRecipes);

        assertTrue(set.getCraftingRecipes().stream().anyMatch(recipe -> recipe.equals(createShapedRecipe3(set))));
    }

    static ShapedRecipeValues createShapedRecipe3(ItemSet set) {
        IngredientValues[] ingredients = {
                CustomItemIngredientValues.createQuick(set.getItemReference("simple1"), 1), new NoIngredientValues(), new NoIngredientValues(),
                new NoIngredientValues(), CustomItemIngredientValues.createQuick(set.getItemReference("simple2"), 1), new NoIngredientValues(),
                new NoIngredientValues(), new NoIngredientValues(), CustomItemIngredientValues.createQuick(set.getItemReference("simple3"), 1)
        };
        return ShapedRecipeValues.createQuick(ingredients, CopiedResultValues.createQuick(copiedFromServerString()), false);
    }

    static void testArmorTexturesOld8(ItemSet set, int numArmorTextures) {
        if (set.getSide() == ItemSet.Side.PLUGIN) {
            assertEquals(0, set.getArmorTextures().size());
            return;
        }

        assertEquals(numArmorTextures, set.getArmorTextures().size());

        ArmorTextureValues armorTexture1 = set.getArmorTexture("armor_texture1").get();
        assertEquals("armor_texture1", armorTexture1.getName());
        assertImageEqual(loadImage("armor1layer1"), armorTexture1.getLayer1());
        assertImageEqual(loadImage("armor1layer2"), armorTexture1.getLayer2());
    }

    static void testItemsOld8(ItemSet set, int numItems) {
        testItemsOld6(set, numItems);

        testShieldDefault8((CustomShieldValues) set.getItem("shield_one").get());
        testWandDefault8((CustomWandValues) set.getItem("wand_one").get());

        testWand2((CustomWandValues) set.getItem("wand2").get(), set);
        // Yeah... naming mistake...
        testSimple4((SimpleCustomItemValues) set.getItem("simple3").get(), set);
        testShovel2((CustomToolValues) set.getItem("shovel2").get(), set);
        testHoe3((CustomHoeValues) set.getItem("hoe3").get(), set);
        testShears3((CustomShearsValues) set.getItem("shears3").get(), set);
        test3dHelmet1((CustomHelmet3dValues) set.getItem("3dhelmet1").get(), set);
        testBow3((CustomBowValues) set.getItem("bow3").get(), set);
        testChestplate2((CustomArmorValues) set.getItem("chestplate2").get(), set);
        testShield2((CustomShieldValues) set.getItem("shield2").get(), set);
    }

    static void testWand2(CustomWandValues item, ItemSet itemSet) {
        assertEquals("wand2", item.getName());
        assertEquals(CustomItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("wand2", item.getAlias());
        assertEquals("Wand 2", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, false, false
        ), item.getItemFlags());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("test1", item.getTexture().getName());
        } else {
            assertNull(item.getTextureReference());
        }
        assertTrue(item.getModel() instanceof DefaultItemModel);
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(0, item.getOnHitTargetEffects().size());
        assertEquals(new ItemCommandSystem(false), item.getCommandSystem());
        assertEquals(ReplacementConditionValues.ConditionOperation.AND, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionValues.createQuick(
                        ReplacementConditionValues.ReplacementCondition.HASITEM,
                        itemSet.getItemReference("simple1"),
                        ReplacementConditionValues.ReplacementOperation.ATLEAST,
                        1,
                        itemSet.getItemReference("simple2")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffectValues.createQuick(
                        EffectType.JUMP, 2, AttributeModifierValues.Slot.OFFHAND
                )
        ), item.getEquippedEffects());
        assertEquals(ExtraItemNbtValues.createQuick(listOf(
                ExtraItemNbtValues.Entry.createQuick(listOf("test_int"), new ExtraItemNbtValues.Value(1)),
                ExtraItemNbtValues.Entry.createQuick(listOf("parent", "child", "test_string"), new ExtraItemNbtValues.Value("2"))
        )), item.getExtraNbt());
        assertEquals(1.5f, item.getAttackRange(), 0f);
        assertEquals("crazy1", item.getProjectile().getName());
        assertNull(item.getCharges());
        assertEquals(1, item.getAmountPerShot());
        assertEquals(40, item.getCooldown());
    }

    static void testSimple4(SimpleCustomItemValues item, ItemSet itemSet) {
        assertEquals("simple3", item.getName());
        assertEquals(CustomItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("sim3", item.getAlias());
        assertEquals("Third Simple", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, false, false
        ), item.getItemFlags());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("gun1", item.getTexture().getName());
        } else {
            assertNull(item.getTextureReference());
        }
        assertTrue(item.getModel() instanceof DefaultItemModel);
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(0, item.getOnHitTargetEffects().size());
        assertEquals(new ItemCommandSystem(false), item.getCommandSystem());
        assertEquals(ReplacementConditionValues.ConditionOperation.NONE, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionValues.createQuick(
                            ReplacementConditionValues.ReplacementCondition.MISSINGITEM,
                            itemSet.getItemReference("simple2"),
                            ReplacementConditionValues.ReplacementOperation.EXACTLY,
                            1,
                            itemSet.getItemReference("simple1")
                        )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffectValues.createQuick(EffectType.SLOW, 1, AttributeModifierValues.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(ExtraItemNbtValues.createQuick(listOf(
                ExtraItemNbtValues.Entry.createQuick(listOf("type"), new ExtraItemNbtValues.Value("simple"))
        )), item.getExtraNbt());
        assertEquals(0.5f, item.getAttackRange(), 0f);
        assertEquals(64, item.getMaxStacksize());
    }

    static void testShovel2(CustomToolValues item, ItemSet itemSet) {
        assertEquals("shovel2", item.getName());
        assertEquals(CustomItemType.IRON_SHOVEL, item.getItemType());
        assertEquals("shov2", item.getAlias());
        assertEquals("Second Shovel", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, false, false
        ), item.getItemFlags());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("gun1", item.getTexture().getName());
        } else {
            assertNull(item.getTextureReference());
        }
        assertTrue(item.getModel() instanceof DefaultItemModel);
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(0, item.getOnHitTargetEffects().size());
        assertEquals(new ItemCommandSystem(false), item.getCommandSystem());
        assertEquals(ReplacementConditionValues.ConditionOperation.OR, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionValues.createQuick(
                        ReplacementConditionValues.ReplacementCondition.ISBROKEN,
                        itemSet.getItemReference( "sword1"),
                        ReplacementConditionValues.ReplacementOperation.EXACTLY,
                        1,
                        itemSet.getItemReference("simple2")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffectValues.createQuick(EffectType.FAST_DIGGING, 1, AttributeModifierValues.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(ExtraItemNbtValues.createQuick(listOf(
                ExtraItemNbtValues.Entry.createQuick(listOf("type"), new ExtraItemNbtValues.Value("shovel"))
        )), item.getExtraNbt());
        assertEquals(3.5f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, (long) item.getMaxDurabilityNew());
        assertEquals(new NoIngredientValues(), item.getRepairItem());
        assertEquals(2, item.getEntityHitDurabilityLoss());
        assertEquals(1, item.getBlockBreakDurabilityLoss());
    }

    static void testHoe3(CustomHoeValues item, ItemSet itemSet) {
        assertEquals("hoe3", item.getName());
        assertEquals(CustomItemType.IRON_HOE, item.getItemType());
        assertEquals("h3", item.getAlias());
        assertEquals("Third Hoe", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, false, false
        ), item.getItemFlags());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("test1", item.getTexture().getName());
        } else {
            assertNull(item.getTextureReference());
        }
        assertTrue(item.getModel() instanceof DefaultItemModel);
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(0, item.getOnHitTargetEffects().size());
        assertEquals(new ItemCommandSystem(false), item.getCommandSystem());
        assertEquals(ReplacementConditionValues.ConditionOperation.NONE, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionValues.createQuick(
                        ReplacementConditionValues.ReplacementCondition.HASITEM,
                        itemSet.getItemReference("simple_three"),
                        ReplacementConditionValues.ReplacementOperation.ATMOST,
                        10,
                        itemSet.getItemReference("simple2")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffectValues.createQuick(EffectType.REGENERATION, 3, AttributeModifierValues.Slot.OFFHAND)
        ), item.getEquippedEffects());
        assertEquals(ExtraItemNbtValues.createQuick(listOf(
                ExtraItemNbtValues.Entry.createQuick(listOf("int_type"), new ExtraItemNbtValues.Value(5))
        )), item.getExtraNbt());
        assertEquals(0.75f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, (long) item.getMaxDurabilityNew());
        assertEquals(new NoIngredientValues(), item.getRepairItem());
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getTillDurabilityLoss());
    }

    static void testShears3(CustomShearsValues item, ItemSet itemSet) {
        assertEquals("shears3", item.getName());
        assertEquals(CustomItemType.SHEARS, item.getItemType());
        assertEquals("sh3", item.getAlias());
        assertEquals("Third Shears", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, false, false
        ), item.getItemFlags());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("test1", item.getTexture().getName());
        } else {
            assertNull(item.getTextureReference());
        }
        assertTrue(item.getModel() instanceof DefaultItemModel);
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(0, item.getOnHitTargetEffects().size());
        assertEquals(new ItemCommandSystem(true), item.getCommandSystem());
        assertEquals(ReplacementConditionValues.ConditionOperation.NONE, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionValues.createQuick(
                        ReplacementConditionValues.ReplacementCondition.HASITEM,
                        itemSet.getItemReference("pickaxe1"),
                        ReplacementConditionValues.ReplacementOperation.EXACTLY,
                        1,
                        itemSet.getItemReference("sword1")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffectValues.createQuick(EffectType.INVISIBILITY, 1, AttributeModifierValues.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(ExtraItemNbtValues.createQuick(listOf(
                ExtraItemNbtValues.Entry.createQuick(listOf("int_type"), new ExtraItemNbtValues.Value(9))
        )), item.getExtraNbt());
        assertEquals(1.25f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, (long) item.getMaxDurabilityNew());
        assertEquals(new NoIngredientValues(), item.getRepairItem());
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(1, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getShearDurabilityLoss());
    }

    static void test3dHelmet1(CustomHelmet3dValues item, ItemSet itemSet) {
        assertEquals("3dhelmet1", item.getName());
        assertEquals(CustomItemType.IRON_HOE, item.getItemType());
        assertEquals("3d1", item.getAlias());
        assertEquals("A 3D Helmet!", item.getDisplayName());
        assertEquals(listOf(
                "The only custom armor",
                "that works without Optifine"
        ), item.getLore());
        assertEquals(listOf(
                AttributeModifierValues.createQuick(
                        AttributeModifierValues.Attribute.ARMOR,
                        AttributeModifierValues.Slot.HEAD,
                        AttributeModifierValues.Operation.ADD,
                        6.0
                )
        ), item.getAttributeModifiers());
        assertEquals(listOf(
                EnchantmentValues.createQuick(EnchantmentType.THORNS, 2)
        ), item.getDefaultEnchantments());
        assertEquals(listOf(
                true, false, true, false, false, false
        ), item.getItemFlags());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("gun1", item.getTexture().getName());
            assertStringResourceEquals("nl/knokko/customitems/serialization/model/blue_crossbow.json", ((LegacyCustomItemModel) item.getModel()).getRawModel());
        } else {
            assertNull(item.getTextureReference());
            assertNull(item.getModel());
        }
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.INVISIBILITY, 30, 1, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.POISON, 100, 1, Chance.percentage(100))
        ), item.getOnHitTargetEffects());
        ItemCommandSystem summonSheepSystem = new ItemCommandSystem(true);
        summonSheepSystem.setCommandsFor(ItemCommandEvent.RIGHT_CLICK_GENERAL, listOf(ItemCommand.createFromLegacy("summon sheep")));
        assertEquals(summonSheepSystem, item.getCommandSystem());
        assertEquals(ReplacementConditionValues.ConditionOperation.NONE, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionValues.createQuick(
                        ReplacementConditionValues.ReplacementCondition.HASITEM,
                        itemSet.getItemReference( "pickaxe1"),
                        ReplacementConditionValues.ReplacementOperation.ATLEAST,
                        5,
                        itemSet.getItemReference("simple2")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffectValues.createQuick(EffectType.SLOW, 1, AttributeModifierValues.Slot.HEAD)
        ), item.getEquippedEffects());
        assertEquals(ExtraItemNbtValues.createQuick(listOf(
                ExtraItemNbtValues.Entry.createQuick(listOf("type"), new ExtraItemNbtValues.Value("3dhelmet"))
        )), item.getExtraNbt());
        assertEquals(0.25f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertFalse(item.allowEnchanting());
        assertEquals(123, (long) item.getMaxDurabilityNew());
        assertEquals(new NoIngredientValues(), item.getRepairItem());
        assertEquals(3, item.getEntityHitDurabilityLoss());
        assertEquals(4, item.getBlockBreakDurabilityLoss());
        for (DamageSource source : DamageSource.values()) {
            if (source == DamageSource.SUFFOCATION) {
                assertEquals(100, item.getDamageResistances().getResistance(source));
            } else {
                assertEquals(0, item.getDamageResistances().getResistance(source));
            }
        }
    }

    static void testBow3(CustomBowValues item, ItemSet itemSet) {
        assertEquals("bow3", item.getName());
        assertEquals(CustomItemType.BOW, item.getItemType());
        assertEquals("b3", item.getAlias());
        assertEquals("Third Bow", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, false, false
        ), item.getItemFlags());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("bow_one", item.getTexture().getName());
        } else {
            assertNull(item.getTextureReference());
        }
        assertNull(item.getModel());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.NIGHT_VISION, 1000, 1, Chance.percentage(100))
        ), item.getOnHitPlayerEffects());
        assertEquals(listOf(
                ChancePotionEffectValues.createQuick(EffectType.WITHER, 100, 2, Chance.percentage(100))
        ), item.getOnHitTargetEffects());
        assertEquals(new ItemCommandSystem(true), item.getCommandSystem());
        assertEquals(ReplacementConditionValues.ConditionOperation.NONE, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionValues.createQuick(
                        ReplacementConditionValues.ReplacementCondition.ISBROKEN,
                        itemSet.getItemReference( "sword1"),
                        ReplacementConditionValues.ReplacementOperation.EXACTLY,
                        1,
                        itemSet.getItemReference("boots_one")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffectValues.createQuick(EffectType.SPEED, 2, AttributeModifierValues.Slot.OFFHAND)
        ), item.getEquippedEffects());
        assertEquals(ExtraItemNbtValues.createQuick(listOf(
                ExtraItemNbtValues.Entry.createQuick(listOf("type"), new ExtraItemNbtValues.Value("bow"))
        )), item.getExtraNbt());
        assertEquals(1.75f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, (long) item.getMaxDurabilityNew());
        assertEquals(new NoIngredientValues(), item.getRepairItem());
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getShootDurabilityLoss());
        assertEquals(1.0, item.getDamageMultiplier(), 0.0);
        assertEquals(1.0, item.getSpeedMultiplier(), 0.0);
        assertEquals(0, item.getKnockbackStrength());
        assertTrue(item.hasGravity());
    }

    static void testChestplate2(CustomArmorValues item, ItemSet itemSet) {
        assertEquals("chestplate2", item.getName());
        assertEquals(CustomItemType.IRON_CHESTPLATE, item.getItemType());
        assertEquals("chess2", item.getAlias());
        assertEquals("Second Chestplate", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, false, false
        ), item.getItemFlags());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("gun1", item.getTexture().getName());
        } else {
            assertNull(item.getTextureReference());
        }
        assertTrue(item.getModel() instanceof DefaultItemModel);
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(0, item.getOnHitTargetEffects().size());
        assertEquals(new ItemCommandSystem(false), item.getCommandSystem());
        assertEquals(ReplacementConditionValues.ConditionOperation.NONE, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionValues.createQuick(
                        ReplacementConditionValues.ReplacementCondition.MISSINGITEM,
                        itemSet.getItemReference("simple1"),
                        ReplacementConditionValues.ReplacementOperation.EXACTLY,
                        7,
                        itemSet.getItemReference("simple2")
                )
        ), item.getReplacementConditions());
        assertEquals(listOf(
                EquippedPotionEffectValues.createQuick(EffectType.DAMAGE_RESISTANCE, 1, AttributeModifierValues.Slot.CHEST)
        ), item.getEquippedEffects());
        assertEquals(ExtraItemNbtValues.createQuick(listOf(
                ExtraItemNbtValues.Entry.createQuick(listOf("type"), new ExtraItemNbtValues.Value("chest"))
        )), item.getExtraNbt());
        assertEquals(4.25f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, (long) item.getMaxDurabilityNew());
        assertEquals(new NoIngredientValues(), item.getRepairItem());
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        for (DamageSource source : DamageSource.values()) {
            assertEquals(0, item.getDamageResistances().getResistance(source));
        }
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("armor_texture1", item.getArmorTexture().getName());
        } else {
            assertNull(item.getArmorTextureReference());
        }
    }

    static void testShield2(CustomShieldValues item, ItemSet itemSet) {
        assertEquals("shield2", item.getName());
        assertEquals(CustomItemType.SHIELD, item.getItemType());
        assertEquals("s2", item.getAlias());
        assertEquals("Second Shield", item.getDisplayName());
        assertEquals(0, item.getLore().size());
        assertEquals(0, item.getAttributeModifiers().size());
        assertEquals(0, item.getDefaultEnchantments().size());
        assertEquals(listOf(
                false, false, true, false, false, false
        ), item.getItemFlags());
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            assertEquals("test1", item.getTexture().getName());
        } else {
            assertNull(item.getTextureReference());
        }
        assertTrue(item.getModel() instanceof DefaultItemModel);
        assertEquals(0, item.getOnHitPlayerEffects().size());
        assertEquals(0, item.getOnHitTargetEffects().size());
        assertEquals(new ItemCommandSystem(true), item.getCommandSystem());
        assertEquals(ReplacementConditionValues.ConditionOperation.AND, item.getConditionOp());
        assertEquals(listOf(
                ReplacementConditionValues.createQuick(
                        ReplacementConditionValues.ReplacementCondition.ISBROKEN,
                        itemSet.getItemReference("pickaxe1"),
                        ReplacementConditionValues.ReplacementOperation.ATMOST,
                        1,
                        itemSet.getItemReference("bow_one")
                )
        ), item.getReplacementConditions());
        assertEquals(0, item.getEquippedEffects().size());
        assertEquals(ExtraItemNbtValues.createQuick(listOf(
                ExtraItemNbtValues.Entry.createQuick(listOf("test"), new ExtraItemNbtValues.Value(-5))
        )), item.getExtraNbt());
        assertEquals(1.5f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, (long) item.getMaxDurabilityNew());
        assertEquals(new NoIngredientValues(), item.getRepairItem());
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        assertEquals(4.0, item.getThresholdDamage(), 0.0);
        assertTrue(item.getBlockingModel() instanceof DefaultItemModel);
    }

    static void testBaseDefault8(CustomItemValues item) {
        // Wands don't have empty string as default alias due to issue #124
        if (!(item instanceof CustomWandValues)) {
            assertEquals("", item.getAlias());
        }
        assertEquals(0, item.getReplacementConditions().size());
        assertEquals(0, item.getEquippedEffects().size());
        assertEquals(0, item.getExtraNbt().getEntries().size());
        assertEquals(1f, item.getAttackRange(), 0f);
        Backward9.testBaseDefault9(item);
    }

    static void testSimpleDefault8(SimpleCustomItemValues item) {
        testBaseDefault8(item);
        Backward9.testSimpleDefault9(item);
    }

    static void testToolDefault8(CustomToolValues item) {
        testBaseDefault8(item);
        Backward9.testToolDefault9(item);
    }

    static void testArmorDefault8(CustomArmorValues item) {
        testToolDefault8(item);
        Backward9.testArmorDefault9(item);
    }

    static void testHoeDefault8(CustomHoeValues item) {
        testToolDefault8(item);
        Backward9.testHoeDefault9(item);
    }

    static void testShearsDefault8(CustomShearsValues item) {
        testToolDefault8(item);
        Backward9.testShearsDefault9(item);
    }

    static void testBowDefault8(CustomBowValues item) {
        testToolDefault8(item);
        Backward9.testBowDefault9(item);
    }

    static void testShieldDefault8(CustomShieldValues item) {
        testToolDefault8(item);
        Backward9.testShieldDefault9(item);
    }

    static void testWandDefault8(CustomWandValues item) {
        testBaseDefault8(item);
        Backward9.testWandDefault9(item);
    }

    static void testTridentDefault8(CustomTridentValues item) {
        testToolDefault8(item);
        Backward9.testTridentDefault9(item);
    }
}
