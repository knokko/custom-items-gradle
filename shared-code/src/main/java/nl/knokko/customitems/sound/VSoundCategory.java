package nl.knokko.customitems.sound;

import nl.knokko.customitems.NameHelper;

import static nl.knokko.customitems.MCVersions.*;

public enum VSoundCategory {

    MASTER(VERSION1_12, VERSION26),
    MUSIC(VERSION1_12, VERSION26),
    RECORDS(VERSION1_12, VERSION26),
    WEATHER(VERSION1_12, VERSION26),
    BLOCKS(VERSION1_12, VERSION26),
    HOSTILE(VERSION1_12, VERSION26),
    NEUTRAL(VERSION1_12, VERSION26),
    PLAYERS(VERSION1_12, VERSION26),
    AMBIENT(VERSION1_12, VERSION26),
    VOICE(VERSION1_12, VERSION26),
    UI(VERSION26, VERSION26);

    public final int minVersion;
    public final int maxVersion;

    VSoundCategory(int minVersion, int maxVersion) {
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
    }

    @Override
    public String toString() {
        return NameHelper.getNiceEnumName(this.name(), this.minVersion, this.maxVersion);
    }
}
