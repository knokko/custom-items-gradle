package nl.knokko.customitems.drops;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.ArrayList;
import java.util.Collection;

public class AllowedBiomes extends ModelValues {

    public static AllowedBiomes load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("AllowedBiomes", encoding);

        AllowedBiomes result = new AllowedBiomes(false);
        int whitelistSize = input.readInt();
        result.whitelist = new ArrayList<>(whitelistSize);
        for (int counter = 0; counter < whitelistSize; counter++) {
            result.whitelist.add(VBiome.valueOf(input.readString()));
        }

        int blacklistSize = input.readInt();
        result.blacklist = new ArrayList<>(blacklistSize);
        for (int counter = 0; counter < blacklistSize; counter++) {
            result.blacklist.add(VBiome.valueOf(input.readString()));
        }

        return result;
    }

    private Collection<VBiome> whitelist;
    private Collection<VBiome> blacklist;

    public AllowedBiomes(boolean mutable) {
        super(mutable);
        this.whitelist = new ArrayList<>();
        this.blacklist = new ArrayList<>();
    }

    public AllowedBiomes(AllowedBiomes toCopy, boolean mutable) {
        super(mutable);
        this.whitelist = toCopy.getWhitelist();
        this.blacklist = toCopy.getBlacklist();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addInt(this.whitelist.size());
        for (VBiome biome : this.whitelist) {
            output.addString(biome.name());
        }

        output.addInt(this.blacklist.size());
        for (VBiome biome : this.blacklist) {
            output.addString(biome.name());
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AllowedBiomes) {
            AllowedBiomes otherBiomes = (AllowedBiomes) other;
            return this.whitelist.equals(otherBiomes.whitelist) && this.blacklist.equals(otherBiomes.blacklist);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        if (whitelist.isEmpty()) {
            if (blacklist.isEmpty()) return "All biomes";
            else return "All biomes except " + blacklist;
        } else {
            return whitelist.toString();
        }
    }

    @Override
    public AllowedBiomes copy(boolean mutable) {
        return new AllowedBiomes(this, mutable);
    }

    public boolean isAllowed(VBiome candidate) {
        return (this.whitelist.isEmpty() || this.whitelist.contains(candidate)) && !this.blacklist.contains(candidate);
    }

    public Collection<VBiome> getWhitelist() {
        return new ArrayList<>(whitelist);
    }

    public Collection<VBiome> getBlacklist() {
        return new ArrayList<>(blacklist);
    }

    public void setWhitelist(Collection<VBiome> whitelist) {
        assertMutable();
        Checks.nonNull(whitelist);
        this.whitelist = new ArrayList<>(whitelist);
    }

    public void setBlacklist(Collection<VBiome> blacklist) {
        assertMutable();
        Checks.nonNull(blacklist);
        this.blacklist = new ArrayList<>(blacklist);
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (this.whitelist == null) throw new ValidationException("No whitelist");
        if (this.blacklist == null) throw new ValidationException("No blacklist");
    }

    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Whitelist", () -> this.validateExportVersion(mcVersion, this.whitelist));
        Validation.scope("Blacklist", () -> this.validateExportVersion(mcVersion, this.blacklist));
    }

    private void validateExportVersion(int mcVersion, Collection<VBiome> biomeList) throws ValidationException {
        for (VBiome biome : biomeList) {
            if (biome.firstVersion > mcVersion) {
                throw new ValidationException("Biome " + biome + " doesn't exist yet in MC " + MCVersions.createString(mcVersion));
            }
            if (biome.lastVersion < mcVersion) {
                throw new ValidationException("Biome " + biome + " was renamed after MC " + MCVersions.createString(mcVersion));
            }
        }
    }
}
