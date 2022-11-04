package net.nergi.lens4j;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * The Lens.
 * <p>
 * A relatively useful object for manipulating immutable data. It provides a composable view into a nested object,
 * which helps eliminate chained calls to getters of the fields of said object.
 * <p>
 * It also provides a way to regenerate fresh instances of the object with new field contents, allowing for fast
 * "immutable modification" of an immutable object.
 *
 * @param <T> The class being viewed.
 * @param <F> The type of the field being viewed.
 */
public final class Lens<T, F> {
    /** The accessor function to a field of a class. */
    private final Function<T, F> accessor;

    /** The function that generates new instances of a class given "new" contents of a field. */
    private final BiFunction<F, T, T> replacer;

    /**
     * Create a lens from an accessor and builder (dubbed "replacer") for new instances of the class with the field set
     * to some given objects.
     * <p>
     * For the lens to work correctly, the following laws need to be satisfied:
     * <ol>
     *     <li><code>accessor</code> must be pure.</li>
     *     <li><code>replacer</code> must not modify the object being operated on.</li>
     * </ol>
     *
     * @param accessor Function that provides a view into a field.
     * @param replacer Function that generates a new instance of the class with the field set to the new value.
     */
    public Lens(Function<T, F> accessor, BiFunction<F, T, T> replacer) {
        this.accessor = accessor;
        this.replacer = replacer;
    }

    /**
     * View the field in a given class instance.
     *
     * @param instance Instance to peek the field contents of.
     * @return Field contents of instance.
     */
    public F view(T instance) {
        return accessor.apply(instance);
    }

    /**
     * Map over the field (immutably) in a given class instance.
     *
     * @param mapper Mapping function, which is a unary operator.
     * @param instance Instance to map over.
     * @return New instance of class with mapped field contents.
     */
    public T over(UnaryOperator<F> mapper, T instance) {
        return replacer.apply(mapper.apply(view(instance)), instance);
    }

    /**
     * Set the field (immutably) in a given class instance.
     *
     * @param value New value to give the field.
     * @param instance Instance to replace the field contents of.
     * @return New instance of class with replaced field contents.
     */
    public T set(F value, T instance) {
        return replacer.apply(value, instance);
    }

    /**
     * Run another lens after this one to form a new, combined lens.
     * <p>
     * This is the bread-and-butter for what makes lenses extremely useful. They can be composed together as if they
     * were functions.
     *
     * @param next The next lens to run.
     * @return The combined lens formed by composing the two lenses together, this lens before the next.
     * @param <O> The type of the field being manipulated inside the original field.
     */
    public <O> Lens<T, O> andThen(Lens<F, O> next) {
        return new Lens<>(t -> next.view(view(t)), (o, t) -> set(next.set(o, view(t)), t));
    }
}
