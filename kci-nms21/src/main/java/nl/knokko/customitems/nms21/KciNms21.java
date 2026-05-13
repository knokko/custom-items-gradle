package nl.knokko.customitems.nms21;

import nl.knokko.customitems.nms21plus.KciNms21Plus;

@SuppressWarnings("unused")
public class KciNms21 extends KciNms21Plus {

    public static final String NMS_VERSION_STRING = "1_21_R7";

    public KciNms21() {
        super(new KciNmsItems21());
    }
}
