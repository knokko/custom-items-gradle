package nl.knokko.customitems.plugin.data;

import static nl.knokko.customitems.plugin.data.IOHelper.getResourceBitInput;
import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomWandValues;
import nl.knokko.customitems.item.SimpleCustomItemValues;
import nl.knokko.customitems.item.command.ItemCommand;
import nl.knokko.customitems.item.command.ItemCommandEvent;
import nl.knokko.customitems.item.command.ItemCommandSystem;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.texture.BaseTextureValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import org.junit.Test;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitOutput;

import static nl.knokko.customitems.plugin.data.TestPlayerWandData.*;

public class TestPlayerData {

	private ItemSetWrapper createDummyItemSet1() {
		ItemSet rawSet = new ItemSet(ItemSet.Side.EDITOR);
		CustomWandValues dummyWand = WITH.copy(true);
		try {
			rawSet.addTexture(BaseTextureValues.createQuick("test", new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)));

			CustomProjectileValues dummyProjectile = new CustomProjectileValues(true);
			dummyProjectile.setName("test");
			rawSet.addProjectile(dummyProjectile);

			dummyWand.setTexture(rawSet.getTextureReference("test"));
			dummyWand.setProjectile(rawSet.getProjectileReference("test"));
			rawSet.addItem(dummyWand);
		} catch (Exception ex) {
			throw new RuntimeException("Failure", ex);
		}

		ItemSetWrapper wrappedSet = new ItemSetWrapper();
		wrappedSet.setItemSet(rawSet);

