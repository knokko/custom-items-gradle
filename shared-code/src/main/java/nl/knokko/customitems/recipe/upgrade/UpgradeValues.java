package nl.knokko.customitems.recipe.upgrade;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.item.AttributeModifierValues;
import nl.knokko.customitems.item.DamageResistanceValues;
import nl.knokko.customitems.item.enchantment.EnchantmentValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class UpgradeValues extends ModelValues {

    public static UpgradeValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("Upgrade", encoding);

        UpgradeValues upgrade = new UpgradeValues(false);
        upgrade.id = new UUID(input.readLong(), input.readLong());
        upgrade.name = input.readString();

        upgrade.enchantments = Collections.unmodifiableList(CollectionHelper.load(input, EnchantmentValues::load1));
        upgrade.attributeModifiers = Collections.unmodifiableList(CollectionHelper.load(input, AttributeModifierValues::load1));
        upgrade.damageResistances = DamageResistanceValues.loadNew(input, itemSet);
        upgrade.variables = Collections.unmodifiableList(CollectionHelper.load(input, VariableUpgradeValues::load));
        return upgrade;
    }

    private UUID id;
    private String name;

    private Collection<EnchantmentValues> enchantments;
    private Collection<AttributeModifierValues> attributeModifiers;
    private DamageResistanceValues damageResistances;
    private Collection<VariableUpgradeValues> variables;

    public UpgradeValues(boolean mutable) {
        super(mutable);
        this.id = UUID.randomUUID();
        this.name = "";

        this.enchantments = Collections.emptyList();
        this.attributeModifiers = Collections.emptyList();
        this.damageResistances = new DamageResistanceValues(false);
        this.variables = Collections.emptyList();
    }

    public UpgradeValues(UpgradeValues toCopy, boolean mutable) {
        super(mutable);
        this.id = toCopy.getId();
        this.name = toCopy.getName();

        this.enchantments = toCopy.getEnchantments();
        this.attributeModifiers = toCopy.getAttributeModifiers();
        this.damageResistances = toCopy.getDamageResistances();
        this.variables = toCopy.getVariables();
    }

    @Override
    public UpgradeValues copy(boolean mutable) {
        return new UpgradeValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof UpgradeValues) {
            UpgradeValues otherUpgrade = (UpgradeValues) other;
            return this.id.equals(otherUpgrade.id) && this.name.equals(otherUpgrade.name)
                    && this.enchantments.equals(otherUpgrade.enchantments)
                    && this.attributeModifiers.equals(otherUpgrade.attributeModifiers)
                    && this.damageResistances.equals(otherUpgrade.damageResistances)
                    && this.variables.equals(otherUpgrade.variables);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Upgrade(" + name + ", " + enchantments + ", " + attributeModifiers +
                ", " + damageResistances + ", " + variables + ")";
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addLong(id.getMostSignificantBits());
        output.addLong(id.getLeastSignificantBits());
        output.addString(name);

        CollectionHelper.save(enchantments, enchantment -> enchantment.save1(output), output);
        CollectionHelper.save(attributeModifiers, modifier -> modifier.save1(output), output);
        damageResistances.saveNew(output);
        CollectionHelper.save(variables, variable -> variable.save(output), output);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Collection<EnchantmentValues> getEnchantments() {
        return enchantments;
    }

    public Collection<AttributeModifierValues> getAttributeModifiers() {
        return attributeModifiers;
    }

    public DamageResistanceValues getDamageResistances() {
        return damageResistances;
    }

    public Collection<VariableUpgradeValues> getVariables() {
        return variables;
    }

    public void chooseNewId() {
        assertMutable();
        this.id = UUID.randomUUID();
    }

    public void setName(String name) {
        assertMutable();
        this.name = Objects.requireNonNull(name);
    }

    public void setEnchantments(Collection<EnchantmentValues> enchantments) {
        assertMutable();
        this.enchantments = Collections.unmodifiableCollection(enchantments);
    }

    public void setAttributeModifiers(Collection<AttributeModifierValues> attributeModifiers) {
        assertMutable();
        this.attributeModifiers = Collections.unmodifiableCollection(attributeModifiers);
    }

    public void setDamageResistances(DamageResistanceValues damageResistances) {
        assertMutable();
        this.damageResistances = damageResistances.copy(false);
    }

    public void setVariables(Collection<VariableUpgradeValues> variables) {
        assertMutable();
        this.variables = Collections.unmodifiableCollection(variables);
    }

    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (id == null) throw new ProgrammingValidationException("No ID");
        if (name == null) throw new ProgrammingValidationException("No name");

        if (enchantments == null) throw new ProgrammingValidationException("No enchantments");
        if (enchantments.contains(null)) throw new ProgrammingValidationException("Missing an enchantment");
        for (EnchantmentValues enchantment : enchantments) {
            Validation.scope("Enchantment", enchantment::validate);
        }

        if (attributeModifiers == null) throw new ProgrammingValidationException("No attribute modifiers");
        if (attributeModifiers.contains(null)) throw new ProgrammingValidationException("Missing an attribute modifier");
        for (AttributeModifierValues attributeModifier : attributeModifiers) {
            Validation.scope("Attribute modifier", attributeModifier::validate);
        }

        if (damageResistances == null) throw new ProgrammingValidationException("No damage resistances");

        if (variables == null) throw new ProgrammingValidationException("No variables");
        if (variables.contains(null)) throw new ProgrammingValidationException("Missing a variable");
        for (VariableUpgradeValues variable : variables) {
            Validation.scope("Variable", variable::validate);
        }
    }

    public void validateComplete(ItemSet itemSet, UUID oldId) throws ValidationException, ProgrammingValidationException {
        validateIndependent();

        if (oldId != null && !oldId.equals(id)) {
            throw new ProgrammingValidationException("Can't change ID");
        }
        if (oldId == null && itemSet.upgrades.get(id).isPresent()) {
            throw new ProgrammingValidationException("Another upgrade already has this ID");
        }

        Validation.scope("Damage resistances", damageResistances::validate, itemSet);
    }

    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        for (EnchantmentValues enchantment : enchantments) {
            Validation.scope("Enchantment", enchantment::validateExportVersion, mcVersion);
        }
    }
}
