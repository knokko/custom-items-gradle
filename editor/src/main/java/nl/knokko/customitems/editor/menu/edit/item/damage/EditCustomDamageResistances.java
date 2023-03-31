package nl.knokko.customitems.editor.menu.edit.item.damage;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.DamageResistanceValues;
import nl.knokko.customitems.itemset.CustomDamageSourceReference;
import nl.knokko.customitems.itemset.CustomDamageSourcesView;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditCustomDamageResistances extends GuiMenu {

    private final CustomDamageSourcesView allDamageSources;
    private final GuiComponent returnMenu;
    private final DamageResistanceValues currentResistances;

    public EditCustomDamageResistances(
            CustomDamageSourcesView allDamageSources, GuiComponent returnMenu, DamageResistanceValues currentResistances
    ) {
        this.allDamageSources = allDamageSources;
        this.returnMenu = returnMenu;
        this.currentResistances = currentResistances;
    }

    private CustomDamageSourceReference nextDamageSource() {
        for (CustomDamageSourceReference candidate : allDamageSources.references()) {
            if (currentResistances.getResistance(candidate) == 0) return candidate;
        }
        return null;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Back", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.15f, 0.8f);

        EntryList entryList = new EntryList();
        addComponent(entryList, 0.4f, 0f, 0.9f, 0.9f);

        addComponent(new ConditionalTextButton("Add entry", BUTTON, HOVER, () -> {
            currentResistances.setResistance(nextDamageSource(), (short) 50);
            entryList.refresh();
        }, () -> nextDamageSource() != null), 0.025f, 0.3f, 0.2f, 0.4f);

        HelpButtons.addHelpLink(this, "edit menu/items/damage source/resistances.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }

    private class EntryList extends GuiMenu {

        @Override
        protected void addComponents() {
            int index = 0;
            for (CustomDamageSourceReference currentDamageSource : allDamageSources.references()) {
                if (currentResistances.getResistance(currentDamageSource) != 0) {
                    float maxY = 1f - index * 0.11f;
                    float minY = maxY - 0.1f;
                    addComponent(CollectionSelect.createButton(
                            allDamageSources.references(), newDamageSource -> {
                                short oldResistance = currentResistances.getResistance(currentDamageSource);
                                currentResistances.setResistance(currentDamageSource, currentResistances.getResistance(newDamageSource));
                                currentResistances.setResistance(newDamageSource, oldResistance);
                                refresh();
                            }, damageSource -> damageSource.get().getName(), currentDamageSource, false
                    ), 0.05f, minY, 0.5f, maxY);
                    addComponent(new EagerIntEditField(
                            currentResistances.getResistance(currentDamageSource), Short.MIN_VALUE, Short.MAX_VALUE,
                            EDIT_BASE, EDIT_ACTIVE, newResistance -> {
                                currentResistances.setResistance(currentDamageSource, (short) newResistance);
                                if (newResistance == 0) refresh();
                            }
                    ), 0.55f, minY, 0.8f, maxY);
                    addComponent(new DynamicTextButton("X", QUIT_BASE, QUIT_HOVER, () -> {
                        currentResistances.setResistance(currentDamageSource, (short) 0);
                        refresh();
                    }), 0.85f, minY, 0.95f, maxY);
                    index += 1;
                }
            }
        }

        private void refresh() {
            clearComponents();
            addComponents();
        }

        @Override
        public GuiColor getBackgroundColor() {
            return BACKGROUND2;
        }
    }
}
