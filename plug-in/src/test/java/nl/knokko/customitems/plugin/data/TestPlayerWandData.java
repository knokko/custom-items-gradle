package nl.knokko.customitems.plugin.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

import com.google.common.collect.Lists;

import nl.knokko.customitems.effect.EffectType;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.AttributeModifier.Attribute;
import nl.knokko.customitems.item.AttributeModifier.Operation;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.EnchantmentType;
import nl.knokko.customitems.item.ItemFlag;
import nl.knokko.customitems.item.ReplaceCondition;
import nl.knokko.customitems.item.ReplaceCondition.ConditionOperation;
import nl.knokko.customitems.item.WandCharges;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.plugin.set.item.CustomWand;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.ByteArrayBitInput;
import nl.knokko.util.bits.ByteArrayBitOutput;

public class TestPlayerWandData {
	
	static final CustomWand WITH = new CustomWand(CustomItemType.GOLD_HOE, (short) 10, 
			"with_charges_one", "", "With charges 1", new String[] {"A wand that needs charges"}, 
			new AttributeModifier[] {new AttributeModifier(Attribute.ATTACK_SPEED, Slot.OFFHAND, 
					Operation.ADD, 0.3)}, new Enchantment[] {new Enchantment(EnchantmentType.FIRE_ASPECT, 1)},
			new boolean[ItemFlag.values().length], 
			Lists.newArrayList(new PotionEffect(EffectType.ABSORPTION, 15, 1)), 
			null, null, null, new ReplaceCondition[0], ConditionOperation.NONE, null, 
			5, new WandCharges(5, 20), 2, new ExtraItemNbt(), 1f);
	
	static final CustomWand WITHOUT = new CustomWand(CustomItemType.SHEARS, (short) 3,
			"without_charges_one", "", "Without charges 1", new String[0], new AttributeModifier[0],
			new Enchantment[0], ItemFlag.getDefaultValues(), Collections.emptyList(), 
			null, null, null, new ReplaceCondition[0], ConditionOperation.NONE, null, 20, 
			null, 3, new ExtraItemNbt(), 1f);

	@Test
	public void testSaveLoadDiscard() {
		
		// Define the wand data
		PlayerWandData with = new PlayerWandData(WITH);
		PlayerWandData without = new PlayerWandData(WITHOUT);
		
		// Convert them to bits (twice to test both discard and load)
		ByteArrayBitOutput output = new ByteArrayBitOutput();
		with.save1(output, WITH, 10);
		with.save1(output, WITH, 10);
		with.onShoot(WITH, 90);
		with.save1(output, WITH, 95);
		with.save1(output, WITH, 95);
		without.save1(output, WITHOUT, 8);
		without.save1(output, WITHOUT, 8);
		without.onShoot(WITHOUT, 20);
		without.save1(output, WITHOUT, 30);
		without.save1(output, WITHOUT, 30);
		output.terminate();
		
		// Convert them back
		BitInput input = new ByteArrayBitInput(output.getBytes());
		PlayerWandData.discard1(input);
		PlayerWandData withNoCD = PlayerWandData.load1(input, WITH);
		PlayerWandData.discard1(input);
		PlayerWandData withCD = PlayerWandData.load1(input, WITH);
		PlayerWandData.discard1(input);
		PlayerWandData withoutNoCD = PlayerWandData.load1(input, WITHOUT);
		PlayerWandData.discard1(input);
		PlayerWandData withoutCD = PlayerWandData.load1(input, WITHOUT);
		input.terminate();
		
		// Check if they were saved successfully
		assertFalse(withNoCD.isMissingCharges(WITH, 95));
		assertFalse(withNoCD.isOnCooldown(95));
		assertTrue(withNoCD.canShootNow(WITH, 95));
		
		assertTrue(withCD.isMissingCharges(WITH, 95));
		assertFalse(withCD.isOnCooldown(95));
		assertTrue(withCD.canShootNow(WITH, 95));
		
		assertFalse(withoutNoCD.isMissingCharges(WITHOUT, 30));
		assertFalse(withoutNoCD.isOnCooldown(30));
		assertTrue(withoutNoCD.canShootNow(WITHOUT, 30));
		
		assertFalse(withoutCD.isMissingCharges(WITHOUT, 30));
		assertTrue(withoutCD.isOnCooldown(30));
		assertFalse(withoutCD.canShootNow(WITHOUT, 30));
	}
	
