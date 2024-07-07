package nl.knokko.customitems.sound;

import nl.knokko.customitems.NameHelper;

import static nl.knokko.customitems.MCVersions.*;

public enum VSoundCategory {

    MASTER(VERSION1_12, VERSION1_21),
    MUSIC(VERSION1_12, VERSION1_21),
    RECORDS(VERSION1_12, VERSION1_21),
    WEATHER(VERSION1_12, VERSION1_21),
    BLOCKS(VERSION1_12, VERSION1_21),
    HOSTILE(VERSION1_12, VERSION1_21),
    NEUTRAL(VERSION1_12, VERSION1_21),
    PLAYERS(VERSION1_12, VERSION1_21),
    AMBIENT(VERSION1_12, VERSION1_21),
    VOICE(VERSION1_12, VERSION1_21);

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
