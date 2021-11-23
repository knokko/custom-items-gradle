package nl.knokko.customitems.projectile.cover;

import nl.knokko.customitems.model.Model;

public class SProjectileCover extends Model<ProjectileCoverValues> {
    public SProjectileCover(ProjectileCoverValues values) {
        super(values);
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
