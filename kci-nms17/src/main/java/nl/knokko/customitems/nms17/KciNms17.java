package nl.knokko.customitems.nms17;

import nl.knokko.customitems.nms16plus.KciNms16Plus;

@SuppressWarnings("unused")
public class KciNms17 extends KciNms16Plus {

    public static final String NMS_VERSION_STRING = "1_17_R1";

    public KciNms17() {
        super(new KciNmsEntities17(), new KciNmsItems17());
    }
}
