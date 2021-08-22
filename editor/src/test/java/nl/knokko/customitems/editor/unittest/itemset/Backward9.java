package nl.knokko.customitems.editor.unittest.itemset;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.block.drop.CustomBlockDrop;
import nl.knokko.customitems.block.drop.RequiredItems;
import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.slot.StorageCustomSlot;
import nl.knokko.customitems.container.slot.display.SimpleVanillaDisplayItem;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.*;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.item.texture.CrossbowTextures;
import nl.knokko.customitems.editor.set.recipe.ShapelessRecipe;
import nl.knokko.customitems.editor.set.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.editor.set.recipe.result.CustomItemResult;
import nl.knokko.customitems.editor.set.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.effect.EffectType;
import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PassivePotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.gun.IndirectGunAmmo;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.item.nbt.NbtKey;
import nl.knokko.customitems.item.nbt.NbtPair;
import nl.knokko.customitems.item.nbt.NbtValue;
import nl.knokko.customitems.projectile.CIProjectile;
import nl.knokko.customitems.projectile.effects.*;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.customitems.sound.CISound;
import nl.knokko.customitems.util.ValidationException;
import org.junit.Test;

import java.awt.*;

import static nl.knokko.customitems.editor.unittest.itemset.Backward3.testTextures3;
import static nl.knokko.customitems.editor.unittest.itemset.Backward6.*;
import static nl.knokko.customitems.editor.unittest.itemset.Backward8.*;
import static nl.knokko.customitems.editor.unittest.itemset.BackwardHelper.*;
import static org.junit.Assert.*;

public class Backward9 {

    @Test
    public void testBackwardCompatibility9() throws ValidationException {
        ItemSet old9 = loadItemSet("backward9old");
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

        ItemSet new9 = loadItemSet("backward9new");
        testTexturesNew9(new9, 2);
        testItemsNew9(new9, 4);
        testRecipesNew6(new9, 1);
        testBlocksNew9(new9, 1);
    }

    static void testBlocksNew9(ItemSet set, int numBlocks) {
        assertEquals(numBlocks, set.getBlocks().size());

        CustomBlockValues block1 = set.getBlockByID(1).getValues();
        assertEquals("block1", block1.getName());
        assertEquals(1, block1.getDrops().size());

        CustomBlockDrop drop = block1.getDrops().iterator().next();
        assertEquals(new OutputTable(listOf(
                new OutputTable.Entry(new SimpleVanillaResult(CIMaterial.COBBLESTONE, (byte) 2), 50)
        )), drop.getItemsToDrop());
        assertEquals(SilkTouchRequirement.FORBIDDEN, drop.getSilkTouchRequirement());

        RequiredItems requiredItems = drop.getRequiredItems();
        assertTrue(requiredItems.isEnabled());
        assertFalse(requiredItems.isInverted());
        assertEquals(listOf(
                set.getCustomItemByName("trident_one")
        ), requiredItems.getCustomItems());
        assertEquals(listOf(
                new RequiredItems.VanillaEntry(CIMaterial.STONE_PICKAXE, false)
        ), requiredItems.getVanillaItems());

        assertEquals("quick_wand", block1.getTexture().getName());
    }

    static void testTexturesNew9(ItemSet set, int numTextures) {
        testTexturesNew6(set, numTextures);

        CrossbowTextures crossbowTextures = (CrossbowTextures) set.getTextureByName("crossbow_texture");
        assertEquals("crossbow_texture", crossbowTextures.getName());
        assertImageEqual(loadImage("gun1"), crossbowTextures.getImage());
        assertImageEqual(loadImage("test1"), crossbowTextures.getArrowImage());
        assertImageEqual(loadImage("test1"), crossbowTextures.getFireworkImage());

        assertEquals(3, crossbowTextures.getPullTextures().size());
        CrossbowTextures.PullTexture pull1 = crossbowTextures.getPullTextures().get(0);
        assertEquals(0.0, pull1.getPull(), 0.0);
        assertImageEqual(loadImage("gun1"), pull1.getImage());
        CrossbowTextures.PullTexture pull2 = crossbowTextures.getPullTextures().get(1);
        assertEquals(0.5, pull2.getPull(), 0.0);
        assertImageEqual(loadImage("gun1"), pull2.getImage());
        CrossbowTextures.PullTexture pull3 = crossbowTextures.getPullTextures().get(2);
        assertEquals(0.75, pull3.getPull(), 0.0);
        assertImageEqual(loadImage("test1"), pull3.getImage());
    }

