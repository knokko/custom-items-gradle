package nl.knokko.gui.testing;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import nl.knokko.gui.window.GuiWindow;

public class GuiTester {
	
	private static GuiTestHelper testHelper;
	
	/**
	 * Starts the provided test on the provided test helper. This method will wait a half second before
	 * starting the actual test to give eventual other threads some time to start up.
	 * This method will block until the test finishes. If the test crashes/fails, a TestException will be
	 * thrown instead.
	 * You will probably want to use start(GuiTestProgram, GuiWindow) instead of this method.
	 * Warning: Your test will very likely take control over the mouse, so it can be quite annoying if you
	 * end up in an endless loop that keeps moving the mouse. That's why I recommend using jnativehook to
	 * register some kind of emergency listener that listens for some key input and calls the stop()
	 * method of this call.
	 * @param test The test program
	 * @param helper The test helper
	 * 
	 * @throws TestException If the provided test program throws a TestException or if the test has been
	 * forced to stop early.
	 * @throws IllegalStateException If a test is currently running
	 */
	public static void start(GuiTestProgram test, GuiTestHelper helper) throws TestException, IllegalStateException {
		if (isRunning()) {
			throw new IllegalStateException("A test is still running");
		}
		testHelper = helper;
		try {
			helper.delay(500);
			long startTime = System.currentTimeMillis();
			test.test(helper);
			long endTime = System.currentTimeMillis();
			System.out.println("The test took " + (endTime - startTime) / 1000 + " seconds.");
		} catch (RuntimeException ex) {
			testHelper = null;
			try {
				BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
				ImageIO.write(image, "png", new File("before_test_failed.png"));
			} catch (Exception screenEx) {
				System.out.println("Failed to make screenshot:");
				screenEx.printStackTrace();
			}
			if (ex instanceof TestException) {
				throw ex;
			} else {
				throw new TestException("Test failed because of runtime exception: ", ex);
			}
		}
		testHelper = null;
	}
	
	/**
	 * Lets the provided test program test the provided window. This method will wait a half second before
	 * starting the actual test to give the window time to open and finish its native business.
	 * This method will open the window and call its run method on another thread, so don't call those
	 * methods yourself.
	 * This method will block until the test finishes. If the test crashes/fails, a TestException will be
	 * thrown instead.
	 * Warning: Your test will very likely take control over the mouse, so it can be quite annoying if you
	 * end up in an endless loop that keeps moving the mouse. That's why I recommend using jnativehook to
	 * register some kind of emergency listener that listens for some key input and calls the stop()
	 * method of this call.
	 * @param test The test program
	 * @param helper The test helper
	 * 
	 * @throws TestException If the provided test program throws a TestException or if the test has been
	 * forced to stop early.
	 * @throws IllegalStateException If a test is currently running
	 */
	public static void start(GuiTestProgram test, GuiWindow window) throws TestException, IllegalStateException {
		Thread windowThread = new Thread(() -> {
			try {
				SwingUtilities.invokeAndWait(() -> {
					window.open("Test Window", 1000, 600, true);
				});
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (InterruptedException e) {
				System.out.println("Interrupted before running");
				e.printStackTrace();
				return;
			}
			window.run(60);
		});
		windowThread.start();
		RobotTestHelper helper = new RobotTestHelper(window);
		try {
			start(test, helper);
			System.out.println("The start method terminated");
			window.stopRunning();
		} catch (TestException testFailed) {
			window.stopRunning();
			throw testFailed;
		}
	}
	
	/**
	 * Checks if a test is currently running. Returns true if so and false if not
	 * @return true if a test is running now and false if no test is running
	 */
	public static boolean isRunning() {
		return testHelper != null;
	}
	
	/**
	 * Forces the current test to stop. The test helper will take care that all involved threads will be
	 * stopped soon. The currently running start method will throw a TestException.
	 * RuntimeException.
	 * 
	 * @throws IllegalStateException If no test is running
	 */
	public static void stop() throws IllegalStateException {
		if (testHelper != null) {
			testHelper.stop();
		} else {
			throw new IllegalStateException("There is no running test");
		}
	}
}