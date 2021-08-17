package customitems.plugin.set.backward;

import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.customitems.container.fuel.FuelEntry;
import nl.knokko.customitems.container.slot.FuelCustomSlot;
import nl.knokko.customitems.container.slot.InputCustomSlot;
import nl.knokko.customitems.container.slot.OutputCustomSlot;
import nl.knokko.customitems.container.slot.display.CustomItemDisplayItem;
import nl.knokko.customitems.container.slot.display.DataVanillaDisplayItem;
import nl.knokko.customitems.container.slot.display.SimpleVanillaDisplayItem;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.drops.*;
import nl.knokko.customitems.effect.EffectType;
import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PassivePotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.item.nbt.NbtKey;
import nl.knokko.customitems.item.nbt.NbtPair;
import nl.knokko.customitems.item.nbt.NbtValue;
import nl.knokko.customitems.plugin.recipe.ShapedCustomRecipe;
import nl.knokko.customitems.plugin.recipe.ingredient.CustomIngredient;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;
import nl.knokko.customitems.plugin.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.plugin.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.*;
import nl.knokko.customitems.recipe.ContainerRecipe;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.customitems.util.StringEncoder;
import nl.knokko.customitems.util.ValidationException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;

import static customitems.plugin.set.backward.Backward6.*;
import static customitems.plugin.set.backward.Backward7.testContainers7;
import static customitems.plugin.set.backward.BackwardHelper.listOf;
import static customitems.plugin.set.backward.BackwardHelper.loadItemSet;
import static nl.knokko.core.plugin.CorePlugin.useNewCommands;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class Backward8 {

    public static void testBackwardCompatibility8() throws ValidationException {
        if (useNewCommands()) {
            ItemSet newSet = loadItemSet("backward8new");
            testItemsNew8(newSet, 2);
            testRecipesNew6(newSet, 1);
        } else {
            ItemSet oldSet = loadItemSet("backward8old");
            testItemsOld8(oldSet, 30);
            testRecipesOld8(oldSet, 4);
            testBlockDropsOld8(oldSet, 2);
            testMobDropsOld8(oldSet, 2);
            testProjectilesOld6(oldSet, 1);
            testFuelRegistriesOld8(oldSet, 1);
            testContainersOld8(oldSet, 2);
        }
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
        assertTrue(item.allowVanillaEnchanting());
        assertEquals(500, item.getMaxDurability());
        assertEquals(new NoIngredient(), item.getRepairItem());
        assertEquals(1, item.getEntityHitDurabilityLoss());
        assertEquals(2, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.throwDurabilityLoss);
        assertEquals(1.0, item.throwDamageMultiplier, 0.0);
        assertEquals(1.0, item.throwSpeedMultiplier, 0.0);
    }

    static void testContainersOld8(ItemSet set, int numContainers) {
        testContainers7(set, numContainers);

        CustomContainer container2 = set.getContainerInfo("container2").getContainer();
        ContainerRecipe recipe = container2.getRecipes().stream().findFirst().get();

        OutputTable output = recipe.getOutputs().stream().findFirst().get().getOutputTable();
        assertEquals(listOf(
                new OutputTable.Entry(set.getCustomItemByName("simple1").create(1), 40),
                new OutputTable.Entry(ItemHelper.createStack(CIMaterial.DIAMOND.name(), 2), 50)
        ), output.getEntries());

        OutputCustomSlot outputSlot = (OutputCustomSlot) container2.getSlot(0, 0);
        SlotDisplay outputPlaceholder = outputSlot.getPlaceholder();
        assertEquals("Simple Test", outputPlaceholder.getDisplayName());
        assertEquals(2, outputPlaceholder.getAmount());
        assertEquals(new CustomItemDisplayItem(set.getCustomItemByName("simple1")), outputPlaceholder.getItem());
        assertEquals(0, outputPlaceholder.getLore().length);

        InputCustomSlot inputSlot = (InputCustomSlot) container2.getSlot(1, 0);
        SlotDisplay inputPlaceholder = inputSlot.getPlaceholder();
        assertEquals("", inputPlaceholder.getDisplayName());
        assertArrayEquals(new String[] {
                "test the lore"
        }, inputPlaceholder.getLore());
        assertEquals(1, inputPlaceholder.getAmount());
        assertEquals(new SimpleVanillaDisplayItem(CIMaterial.ACACIA_FENCE), inputPlaceholder.getItem());

        FuelCustomSlot fuelSlot = (FuelCustomSlot) container2.getSlot(0, 1);
        SlotDisplay fuelPlaceholder = fuelSlot.getPlaceholder();
        assertEquals("", fuelPlaceholder.getDisplayName());
        assertEquals(0, fuelPlaceholder.getLore().length);
        assertEquals(10, fuelPlaceholder.getAmount());
        assertEquals(new DataVanillaDisplayItem(CIMaterial.WOOL, (byte) 8), fuelPlaceholder.getItem());
    }

    static void testFuelRegistriesOld8(ItemSet set, int numFuelRegistries) {
        assertEquals(numFuelRegistries, set.getNumFuelRegistries());

        CustomFuelRegistry registry1 = set.getFuelRegistryByName("registry1");
        assertEquals("registry1", registry1.getName());
        assertEquals(listOf(
                new FuelEntry(new SimpleVanillaIngredient(CIMaterial.COAL, (byte) 1, null), 100),
                new FuelEntry(new CustomIngredient(set.getCustomItemByName("simple1"), (byte) 1, null), 500)
        ), registry1.getEntries());
    }

    static void testMobDropsOld8(ItemSet set, int numBlockDrops) {
        testMobDropsOld6(set, numBlockDrops);

        EntityDrop swordMobDrop = set.getBackingMobDrops()[CIEntityType.ZOMBIE.ordinal()][0];
        EntityDrop axeMobDrop = set.getBackingMobDrops()[CIEntityType.SKELETON.ordinal()][0];
        testDefaultMobDrop8(swordMobDrop);
        testDefaultMobDrop8(axeMobDrop);
    }

    private static ItemStack itemStackFromCopiedString(String encoded) {
        String serialized = StringEncoder.decode(encoded);

        YamlConfiguration helperConfig = new YamlConfiguration();
        try {
            helperConfig.loadFromString(serialized);
            return helperConfig.getItemStack("TheItemStack");
        } catch (InvalidConfigurationException invalidConfig) {
            throw new RuntimeException(invalidConfig);
        }
    }

    @SuppressWarnings("deprecation")
    static void testBlockDropsOld8(ItemSet set, int numBlockDrops) {
        testBlockDropsOld6(set, numBlockDrops);

        BlockDrop[] drops = set.getDrops(CIMaterial.STONE);
        testDefaultBlockDrop8(drops[0]);

        BlockDrop blockDrop = drops[1];
        assertEquals(BlockType.STONE, blockDrop.getBlock());
        assertTrue(blockDrop.allowSilkTouch());
        Drop drop = blockDrop.getDrop();

        assertFalse(drop.cancelNormalDrop());
        assertEquals(listOf(
                set.getCustomItemByName("pickaxe1"), set.getCustomItemByName("pickaxe_two")
        ), drop.getRequiredHeldItems());

        OutputTable dropTable = drop.getDropTable();
        assertEquals(35, dropTable.getNothingChance());

        ItemStack dataStack = ItemHelper.createStack(CIMaterial.WOOL.name(), 3);
        MaterialData data = dataStack.getData();
        data.setData((byte) 3);
        dataStack.setData(data);
        dataStack.setDurability(data.getData());

        assertEquals(listOf(
                new OutputTable.Entry(set.getCustomItemByName("simple1").create(1), 30),
                new OutputTable.Entry(ItemHelper.createStack(CIMaterial.ACACIA_DOOR.name(), 2), 20),
                new OutputTable.Entry(dataStack, 10),
                new OutputTable.Entry(itemStackFromCopiedString(copiedFromServerString()), 5)
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

        assertTrue(Arrays.stream(set.getRecipes()).anyMatch(recipe -> recipe.equals(createShapedRecipe3(set))));
    }

    static ShapedCustomRecipe createShapedRecipe3(ItemSet set) {
        Ingredient[] ingredients = {
                new CustomIngredient(set.getCustomItemByName("simple1"), (byte) 1, null), new NoIngredient(), new NoIngredient(),
                new NoIngredient(), new CustomIngredient(set.getCustomItemByName("simple2"), (byte) 1, null), new NoIngredient(),
                new NoIngredient(), new NoIngredient(), new CustomIngredient(set.getCustomItemByName("simple3"), (byte) 1, null)
        };
        return new ShapedCustomRecipe(itemStackFromCopiedString(copiedFromServerString()), ingredients);
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
        assertEquals(0, item.getPlayerEffects().size());
        assertEquals(0, item.getTargetEffects().size());
        assertEquals(0, item.getCommands().length);
        assertEquals(ReplaceCondition.ConditionOperation.AND, item.getConditionOperator());
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
                new NbtPair(new NbtKey("parent", "child", "test_string"), new NbtValue("2"))
        )), item.getExtraNbt());
        assertEquals(1.5f, item.getAttackRange(), 0f);
        assertEquals("crazy1", item.projectile.name);
        assertNull(item.charges);
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
        assertEquals(0, item.getPlayerEffects().size());
        assertEquals(0, item.getTargetEffects().size());
        assertEquals(0, item.getCommands().length);
        assertEquals(ReplaceCondition.ConditionOperation.OR, item.getConditionOperator());
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
        assertTrue(item.allowVanillaEnchanting());
        assertEquals(500, item.getMaxDurability());
        assertEquals(new NoIngredient(), item.getRepairItem());
        assertEquals(2, item.getEntityHitDurabilityLoss());
        assertEquals(1, item.getBlockBreakDurabilityLoss());
    }

    static void testHoe3(CustomHoe item) throws ValidationException {
        assertEquals("hoe3", item.getName());
        assertEquals(CustomItemType.IRON_HOE, item.getItemType());
        assertEquals("h3", item.getAlias());
        assertEquals("Third Hoe", item.getDisplayName());
        assertEquals(0, item.getLore().length);
        assertEquals(0, item.getAttributes().length);
        assertEquals(0, item.getDefaultEnchantments().length);
        assertArrayEquals(new boolean[] {
                false, false, true, false, false, false
        }, item.getItemFlags());
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
        assertTrue(item.allowVanillaEnchanting());
        assertEquals(500, item.getMaxDurability());
        assertEquals(new NoIngredient(), item.getRepairItem());
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        assertEquals(1, item.getTillDurabilityLoss());
    }

    static void testShears3(CustomShears item) throws ValidationException {
        assertEquals("shears3", item.getName());
        assertEquals(CustomItemType.SHEARS, item.getItemType());
        assertEquals("sh3", item.getAlias());
        assertEquals("Third Shears", item.getDisplayName());
        assertEquals(0, item.getLore().length);
        assertEquals(0, item.getAttributes().length);
        assertEquals(0, item.getDefaultEnchantments().length);
        assertArrayEquals(new boolean[] {
                false, false, true, false, false, false
        }, item.getItemFlags());
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
                new EquippedPotionEffect(new PassivePotionEffect(EffectType.INVISIBILITY, 1), AttributeModifier.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(new ExtraItemNbt(listOf(
                new NbtPair(new NbtKey("int_type"), new NbtValue(9))
        )), item.getExtraNbt());
        assertEquals(1.25f, item.getAttackRange(), 0f);
        assertTrue(item.allowAnvilActions());
        assertTrue(item.allowVanillaEnchanting());
        assertEquals(500, item.getMaxDurability());
        assertEquals(new NoIngredient(), item.getRepairItem());
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
        assertFalse(item.allowVanillaEnchanting());
        assertEquals(123, item.getMaxDurability());
        assertEquals(new NoIngredient(), item.getRepairItem());
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
        assertEquals(listOf(
                new PotionEffect(EffectType.NIGHT_VISION, 1000, 1)
        ), item.getPlayerEffects());
        assertEquals(listOf(
                new PotionEffect(EffectType.WITHER, 100, 2)
        ), item.getTargetEffects());
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
        assertTrue(item.allowVanillaEnchanting());
        assertEquals(new NoIngredient(), item.getRepairItem());
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
        assertTrue(item.allowVanillaEnchanting());
        assertEquals(500, item.getMaxDurability());
        assertEquals(new NoIngredient(), item.getRepairItem());
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        for (DamageSource source : DamageSource.values()) {
            assertEquals(0, item.getDamageResistances().getResistance(source));
        }
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
        assertEquals(0, item.getPlayerEffects().size());
        assertEquals(0, item.getTargetEffects().size());
        assertEquals(0, item.getCommands().length);
        assertEquals(ReplaceCondition.ConditionOperation.AND, item.getConditionOperator());
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
        assertTrue(item.allowVanillaEnchanting());
        assertEquals(500, item.getMaxDurability());
        assertEquals(new NoIngredient(), item.getRepairItem());
        assertEquals(0, item.getEntityHitDurabilityLoss());
        assertEquals(0, item.getBlockBreakDurabilityLoss());
        assertEquals(4.0, item.getDurabilityThreshold(), 0.0);
    }

    static void testBaseDefault8(CustomItem item) {
        // Wands don't have empty string as default alias due to issue #124
        if (!(item instanceof CustomWand)) {
            assertEquals("", item.getAlias());
        }
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
