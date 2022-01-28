package nl.knokko.customitems.projectile.cover;

import nl.knokko.customitems.model.Model;

public class ProjectileCover extends Model<ProjectileCoverValues> {
    public ProjectileCover(ProjectileCoverValues values) {
        super(values);
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
