package nl.knokko.customitems.editor.menu.edit.export;

import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.LABEL;

public class AfterExportMenuMixed extends AfterExportMenu {

    public AfterExportMenuMixed(GuiComponent returnMenu) {
        super(returnMenu);
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addExportedFilesInfo();
        addComponent(new DynamicTextComponent(
                "You should copy resource-pack.zip to YourServerFolder/plugins/CustomItems/resource-pack.zip", LABEL
        ), 0.025f, 0.5f, 0.975f, 0.6f);
    }
}
