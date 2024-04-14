package nl.knokko.customitems.container;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.NameHelper;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.drops.CIEntityType;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.BlockReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.Objects;

public class CustomContainerHost {

    public static CustomContainerHost load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte primaryEncoding = input.readByte();
        if (primaryEncoding != 1) throw new UnknownEncodingException("ContainerHost", primaryEncoding);

        byte hostType = input.readByte();
        if (hostType == 1) {
            return new CustomContainerHost(VanillaContainerType.valueOf(input.readString()));
        } else if (hostType == 2) {
            return new CustomContainerHost(CIMaterial.valueOf(input.readString()));
        } else if (hostType == 3) {
            return new CustomContainerHost(itemSet.blocks.getReference(input.readInt()));
        } else if (hostType == 4) {
            return new CustomContainerHost(CIEntityType.valueOf(input.readString()));
        } else {
            throw new UnknownEncodingException("ContainerHostType", hostType);
        }
    }

    private final VanillaContainerType vanillaType;
    private final CIMaterial vanillaMaterial;
    private final CIEntityType vanillaEntity;
    private final BlockReference customBlock;

    public CustomContainerHost(VanillaContainerType vanillaType) {
        Checks.notNull(vanillaType);
        this.vanillaType = vanillaType;
        this.vanillaMaterial = null;
        this.vanillaEntity = null;
        this.customBlock = null;
    }

    public CustomContainerHost(CIMaterial vanillaMaterial) {
        Checks.notNull(vanillaMaterial);
        this.vanillaType = null;
        this.vanillaMaterial = vanillaMaterial;
        this.vanillaEntity = null;
        this.customBlock = null;
    }

    public CustomContainerHost(CIEntityType vanillaEntity) {
        this.vanillaType = null;
        this.vanillaMaterial = null;
        this.vanillaEntity = Objects.requireNonNull(vanillaEntity);
        this.customBlock = null;
    }

    public CustomContainerHost(BlockReference customBlock) {
        Checks.notNull(customBlock);
        this.vanillaType = null;
        this.vanillaMaterial = null;
        this.vanillaEntity = null;
        this.customBlock = customBlock;
    }

    public VanillaContainerType getVanillaType() {
        return this.vanillaType;
    }

    public CIMaterial getVanillaMaterial() {
        return this.vanillaMaterial;
    }

    public CIEntityType getVanillaEntity() {
        return this.vanillaEntity;
    }

    public BlockReference getCustomBlockReference() {
        return this.customBlock;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CustomContainerHost) {
            CustomContainerHost otherHost = (CustomContainerHost) other;
            return Objects.equals(this.vanillaType, otherHost.vanillaType)
                    && Objects.equals(this.vanillaMaterial, otherHost.vanillaMaterial)
                    && Objects.equals(this.vanillaEntity, otherHost.vanillaEntity)
                    && Objects.equals(this.customBlock, otherHost.customBlock);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        if (this.vanillaType != null) return NameHelper.getNiceEnumName(this.vanillaType.name());
        if (this.vanillaMaterial != null) return this.vanillaMaterial.toString();
        if (this.vanillaEntity != null) return this.vanillaEntity.toString();
        return this.customBlock.get().getName();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.vanillaType) + 3 * Objects.hashCode(this.vanillaMaterial)
                + 5 * Objects.hashCode(vanillaEntity) - 13 * Objects.hashCode(this.customBlock);
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        if (this.vanillaType != null) {
            output.addByte((byte) 1);
            output.addString(this.vanillaType.name());
        } else if (this.vanillaMaterial != null) {
            output.addByte((byte) 2);
            output.addString(this.vanillaMaterial.name());
        } else if (this.customBlock != null) {
            output.addByte((byte) 3);
            output.addInt(this.customBlock.get().getInternalID());
        } else if (this.vanillaEntity != null) {
            output.addByte((byte) 4);
            output.addString(this.vanillaEntity.name());
        } else {
            throw new IllegalStateException("Can't determine host type of " + this);
        }
    }

    public void validate(ItemSet itemSet) throws ProgrammingValidationException {
        if (this.customBlock != null && !itemSet.blocks.isValid(this.customBlock)) {
            throw new ProgrammingValidationException("Custom block is no longer valid");
        }
    }

    public void validateExportVersion(int version) throws ValidationException {
        if (this.vanillaType != null) {
            if (this.vanillaType.firstVersion > version) {
                throw new ValidationException(this.vanillaType + " didn't exist yet in minecraft " + MCVersions.createString(version));
            }
            if (this.vanillaType.lastVersion < version) {
                throw new ValidationException(this.vanillaType + " doesn't exist anymore in minecraft " + MCVersions.createString(version));
            }
        } else if (this.vanillaMaterial != null) {
            if (this.vanillaMaterial.firstVersion > version) {
                throw new ValidationException(this.vanillaMaterial + " didn't exist yet in minecraft " + MCVersions.createString(version));
            }
            if (this.vanillaMaterial.lastVersion < version) {
                throw new ValidationException(this.vanillaMaterial + " doesn't exist anymore in minecraft " + MCVersions.createString(version));
            }
        } else if (this.vanillaEntity != null) {
            if (version < MCVersions.VERSION1_14) throw new ValidationException("Entity container hosts require MC 1.14 or later");
            if (this.vanillaEntity.firstVersion > version) {
                throw new ValidationException(this.vanillaEntity + " didn't exist yet in minecraft " + MCVersions.createString(version));
            }
            if (this.vanillaEntity.lastVersion < version) {
                throw new ValidationException(this.vanillaEntity + " doesn't exist anymore in minecraft " + MCVersions.createString(version));
            }
        } else if (this.customBlock != null) {
            if (version < MCVersions.VERSION1_13) {
                throw new ValidationException("Custom blocks aren't supported before minecraft 1.13");
            }
        } else {
            throw new ValidationException("Unknown container host type: " + this);
        }
    }
}
