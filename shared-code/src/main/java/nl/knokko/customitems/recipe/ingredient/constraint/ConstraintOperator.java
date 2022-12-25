package nl.knokko.customitems.recipe.ingredient.constraint;

public enum ConstraintOperator {

    GREATER_THAN(">"),
    AT_LEAST(">="),
    EQUAL("="),
    AT_MOST("<="),
    SMALLER_THAN("<");

    public final String token;

    ConstraintOperator(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return token;
    }
}
