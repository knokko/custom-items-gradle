package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.projectile.CustomProjectile;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.NoSuchElementException;
import java.util.Optional;

public class ProjectileManager extends ModelManager<CustomProjectile, CustomProjectileValues, ProjectileReference> {

    protected ProjectileManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(CustomProjectile projectile, BitOutput output, ItemSet.Side targetSide) {
        projectile.getValues().save(output);
    }

    @Override
    protected ProjectileReference createReference(CustomProjectile element) {
        return new ProjectileReference(element);
    }

    @Override
    protected CustomProjectile loadElement(BitInput input) throws UnknownEncodingException {
        return new CustomProjectile(CustomProjectileValues.load(input, itemSet));
    }

    @Override
    protected void validateExportVersion(CustomProjectileValues projectile, int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Projectile " + projectile.getName(),
                () -> projectile.validateExportVersion(mcVersion)
        );
    }

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        validateUniqueIDs("projectile name", elements, projectile -> projectile.getValues().getName());
    }

    @Override
    protected void validate(CustomProjectileValues projectile) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Projectile " + projectile.getName(),
                () -> projectile.validate(itemSet, projectile.getName())
        );
    }

    @Override
    protected CustomProjectile checkAndCreateElement(CustomProjectileValues values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet, null);
        return new CustomProjectile(values);
    }

    @Override
    protected void validateChange(ProjectileReference reference, CustomProjectileValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet, reference.get().getName());
    }

    public ProjectileReference getReference(String name) throws NoSuchElementException {
        if (itemSet.finishedLoading) {
            return new ProjectileReference(CollectionHelper.find(elements, projectile -> projectile.getValues().getName(), name).get());
        } else {
            return new ProjectileReference(name, itemSet);
        }
    }

    public Optional<CustomProjectileValues> get(String name) {
        return CollectionHelper.find(elements, projectile -> projectile.getValues().getName(), name).map(CustomProjectile::getValues);
    }
}
