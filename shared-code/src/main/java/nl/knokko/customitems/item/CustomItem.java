package nl.knokko.customitems.item;

import nl.knokko.customitems.model.Model;

public class CustomItem extends Model<CustomItemValues> {

    public CustomItem(CustomItemValues values) {
        super(values);
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
