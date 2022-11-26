package nl.knokko.gui.mousecode;

public class AWTMouseConverter {

	public static int getMouseButton(int awtButton) {
		if (awtButton == 2) {
			return 3;
		} else if (awtButton == 3) {
			return 2;
		} else {
			return awtButton;
		}
	}
}