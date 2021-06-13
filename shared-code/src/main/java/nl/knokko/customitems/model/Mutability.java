package nl.knokko.customitems.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Mutability {

    public static <T extends ModelValues> List<T> createShallowCopy(Collection<T> original) {
        return new ArrayList<>(original);
    }

    public static <T extends ModelValues> Collection<T> createDeepCopy(Collection<T> original, boolean mutable) {
        List<T> result = new ArrayList<>(original.size());

        for (T originalItem : original) {
            result.add(copy(originalItem, mutable));
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ModelValues> T copy(T originalItem, boolean mutable) {
        ModelValues copiedValue = originalItem.copy(mutable);
        if (copiedValue.getClass() == originalItem.getClass()) {
            return (T) copiedValue;
        } else {
            throw new Error("The copy method of " + originalItem.getClass() + " returned an instance of " + copiedValue.getClass());
        }
    }
}
