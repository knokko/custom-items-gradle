package nl.knokko.gui.testing;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import static java.awt.event.KeyEvent.*;

import nl.knokko.gui.window.GuiWindow;

public class RobotTestHelper extends GuiTestHelper {

	private final Robot robot;

	public RobotTestHelper(GuiWindow window) {
		super(window);
		try {
			robot = new Robot();

			// The GuiTestHelper already keeps track of the right delay
			robot.setAutoDelay(1);
		} catch (AWTException e) {
			throw new TestException("Couldn't create Robot instance: " + e.getMessage());
		}
	}

	@Override
	protected void moveMouseNow(int destX, int destY) {
		int absoluteX = destX + window.getPosX();
		int absoluteY = destY + window.getPosY();

		// We do this multiple times because of a bug in the JDK. The more calls to
		// mouseMove, the more accurate it will be...
		for (int counter = 0; counter < 5; counter++) {
			robot.mouseMove(absoluteX, absoluteY);
		}
	}

	@Override
	protected void clickNow(int button) {
		int buttonMask = InputEvent.getMaskForButton(button + 1);
		robot.mousePress(buttonMask);
		delay();
		robot.mouseRelease(buttonMask);
	}

	@Override
	protected void typeNow(char character) {
		boolean useShift = false;
		if (Character.isUpperCase(character)) {
			character = Character.toLowerCase(character);
			useShift = true;
		}

		int code;

		switch (character) {
		case '`':
			code = VK_BACK_QUOTE;
			break;
		case '0':
			code = VK_0;
			break;
		case '1':
			code = VK_1;
			break;
		case '2':
			code = VK_2;
			break;
		case '3':
			code = VK_3;
			break;
		case '4':
			code = VK_4;
			break;
		case '5':
			code = VK_5;
			break;
		case '6':
			code = VK_6;
			break;
		case '7':
			code = VK_7;
			break;
		case '8':
			code = VK_8;
			break;
		case '9':
			code = VK_9;
			break;
		case '-':
			code = VK_MINUS;
			break;
		case '=':
			code = VK_EQUALS;
			break;
		case '~':
			useShift = true;
			code = VK_BACK_QUOTE;
			break;
		case '!':
			code = VK_EXCLAMATION_MARK;
			break;
		case '@':
			code = VK_AT;
			break;
		case '#':
			code = VK_NUMBER_SIGN;
			break;
		case '$':
			code = VK_DOLLAR;
			break;
		case '%':
			useShift = true;
			code = VK_5;
			break;
		case '^':
			code = VK_CIRCUMFLEX;
			break;
		case '&':
			code = VK_AMPERSAND;
			break;
		case '*':
			code = VK_ASTERISK;
			break;
		case '(':
			code = VK_LEFT_PARENTHESIS;
			break;
		case ')':
			code = VK_RIGHT_PARENTHESIS;
			break;
		case '_':
			useShift = true;
			code = KeyEvent.VK_MINUS;
			break;
		case '+':
			code = VK_PLUS;
			break;
		case '\t':
			code = VK_TAB;
			break;
		case '\n':
			code = VK_ENTER;
			break;
		case '[':
			code = VK_OPEN_BRACKET;
			break;
		case ']':
			code = VK_CLOSE_BRACKET;
			break;
		case '\\':
			code = VK_BACK_SLASH;
			break;
		case '{':
			useShift = true;
			code = VK_OPEN_BRACKET;
			break;
		case '}':
			useShift = true;
			code = VK_CLOSE_BRACKET;
			break;
		case '|':
			useShift = true;
			code = VK_BACK_SLASH;
			break;
		case ';':
			code = VK_SEMICOLON;
			break;
		case ':':
			code = VK_COLON;
			break;
		case '\'':
			code = VK_QUOTE;
			break;
		case '"':
			code = VK_QUOTEDBL;
			break;
		case ',':
			code = VK_COMMA;
			break;
		case '<':
			useShift = true;
			code = VK_COMMA;
			break;
		case '.':
			code = VK_PERIOD;
			break;
		case '>':
			useShift = true;
			code = VK_PERIOD;
			break;
		case '/':
			code = VK_SLASH;
			break;
		case '?':
			useShift = true;
			code = VK_SLASH;
			break;
		case ' ':
			code = VK_SPACE;
			break;
		default:
			code = KeyEvent.getExtendedKeyCodeForChar(character);
		}
		if (useShift) {
			robot.keyPress(VK_SHIFT);
		}
		robot.keyPress(code);
		typeDelay();
		robot.keyRelease(code);
		if (useShift) {
			robot.keyRelease(KeyEvent.VK_SHIFT);
		}
	}

	@Override
	protected void pressAndReleaseNow(int keycode) {
		robot.keyPress(keycode);
		typeDelay();
		robot.keyRelease(keycode);
	}
}