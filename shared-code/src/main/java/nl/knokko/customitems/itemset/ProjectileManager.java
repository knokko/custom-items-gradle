package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.projectile.KciProjectile;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.NoSuchElementException;
import java.util.Optional;

public class ProjectileManager extends ModelManager<KciProjectile, ProjectileReference> {

    protected ProjectileManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(KciProjectile projectile, BitOutput output, ItemSet.Side targetSide) {
        projectile.save(output);
    }

    @Override
    ProjectileReference createReference(Model<KciProjectile> element) {
        return new ProjectileReference(element);
    }

    @Override
    protected KciProjectile loadElement(BitInput input) throws UnknownEncodingException {
        return KciProjectile.load(input, itemSet);
    }

    @Override
    protected void validateExportVersion(KciProjectile projectile, int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Projectile " + projectile.getName(),
                () -> projectile.validateExportVersion(mcVersion)
        );
    }

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        super.validate();
        validateUniqueIDs("projectile name", elements, projectile -> projectile.getValues().getName());
    }

    @Override
    protected void validateCreation(KciProjectile values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet, null);
    }

    @Override
    protected void validate(KciProjectile projectile) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Projectile " + projectile.getName(),
                () -> projectile.validate(itemSet, projectile.getName())
        );
    }

    @Override
    protected void validateChange(ProjectileReference reference, KciProjectile newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet, reference.get().getName());
    }

    public ProjectileReference getReference(String name) throws NoSuchElementException {
        if (itemSet.finishedLoading) {
            return new ProjectileReference(CollectionHelper.find(elements, projectile -> projectile.getValues().getName(), name).get());
        } else {
            return new ProjectileReference(name, itemSet);
        }
    }

    public Optional<KciProjectile> get(String name) {
        return CollectionHelper.find(elements, projectile -> projectile.getValues().getName(), name).map(Model::getValues);
    }
}
