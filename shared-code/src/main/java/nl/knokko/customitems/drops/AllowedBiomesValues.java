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

public class AllowedBiomesValues extends ModelValues {

    public static AllowedBiomesValues load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("AllowedBiomes", encoding);

        AllowedBiomesValues result = new AllowedBiomesValues(false);
        int whitelistSize = input.readInt();
        result.whitelist = new ArrayList<>(whitelistSize);
        for (int counter = 0; counter < whitelistSize; counter++) {
            result.whitelist.add(CIBiome.valueOf(input.readString()));
        }

        int blacklistSize = input.readInt();
        result.blacklist = new ArrayList<>(blacklistSize);
        for (int counter = 0; counter < blacklistSize; counter++) {
            result.blacklist.add(CIBiome.valueOf(input.readString()));
        }

        return result;
    }

    private Collection<CIBiome> whitelist;
    private Collection<CIBiome> blacklist;

    public AllowedBiomesValues(boolean mutable) {
        super(mutable);
        this.whitelist = new ArrayList<>();
        this.blacklist = new ArrayList<>();
    }

    public AllowedBiomesValues(AllowedBiomesValues toCopy, boolean mutable) {
        super(mutable);
        this.whitelist = toCopy.getWhitelist();
        this.blacklist = toCopy.getBlacklist();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addInt(this.whitelist.size());
        for (CIBiome biome : this.whitelist) {
            output.addString(biome.name());
        }

        output.addInt(this.blacklist.size());
        for (CIBiome biome : this.blacklist) {
            output.addString(biome.name());
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AllowedBiomesValues) {
            AllowedBiomesValues otherBiomes = (AllowedBiomesValues) other;
            return this.whitelist.equals(otherBiomes.whitelist) && this.blacklist.equals(otherBiomes.blacklist);
        } else {
            return false;
        }
    }

    @Override
    public AllowedBiomesValues copy(boolean mutable) {
        return new AllowedBiomesValues(this, mutable);
    }

    public boolean isAllowed(CIBiome candidate) {
        return (this.whitelist.isEmpty() || this.whitelist.contains(candidate)) && !this.blacklist.contains(candidate);
    }

    public Collection<CIBiome> getWhitelist() {
        return new ArrayList<>(whitelist);
    }

    public Collection<CIBiome> getBlacklist() {
        return new ArrayList<>(blacklist);
    }

    public void setWhitelist(Collection<CIBiome> whitelist) {
        assertMutable();
        Checks.nonNull(whitelist);
        this.whitelist = new ArrayList<>(whitelist);
    }

    public void setBlacklist(Collection<CIBiome> blacklist) {
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

    private void validateExportVersion(int mcVersion, Collection<CIBiome> biomeList) throws ValidationException {
        for (CIBiome biome : biomeList) {
            if (biome.firstVersion > mcVersion) {
                throw new ValidationException("Biome " + biome + " doesn't exist yet in MC " + MCVersions.createString(mcVersion));
            }
            if (biome.lastVersion < mcVersion) {
                throw new ValidationException("Biome " + biome + " was renamed after MC " + MCVersions.createString(mcVersion));
            }
        }
    }
}
