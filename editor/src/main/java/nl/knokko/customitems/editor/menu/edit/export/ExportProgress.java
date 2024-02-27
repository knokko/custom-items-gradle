package nl.knokko.customitems.editor.menu.edit.export;

import nl.knokko.gui.component.GuiComponent;

public class ExportProgress {

    public static final int STATUS_GENERATING_RESOURCEPACK = 1;
    public static final int STATUS_GENERATING_GEYSERPACK = 2;
    public static final int STATUS_UPLOADING_RESOURCEPACK = 3;
    public static final int STATUS_SAVING_ITEM_SET = 4;

    public volatile int status;
    public volatile GuiComponent nextMenu;
    public volatile String error;
}
