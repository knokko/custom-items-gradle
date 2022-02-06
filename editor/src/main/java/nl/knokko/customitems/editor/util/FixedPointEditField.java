package nl.knokko.customitems.editor.util;

import nl.knokko.gui.component.text.EagerTextEditField;

import java.util.function.IntConsumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_ACTIVE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_BASE;

public class FixedPointEditField extends EagerTextEditField {

    private static String toString(int numBackDigits, int value) {
        int modulo = (int) Math.pow(10, numBackDigits);
        int frontValue = value / modulo;
        int backValue = value % modulo;

        StringBuilder backString = new StringBuilder(Integer.toString(backValue));
        while (backString.length() < numBackDigits) {
            backString.insert(0, "0");
        }
        while (backString.length() > 0 && backString.charAt(backString.length() - 1) == '0') {
            backString.deleteCharAt(backString.length() - 1);
        }

        if (backString.length() == 0) {
            return Integer.toString(frontValue);
        } else {
            return frontValue + "." + backString;
        }
    }

    private static Integer fromString(int numBackDigits, int minIntValue, int maxIntValue, String stringValue) {
        try {
            int modulo = (int) Math.pow(10, numBackDigits);

            int indexDot = stringValue.indexOf('.');
            if (indexDot != -1) {
                int frontValue = Integer.parseInt(stringValue.substring(0, indexDot));
                StringBuilder backString = new StringBuilder(stringValue.substring(indexDot + 1));

                // Having too many back digits shouldn't be possible, but when it somehow happens, we shouldn't proceed
                if (backString.length() > numBackDigits) return null;

                // When less than numBackDigits are used, the remaining back digits should be padded with zero's
                while (backString.length() < numBackDigits) {
                    backString.append('0');
                }
                int backValue = Integer.parseInt(backString.toString());

                if (frontValue > maxIntValue) return null;
                if (frontValue < minIntValue) return null;
                // Typically, no digits behind the decimal point are desired when the value before the decimal point is the max value
                if (frontValue == maxIntValue && backValue != 0) return null;

                return frontValue * modulo + backValue;
            } else {
                int frontValue = Integer.parseInt(stringValue);
                if (frontValue > maxIntValue) return null;
                if (frontValue < minIntValue) return null;
                return modulo * frontValue;
            }
        } catch (NumberFormatException invalid) {
            return null;
        }
    }

    private final int numBackDigits, minIntValue, maxIntValue;

    public FixedPointEditField(int numBackDigits, int initialValue, int minIntValue, int maxIntValue, IntConsumer onChange) {
        super(toString(numBackDigits, initialValue), EDIT_BASE, EDIT_ACTIVE, newText -> {
            Integer maybeNewValue = fromString(numBackDigits, minIntValue, maxIntValue, newText);
            if (maybeNewValue != null) onChange.accept(maybeNewValue);
        });
        this.numBackDigits = numBackDigits;
        this.minIntValue = minIntValue;
        this.maxIntValue = maxIntValue;
    }

    private boolean allowKeyPress(char character) {
        if ((character >= '0' && character <= '9') || character == '.' || (text.isEmpty() && character == '-' && minIntValue < 0)) {
            int indexDot = text.indexOf('.');
            if (indexDot != -1) {
                try {
                    int currentNumBackDigits = text.length() - indexDot - 1;
                    int frontValue = Integer.parseInt(text.substring(0, indexDot));
                    return frontValue < maxIntValue && currentNumBackDigits < numBackDigits && character >= '0' && character <= '9';
                } catch (NumberFormatException invalidFrontValue) {
                    return false;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void keyPressed(char character) {
        if (this.allowKeyPress(character)) {
            super.keyPressed(character);
        }
    }

    @Override
    protected void paste(String clipboardContent) {
        clipboardContent.chars().forEachOrdered(character -> {
            if (this.allowKeyPress((char) character)) {
                this.text += (char) character;
            }
        });
        updateTexture();
    }
}
