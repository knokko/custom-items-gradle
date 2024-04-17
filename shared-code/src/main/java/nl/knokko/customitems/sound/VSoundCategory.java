package nl.knokko.customitems.sound;

import nl.knokko.customitems.NameHelper;

import static nl.knokko.customitems.MCVersions.*;

public enum VSoundCategory {

    MASTER(VERSION1_12, VERSION1_20),
    MUSIC(VERSION1_12, VERSION1_20),
    RECORDS(VERSION1_12, VERSION1_20),
    WEATHER(VERSION1_12, VERSION1_20),
    BLOCKS(VERSION1_12, VERSION1_20),
    HOSTILE(VERSION1_12, VERSION1_20),
    NEUTRAL(VERSION1_12, VERSION1_20),
    PLAYERS(VERSION1_12, VERSION1_20),
    AMBIENT(VERSION1_12, VERSION1_20),
    VOICE(VERSION1_12, VERSION1_20);

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
