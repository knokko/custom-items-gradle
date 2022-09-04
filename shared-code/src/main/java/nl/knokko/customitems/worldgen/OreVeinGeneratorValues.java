package nl.knokko.customitems.worldgen;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.drops.AllowedBiomesValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Chance;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.Comparator;
import java.util.Optional;

public class OreVeinGeneratorValues extends ModelValues {

    public static OreVeinGeneratorValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("OreVeinGenerator", encoding);

        OreVeinGeneratorValues result = new OreVeinGeneratorValues(false);
        result.blocksToReplace = ReplaceBlocksValues.load(input, itemSet);
        result.allowedBiomes = AllowedBiomesValues.load(input);
        result.oreMaterial = BlockProducerValues.load(input, itemSet);
        result.minY = input.readInt();
        result.maxY = input.readInt();
        result.chance = Chance.load(input);
        result.minNumVeins = input.readInt();
        result.maxNumVeins = input.readInt();
        result.maxNumVeinAttempts = input.readInt();
        result.minVeinSize = input.readInt();
        result.maxVeinSize = input.readInt();
        result.maxNumGrowAttempts = input.readInt();

        return result;
    }

    private ReplaceBlocksValues blocksToReplace;
    private AllowedBiomesValues allowedBiomes;

    private BlockProducerValues oreMaterial;

    private int minY, maxY;

    private Chance chance;
    private int minNumVeins, maxNumVeins, maxNumVeinAttempts;
    private int minVeinSize, maxVeinSize, maxNumGrowAttempts;

    public OreVeinGeneratorValues(boolean mutable) {
        super(mutable);
        this.blocksToReplace = new ReplaceBlocksValues(false);
        this.allowedBiomes = new AllowedBiomesValues(false);
        this.oreMaterial = new BlockProducerValues(false);
        this.minY = 20;
        this.maxY = 40;
        this.chance = Chance.percentage(100);
        this.minNumVeins = 2;
        this.maxNumVeins = 5;
        this.maxNumVeinAttempts = 10;
        this.minVeinSize = 4;
        this.maxVeinSize = 12;
        this.maxNumGrowAttempts = 20;
    }

    public OreVeinGeneratorValues(OreVeinGeneratorValues toCopy, boolean mutable) {
        super(mutable);
        this.blocksToReplace = toCopy.getBlocksToReplace();
        this.allowedBiomes = toCopy.getAllowedBiomes();
        this.oreMaterial = toCopy.getOreMaterial();
        this.minY = toCopy.getMinY();
        this.maxY = toCopy.getMaxY();
        this.chance = toCopy.getChance();
        this.minNumVeins = toCopy.getMinNumVeins();
        this.maxNumVeins = toCopy.getMaxNumVeins();
        this.maxNumVeinAttempts = toCopy.getMaxNumVeinAttempts();
        this.minVeinSize = toCopy.getMinVeinSize();
        this.maxVeinSize = toCopy.getMaxVeinSize();
        this.maxNumGrowAttempts = toCopy.getMaxNumGrowAttempts();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        blocksToReplace.save(output);
        allowedBiomes.save(output);
        oreMaterial.save(output);
        output.addInts(minY, maxY);
        chance.save(output);
        output.addInts(minNumVeins, maxNumVeins, maxNumVeinAttempts);
        output.addInts(minVeinSize, maxVeinSize, maxNumGrowAttempts);
    }

    @Override
    public OreVeinGeneratorValues copy(boolean mutable) {
        return new OreVeinGeneratorValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof OreVeinGeneratorValues) {
            OreVeinGeneratorValues otherVein = (OreVeinGeneratorValues) other;
            return this.blocksToReplace.equals(otherVein.blocksToReplace) && this.allowedBiomes.equals(otherVein.allowedBiomes)
                    && this.oreMaterial.equals(otherVein.oreMaterial) && this.minY == otherVein.minY
                    && this.maxY == otherVein.maxY && this.chance.equals(otherVein.chance)
                    && this.minNumVeins == otherVein.minNumVeins && this.maxNumVeins == otherVein.maxNumVeins
                    && this.maxNumVeinAttempts == otherVein.maxNumVeinAttempts && this.minVeinSize == otherVein.minVeinSize
                    && this.maxVeinSize == otherVein.maxVeinSize && this.maxNumGrowAttempts == otherVein.maxNumGrowAttempts;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        Optional<BlockProducerValues.Entry> maxEntry = oreMaterial.getEntries().stream().max(
                Comparator.comparingInt(entry -> entry.getChance().getRawValue())
        );
        if (maxEntry.isPresent()) {
            return maxEntry.get().getBlock().toString();
        } else {
            return "nothing";
        }
    }

    public ReplaceBlocksValues getBlocksToReplace() {
        return blocksToReplace;
    }

    public AllowedBiomesValues getAllowedBiomes() {
        return allowedBiomes;
    }

    public BlockProducerValues getOreMaterial() {
        return oreMaterial;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public Chance getChance() {
        return chance;
    }

    public int getMinNumVeins() {
        return minNumVeins;
    }

    public int getMaxNumVeins() {
        return maxNumVeins;
    }

    public int getMaxNumVeinAttempts() {
        return maxNumVeinAttempts;
    }

    public int getMinVeinSize() {
        return minVeinSize;
    }

    public int getMaxVeinSize() {
        return maxVeinSize;
    }

    public int getMaxNumGrowAttempts() {
        return maxNumGrowAttempts;
    }

    public void setBlocksToReplace(ReplaceBlocksValues blocksToReplace) {
        assertMutable();
        this.blocksToReplace = blocksToReplace.copy(false);
    }

    public void setAllowedBiomes(AllowedBiomesValues allowedBiomes) {
        assertMutable();
        this.allowedBiomes = allowedBiomes.copy(false);
    }

    public void setOreMaterial(BlockProducerValues oreMaterial) {
        assertMutable();
        this.oreMaterial = oreMaterial.copy(false);
    }

    public void setMinY(int minY) {
        assertMutable();
        this.minY = minY;
    }

    public void setMaxY(int maxY) {
        assertMutable();
        this.maxY = maxY;
    }

    public void setChance(Chance chance) {
        assertMutable();
        this.chance = chance;
    }

    public void setMinNumVeins(int minNumVeins) {
        assertMutable();
        this.minNumVeins = minNumVeins;
    }

    public void setMaxNumVeins(int maxNumVeins) {
        assertMutable();
        this.maxNumVeins = maxNumVeins;
    }

    public void setMaxNumVeinAttempts(int maxNumVeinAttempts) {
        assertMutable();
        this.maxNumVeinAttempts = maxNumVeinAttempts;
    }

    public void setMinVeinSize(int minVeinSize) {
        assertMutable();
        this.minVeinSize = minVeinSize;
    }

    public void setMaxVeinSize(int maxVeinSize) {
        assertMutable();
        this.maxVeinSize = maxVeinSize;
    }

    public void setMaxNumGrowAttempts(int maxNumGrowAttempts) {
        assertMutable();
        this.maxNumGrowAttempts = maxNumGrowAttempts;
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (blocksToReplace == null) throw new ProgrammingValidationException("No blocks to replace");
        Validation.scope("Blocks to replace", blocksToReplace::validate, itemSet);

        if (allowedBiomes == null) throw new ProgrammingValidationException("No allowed biomes");
        Validation.scope("Allowed biomes", allowedBiomes::validate);

        if (oreMaterial == null) throw new ProgrammingValidationException("No ore material");
        Validation.scope("Ore material", oreMaterial::validate, itemSet);

        if (minY < 0) throw new ValidationException("Minimum Y-coordinate can't be negative");
        if (minY > maxY) throw new ValidationException("Minimum Y-coordinate can't be larger than maximum Y-coordinate");

        if (chance == null) throw new ProgrammingValidationException("No chance");

        if (minNumVeins <= 0) throw new ValidationException("Minimum number of veins must be positive");
        if (minNumVeins > maxNumVeins) {
            throw new ValidationException("Minimum number of veins can't be larger than maximum number of veins");
        }
        if (maxNumVeins > maxNumVeinAttempts) {
            throw new ValidationException("Maximum number of veins can't be larger than maximum number of vein attempts");
        }

        if (minVeinSize <= 0) throw new ValidationException("Minimum vein size must be positive");
        if (minVeinSize > maxVeinSize) {
            throw new ValidationException("Minimum vein size can't be larger than maximum vein size");
        }
        if (maxVeinSize > maxNumGrowAttempts) {
            throw new ValidationException("Maximum vein size can't be larger than maximum number of grow attempts");
        }
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Blocks to replace", blocksToReplace::validateExportVersion, version);
        Validation.scope("Allowed biomes", allowedBiomes::validateExportVersion, version);
        Validation.scope("Ore material", oreMaterial::validateExportVersion, version);
    }
}
