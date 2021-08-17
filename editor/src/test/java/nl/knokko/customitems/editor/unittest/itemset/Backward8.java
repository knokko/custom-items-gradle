package nl.knokko.customitems.editor.unittest.itemset;

import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.customitems.container.fuel.FuelEntry;
import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.drops.BlockDrop;
import nl.knokko.customitems.drops.BlockType;
import nl.knokko.customitems.drops.Drop;
import nl.knokko.customitems.drops.EntityDrop;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.*;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.item.texture.ArmorTextures;
import nl.knokko.customitems.editor.set.recipe.ShapedRecipe;
import nl.knokko.customitems.editor.set.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.editor.set.recipe.result.CopiedResult;
import nl.knokko.customitems.editor.set.recipe.result.CustomItemResult;
import nl.knokko.customitems.editor.set.recipe.result.DataVanillaResult;
import nl.knokko.customitems.editor.set.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.effect.EffectType;
import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PassivePotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.item.nbt.NbtKey;
import nl.knokko.customitems.item.nbt.NbtPair;
import nl.knokko.customitems.item.nbt.NbtValue;
import nl.knokko.customitems.recipe.ContainerRecipe;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.customitems.util.ValidationException;
import org.junit.Test;

import java.util.Iterator;
import java.util.Scanner;

import static nl.knokko.customitems.editor.unittest.itemset.Backward3.testTextures3;
import static nl.knokko.customitems.editor.unittest.itemset.Backward6.*;
import static nl.knokko.customitems.editor.unittest.itemset.Backward7.testContainers7;
import static nl.knokko.customitems.editor.unittest.itemset.BackwardHelper.*;
import static org.junit.Assert.*;

public class Backward8 {

    @Test
    public void testBackwardCompatibility8() throws ValidationException {
        ItemSet oldSet = loadItemSet("backward8old");
        testTextures3(oldSet, 3);
        testArmorTexturesOld8(oldSet, 1);
        testItemsOld8(oldSet, 30);
        testRecipesOld8(oldSet, 4);
        testBlockDropsOld8(oldSet, 2);
        testMobDropsOld8(oldSet, 2);
        testProjectileCoversOld6(oldSet, 2);
        testProjectilesOld6(oldSet, 1);
        testFuelRegistriesOld8(oldSet, 1);
        testContainersOld8(oldSet, 1);

        ItemSet newSet = loadItemSet("backward8new");
        testTexturesNew6(newSet, 1);
        testItemsNew8(newSet, 2);
        testRecipesNew6(newSet, 1);
    }

    static String copiedFromServerString() {
        Scanner scanner = new Scanner(
                Backward8.class.getClassLoader().getResourceAsStream("backward/itemset/copiedFromServer.txt")
        );
        String result = scanner.next();
        scanner.close();
        return result;
    }

