package nl.knokko.customitems.nms14;

import nl.knokko.customitems.nms13plus.KciNms13Plus;

@SuppressWarnings("unused")
public class KciNms14 extends KciNms13Plus {

    public static final String NMS_VERSION_STRING = "1_14_R1";

    public KciNms14() {
        super(new KciNmsEntities14(), new KciNmsItems14());
    }
}
