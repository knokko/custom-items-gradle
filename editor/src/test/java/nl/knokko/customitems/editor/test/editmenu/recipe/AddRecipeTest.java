package nl.knokko.customitems.editor.test.editmenu.recipe;

import nl.knokko.gui.testing.GuiTestHelper;

public class AddRecipeTest {
	
	public static void addShapelessRecipe(GuiTestHelper test, String result, String resultAmount, String...ingredients) {
		
		test.click("Create shapeless recipe");
		test.assertComponentsWithTexts("Cancel", "Add ingredient", "Result", "Diamond x 1");
		
		test.click("Diamond x 1");
		test.click("Change");
		test.click("1");
		test.backspace(1);
		test.type(resultAmount);
		test.assertComponentWithText(resultAmount);
		select(test, result, "Select");
		test.click("Select");
		
		for (String ingredient : ingredients) {
			test.click("Add ingredient");
			select(test, ingredient, "Result");
		}
		
		test.click("Create");
	}
	
	private static void select(GuiTestHelper test, String ingredient, String indication) {
		int index1 = ingredient.indexOf(';');
		test.click(ingredient.substring(0, index1));
		int index2 = ingredient.indexOf(';', index1 + 1);
		if (index2 == -1) {
			test.click(ingredient.substring(index1 + 1));
			test.assertComponentWithText(indication);
		} else {
			test.click(ingredient.substring(index1 + 1, index2));
			test.click(ingredient.substring(index2 + 1));
			test.assertComponentWithText(indication);
		}
	}
}
