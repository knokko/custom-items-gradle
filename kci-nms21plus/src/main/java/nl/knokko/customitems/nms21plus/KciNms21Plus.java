package nl.knokko.customitems.nms21plus;

import nl.knokko.customitems.nms.KciNmsItems;
import nl.knokko.customitems.nms16plus.KciNms16Plus;

public class KciNms21Plus extends KciNms16Plus {

	public KciNms21Plus(KciNmsItems items) {
		super(new KciNmsEntities21Plus(), items);
	}
}
