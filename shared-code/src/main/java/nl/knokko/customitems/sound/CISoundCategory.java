package nl.knokko.customitems.sound;

import nl.knokko.customitems.NameHelper;

import static nl.knokko.customitems.MCVersions.VERSION1_12;
import static nl.knokko.customitems.MCVersions.VERSION1_19;

public enum CISoundCategory {

    MASTER(VERSION1_12, VERSION1_19),
    MUSIC(VERSION1_12, VERSION1_19),
    RECORDS(VERSION1_12, VERSION1_19),
    WEATHER(VERSION1_12, VERSION1_19),
    BLOCKS(VERSION1_12, VERSION1_19),
    HOSTILE(VERSION1_12, VERSION1_19),
    NEUTRAL(VERSION1_12, VERSION1_19),
    PLAYERS(VERSION1_12, VERSION1_19),
    AMBIENT(VERSION1_12, VERSION1_19),
    VOICE(VERSION1_12, VERSION1_19);

    public final int minVersion;
    public final int maxVersion;

    CISoundCategory(int minVersion, int maxVersion) {
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
    }

    @Override
    public String toString() {
        return NameHelper.getNiceEnumName(this.name(), this.minVersion, this.maxVersion);
    }
}
