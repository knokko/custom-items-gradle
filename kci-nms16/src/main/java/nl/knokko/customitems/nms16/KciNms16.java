package nl.knokko.customitems.nms16;

import nl.knokko.customitems.nms16plus.KciNms16Plus;

@SuppressWarnings("unused")
public class KciNms16 extends KciNms16Plus {

    public static final String NMS_VERSION_STRING = "1_16_R3";

    public KciNms16() {
        super(new KciNmsEntities16(), new KciNmsItems16());
    }
}