	@Test
	public void testShootCdAndCharges() {
		
		// This test will use the wand with charges
		PlayerWandData with = new PlayerWandData(WITH);
		
		// Should not be on cooldown and not miss any charges before it fired any projectile
		assertFalse(with.isOnCooldown(10));
		assertFalse(with.isMissingCharges(WITH, 10));
		assertTrue(with.canShootNow(WITH, 10));
		
		// Should be on cooldown and missing a charge right after firing
		with.onShoot(WITH, 20);
		assertTrue(with.isMissingCharges(WITH, 21));
		assertTrue(with.isOnCooldown(21));
		assertFalse(with.canShootNow(WITH, 21));
		
		// The cooldown should be over a few ticks later, but should still be missing a charge
		assertTrue(with.isMissingCharges(WITH, 25));
		assertFalse(with.isOnCooldown(25));
		assertTrue(with.canShootNow(WITH, 25));
		
		// A bit later, the charge should have been restored
		assertFalse(with.isMissingCharges(WITH, 40));
		assertFalse(with.isOnCooldown(40));
		assertTrue(with.canShootNow(WITH, 40));
	}
	
	@Test
	public void testShootCd() {
		
		// This test will use the wand without charges
		PlayerWandData without = new PlayerWandData(WITHOUT);
		
		// Should not be on cooldown before firing any projectile (and never miss charges)
		assertFalse(without.isOnCooldown(10));
		assertFalse(without.isMissingCharges(WITHOUT, 10));
		assertTrue(without.canShootNow(WITHOUT, 10));
		
		// Should be on cooldown right after firing a projectile
		without.onShoot(WITHOUT, 20);
		assertTrue(without.isOnCooldown(25));
		assertFalse(without.isMissingCharges(WITHOUT, 25));
		assertFalse(without.canShootNow(WITHOUT, 25));
		
		// Cooldown should expire
		assertFalse(without.isOnCooldown(40));
		assertFalse(without.isMissingCharges(WITHOUT, 40));
		assertTrue(without.canShootNow(WITHOUT, 40));
	}
	
	@Test
	public void testMultipleCharges() {
		PlayerWandData with = new PlayerWandData(WITH);
		
		// Start with 5 out of 5 charges
		assertFalse(with.isMissingCharges(WITH, 5));
		assertTrue(with.canShootNow(WITH, 5));
		with.onShoot(WITH, 5);
		// 4 out of 5 charges left
		
		assertTrue(with.isMissingCharges(WITH, 10));
		assertTrue(with.canShootNow(WITH, 10));
		with.onShoot(WITH, 10);
		// 3 out of 5 charges left
		
		assertTrue(with.isMissingCharges(WITH, 15));
		assertTrue(with.canShootNow(WITH, 15));
		with.onShoot(WITH, 15);
		// 2 out of 5 charges left
		
		assertTrue(with.isMissingCharges(WITH, 20));
		assertTrue(with.canShootNow(WITH, 20));
		with.onShoot(WITH, 20);
		// 1 charge left
		
		// Note that the first charge should restore at tick 25
		assertTrue(with.isMissingCharges(WITH, 25));
		assertTrue(with.canShootNow(WITH, 25));
		with.onShoot(WITH, 25);
		// 1 charge left
		
		assertTrue(with.isMissingCharges(WITH, 30));
		assertTrue(with.canShootNow(WITH, 30));
		with.onShoot(WITH, 30);
		// out of charges!!
		
		assertTrue(with.isMissingCharges(WITH, 35));
		assertFalse(with.canShootNow(WITH, 35));
		
		// Another charge should be restored at tick 45
		assertTrue(with.isMissingCharges(WITH, 45));
		assertTrue(with.canShootNow(WITH, 45));
		
		// All charges should have been restored at tick 125
		assertFalse(with.isMissingCharges(WITH, 125));
		assertTrue(with.canShootNow(WITH, 125));
	}
}
