package nl.knokko.customitems.nms16plus;

import nl.knokko.customitems.nms.KciNmsEntities;
import nl.knokko.customitems.nms.KciNmsItems;
import nl.knokko.customitems.nms13plus.KciNms13Plus;

public abstract class KciNms16Plus extends KciNms13Plus {

    public KciNms16Plus(KciNmsEntities entities, KciNmsItems items) {
        super(new KciNmsBlocks16Plus(), entities, items);
    }
}