    static void testItemsNew9(ItemSet set, int numItems) throws ValidationException {
        testItemsNew8(set, numItems);

        testTridentDefault9((CustomTrident) set.getCustomItemByName("trident2"));

        testCrossbow1((CustomCrossbow) set.getCustomItemByName("crossbow1"));
        testBlockItem1((CustomBlockItem) set.getCustomItemByName("block_item1"));
    }

    static void testBlockItem1(CustomBlockItem item) throws ValidationException {
        assertEquals("block_item1", item.getName());
        assertEquals(CustomItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("bi1", item.getAlias());
        assertEquals("Block 1", item.getDisplayName());
        assertArrayEquals(new String[] {
                "This is not an actual block",
                "Just the item that places it!"
        }, item.getLore());
        assertArrayEquals(new AttributeModifier[] {
                new AttributeModifier(
                        AttributeModifier.Attribute.ATTACK_SPEED,
                        AttributeModifier.Slot.MAINHAND,
                        AttributeModifier.Operation.ADD,
                        5.0
                )
        }, item.getAttributes());
        assertArrayEquals(new Enchantment[] {
                new Enchantment(EnchantmentType.DURABILITY, 2)
        }, item.getDefaultEnchantments());
        assertArrayEquals(new boolean[] {
                false, false, true, true, false, false
        }, item.getItemFlags());
        assertEquals(listOf(
                new PotionEffect(EffectType.REGENERATION, 100, 1)
        ), item.getPlayerEffects());
        assertEquals(listOf(
                new PotionEffect(EffectType.POISON, 100, 2)
        ), item.getTargetEffects());
        assertArrayEquals(new String[] {
                "kill @a"
        }, item.getCommands());
        assertEquals(ReplaceCondition.ConditionOperation.NONE, item.getConditionOperator());
        assertArrayEquals(new ReplaceCondition[] {
                new ReplaceCondition(
                        ReplaceCondition.ReplacementCondition.ISBROKEN,
                        "trident2",
                        ReplaceCondition.ReplacementOperation.EXACTLY,
                        1,
                        "crossbow1"
                )
        }, item.getReplaceConditions());
        assertEquals(listOf(
                new EquippedPotionEffect(new PassivePotionEffect(EffectType.FAST_DIGGING, 3), AttributeModifier.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(new ExtraItemNbt(listOf(
                new NbtPair(new NbtKey("block"), new NbtValue(1))
        )), item.getExtraNbt());
        assertEquals(0.25, item.getAttackRange(), 0.0);
        assertEquals("block1", item.getBlock().getValues().getName());
        assertEquals(15, item.getStackSize());
    }

    static void testCrossbow1(CustomCrossbow item) throws ValidationException {
        assertEquals("crossbow1", item.getName());
        assertEquals(CustomItemType.CROSSBOW, item.getItemType());
        assertEquals("cb1", item.getAlias());
        assertEquals("Test Crossbow", item.getDisplayName());
        assertArrayEquals(new String[] {
                "We finally have crossbows"
        }, item.getLore());
        assertArrayEquals(new AttributeModifier[] {
                new AttributeModifier(
                        AttributeModifier.Attribute.MOVEMENT_SPEED,
                        AttributeModifier.Slot.OFFHAND,
                        AttributeModifier.Operation.MULTIPLY,
                        0.8
                )
        }, item.getAttributes());
        assertArrayEquals(new Enchantment[] {
                new Enchantment(EnchantmentType.PIERCING, 2)
        }, item.getDefaultEnchantments());
        assertArrayEquals(new boolean[] {
                true, true, true, true, true, true
        }, item.getItemFlags());
        assertEquals("crossbow_texture", item.getTexture().getName());
        assertEquals(listOf(
                new PotionEffect(EffectType.SPEED, 20, 1)
        ), item.getPlayerEffects());
        assertEquals(listOf(
                new PotionEffect(EffectType.WITHER, 50, 2)
        ), item.getTargetEffects());
        assertArrayEquals(new String[] {
                "effect @p night_vision 5"
        }, item.getCommands());
        assertEquals(ReplaceCondition.ConditionOperation.OR, item.getConditionOperator());
        assertArrayEquals(new ReplaceCondition[] {
                new ReplaceCondition(
                        ReplaceCondition.ReplacementCondition.HASITEM,
                        "trident2",
                        ReplaceCondition.ReplacementOperation.EXACTLY,
                        1,
                        "trident_one"
                )
        }, item.getReplaceConditions());
        assertEquals(listOf(
                new EquippedPotionEffect(new PassivePotionEffect(EffectType.INVISIBILITY, 1), AttributeModifier.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(new ExtraItemNbt(listOf(
                new NbtPair(new NbtKey("cross"), new NbtValue("bow"))
        )), item.getExtraNbt());
        assertEquals(0.5, item.getAttackRange(), 0.0);
        assertFalse(item.allowEnchanting());
        assertTrue(item.allowAnvilActions());
        assertEquals(567, item.getDurability());
        assertEquals(new SimpleVanillaIngredient(
                CIMaterial.WHITE_WOOL, (byte) 2, new SimpleVanillaResult(CIMaterial.BLACK_WOOL, (byte) 5)
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

        CustomContainer container3 = set.getContainerByName("container3");
        assertEquals(new StorageCustomSlot(new SlotDisplay(
                new SimpleVanillaDisplayItem(CIMaterial.IRON_BARDING), "safe storage", new String[0], 1
        )), container3.getSlot(0, 0));
        for (int x = 1; x < 9; x++) {
            assertEquals(new StorageCustomSlot(null), container3.getSlot(x, 0));
        }
    }

    static void testProjectilesOld9(ItemSet set, int numProjectiles) {
        testProjectilesOld6(set, numProjectiles);

        CIProjectile crazy2 = set.getProjectileByName("crazy2");
        assertEquals(1, crazy2.inFlightEffects.size());
        ProjectileEffects inFlightEffects = crazy2.inFlightEffects.iterator().next();
        assertEquals(listOf(
                new PushOrPull(0.5f, 1.5f),
                new ShowFirework(listOf(
                        new ShowFirework.Effect(true, false, ShowFirework.EffectType.CREEPER, listOf(
                                new Color(200, 100, 150)
                        ), listOf(
                                new Color(0, 50, 150)
                        ))
                ))
        ), inFlightEffects.effects);

        assertEquals(listOf(
                new PlaySound(CISound.BLOCK_ANVIL_HIT, 1.25f, 1.75f),
                new PotionAura(3f, listOf(
                        new PotionEffect(EffectType.FAST_DIGGING, 100, 2)
                ))
        ), crazy2.impactEffects);

        assertEquals(1f, crazy2.launchKnockback, 0.0);
        assertEquals(2f, crazy2.impactKnockback, 0.0);

        assertEquals(listOf(
                new PotionEffect(EffectType.INCREASE_DAMAGE, 100, 2)
        ), crazy2.impactPotionEffects);
    }

    static void testRecipesOld9(ItemSet set, int numRecipes) {
        testRecipesOld8(set, numRecipes);

        assertTrue(set.getBackingRecipes().contains(createShapelessRecipe2(set)));
    }

    static ShapelessRecipe createShapelessRecipe2(ItemSet set) {
        Ingredient[] ingredients = {
                new CustomItemIngredient(set.getCustomItemByName("simple1"), (byte) 3, new CustomItemResult(
                        set.getCustomItemByName("simple2"), (byte) 2
                ))
        };
        return new ShapelessRecipe(new SimpleVanillaResult(CIMaterial.CHORUS_PLANT, (byte) 8), ingredients);
    }

    static void testItemsOld9(ItemSet set, int numItems) throws ValidationException {
        testItemsOld8(set, numItems);

        testWandDefault9((CustomWand) set.getCustomItemByName("wand2"));
        testSimpleDefault9((SimpleCustomItem) set.getCustomItemByName("simple3"));
        testToolDefault9((CustomTool) set.getCustomItemByName("shovel2"));
        testHoeDefault9((CustomHoe) set.getCustomItemByName("hoe3"));
        testShearsDefault9((CustomShears) set.getCustomItemByName("shears3"));
        test3dHelmetDefault9((CustomHelmet3D) set.getCustomItemByName("3dhelmet1"));
        testBowDefault9((CustomBow) set.getCustomItemByName("bow3"));
        testArmorDefault9((CustomArmor) set.getCustomItemByName("chestplate2"));
        testShieldDefault9((CustomShield) set.getCustomItemByName("shield2"));

        testGun1((CustomGun) set.getCustomItemByName("gun1"), set);
        testPocketContainer1((CustomPocketContainer) set.getCustomItemByName("pocket_container1"), set);
        testFood1((CustomFood) set.getCustomItemByName("food1"));
    }

    static void testGun1(CustomGun item, ItemSet set) throws ValidationException {
        assertEquals("gun1", item.getName());
        assertEquals(CustomItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("g1", item.getAlias());
        assertEquals("The first gun", item.getDisplayName());
        assertArrayEquals(new String[] {
                "It's like a wand,",
                "but requires ammunition."
        }, item.getLore());
        assertArrayEquals(new AttributeModifier[] {
                new AttributeModifier(
                        AttributeModifier.Attribute.KNOCKBACK_RESISTANCE,
                        AttributeModifier.Slot.OFFHAND,
                        AttributeModifier.Operation.ADD,
                        0.4
                )
        }, item.getAttributes());
        assertArrayEquals(new Enchantment[] {
                new Enchantment(EnchantmentType.SILK_TOUCH, 1)
        }, item.getDefaultEnchantments());
        assertArrayEquals(new boolean[] {
                false, true, true, false, false, false
        }, item.getItemFlags());
        assertEquals("gun1", item.getTexture().getName());
        assertNull(item.getCustomModel());
        assertEquals(listOf(
                new PotionEffect(EffectType.SPEED, 10, 1)
        ), item.getPlayerEffects());
        assertEquals(listOf(
                new PotionEffect(EffectType.SLOW, 20, 1)
        ), item.getTargetEffects());
        assertArrayEquals(new String[] {
                "summon arrow"
        }, item.getCommands());
        assertEquals(ReplaceCondition.ConditionOperation.OR, item.getConditionOperator());
        assertArrayEquals(new ReplaceCondition[] {
                new ReplaceCondition(
                        ReplaceCondition.ReplacementCondition.HASITEM,
                        "simple2",
                        ReplaceCondition.ReplacementOperation.ATMOST,
                        5,
                        "simple1"
                )
        }, item.getReplaceConditions());
        assertEquals(listOf(
                new EquippedPotionEffect(new PassivePotionEffect(EffectType.NIGHT_VISION, 1), AttributeModifier.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(new ExtraItemNbt(listOf(
                new NbtPair(new NbtKey("the"), new NbtValue("gun"))
        )), item.getExtraNbt());
        assertEquals(1.25, item.getAttackRange(), 0.0);
        assertEquals("crazy1", item.projectile.name);
        IndirectGunAmmo ammo = (IndirectGunAmmo) item.ammo;
        assertEquals(new CustomItemIngredient(set.getCustomItemByName("simple1"), (byte) 1, null), ammo.reloadItem);
        assertEquals(15, ammo.cooldown);
        assertEquals(35, ammo.storedAmmo);
        assertEquals(25, ammo.reloadTime);
        assertNull(ammo.startReloadSound);
        assertEquals(CISound.ENTITY_SKELETON_HORSE_HURT, ammo.finishReloadSound);
        assertEquals(2, item.amountPerShot);
    }

    static void testPocketContainer1(CustomPocketContainer item, ItemSet set) throws ValidationException {
        assertEquals("pocket_container1", item.getName());
        assertEquals(CustomItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("pc1", item.getAlias());
        assertEquals("Pocket Container", item.getDisplayName());
        assertArrayEquals(new String[] {
                "You can carry containers around.",
                "Isn't that great?"
        }, item.getLore());
        assertArrayEquals(new AttributeModifier[] {
                new AttributeModifier(
                        AttributeModifier.Attribute.ATTACK_DAMAGE,
                        AttributeModifier.Slot.OFFHAND,
                        AttributeModifier.Operation.ADD,
                        5.0
                )
        }, item.getAttributes());
        assertArrayEquals(new Enchantment[] {
                new Enchantment(EnchantmentType.DAMAGE_ARTHROPODS, 2)
        }, item.getDefaultEnchantments());
        assertArrayEquals(new boolean[6], item.getItemFlags());
        assertEquals("test1", item.getTexture().getName());
        assertStringResourceEquals("backward/itemset/model/spear_diamond.json", item.getCustomModel());
        assertEquals(listOf(
                new PotionEffect(EffectType.HEAL, 1, 1)
        ), item.getPlayerEffects());
        assertEquals(listOf(
                new PotionEffect(EffectType.HARM, 1, 1)
        ), item.getTargetEffects());
        assertArrayEquals(new String[] {
                "setblock ~ ~ ~ stone"
        }, item.getCommands());
        assertEquals(ReplaceCondition.ConditionOperation.NONE, item.getConditionOperator());
        assertArrayEquals(new ReplaceCondition[] {
                new ReplaceCondition(
                        ReplaceCondition.ReplacementCondition.ISBROKEN,
                        "sword1",
                        ReplaceCondition.ReplacementOperation.ATLEAST,
                        5,
                        "simple1"
                )
        }, item.getReplaceConditions());
        assertEquals(listOf(
                new EquippedPotionEffect(new PassivePotionEffect(EffectType.SLOW_DIGGING, 2), AttributeModifier.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(new ExtraItemNbt(listOf(
                new NbtPair(new NbtKey("rank"), new NbtValue(1))
        )), item.getExtraNbt());
        assertEquals(0.75, item.getAttackRange(), 0.0);
        assertEquals(listOf(
                set.getContainerByName("container2")
        ), item.getContainers());
    }

    static void testFood1(CustomFood item) throws ValidationException {
        assertEquals("food1", item.getName());
        assertEquals(CustomItemType.DIAMOND_HOE, item.getItemType());
        assertEquals("f1", item.getAlias());
        assertEquals("Food!!", item.getDisplayName());
        assertArrayEquals(new String[] {
                "Food!",
                "Food!!"
        }, item.getLore());
        assertArrayEquals(new AttributeModifier[] {
                new AttributeModifier(
                        AttributeModifier.Attribute.LUCK,
                        AttributeModifier.Slot.OFFHAND,
                        AttributeModifier.Operation.ADD_FACTOR,
                        1.0
                )
        }, item.getAttributes());
        assertArrayEquals(new Enchantment[] {
                new Enchantment(EnchantmentType.LOOT_BONUS_BLOCKS, 2)
        }, item.getDefaultEnchantments());
        assertArrayEquals(new boolean[] {
                false, false, true, true, true, true
        }, item.getItemFlags());
        assertEquals("test1", item.getTexture().getName());
        assertStringResourceEquals("backward/itemset/model/blue_crossbow.json", item.getCustomModel());
        assertEquals(listOf(
                new PotionEffect(EffectType.SATURATION, 100, 1)
        ), item.getPlayerEffects());
        assertEquals(listOf(
                new PotionEffect(EffectType.HUNGER, 100, 3)
        ), item.getTargetEffects());
        assertArrayEquals(new String[] {
                "effect @p clear"
        }, item.getCommands());
        assertEquals(ReplaceCondition.ConditionOperation.AND, item.getConditionOperator());
        assertArrayEquals(new ReplaceCondition[] {
                new ReplaceCondition(
                        ReplaceCondition.ReplacementCondition.MISSINGITEM,
                        "simple1",
                        ReplaceCondition.ReplacementOperation.EXACTLY,
                        3,
                        "simple2"
                )
        }, item.getReplaceConditions());
        assertEquals(listOf(
                new EquippedPotionEffect(new PassivePotionEffect(EffectType.JUMP, 1), AttributeModifier.Slot.MAINHAND)
        ), item.getEquippedEffects());
        assertEquals(new ExtraItemNbt(listOf(
                new NbtPair(new NbtKey("food", "is"), new NbtValue("jum jum"))
        )), item.getExtraNbt());
        assertEquals(1.5, item.getAttackRange(), 0.0);
        assertEquals(6, item.foodValue);
        assertEquals(listOf(
                new PotionEffect(EffectType.WATER_BREATHING, 300, 1)
        ), item.eatEffects);
        assertEquals(37, item.eatTime);
        assertEquals(CISound.ENTITY_WITCH_DRINK, item.eatSound);
        assertEquals(1.25, item.soundVolume, 0.0);
        assertEquals(0.75, item.soundPitch, 0.0);
        assertEquals(7, item.soundPeriod);
        assertEquals(61, item.maxStacksize);
    }

    static void testBaseDefault9(CustomItem item) {
        // TODO Call testBaseDefault10
    }

    static void testSimpleDefault9(SimpleCustomItem item) {
        testBaseDefault9(item);
        // TODO Call testSimpleDefault10
    }

    static void testToolDefault9(CustomTool item) {
        testBaseDefault9(item);
        // TODO Call testToolDefault10
    }

    static void testArmorDefault9(CustomArmor item) {
        testToolDefault9(item);
        // TODO Call testArmorDefault10
    }

    static void testHoeDefault9(CustomHoe item) {
        testToolDefault9(item);
        // TODO Call testHoeDefault10
    }

    static void testShearsDefault9(CustomShears item) {
        testToolDefault9(item);
        // TODO Call testShearsDefault10
    }

    static void testBowDefault9(CustomBow item) {
        testToolDefault9(item);
        // TODO Call testBowDefault10
    }

    static void testShieldDefault9(CustomShield item) {
        testToolDefault9(item);
        // TODO Call testShieldDefault10
    }
    static void testWandDefault9(CustomWand item) {
        testBaseDefault9(item);
        // TODO Call testWandDefault10
    }

    static void test3dHelmetDefault9(CustomHelmet3D item) {
        testArmorDefault9(item);
        // TODO Call test3dHelmetDefault10
    }

    static void testTridentDefault9(CustomTrident item) {
        testToolDefault9(item);
        // TODO Call testTridentDefault10
    }
}
