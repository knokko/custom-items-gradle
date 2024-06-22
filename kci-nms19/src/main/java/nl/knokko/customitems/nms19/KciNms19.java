package nl.knokko.customitems.nms19;

import nl.knokko.customitems.nms16plus.KciNms16Plus;

@SuppressWarnings("unused")
public class KciNms19 extends KciNms16Plus {

    public static final String NMS_VERSION_STRING = "1_19_R3";

    public KciNms19() {
        super(new KciNmsEntities19(), new KciNmsItems19());
    }
}
