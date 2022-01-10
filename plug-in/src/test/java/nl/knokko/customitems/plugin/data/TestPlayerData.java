package nl.knokko.customitems.plugin.data;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import nl.knokko.customitems.item.CustomWandValues;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.texture.BaseTextureValues;
import org.junit.Test;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.ByteArrayBitInput;
import nl.knokko.util.bits.ByteArrayBitOutput;

import static nl.knokko.customitems.plugin.data.TestPlayerWandData.*;

public class TestPlayerData {

	@Test
	public void testSaveLoad1() {
		
		// Create a dummy set
		SItemSet rawSet = new SItemSet(SItemSet.Side.EDITOR);
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
	}
}
