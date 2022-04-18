package nl.knokko.customitems.editor.menu.edit.container.recipe;

import nl.knokko.customitems.container.ContainerRecipeValues;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.recipe.result.ChooseResult;
import nl.knokko.customitems.editor.util.StringLength;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.mousecode.MouseCode;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.TextBuilder;

public class ManualOutputSlotComponent implements GuiComponent {

    private static final GuiColor BASE_COLOR = new SimpleGuiColor(100, 0, 0);
    private static final GuiColor HOVER_COLOR = new SimpleGuiColor(100, 50, 50);

    private final String name;
    private final GuiComponent outerMenu;
    private final ItemSet set;
    private final ContainerRecipeValues recipe;

    private GuiComponentState state;
    private GuiTexture topTextTexture;
    private GuiTexture bottomTextTexture;
    private String currentBottomText;

    public ManualOutputSlotComponent(String name, GuiComponent outerMenu,
                               ContainerRecipeValues recipe, ItemSet set) {
        this.name = name;
        this.outerMenu = outerMenu;
        this.recipe = recipe;
        this.set = set;
    }

    private void setResult(ResultValues newResult) {

        if (newResult != null) {
            recipe.setManualOutput(this.name, newResult);
        } else {
            if (this.name.equals(recipe.getManualOutputSlotName())) {
                recipe.setManualOutput(null, null);
            }
        }

        // Update text
        this.currentBottomText = newResult == null ? "" : StringLength.fixLength(newResult.toString(), 12);
        this.bottomTextTexture = state.getWindow().getTextureLoader().loadTexture(
                TextBuilder.createTexture(this.currentBottomText, EditProps.LABEL)
        );
        state.getWindow().markChange();
    }

    @Override
    public void init() {
        this.topTextTexture = state.getWindow().getTextureLoader().loadTexture(
                TextBuilder.createTexture("man. out.", EditProps.LABEL)
        );
        if (this.name.equals(recipe.getManualOutputSlotName())) {
            this.setResult(recipe.getManualOutput());
        } else {
            this.setResult(null);
        }
    }

    @Override
    public void setState(GuiComponentState state) {
        this.state = state;
    }

    @Override
    public GuiComponentState getState() {
        return state;
    }

    @Override
    public void update() {}

    @Override
    public void render(GuiRenderer renderer) {
        renderer.fill(state.isMouseOver() ? BASE_COLOR : HOVER_COLOR,
                0.1f, 0.1f, 0.9f, 0.9f
        );
        renderer.renderTexture(topTextTexture, 0.1f, 0.6f, 0.9f, 0.9f);

        ResultValues ownResult = this.name.equals(recipe.getManualOutputSlotName()) ? recipe.getManualOutput() : null;
        String expectedBottomText = ownResult == null ? "" : StringLength.fixLength(ownResult.toString(), 12);
        if (!expectedBottomText.equals(this.currentBottomText)) {
            this.currentBottomText = expectedBottomText;
            this.bottomTextTexture = state.getWindow().getTextureLoader().loadTexture(
                    TextBuilder.createTexture(this.currentBottomText, EditProps.LABEL)
            );
        }
        renderer.renderTexture(bottomTextTexture, 0.1f, 0.1f, 0.9f, 0.4f);
    }

    @Override
    public void click(float x, float y, int button) {
        if (button == MouseCode.BUTTON_LEFT) {
            state.getWindow().setMainComponent(new ChooseResult(
                    outerMenu, this::setResult, set
            ));
        } else if (button == MouseCode.BUTTON_RIGHT) {
            if (this.name.equals(recipe.getManualOutputSlotName())) {
                this.setResult(null);
            }
        }
    }

    @Override
    public void clickOut(int button) {}

    @Override
    public boolean scroll(float amount) {
        return false;
    }

    @Override
    public void keyPressed(int keyCode) {}

    @Override
    public void keyPressed(char character) {}

    @Override
    public void keyReleased(int keyCode) {}
}
