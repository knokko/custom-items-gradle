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
		assertEquals(32, domain.getStacksize(700, 1000));
		assertEquals(0, domain.getStacksize(599, 1000));
		assertEquals(0, domain.getStacksize(600, 1000));
		assertEquals(1, domain.getStacksize(601, 1000));
		assertEquals(63, domain.getStacksize(799, 1000));
		assertEquals(64, domain.getStacksize(800, 1000));
		assertEquals(0, domain.getStacksize(500, 1000));
		assertEquals(64, domain.getStacksize(900, 1000));
	}
}
