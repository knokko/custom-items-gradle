package nl.knokko.customitems.plugin.data;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.Test;

import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.test.MockItemSet;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.ByteArrayBitInput;
import nl.knokko.util.bits.ByteArrayBitOutput;

import static nl.knokko.customitems.plugin.data.TestPlayerWandData.*;

public class TestPlayerData {

	@Test
	public void testSaveLoad1() {
		
		// Create a dummy set
		ItemSet set = new MockItemSet(WITH);
		
		// This currently prints stupid messages in the console, but doesn't cause problems
		Logger dummyLogger = Logger.getGlobal();
		
		// Create new empty player data
		PlayerData data = new PlayerData();
		
		// Add data for the WITHOUT wand, to test if the discarding works well
		assertTrue(data.shootIfAllowed(WITHOUT, 12));
		
		// Convert it to bits
		ByteArrayBitOutput output = new ByteArrayBitOutput();
		data.save1(output, 10);
		assertTrue(data.shootIfAllowed(WITH, 15));
		data.save1(output, 17);
		data.save1(output, 20);
		output.terminate();
		
		// Convert it back to player data
		BitInput input = new ByteArrayBitInput(output.getBytes());
		PlayerData beforeShoot = PlayerData.load1(input, set, dummyLogger);
		PlayerData rightAfterShoot = PlayerData.load1(input, set, dummyLogger);
		PlayerData afterShoot = PlayerData.load1(input, set, dummyLogger);
		input.terminate();
		
		assertTrue(beforeShoot.shootIfAllowed(WITH, 10));
		assertFalse(rightAfterShoot.shootIfAllowed(WITH, 17));
		assertTrue(afterShoot.shootIfAllowed(WITH, 20));
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
		assertTrue(data.shootIfAllowed(WITHOUT, 100));
		assertFalse(data.clean(100));
		
		// And returns true when the cooldown expired
		assertTrue(data.clean(125));
		
		// Now try the charges
		assertTrue(data.shootIfAllowed(WITH, 200));
		assertFalse(data.clean(201));
		assertFalse(data.clean(205));
		assertTrue(data.clean(220));
	}
}
