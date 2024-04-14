package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitHelper;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.bithelper.ByteArrayBitOutput;
import nl.knokko.customitems.model.Model;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class ModelManager<
        M extends Model<V>, V extends ModelValues, R extends ModelReference<M, V>
        > implements Iterable<V> {

    protected final ItemSet itemSet;
    protected Collection<M> elements = new ArrayList<>();

    protected ModelManager(ItemSet itemSet) {
        this.itemSet = itemSet;
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    protected abstract void saveElement(M element, BitOutput output, ItemSet.Side targetSide);

    public void save(BitOutput output, ExecutorService threadPool, ItemSet.Side targetSide) {
        output.addInt(elements.size());
        List<Future<ByteArrayBitOutput>> allElementsData = new ArrayList<>();
        for (M element : elements) {
            allElementsData.add(threadPool.submit(() -> {
                ByteArrayBitOutput elementData = new ByteArrayBitOutput(10000);
                saveElement(element, elementData, targetSide);
                return elementData;
            }));
        }
        allElementsData.forEach(futureData -> {
            try {
                ByteArrayBitOutput elementData = futureData.get();
                output.addBytes(Arrays.copyOf(elementData.getBackingArray(), elementData.getByteIndex()));
                if (elementData.getBoolIndex() > 0) {
                    boolean[] last = BitHelper.byteToBinary(elementData.getBackingArray()[elementData.getByteIndex()]);
                    for (int index = 0; index < elementData.getBoolIndex(); index++)
                        output.addBoolean(last[index]);
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected <I> void validateUniqueIDs(
            String description, Collection<M> collection, Function<M, I> getID
    ) throws ProgrammingValidationException {
        Set<I> foundIDs = new HashSet<>(collection.size());
        for (M element : collection) {
            I id = getID.apply(element);
            if (foundIDs.contains(id)) throw new ProgrammingValidationException("Duplicate " + description + " " + id);
            foundIDs.add(id);
        }
    }

    protected abstract R createReference(M element);

    protected abstract M loadElement(BitInput input) throws UnknownEncodingException;

    public void load(BitInput input) throws UnknownEncodingException {
        int numElements = input.readInt();
        elements = new ArrayList<>(numElements);
        for (int counter = 0; counter < numElements; counter++) {
            elements.add(loadElement(input));
        }
    }

    public int size() {
        return elements.size();
    }

    public boolean isValid(R reference) {
        M element = reference.getModel();
        if (element == null) return false;
        return elements.contains(element);
    }

    protected abstract void validateExportVersion(
            V element, int mcVersion
    ) throws ValidationException, ProgrammingValidationException;

    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        for (M element : elements) validateExportVersion(element.getValues(), mcVersion);
    }

    protected abstract void validate(V element) throws ValidationException, ProgrammingValidationException;

    public void validate() throws ValidationException, ProgrammingValidationException {
        for (M element : elements) validate(element.getValues());
    }

    protected abstract M checkAndCreateElement(V values) throws ValidationException, ProgrammingValidationException;

    public void add(V values) throws ValidationException, ProgrammingValidationException {
        M toAdd = checkAndCreateElement(values);
        elements.add(toAdd);
        itemSet.maybeCreateBackup();
    }

    protected abstract void validateChange(R reference, V newValues) throws ValidationException, ProgrammingValidationException;

    public void change(R reference, V newValues) throws ValidationException, ProgrammingValidationException {
        if (!isValid(reference)) throw new ProgrammingValidationException("Element to change is invalid");
        validateChange(reference, newValues);
        reference.getModel().setValues(newValues);
        itemSet.maybeCreateBackup();
    }

    public void remove(R reference) throws ValidationException, ProgrammingValidationException {
        M model = reference.getModel();
        if (model == null) throw new ProgrammingValidationException("Model is invalid");
        String errorMessage = null;

        if (!elements.remove(model)) throw new ProgrammingValidationException("Model no longer exists");
        try {
            this.validate();
        } catch (ValidationException | ProgrammingValidationException validation) {
            errorMessage = validation.getMessage();
        }

        if (errorMessage != null) {
            elements.add(model);
            throw new ValidationException(errorMessage);
        }

        itemSet.maybeCreateBackup();
    }

    @Override
    public Iterator<V> iterator() {
        return new ModelManager.CollectionViewIterator<>(elements.iterator());
    }

    public Stream<V> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    public Iterable<R> references() {
        return new ModelManager<M, V, R>.References();
    }

    public void combine(ModelManager<M, ?, ?> primary, ModelManager<M, ?, ?> secondary) throws ValidationException {
        elements.addAll(primary.elements);
        elements.addAll(secondary.elements);
    }

    public void combineUnchecked(ModelManager<?, ?, ?> primary, ModelManager<?, ?, ?> secondary) throws ValidationException {
        //noinspection unchecked
        combine((ModelManager<M, ?, ?>) primary, (ModelManager<M, ?, ?>) secondary);
    }

    private static class CollectionViewIterator<M extends Model<V>, V extends ModelValues> implements Iterator<V> {

        private final Iterator<M> modelIterator;

        CollectionViewIterator(Iterator<M> modelIterator) {
            this.modelIterator = modelIterator;
        }

        @Override
        public boolean hasNext() {
            return modelIterator.hasNext();
        }

        @Override
        public V next() {
            return modelIterator.next().getValues();
        }
    }

    private class References implements Iterable<R> {

        @Override
        public Iterator<R> iterator() {
            return new ModelManager<M, V, R>.ReferenceIterator(elements.iterator());
        }
    }

    private class ReferenceIterator implements Iterator<R> {

        private final Iterator<M> modelIterator;

        private ReferenceIterator(Iterator<M> modelIterator) {
            this.modelIterator = modelIterator;
        }

        @Override
        public boolean hasNext() {
            return modelIterator.hasNext();
        }

        @Override
        public R next() {
            return createReference(modelIterator.next());
        }
    }
}
