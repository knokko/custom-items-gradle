package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.projectile.cover.ProjectileCoverValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.NoSuchElementException;
import java.util.Optional;

public class ProjectileCoverManager extends ModelManager<ProjectileCoverValues, ProjectileCoverReference> {

    protected ProjectileCoverManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(ProjectileCoverValues projectileCover, BitOutput output, ItemSet.Side targetSide) {
        projectileCover.save(output, targetSide);
    }

    @Override
    ProjectileCoverReference createReference(Model<ProjectileCoverValues> element) {
        return new ProjectileCoverReference(element);
    }

    @Override
    protected ProjectileCoverValues loadElement(BitInput input) throws UnknownEncodingException {
        return ProjectileCoverValues.load(input, itemSet);
    }

    @Override
    protected void validateExportVersion(
            ProjectileCoverValues projectileCover, int mcVersion
    ) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Projectile cover " + projectileCover.getName(),
                () -> projectileCover.validateExportVersion(mcVersion)
        );
    }

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        super.validate();
        validateUniqueIDs("projectile cover name", elements, projectileCover -> projectileCover.getValues().getName());
    }

    @Override
    protected void validateCreation(ProjectileCoverValues values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet, null);
    }

    @Override
    protected void validate(ProjectileCoverValues projectileCover) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Projectile cover " + projectileCover.getName(),
                () -> projectileCover.validate(itemSet, projectileCover.getName())
        );
    }

    @Override
    protected void validateChange(ProjectileCoverReference reference, ProjectileCoverValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet, reference.get().getName());
    }

    public ProjectileCoverReference getReference(String name) throws NoSuchElementException {
        if (itemSet.finishedLoading) {
            return new ProjectileCoverReference(CollectionHelper.find(elements, cover -> cover.getValues().getName(), name).get());
        } else {
            return new ProjectileCoverReference(name, itemSet);
        }
    }

    public Optional<ProjectileCoverValues> get(String name) {
        return CollectionHelper.find(elements, cover -> cover.getValues().getName(), name).map(Model::getValues);
    }
}
