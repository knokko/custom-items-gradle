package nl.knokko.customitems.nms18;

import nl.knokko.customitems.nms16plus.KciNms16Plus;

@SuppressWarnings("unused")
public class KciNms18 extends KciNms16Plus {

    public static final String NMS_VERSION_STRING = "1_18_R2";

    public KciNms18() {
        super(new KciNmsEntities18(), new KciNmsItems18());
    }
}
