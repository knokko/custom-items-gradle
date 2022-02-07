package nl.knokko.customitems.plugin.data;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitOutput;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomToolValues;
import nl.knokko.customitems.item.SimpleCustomItemValues;
import nl.knokko.customitems.item.command.ItemCommand;
import nl.knokko.customitems.item.command.ItemCommandEvent;
import nl.knokko.customitems.item.command.ItemCommandSystem;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.texture.BaseTextureValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static nl.knokko.customitems.plugin.data.IOHelper.getResourceBitInput;
import static org.junit.Assert.*;

public class TestPlayerCommandCooldowns {

    private ItemSetWrapper createSingleItemSet(CustomItemValues item) {
        try {
            ItemSet itemSet = new ItemSet(ItemSet.Side.EDITOR);

            if (item.getTextureReference() == null) {
                BaseTextureValues testTexture = new BaseTextureValues(true);
                testTexture.setName("test_texture");
                testTexture.setImage(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB));
                itemSet.addTexture(testTexture);

                item.setTexture(itemSet.getTextureReference("test_texture"));
            }

            itemSet.addItem(item);

            ItemSetWrapper wrapper = new ItemSetWrapper();
            wrapper.setItemSet(itemSet);
            return wrapper;
        } catch (ValidationException | ProgrammingValidationException error) {
            throw new RuntimeException(error);
        }
    }

    private ItemCommand createTestCommand(String rawCommand, int cooldown) {
        ItemCommand command = new ItemCommand(true);
        command.setRawCommand(rawCommand);
        command.setCooldown(cooldown);
        return command;
    }

    private CustomItemValues generateOriginalTestItem() {
        List<ItemCommand> blockBreakCommands = new ArrayList<>(4);
        blockBreakCommands.add(createTestCommand("summon sheep", 20));
        blockBreakCommands.add(createTestCommand("summon zombie", 30));
        blockBreakCommands.add(createTestCommand("summon zombie", 40));
        blockBreakCommands.add(createTestCommand("summon zombie", 50));

        List<ItemCommand> leftClickCommands = new ArrayList<>(3);
        leftClickCommands.add(createTestCommand("summon skeleton", 30));
        leftClickCommands.add(createTestCommand("summon chicken", 40));
        leftClickCommands.add(createTestCommand("summon bat", 20));

        List<ItemCommand> rightClickCommands = new ArrayList<>(1);
        rightClickCommands.add(createTestCommand("summon villager", 40));

        ItemCommandSystem commandSystem = new ItemCommandSystem(true);
        commandSystem.setCommandsFor(ItemCommandEvent.BREAK_BLOCK, blockBreakCommands);
        commandSystem.setCommandsFor(ItemCommandEvent.LEFT_CLICK_GENERAL, leftClickCommands);
        commandSystem.setCommandsFor(ItemCommandEvent.RIGHT_CLICK_GENERAL, rightClickCommands);

        CustomItemValues testItem = new SimpleCustomItemValues(true);
        testItem.setName("test_item");
        testItem.setCommandSystem(commandSystem);
        return testItem;
    }

    private CustomItemValues generateNewTestItem() {
        List<ItemCommand> blockBreakCommands = new ArrayList<>(5);
        blockBreakCommands.add(createTestCommand("summon pig", 20));
        blockBreakCommands.add(createTestCommand("summon zombie", 30));
        blockBreakCommands.add(createTestCommand("summon zombie", 40));
        blockBreakCommands.add(createTestCommand("summon zombie", 50));
        blockBreakCommands.add(createTestCommand("summon sheep", 20));

        List<ItemCommand> leftClickCommands = new ArrayList<>(2);
        leftClickCommands.add(createTestCommand("summon skeleton", 30));
        leftClickCommands.add(createTestCommand("summon chicken", 40));

        ItemCommandSystem commandSystem = new ItemCommandSystem(true);
        commandSystem.setCommandsFor(ItemCommandEvent.BREAK_BLOCK, blockBreakCommands);
        commandSystem.setCommandsFor(ItemCommandEvent.LEFT_CLICK_GENERAL, leftClickCommands);

        CustomItemValues testItem = new SimpleCustomItemValues(true);
        testItem.setName("test_item");
        testItem.setCommandSystem(commandSystem);
        return testItem;
    }

    private void setTestCooldowns(PlayerCommandCooldowns cooldowns) {
        CustomItemValues originalItem = generateOriginalTestItem();
        long currentTime = 100;

        cooldowns.setOnCooldown(originalItem, ItemCommandEvent.BREAK_BLOCK, 0, currentTime);
        cooldowns.setOnCooldown(originalItem, ItemCommandEvent.BREAK_BLOCK, 2, currentTime);
        cooldowns.setOnCooldown(originalItem, ItemCommandEvent.LEFT_CLICK_GENERAL, 1, currentTime);
        cooldowns.setOnCooldown(originalItem, ItemCommandEvent.RIGHT_CLICK_GENERAL, 0, currentTime);
    }

    @Test
    public void testCooldownCheck() {
        PlayerCommandCooldowns cooldowns = new PlayerCommandCooldowns();
        setTestCooldowns(cooldowns);
        testOriginalItemCooldowns(cooldowns);
    }

    private void testOriginalItemCooldowns(PlayerCommandCooldowns cooldowns) {
        CustomItemValues originalItem = generateOriginalTestItem();

        long[] tickTimes = { 100, 135, 200 };
        boolean[][] stillOnCooldown = {
                { true, true, true, true },
                { false, true, true, true },
                { false, false, false, false }
        };

        for (int trial = 0; trial < 3; trial++) {
            boolean[] remainingCooldowns = stillOnCooldown[trial];
            long currentTime = tickTimes[trial];

            assertEquals(remainingCooldowns[0], cooldowns.isOnCooldown(originalItem, ItemCommandEvent.BREAK_BLOCK, 0, currentTime));
            assertFalse(cooldowns.isOnCooldown(originalItem, ItemCommandEvent.BREAK_BLOCK, 1, currentTime));
            assertEquals(remainingCooldowns[1], cooldowns.isOnCooldown(originalItem, ItemCommandEvent.BREAK_BLOCK, 2, currentTime));
            assertFalse(cooldowns.isOnCooldown(originalItem, ItemCommandEvent.BREAK_BLOCK, 3, currentTime));
            assertFalse(cooldowns.isOnCooldown(originalItem, ItemCommandEvent.LEFT_CLICK_GENERAL, 0, currentTime));
            assertEquals(remainingCooldowns[2], cooldowns.isOnCooldown(originalItem, ItemCommandEvent.LEFT_CLICK_GENERAL, 1, currentTime));
            assertEquals(remainingCooldowns[3], cooldowns.isOnCooldown(originalItem, ItemCommandEvent.RIGHT_CLICK_GENERAL, 0, currentTime));
        }
    }

    @Test
    public void testBackwardCompatibility1() throws UnknownEncodingException {
        PlayerCommandCooldowns loadedCooldowns = new PlayerCommandCooldowns();
        loadedCooldowns.load(
                getResourceBitInput("data/commandCooldowns/backward1.bin", 185),
                createSingleItemSet(generateOriginalTestItem())
        );

        PlayerCommandCooldowns originalCooldowns = new PlayerCommandCooldowns();
        setTestCooldowns(originalCooldowns);

        assertEquals(originalCooldowns, loadedCooldowns);
    }

    @Test
    public void testSaveEquality() throws UnknownEncodingException {
        ItemSetWrapper itemSet = createSingleItemSet(generateOriginalTestItem());

        PlayerCommandCooldowns originalCooldowns = new PlayerCommandCooldowns();
        setTestCooldowns(originalCooldowns);

        ByteArrayBitOutput bitOutput = new ByteArrayBitOutput();
        originalCooldowns.save(bitOutput, itemSet);

        ByteArrayBitInput bitInput = new ByteArrayBitInput(bitOutput.getBytes());
        PlayerCommandCooldowns loadedCooldowns = new PlayerCommandCooldowns();
        loadedCooldowns.load(bitInput, itemSet);

        assertEquals(originalCooldowns, loadedCooldowns);
    }

    @Test
    public void testMigrationBehaviour() throws UnknownEncodingException, ValidationException, ProgrammingValidationException {
        ItemSetWrapper originalItemSet = createSingleItemSet(generateOriginalTestItem());
        CustomItemValues extraItem = new SimpleCustomItemValues(true);
        extraItem.setName("extra_item");
        extraItem.setTexture(originalItemSet.get().getTextureReference("test_texture"));

        ItemCommandSystem extraCommandSystem = new ItemCommandSystem(true);
        List<ItemCommand> extraCommandList = new ArrayList<>(1);
        extraCommandList.add(createTestCommand("summon ghast", 10));
        extraCommandSystem.setCommandsFor(ItemCommandEvent.RIGHT_CLICK_GENERAL, extraCommandList);
        extraItem.setCommandSystem(extraCommandSystem);

        originalItemSet.get().addItem(extraItem);
        originalItemSet.setItemSet(originalItemSet.get());

        PlayerCommandCooldowns originalCooldowns = new PlayerCommandCooldowns();
        setTestCooldowns(originalCooldowns);
        originalCooldowns.setOnCooldown(extraItem, ItemCommandEvent.RIGHT_CLICK_GENERAL, 0, 100);

        ByteArrayBitOutput bitOutput = new ByteArrayBitOutput();
        originalCooldowns.save(bitOutput, originalItemSet);
        bitOutput.addInt(1234); // This magic number is useful for testing the discard method

        CustomItemValues newItem = generateNewTestItem();
        ItemSetWrapper newItemSet = createSingleItemSet(newItem);
        PlayerCommandCooldowns loadedCooldowns = new PlayerCommandCooldowns();
        ByteArrayBitInput bitInput = new ByteArrayBitInput(bitOutput.getBytes());
        loadedCooldowns.load(bitInput, newItemSet);
        assertEquals(1234, bitInput.readInt());

        long baseTime = 100;
        assertFalse(loadedCooldowns.isOnCooldown(newItem, ItemCommandEvent.BREAK_BLOCK, 0, baseTime));
        assertFalse(loadedCooldowns.isOnCooldown(newItem, ItemCommandEvent.BREAK_BLOCK, 1, baseTime));
        assertTrue(loadedCooldowns.isOnCooldown(newItem, ItemCommandEvent.BREAK_BLOCK, 2, baseTime));
        assertFalse(loadedCooldowns.isOnCooldown(newItem, ItemCommandEvent.BREAK_BLOCK, 3, baseTime));
        assertTrue(loadedCooldowns.isOnCooldown(newItem, ItemCommandEvent.BREAK_BLOCK, 4, baseTime));
        assertFalse(loadedCooldowns.isOnCooldown(newItem, ItemCommandEvent.LEFT_CLICK_GENERAL, 0, baseTime));
        assertTrue(loadedCooldowns.isOnCooldown(newItem, ItemCommandEvent.LEFT_CLICK_GENERAL, 1, baseTime));
    }

    // TODO Test full discard

    @Test
    public void testClean() {
        PlayerCommandCooldowns cooldowns = new PlayerCommandCooldowns();
        assertTrue(cooldowns.clean(10));

        List<ItemCommand> commands1 = new ArrayList<>(2);
        commands1.add(createTestCommand("say hi", 10));
        commands1.add(createTestCommand("say hello", 20));

        List<ItemCommand> commands2 = new ArrayList<>(1);
        commands2.add(createTestCommand("say ehm", 15));

        ItemCommandSystem commandSystem1 = new ItemCommandSystem(true);
        commandSystem1.setCommandsFor(ItemCommandEvent.RIGHT_CLICK_PLAYER, commands1);
        commandSystem1.setCommandsFor(ItemCommandEvent.MELEE_ATTACK_ENTITY, commands2);

        CustomItemValues item1 = new SimpleCustomItemValues(true);
        item1.setName("item1");
        item1.setCommandSystem(commandSystem1);

        List<ItemCommand> commands3 = new ArrayList<>(1);
        commands3.add(createTestCommand("summon bat", 5));

        ItemCommandSystem commandSystem2 = new ItemCommandSystem(true);
        commandSystem2.setCommandsFor(ItemCommandEvent.RIGHT_CLICK_ENTITY, commands3);

        CustomItemValues item2 = new CustomToolValues(true, CustomItemType.DIAMOND_PICKAXE);
        item2.setName("item2");
        item2.setCommandSystem(commandSystem2);

        cooldowns.setOnCooldown(item1, ItemCommandEvent.RIGHT_CLICK_PLAYER, 0, 10);
        cooldowns.setOnCooldown(item1, ItemCommandEvent.RIGHT_CLICK_PLAYER, 1, 10);
        cooldowns.setOnCooldown(item1, ItemCommandEvent.MELEE_ATTACK_ENTITY, 0, 10);
        cooldowns.setOnCooldown(item2, ItemCommandEvent.RIGHT_CLICK_ENTITY, 0, 10);

        assertFalse(cooldowns.clean(12));
        assertEquals(2, cooldowns.itemMap.size());

        assertFalse(cooldowns.clean(17));
        assertEquals(1, cooldowns.itemMap.size());
        assertEquals(2, cooldowns.itemMap.get(item1.getName()).commandMap.size());
        assertEquals(2, cooldowns.itemMap.get(item1.getName()).commandMap.get(ItemCommandEvent.RIGHT_CLICK_PLAYER).cooldownsPerCommandIndex.size());

        assertFalse(cooldowns.clean(22));
        assertEquals(2, cooldowns.itemMap.get(item1.getName()).commandMap.size());
        assertEquals(1, cooldowns.itemMap.get(item1.getName()).commandMap.get(ItemCommandEvent.RIGHT_CLICK_PLAYER).cooldownsPerCommandIndex.size());

        assertFalse(cooldowns.clean(28));
        assertEquals(1, cooldowns.itemMap.get(item1.getName()).commandMap.size());
        assertEquals(1, cooldowns.itemMap.get(item1.getName()).commandMap.get(ItemCommandEvent.RIGHT_CLICK_PLAYER).cooldownsPerCommandIndex.size());

        assertTrue(cooldowns.clean(33));
    }
}
