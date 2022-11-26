package nl.knokko.gui.util;

public interface Condition {
    
    /**
     * Determines whether this condition is true at this moment, or not
     * @return true if this condition evaluates to true, false otherwise
     */
    boolean isTrue();
    
    Condition TRUE = () -> {
    	return true;
    };
    
    Condition FALSE = () -> {
    	return false;
    };
}