    static void testItemsNew8(ItemSet set, int numItems) throws ValidationException {
        testItemsNew6(set, numItems);

        CustomTrident item = (CustomTrident) set.getCustomItemByName("trident2");
        assertEquals("trident2", item.getName());
        assertEquals(CustomItemType.TRIDENT, item.getItemType());
        assertEquals("t2", item.getAlias());
        assertEquals("Second Test Trident", item.getDisplayName());
        assertEquals(0, item.getLore().length);
        assertEquals(0, item.getAttributes().length);
        assertEquals(0, item.getDefaultEnchantments().length);
        assertArrayEquals(new boolean[] {
                false, false, true, false, false, false
        }, item.getItemFlags());
        assertEquals("quick_wand", item.getTexture().getName());
        assertNull(item.getCustomModel());
        assertEquals(0, item.getPlayerEffects().size());
        assertEquals(0, item.getTargetEffects().size());
        assertEquals(0, item.getCommands().length);
        assertEquals(ReplaceCondition.ConditionOperation.NONE, item.getConditionOperator());
        assertArrayEquals(new ReplaceCondition[] {
                new ReplaceCondition(
                        ReplaceCondition.ReplacementCondition.HASITEM,
                        "trident_one",
                        ReplaceCondition.ReplacementOperation.ATLEAST,
                        1,
                        "trident_one"
                )
        }, item.getReplaceConditions());
        assertEquals(listOf(
                new EquippedPotionEffect(new PassivePotionEffect(EffectType.REGENERATION, 2), AttributeModifier.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(new ExtraItemNbt(listOf(
                new NbtPair(new NbtKey("trident"), new NbtValue("t2"))
        )), item.getExtraNbt());
        assertEquals(1.75f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, item.getDurability());
        assertNull(item.getRepairItem());
        assertEquals(1, item.getEntityHitDurabilityLoss());
        assertEquals(2, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.throwDurabilityLoss);
        assertEquals(1.0, item.throwDamageMultiplier, 0.0);
        assertEquals(1.0, item.speedMultiplier, 0.0);
        assertNull(item.customInHandModel);
        assertNull(item.customThrowingModel);
    }

    static void testContainersOld8(ItemSet set, int numContainers) {
        testContainers7(set, numContainers);

        // TODO Also test for placeholders for input, output, and fuel slots
        CustomContainer container2 = set.getContainerByName("container2");
        ContainerRecipe recipe = container2.getRecipes().stream().findFirst().get();

        OutputTable output = recipe.getOutputs().stream().findFirst().get().getOutputTable();
        assertEquals(listOf(
                new OutputTable.Entry(new CustomItemResult(set.getCustomItemByName("simple1"), (byte) 1), 40),
                new OutputTable.Entry(new SimpleVanillaResult(CIMaterial.DIAMOND, (byte) 2), 50)
        ), output.getEntries());
    }

    static void testFuelRegistriesOld8(ItemSet set, int numFuelRegistries) {
        assertEquals(numFuelRegistries, set.getBackingFuelRegistries().size());

        CustomFuelRegistry registry1 = set.getFuelRegistryByName("registry1");
        assertEquals("registry1", registry1.getName());
        assertEquals(listOf(
                new FuelEntry(new SimpleVanillaIngredient(CIMaterial.COAL, (byte) 1, null), 100),
                new FuelEntry(new CustomItemIngredient(set.getCustomItemByName("simple1"), (byte) 1, null), 500)
        ), registry1.getEntries());
    }

    static void testMobDropsOld8(ItemSet set, int numBlockDrops) {
        testBlockDropsOld6(set, numBlockDrops);

        Iterator<EntityDrop> mobDropIterator = set.getBackingMobDrops().iterator();
        testDefaultMobDrop8(mobDropIterator.next());
        testDefaultMobDrop8(mobDropIterator.next());
    }

    static void testBlockDropsOld8(ItemSet set, int numBlockDrops) {
        testBlockDropsOld6(set, numBlockDrops);

        Iterator<BlockDrop> blockDropIterator = set.getBackingBlockDrops().iterator();
        BlockDrop firstBlockDrop = blockDropIterator.next();
        testDefaultBlockDrop8(firstBlockDrop);

        BlockDrop blockDrop = blockDropIterator.next();
        assertEquals(BlockType.STONE, blockDrop.getBlock());
        assertTrue(blockDrop.allowSilkTouch());
        Drop drop = blockDrop.getDrop();

        assertFalse(drop.cancelNormalDrop());
        assertEquals(listOf(
                set.getCustomItemByName("pickaxe1"), set.getCustomItemByName("pickaxe_two")
        ), drop.getRequiredHeldItems());

        OutputTable dropTable = drop.getDropTable();
        assertEquals(35, dropTable.getNothingChance());
        assertEquals(listOf(
                new OutputTable.Entry(new CustomItemResult(set.getCustomItemByName("simple1"), (byte) 1), 30),
                new OutputTable.Entry(new SimpleVanillaResult(CIMaterial.ACACIA_DOOR, (byte) 1), 20),
                new OutputTable.Entry(new DataVanillaResult(CIMaterial.WOOL, (byte) 3, (byte) 3), 10),
                new OutputTable.Entry(new CopiedResult(copiedFromServerString()), 5)
        ), dropTable.getEntries());
    }

    static void testDefaultBlockDrop8(BlockDrop blockDrop) {
        assertFalse(blockDrop.allowSilkTouch());
        testDefaultDrop8(blockDrop.getDrop());
        // TODO Call testDefaultBlockDrop9
    }

    static void testDefaultMobDrop8(EntityDrop mobDrop) {
        testDefaultDrop8(mobDrop.getDrop());
        // TODO Call testDefaultMobDrop9
    }

    static void testDefaultDrop8(Drop drop) {
        assertEquals(0, drop.getRequiredHeldItems().size());
        // TODO Call testDefaultDrop9
    }

    static void testRecipesOld8(ItemSet set, int numRecipes) {
        testRecipesOld6(set, numRecipes);

        assertTrue(set.getBackingRecipes().contains(createShapedRecipe3(set)));
    }

    static ShapedRecipe createShapedRecipe3(ItemSet set) {
        Ingredient[] ingredients = {
                new CustomItemIngredient(set.getCustomItemByName("simple1"), (byte) 1, null), new NoIngredient(), new NoIngredient(),
                new NoIngredient(), new CustomItemIngredient(set.getCustomItemByName("simple2"), (byte) 1, null), new NoIngredient(),
                new NoIngredient(), new NoIngredient(), new CustomItemIngredient(set.getCustomItemByName("simple3"), (byte) 1, null)
        };
        return new ShapedRecipe(ingredients, new CopiedResult(copiedFromServerString()));
    }

    static void testArmorTexturesOld8(ItemSet set, int numArmorTextures) {
        assertEquals(numArmorTextures, set.getBackingArmorTextures().size());

        ArmorTextures armorTexture1 = set.getArmorTexture("armor_texture1").get();
        assertEquals("armor_texture1", armorTexture1.getName());
        assertImageEqual(loadImage("armor1layer1"), armorTexture1.getLayer1());
        assertImageEqual(loadImage("armor1layer2"), armorTexture1.getLayer2());
    }

    static void testItemsOld8(ItemSet set, int numItems) throws ValidationException {
        testItemsOld6(set, numItems);

        testShieldDefault8((CustomShield) set.getCustomItemByName("shield_one"));
        testWandDefault8((CustomWand) set.getCustomItemByName("wand_one"));

        testWand2((CustomWand) set.getCustomItemByName("wand2"));
        // Yeah... naming mistake...
        testSimple4((SimpleCustomItem) set.getCustomItemByName("simple3"));
        testShovel2((CustomTool) set.getCustomItemByName("shovel2"));
        testHoe3((CustomHoe) set.getCustomItemByName("hoe3"));
        testShears3((CustomShears) set.getCustomItemByName("shears3"));
        test3dHelmet1((CustomHelmet3D) set.getCustomItemByName("3dhelmet1"));
        testBow3((CustomBow) set.getCustomItemByName("bow3"));
        testChestplate2((CustomArmor) set.getCustomItemByName("chestplate2"));
        testShield2((CustomShield) set.getCustomItemByName("shield2"));
    }

    static void testWand2(CustomWand item) throws ValidationException {
        assertEquals("wand2", item.getName());
        assertEquals(CustomItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("wand2", item.getAlias());
        assertEquals("Wand 2", item.getDisplayName());
        assertEquals(0, item.getLore().length);
        assertEquals(0, item.getAttributes().length);
        assertEquals(0, item.getDefaultEnchantments().length);
        assertArrayEquals(new boolean[] {
                false, false, true, false, false, false
        }, item.getItemFlags());
        assertEquals("test1", item.getTexture().getName());
        assertNull(item.getCustomModel());
        assertEquals(0, item.getPlayerEffects().size());
        assertEquals(0, item.getTargetEffects().size());
        assertEquals(0, item.getCommands().length);
        assertEquals(ReplaceCondition.ConditionOperation.NONE, item.getConditionOperator());
        assertArrayEquals(new ReplaceCondition[] {
                new ReplaceCondition(
                        ReplaceCondition.ReplacementCondition.HASITEM,
                        "simple1",
                        ReplaceCondition.ReplacementOperation.ATLEAST,
                        1,
                        "simple2"
                )
        }, item.getReplaceConditions());
        assertEquals(listOf(
                new EquippedPotionEffect(new PassivePotionEffect(EffectType.JUMP, 2), AttributeModifier.Slot.OFFHAND)
        ), item.getEquippedEffects());
        assertEquals(new ExtraItemNbt(listOf(
                new NbtPair(new NbtKey("test_int"), new NbtValue(1)),
                new NbtPair(new NbtKey("parent", "child"), new NbtValue("test_string"))
        )), item.getExtraNbt());
        assertEquals(1.5f, item.getAttackRange(), 0f);
        assertEquals("crazy1", item.projectile.name);
        assertEquals(1, item.charges.maxCharges);
        assertEquals(1, item.amountPerShot);
        assertEquals(40, item.cooldown);
    }

    static void testSimple4(SimpleCustomItem item) throws ValidationException {
        assertEquals("simple3", item.getName());
        assertEquals(CustomItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("sim3", item.getAlias());
        assertEquals("Third Simple", item.getDisplayName());
        assertEquals(0, item.getLore().length);
        assertEquals(0, item.getAttributes().length);
        assertEquals(0, item.getDefaultEnchantments().length);
        assertArrayEquals(new boolean[] {
                false, false, true, false, false, false
        }, item.getItemFlags());
        assertEquals("gun1", item.getTexture().getName());
        assertNull(item.getCustomModel());
        assertEquals(0, item.getPlayerEffects().size());
        assertEquals(0, item.getTargetEffects().size());
        assertEquals(0, item.getCommands().length);
        assertEquals(ReplaceCondition.ConditionOperation.NONE, item.getConditionOperator());
        assertArrayEquals(new ReplaceCondition[] {
                new ReplaceCondition(
                        ReplaceCondition.ReplacementCondition.MISSINGITEM,
                        "simple2",
                        ReplaceCondition.ReplacementOperation.EXACTLY,
                        1,
                        "simple1"
                )
        }, item.getReplaceConditions());
        assertEquals(listOf(
                new EquippedPotionEffect(new PassivePotionEffect(EffectType.SLOW, 1), AttributeModifier.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(new ExtraItemNbt(listOf(
                new NbtPair(new NbtKey("type"), new NbtValue("simple"))
        )), item.getExtraNbt());
        assertEquals(0.5f, item.getAttackRange(), 0f);
        assertEquals(64, item.getMaxStacksize());
    }

    static void testShovel2(CustomTool item) throws ValidationException {
        assertEquals("shovel2", item.getName());
        assertEquals(CustomItemType.IRON_SHOVEL, item.getItemType());
        assertEquals("shov2", item.getAlias());
        assertEquals("Second Shovel", item.getDisplayName());
        assertEquals(0, item.getLore().length);
        assertEquals(0, item.getAttributes().length);
        assertEquals(0, item.getDefaultEnchantments().length);
        assertArrayEquals(new boolean[] {
                false, false, true, false, false, false
        }, item.getItemFlags());
        assertEquals("gun1", item.getTexture().getName());
        assertNull(item.getCustomModel());
        assertEquals(0, item.getPlayerEffects().size());
        assertEquals(0, item.getTargetEffects().size());
        assertEquals(0, item.getCommands().length);
        assertEquals(ReplaceCondition.ConditionOperation.NONE, item.getConditionOperator());
        assertArrayEquals(new ReplaceCondition[] {
                new ReplaceCondition(
                        ReplaceCondition.ReplacementCondition.ISBROKEN,
                        "sword1",
                        ReplaceCondition.ReplacementOperation.EXACTLY,
                        1,
                        "simple2"
                )
        }, item.getReplaceConditions());
        assertEquals(listOf(
                new EquippedPotionEffect(new PassivePotionEffect(EffectType.FAST_DIGGING, 1), AttributeModifier.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(new ExtraItemNbt(listOf(
                new NbtPair(new NbtKey("type"), new NbtValue("shovel"))
        )), item.getExtraNbt());
        assertEquals(3.5f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, item.getDurability());
        assertNull(item.getRepairItem());
        assertEquals(2, item.getEntityHitDurabilityLoss());
        assertEquals(1, item.getBlockBreakDurabilityLoss());
    }

    static void testHoe3(CustomHoe item) throws ValidationException {
        assertEquals("hoe3", item.getName());
        assertEquals(CustomItemType.IRON_HOE, item.getItemType());
        assertEquals("h3", item.getAlias());
        assertEquals("First Hoe", item.getDisplayName());
        assertEquals(0, item.getLore().length);
        assertEquals(0, item.getAttributes().length);
        assertEquals(0, item.getDefaultEnchantments().length);
        assertArrayEquals(new boolean[] {
                false, false, true, false, false, false
        }, item.getItemFlags());
        assertEquals("test1", item.getTexture().getName());
        assertNull(item.getCustomModel());
        assertEquals(0, item.getPlayerEffects().size());
        assertEquals(0, item.getTargetEffects().size());
        assertEquals(0, item.getCommands().length);
        assertEquals(ReplaceCondition.ConditionOperation.NONE, item.getConditionOperator());
        assertArrayEquals(new ReplaceCondition[] {
                new ReplaceCondition(
                        ReplaceCondition.ReplacementCondition.HASITEM,
                        "simple_three",
                        ReplaceCondition.ReplacementOperation.ATMOST,
                        10,
                        "simple2"
                )
        }, item.getReplaceConditions());
        assertEquals(listOf(
                new EquippedPotionEffect(new PassivePotionEffect(EffectType.REGENERATION, 3), AttributeModifier.Slot.OFFHAND)
        ), item.getEquippedEffects());
        assertEquals(new ExtraItemNbt(listOf(
                new NbtPair(new NbtKey("int_type"), new NbtValue(5))
        )), item.getExtraNbt());
        assertEquals(0.75f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, item.getDurability());
        assertNull(item.getRepairItem());
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getTillDurabilityLoss());
    }

    static void testShears3(CustomShears item) throws ValidationException {
        assertEquals("shears3", item.getName());
        assertEquals(CustomItemType.IRON_HOE, item.getItemType());
        assertEquals("sh3", item.getAlias());
        assertEquals("Third Shears", item.getDisplayName());
        assertEquals(0, item.getLore().length);
        assertEquals(0, item.getAttributes().length);
        assertEquals(0, item.getDefaultEnchantments().length);
        assertArrayEquals(new boolean[] {
                false, false, true, false, false, false
        }, item.getItemFlags());
        assertEquals("test1", item.getTexture().getName());
        assertNull(item.getCustomModel());
        assertEquals(0, item.getPlayerEffects().size());
        assertEquals(0, item.getTargetEffects().size());
        assertEquals(0, item.getCommands().length);
        assertEquals(ReplaceCondition.ConditionOperation.NONE, item.getConditionOperator());
        assertArrayEquals(new ReplaceCondition[] {
                new ReplaceCondition(
                        ReplaceCondition.ReplacementCondition.HASITEM,
                        "pickaxe1",
                        ReplaceCondition.ReplacementOperation.EXACTLY,
                        1,
                        "sword1"
                )
        }, item.getReplaceConditions());
        assertEquals(listOf(
                new EquippedPotionEffect(new PassivePotionEffect(EffectType.INVISIBILITY, 3), AttributeModifier.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(new ExtraItemNbt(listOf(
                new NbtPair(new NbtKey("int_type"), new NbtValue(9))
        )), item.getExtraNbt());
        assertEquals(1.25f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, item.getDurability());
        assertNull(item.getRepairItem());
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(1, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getShearDurabilityLoss());
    }

    static void test3dHelmet1(CustomHelmet3D item) throws ValidationException {
        assertEquals("3dhelmet1", item.getName());
        assertEquals(CustomItemType.IRON_HOE, item.getItemType());
        assertEquals("3d1", item.getAlias());
        assertEquals("A 3D Helmet!", item.getDisplayName());
        assertArrayEquals(new String[] {
                "The only custom armor",
                "that works without Optifine"
        }, item.getLore());
        assertArrayEquals(new AttributeModifier[] {
                new AttributeModifier(
                        AttributeModifier.Attribute.ARMOR,
                        AttributeModifier.Slot.HEAD,
                        AttributeModifier.Operation.ADD,
                        6.0
                )
        }, item.getAttributes());
        assertArrayEquals(new Enchantment[] {
                new Enchantment(EnchantmentType.THORNS, 2)
        }, item.getDefaultEnchantments());
        assertArrayEquals(new boolean[] {
                true, false, true, false, false, false
        }, item.getItemFlags());
        assertEquals("gun1", item.getTexture().getName());
        assertResourceEquals("backward/itemset/model/blue_crossbow.json", item.getCustomModel());
        assertEquals(listOf(
                new PotionEffect(EffectType.INVISIBILITY, 30, 1)
        ), item.getPlayerEffects());
        assertEquals(listOf(
                new PotionEffect(EffectType.POISON, 100, 1)
        ), item.getTargetEffects());
        assertArrayEquals(new String[] {
                "summon sheep"
        }, item.getCommands());
        assertEquals(ReplaceCondition.ConditionOperation.NONE, item.getConditionOperator());
        assertArrayEquals(new ReplaceCondition[] {
                new ReplaceCondition(
                        ReplaceCondition.ReplacementCondition.HASITEM,
                        "pickaxe1",
                        ReplaceCondition.ReplacementOperation.ATLEAST,
                        5,
                        "simple2"
                )
        }, item.getReplaceConditions());
        assertEquals(listOf(
                new EquippedPotionEffect(new PassivePotionEffect(EffectType.SLOW, 1), AttributeModifier.Slot.HEAD)
        ), item.getEquippedEffects());
        assertEquals(new ExtraItemNbt(listOf(
                new NbtPair(new NbtKey("type"), new NbtValue("3dhelmet"))
        )), item.getExtraNbt());
        assertEquals(0.25f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertFalse(item.allowEnchanting());
        assertEquals(123, item.getDurability());
        assertNull(item.getRepairItem());
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

    static void testBow3(CustomBow item) throws ValidationException {
        assertEquals("bow3", item.getName());
        assertEquals(CustomItemType.BOW, item.getItemType());
        assertEquals("b3", item.getAlias());
        assertEquals("Third Bow", item.getDisplayName());
        assertEquals(0, item.getLore().length);
        assertEquals(0, item.getAttributes().length);
        assertEquals(0, item.getDefaultEnchantments().length);
        assertArrayEquals(new boolean[] {
                false, false, true, false, false, false
        }, item.getItemFlags());
        assertEquals("bow_one", item.getTexture().getName());
        assertNull(item.getCustomModel());
        assertEquals(0, item.getPlayerEffects().size());
        assertEquals(0, item.getTargetEffects().size());
        assertEquals(0, item.getCommands().length);
        assertEquals(ReplaceCondition.ConditionOperation.NONE, item.getConditionOperator());
        assertArrayEquals(new ReplaceCondition[] {
                new ReplaceCondition(
                        ReplaceCondition.ReplacementCondition.ISBROKEN,
                        "sword1",
                        ReplaceCondition.ReplacementOperation.EXACTLY,
                        1,
                        "boots_one"
                )
        }, item.getReplaceConditions());
        assertEquals(listOf(
                new EquippedPotionEffect(new PassivePotionEffect(EffectType.SPEED, 2), AttributeModifier.Slot.OFFHAND)
        ), item.getEquippedEffects());
        assertEquals(new ExtraItemNbt(listOf(
                new NbtPair(new NbtKey("type"), new NbtValue("bow"))
        )), item.getExtraNbt());
        assertEquals(1.75f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, item.getDurability());
        assertNull(item.getRepairItem());
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getShootDurabilityLoss());
        assertEquals(1.0, item.getDamageMultiplier(), 0.0);
        assertEquals(1.0, item.getSpeedMultiplier(), 0.0);
        assertEquals(0, item.getKnockbackStrength());
        assertTrue(item.hasGravity());
    }

    static void testChestplate2(CustomArmor item) throws ValidationException {
        assertEquals("chestplate2", item.getName());
        assertEquals(CustomItemType.IRON_CHESTPLATE, item.getItemType());
        assertEquals("chess2", item.getAlias());
        assertEquals("Second Chestplate", item.getDisplayName());
        assertEquals(0, item.getLore().length);
        assertEquals(0, item.getAttributes().length);
        assertEquals(0, item.getDefaultEnchantments().length);
        assertArrayEquals(new boolean[] {
                false, false, true, false, false, false
        }, item.getItemFlags());
        assertEquals("gun1", item.getTexture().getName());
        assertNull(item.getCustomModel());
        assertEquals(0, item.getPlayerEffects().size());
        assertEquals(0, item.getTargetEffects().size());
        assertEquals(0, item.getCommands().length);
        assertEquals(ReplaceCondition.ConditionOperation.NONE, item.getConditionOperator());
        assertArrayEquals(new ReplaceCondition[] {
                new ReplaceCondition(
                        ReplaceCondition.ReplacementCondition.MISSINGITEM,
                        "simple1",
                        ReplaceCondition.ReplacementOperation.EXACTLY,
                        7,
                        "simple2"
                )
        }, item.getReplaceConditions());
        assertEquals(listOf(
                new EquippedPotionEffect(new PassivePotionEffect(EffectType.DAMAGE_RESISTANCE, 1), AttributeModifier.Slot.CHEST)
        ), item.getEquippedEffects());
        assertEquals(new ExtraItemNbt(listOf(
                new NbtPair(new NbtKey("type"), new NbtValue("chest"))
        )), item.getExtraNbt());
        assertEquals(4.25f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, item.getDurability());
        assertNull(item.getRepairItem());
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        for (DamageSource source : DamageSource.values()) {
            assertEquals(0, item.getDamageResistances().getResistance(source));
        }
        assertEquals("armor_texture1", item.getWornTexture().get().getName());
    }

    static void testShield2(CustomShield item) throws ValidationException {
        assertEquals("shield2", item.getName());
        assertEquals(CustomItemType.SHIELD, item.getItemType());
        assertEquals("s2", item.getAlias());
        assertEquals("Second Shield", item.getDisplayName());
        assertEquals(0, item.getLore().length);
        assertEquals(0, item.getAttributes().length);
        assertEquals(0, item.getDefaultEnchantments().length);
        assertArrayEquals(new boolean[] {
                false, false, true, false, false, false
        }, item.getItemFlags());
        assertEquals("test1", item.getTexture().getName());
        assertNull(item.getCustomModel());
        assertEquals(0, item.getPlayerEffects().size());
        assertEquals(0, item.getTargetEffects().size());
        assertEquals(0, item.getCommands().length);
        assertEquals(ReplaceCondition.ConditionOperation.NONE, item.getConditionOperator());
        assertArrayEquals(new ReplaceCondition[] {
                new ReplaceCondition(
                        ReplaceCondition.ReplacementCondition.ISBROKEN,
                        "pickaxe1",
                        ReplaceCondition.ReplacementOperation.ATMOST,
                        1,
                        "bow_one"
                )
        }, item.getReplaceConditions());
        assertEquals(0, item.getEquippedEffects().size());
        assertEquals(new ExtraItemNbt(listOf(
                new NbtPair(new NbtKey("test"), new NbtValue(-5))
        )), item.getExtraNbt());
        assertEquals(1.5f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowEnchanting());
        assertEquals(500, item.getDurability());
        assertNull(item.getRepairItem());
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        assertEquals(4.0, item.getThresholdDamage(), 0.0);
        assertNull(item.getBlockingModel());
    }

    static void testBaseDefault8(CustomItem item) {
        assertEquals("", item.getAlias());
        assertEquals(0, item.getReplaceConditions().length);
        assertEquals(0, item.getEquippedEffects().size());
        assertEquals(0, item.getExtraNbt().getPairs().size());
        assertEquals(1f, item.getAttackRange(), 0f);
        // TODO Call testBaseDefault9
    }

    static void testSimpleDefault8(SimpleCustomItem item) {
        testBaseDefault8(item);
        // TODO Call testSimpleDefault9
    }

    static void testToolDefault8(CustomTool item) {
        testBaseDefault8(item);
        // TODO Call testToolDefault9
    }

    static void testArmorDefault8(CustomArmor item) {
        testToolDefault8(item);
        // TODO Call testArmorDefault9
    }

    static void testHoeDefault8(CustomHoe item) {
        testToolDefault8(item);
        // TODO Call testHoeDefault9
    }

    static void testShearsDefault8(CustomShears item) {
        testToolDefault8(item);
        // TODO Call testShearsDefault9
    }

    static void testBowDefault8(CustomBow item) {
        testToolDefault8(item);

        // TODO Call testBowDefault9
    }

    static void testShieldDefault8(CustomShield item) {
        testToolDefault8(item);
        // TODO Call testShieldDefault9
    }

    static void testWandDefault8(CustomWand item) {
        testBaseDefault8(item);
        // TODO Call testWandDefault9
    }
}
