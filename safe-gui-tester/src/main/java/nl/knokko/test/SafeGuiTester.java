package nl.knokko.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import nl.knokko.gui.testing.GuiTestProgram;
import nl.knokko.gui.testing.GuiTester;
import nl.knokko.gui.testing.TestException;
import nl.knokko.gui.window.GuiWindow;

public class SafeGuiTester {

	public static void testSafely(GuiTestProgram test, GuiWindow window) throws NativeHookException {
		
		// Prevent jnativehook from spamming the console
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		// Register the so-called emergency key listener
		// It will terminate the test as soon as the x-key is pressed
		GlobalScreen.addNativeKeyListener(new EmergencyKeyListener());
		GlobalScreen.registerNativeHook();

		// Do the actual test
		// The catching is necessary to make sure the emergency key listener will be
		// removed at the end
		try {
			GuiTester.start(test, window);
		} catch (TestException testex) {
			testex.printStackTrace();
		}

		System.out.println("Unregister native hooks...");

		// Now that the test is over, unregister the emergency key listener
		try {
			GlobalScreen.unregisterNativeHook();
		} catch (Throwable letsPrintIt) {
			letsPrintIt.printStackTrace();
		}

		System.out.println("Unregistered native hooks");
	}

	private static class EmergencyKeyListener implements NativeKeyListener {

		@Override
		public void nativeKeyTyped(NativeKeyEvent nativeEvent) {
		}

		@Override
		public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
			if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_X) {
				GuiTester.stop();
			}
		}

		@Override
		public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
		}
	}
}