		return wrappedSet;
	}

	@Test
	public void testSaveLoad1() {
		
		// Create a dummy set
		ItemSetWrapper wrappedSet = createDummyItemSet1();
		CustomItemValues dummyWand = wrappedSet.getItem("with_charges_one");

		// This currently prints stupid messages in the console, but doesn't cause problems
		Logger dummyLogger = Logger.getGlobal();
		
		// Create new empty player data
		PlayerData data = new PlayerData();
		
		// Add data for the WITHOUT wand, to test if the discarding works well
		assertTrue(data.shootIfAllowed(WITHOUT, 12, true));
		
		// Convert it to bits
		ByteArrayBitOutput output = new ByteArrayBitOutput();
		data.save1(output, 10);
		assertTrue(data.shootIfAllowed(dummyWand, 15, true));
		data.save1(output, 17);
		data.save1(output, 20);
		output.terminate();

		// Convert it back to player data
		BitInput input = new ByteArrayBitInput(output.getBytes());
		PlayerData beforeShoot = PlayerData.load1(input, wrappedSet, dummyLogger);
		PlayerData rightAfterShoot = PlayerData.load1(input, wrappedSet, dummyLogger);
		PlayerData afterShoot = PlayerData.load1(input, wrappedSet, dummyLogger);
		input.terminate();
		
		assertTrue(beforeShoot.shootIfAllowed(dummyWand, 10, true));
		assertFalse(rightAfterShoot.shootIfAllowed(dummyWand, 17, true));
		assertTrue(afterShoot.shootIfAllowed(dummyWand, 20, true));
	}

	private ItemSetWrapper createTestItemSet2() throws ValidationException, ProgrammingValidationException {
		ItemSet dummyItemSet = new ItemSet(ItemSet.Side.EDITOR);

		CustomProjectileValues dummyProjectile = new CustomProjectileValues(true);
		dummyProjectile.setName("dummy_projectile");
		dummyItemSet.addProjectile(dummyProjectile);

		List<ItemCommand> dummyCommands = new ArrayList<>(1);
		dummyCommands.add(TestPlayerCommandCooldowns.createTestCommand("summon villager", 100));

		ItemCommandSystem dummyCommandSystem = new ItemCommandSystem(true);
		dummyCommandSystem.setCommandsFor(ItemCommandEvent.MELEE_ATTACK_ENTITY, dummyCommands);

		dummyItemSet.addTexture(BaseTextureValues.createQuick("dummy_texture", new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)));

		CustomWandValues dummyWand = new CustomWandValues(true);
		dummyWand.setName("dummy_wand");
		dummyWand.setProjectile(dummyItemSet.getProjectileReference(dummyProjectile.getName()));
		dummyWand.setCommandSystem(dummyCommandSystem);
		dummyWand.setTexture(dummyItemSet.getTextureReference("dummy_texture"));

		dummyItemSet.addItem(dummyWand);

		ItemSetWrapper wrappedSet = new ItemSetWrapper();
		wrappedSet.setItemSet(dummyItemSet);
		return wrappedSet;
	}

	@Test
	public void testSaveLoad2() throws UnknownEncodingException, ValidationException, ProgrammingValidationException {
		ItemSetWrapper wrappedSet = createTestItemSet2();
		CustomItemValues dummyWand = wrappedSet.getItem("dummy_wand");

		// This currently prints stupid messages in the console, but doesn't cause problems
		Logger dummyLogger = Logger.getGlobal();

		PlayerData playerDataToSave = new PlayerData();
		playerDataToSave.commandCooldowns.setOnCooldown(dummyWand, ItemCommandEvent.MELEE_ATTACK_ENTITY, 0, 10);

		// Add data for the WITHOUT wand, to test if the discarding works well
		assertTrue(playerDataToSave.shootIfAllowed(WITHOUT, 12, true));
		assertTrue(playerDataToSave.shootIfAllowed(dummyWand, 15, true));

		// Convert it to bits
		ByteArrayBitOutput output = new ByteArrayBitOutput();

		playerDataToSave.save2(output, wrappedSet, 10);
		output.addInt(-1234);
		output.terminate();

		// Convert it back to player data
		BitInput input = new ByteArrayBitInput(output.getBytes());
		PlayerData loadedPlayerData = PlayerData.load2(input, wrappedSet, dummyLogger);
		assertEquals(-1234, input.readInt());
		input.terminate();

		assertFalse(loadedPlayerData.shootIfAllowed(dummyWand, 17, true));
		assertTrue(loadedPlayerData.commandCooldowns.isOnCooldown(dummyWand, ItemCommandEvent.MELEE_ATTACK_ENTITY, 0, 17));
	}

	@Test
	public void testBackwardCompatibility2() throws UnknownEncodingException, ValidationException, ProgrammingValidationException{
		ItemSetWrapper wrappedSet = createTestItemSet2();
		CustomItemValues dummyWand = wrappedSet.getItem("dummy_wand");

		BitInput input = IOHelper.getResourceBitInput("data/player/backward2.bin", 120);
		PlayerData loadedPlayerData = PlayerData.load2(input, wrappedSet, Logger.getGlobal());
		assertEquals(-1234, input.readInt());
		input.terminate();

		assertFalse(loadedPlayerData.shootIfAllowed(dummyWand, 17, true));
		assertTrue(loadedPlayerData.commandCooldowns.isOnCooldown(dummyWand, ItemCommandEvent.MELEE_ATTACK_ENTITY, 0, 17));
	}

	@Test
	public void testBackwardCompatibility1() {
		ItemSetWrapper wrappedSet = createDummyItemSet1();
		CustomItemValues dummyWand = wrappedSet.getItem("with_charges_one");

		// This currently prints stupid messages in the console, but doesn't cause problems
		Logger dummyLogger = Logger.getGlobal();

		BitInput input = getResourceBitInput("data/player/backward1.bin", 143);

		PlayerData beforeShoot = PlayerData.load1(input, wrappedSet, dummyLogger);
		PlayerData rightAfterShoot = PlayerData.load1(input, wrappedSet, dummyLogger);
		PlayerData afterShoot = PlayerData.load1(input, wrappedSet, dummyLogger);

		assertTrue(beforeShoot.shootIfAllowed(dummyWand, 10, true));
		assertFalse(rightAfterShoot.shootIfAllowed(dummyWand, 17, true));
		assertTrue(afterShoot.shootIfAllowed(dummyWand, 20, true));
	}
	
	@Test
	public void testShooting() {
		
		// Create new empty player data, that should not yet be shooting
		PlayerData data = new PlayerData();
		
		assertFalse(data.isShooting(5));
		data.setShooting(10);
		assertTrue(data.isShooting(10));
		assertTrue(data.isShooting(10 + PlayerData.SHOOT_TIME));
		assertFalse(data.isShooting(11 + PlayerData.SHOOT_TIME));
	}
	
	@Test
	public void testClean() {
		
		// Create new empty player data, whose clean should return true initially
		PlayerData data = new PlayerData();
		assertTrue(data.clean(10));
		
		// Clean should return false while shooting
		data.setShooting(15);
		assertFalse(data.clean(15));
		
		// But clean should return true when the 'player' stopped shooting
		assertTrue(data.clean(16 + PlayerData.SHOOT_TIME));
		
		// Now check if clean returns false when something is on cooldown
		assertTrue(data.shootIfAllowed(WITHOUT, 100, true));
		assertFalse(data.clean(100));
		
		// And returns true when the cooldown expired
		assertTrue(data.clean(125));
		
		// Now try the charges
		assertTrue(data.shootIfAllowed(WITH, 200, true));
		assertFalse(data.clean(201));
		assertFalse(data.clean(205));
		assertTrue(data.clean(220));

		// Now try the command cooldowns
		ItemCommand dummyCommand = new ItemCommand(true);
		dummyCommand.setRawCommand("say hello");
		dummyCommand.setCooldown(12);

		List<ItemCommand> dummyCommands = new ArrayList<>(1);
		dummyCommands.add(dummyCommand);

		ItemCommandSystem dummyCommandSystem = new ItemCommandSystem(true);
		dummyCommandSystem.setCommandsFor(ItemCommandEvent.RIGHT_CLICK_GENERAL, dummyCommands);

		CustomItemValues dummyItem = new SimpleCustomItemValues(true);
		dummyItem.setName("dummy_item");
		dummyItem.setCommandSystem(dummyCommandSystem);

		data.commandCooldowns.setOnCooldown(dummyItem, ItemCommandEvent.RIGHT_CLICK_GENERAL, 0, 250);
		assertFalse(data.clean(250));
		assertFalse(data.clean(260));
		assertTrue(data.clean(270));
	}
}
