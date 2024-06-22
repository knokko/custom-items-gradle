package nl.knokko.customitems.nms13;

import nl.knokko.customitems.nms13plus.KciNms13Plus;

@SuppressWarnings("unused")
public class KciNms13 extends KciNms13Plus {

    public static final String NMS_VERSION_STRING = "1_13_R2";

    public KciNms13() {
        super(new KciNmsEntities13(), new KciNmsItems13());
    }
}
