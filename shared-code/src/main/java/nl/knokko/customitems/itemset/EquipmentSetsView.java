package nl.knokko.customitems.itemset;

import nl.knokko.customitems.item.equipment.EquipmentSet;
import nl.knokko.customitems.item.equipment.EquipmentSetValues;
import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class EquipmentSetsView extends CollectionView<EquipmentSet, EquipmentSetValues, EquipmentSetReference> {

    public EquipmentSetsView(Collection<EquipmentSet> liveCollection) {
        super(liveCollection, EquipmentSetReference::new);
    }
}
