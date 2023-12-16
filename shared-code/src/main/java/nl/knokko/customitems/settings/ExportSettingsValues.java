package nl.knokko.customitems.settings;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import static nl.knokko.customitems.MCVersions.VERSION1_12;

public class ExportSettingsValues extends ModelValues {

    public static ExportSettingsValues load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("ExportSettings", encoding);

        ExportSettingsValues settings = new ExportSettingsValues(false);
        settings.mcVersion = input.readInt();
        settings.mode = Mode.valueOf(input.readString());
        settings.reloadMessage = input.readString();

        settings.kickUponReject = input.readBoolean();
        settings.forceRejectMessage = input.readString();
        settings.optionalRejectMessage = input.readString();

        settings.kickUponFailedDownload = input.readBoolean();
        settings.forceFailedMessage = input.readString();
        settings.optionalFailedMessage = input.readString();

        return settings;
    }

    private int mcVersion;
    private Mode mode;
    private String reloadMessage;

    private boolean kickUponReject;
    private String forceRejectMessage;
    private String optionalRejectMessage;

    private boolean kickUponFailedDownload;
    private String forceFailedMessage;
    private String optionalFailedMessage;

    public ExportSettingsValues(boolean mutable) {
        super(mutable);
        this.mcVersion = VERSION1_12;
        this.mode = Mode.AUTOMATIC;
        this.reloadMessage = "The server resource pack has changed. You can get the new resource pack by " +
                "logging out and back in, or by executing /kci resourcepack. Note that either way, you will " +
                "probably freeze for several seconds while downloading it, so please do this at a safe location.";
        this.kickUponReject = false;
        this.forceRejectMessage = "You must accept the server resource pack. If you didn't get the chance to accept it, " +
                "check out https://knokko.github.io/resource-pack-host/accept.html";
        this.optionalRejectMessage = "You rejected the server resource pack. If you would like to use it " +
                "but didn't get the chance to accept it, check out https://knokko.github.io/resource-pack-host/accept.html";
        this.kickUponFailedDownload = false;
        this.forceFailedMessage = "You must use the server resource pack, but the download failed. Please try again.";
        this.optionalFailedMessage = "Downloading the server resource pack failed for some reason. " +
                "You can retry by reconnecting to the server. If this keeps happening, contact the admin.";
    }

    public ExportSettingsValues(ExportSettingsValues toCopy, boolean mutable) {
        super(mutable);
        this.mcVersion = toCopy.getMcVersion();
        this.mode = toCopy.getMode();
        this.reloadMessage = toCopy.getReloadMessage();
        this.kickUponReject = toCopy.shouldKickUponReject();
        this.forceRejectMessage = toCopy.getForceRejectMessage();
        this.optionalRejectMessage = toCopy.getOptionalRejectMessage();
        this.kickUponFailedDownload = toCopy.shouldKickUponFailedDownload();
        this.forceFailedMessage = toCopy.getForceFailedMessage();
        this.optionalFailedMessage = toCopy.getOptionalFailedMessage();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addInt(mcVersion);
        output.addString(mode.name());
        output.addString(reloadMessage);

        output.addBoolean(kickUponReject);
        output.addString(forceRejectMessage);
        output.addString(optionalRejectMessage);

        output.addBoolean(kickUponFailedDownload);
        output.addString(forceFailedMessage);
        output.addString(optionalFailedMessage);
    }

    public int getMcVersion() {
        return mcVersion;
    }

    public Mode getMode() {
        return mode;
    }

    public String getReloadMessage() {
        return reloadMessage;
    }

    public boolean shouldKickUponReject() {
        return kickUponReject;
    }

    public String getForceRejectMessage() {
        return forceRejectMessage;
    }

    public String getOptionalRejectMessage() {
        return optionalRejectMessage;
    }

    public boolean shouldKickUponFailedDownload() {
        return kickUponFailedDownload;
    }

    public String getForceFailedMessage() {
        return forceFailedMessage;
    }

    public String getOptionalFailedMessage() {
        return optionalFailedMessage;
    }

    public void setMcVersion(int mcVersion) {
        assertMutable();
        this.mcVersion = mcVersion;
    }

    public void setMode(Mode mode) {
        assertMutable();
        Checks.notNull(mode);
        this.mode = mode;
    }

    public void setReloadMessage(String reloadMessage) {
        assertMutable();
        Checks.notNull(reloadMessage);
        this.reloadMessage = reloadMessage;
    }

    public void setKickUponReject(boolean kickUponReject) {
        assertMutable();
        this.kickUponReject = kickUponReject;
    }

    public void setForceRejectMessage(String forceRejectMessage) {
        assertMutable();
        Checks.notNull(forceRejectMessage);
        this.forceRejectMessage = forceRejectMessage;
    }

    public void setOptionalRejectMessage(String optionalRejectMessage) {
        assertMutable();
        Checks.notNull(optionalRejectMessage);
        this.optionalRejectMessage = optionalRejectMessage;
    }

    public void setKickUponFailedDownload(boolean kickUponFailedDownload) {
        assertMutable();
        this.kickUponFailedDownload = kickUponFailedDownload;
    }

    public void setForceFailedMessage(String forceFailedMessage) {
        assertMutable();
        Checks.notNull(forceFailedMessage);
        this.forceFailedMessage = forceFailedMessage;
    }

    public void setOptionalFailedMessage(String optionalFailedMessage) {
        assertMutable();
        Checks.notNull(optionalFailedMessage);
        this.optionalFailedMessage = optionalFailedMessage;
    }

    @Override
    public ExportSettingsValues copy(boolean mutable) {
        return new ExportSettingsValues(this, mutable);
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (mcVersion < MCVersions.FIRST_VERSION || mcVersion > MCVersions.LAST_VERSION) {
            throw new ValidationException("Unsupported MC version: " + mcVersion);
        }
        if (mode == null) throw new ProgrammingValidationException("No mode");
        if (reloadMessage == null) throw new ProgrammingValidationException("No reload message");

        if (forceRejectMessage == null) throw new ProgrammingValidationException("No force reject message");
        if ((mode == Mode.AUTOMATIC || mode == Mode.MIXED) && forceRejectMessage.isEmpty()) {
            throw new ValidationException("Rejection kick message can't be empty");
        }
        if (optionalRejectMessage == null) throw new ProgrammingValidationException("No optional reject message");

        if (forceFailedMessage == null) throw new ProgrammingValidationException("No force failed message");
        if ((mode == Mode.AUTOMATIC || mode == Mode.MIXED) && forceFailedMessage.isEmpty()) {
            throw new ValidationException("Failure kick message can't be empty");
        }
        if (optionalFailedMessage == null) throw new ProgrammingValidationException("No optional failure message");
    }

    public enum Mode {
        AUTOMATIC,
        MANUAL,
        MIXED
    }
}
