package nl.knokko.customitems.nms15;

import nl.knokko.customitems.nms13plus.KciNms13Plus;

@SuppressWarnings("unused")
public class KciNms15 extends KciNms13Plus {

    public static final String NMS_VERSION_STRING = "1_15_R1";

    public KciNms15() {
        super(new KciNmsEntities15(), new KciNmsItems15());
    }
}
