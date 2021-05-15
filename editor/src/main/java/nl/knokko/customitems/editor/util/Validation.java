package nl.knokko.customitems.editor.util;

import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

public class Validation {

    public static String toErrorString(ValidationFunction validationFunction) {
        try {
            validationFunction.validate();
            return null;
        } catch (ValidationException validationError) {
            return validationError.getMessage();
        } catch (ProgrammingValidationException programmingError) {
            return "Programming error: " + programmingError.getMessage();
        }
    }

    @FunctionalInterface
    public interface ValidationFunction {

        void validate() throws ValidationException, ProgrammingValidationException;
    }
}
