package nl.knokko.customitems.util;

import nl.knokko.customitems.itemset.ItemSet;

public class Validation {

    public static void scope(
            String scopeName, ValidationFunction validationFunction
    ) throws ValidationException, ProgrammingValidationException {
        try {
            validationFunction.validate();
        } catch (ValidationException ex) {
            throw new ValidationException(scopeName + ": " + ex.getMessage());
        } catch (ProgrammingValidationException ex) {
            throw new ProgrammingValidationException(scopeName + ":  " + ex.getMessage());
        }
    }

    public static void scope(
            String scopeName, ValidationFunction2 validationFunction, ItemSet itemSet
    ) throws ValidationException, ProgrammingValidationException {
        scope(scopeName, () -> validationFunction.validate(itemSet));
    }

    @FunctionalInterface
    public interface ValidationFunction {
        void validate() throws ValidationException, ProgrammingValidationException;
    }

    @FunctionalInterface
    public interface ValidationFunction2 {
        void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException;
    }

    public static void safeName(String safeName) throws ValidationException, ProgrammingValidationException {
        if (safeName == null) throw new ProgrammingValidationException("No name");
        if (safeName.isEmpty()) throw new ValidationException("Name is empty");
        for (int index = 0; index < safeName.length(); index++) {
            char currentChar = safeName.charAt(index);
            if (currentChar >= 'A' && currentChar <= 'Z') {
                throw new ValidationException("The name can't contain uppercase characters");
            }
            if (!(currentChar >= 'a' && currentChar <= 'z') && !(currentChar >= '0' && currentChar <= '9') && currentChar != '_') {
                throw new ValidationException("The name contains the forbidden character '" + currentChar + "'");
            }
        }
    }
}
