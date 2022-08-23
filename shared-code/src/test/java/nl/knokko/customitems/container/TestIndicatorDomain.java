package nl.knokko.customitems.container;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import nl.knokko.customitems.test.TestHelper;

public class TestIndicatorDomain {

	@Test
	public void testSaveLoad() {
		IndicatorDomain original = new IndicatorDomain(12, 170);
		TestHelper.testSaveLoad(original::save, input -> {
			IndicatorDomain loaded = IndicatorDomain.load(input);
			assertEquals(12, loaded.getBegin());
			assertEquals(170, loaded.getEnd());
		});
	}
	
	@Test
	public void testStacksize() {
		IndicatorDomain domain = new IndicatorDomain(60, 80);
		assertEquals(32, domain.getStacksize(700, 0, 1000));
		assertEquals(0, domain.getStacksize(599, 0, 1000));
		assertEquals(0, domain.getStacksize(600, 0, 1000));
		assertEquals(1, domain.getStacksize(601, 0, 1000));
		assertEquals(63, domain.getStacksize(799, 0, 1000));
		assertEquals(64, domain.getStacksize(800, 0, 1000));
		assertEquals(0, domain.getStacksize(500, 0, 1000));
		assertEquals(64, domain.getStacksize(900, 0, 1000));

		assertEquals(32, domain.getStacksize(730, 100, 1000));
		assertEquals(0, domain.getStacksize(199, -1000, 1000));
		assertEquals(0, domain.getStacksize(400, -500, 1000));
		assertEquals(1, domain.getStacksize(401, -500, 1000));
		assertEquals(63, domain.getStacksize(-1001, -9000, 1000));
		assertEquals(64, domain.getStacksize(-1000, -9000, 1000));
		assertEquals(0, domain.getStacksize(950, 900, 1000));
		assertEquals(64, domain.getStacksize(990, 900, 1000));
	}
}
