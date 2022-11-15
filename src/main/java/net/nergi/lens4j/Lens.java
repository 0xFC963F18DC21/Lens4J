package net.nergi.lens4j;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * The Lens.
 * <p>
 * A relatively useful object for manipulating immutable data. It provides a composable view into a nested object,
 * which helps eliminate chained calls to getters of the fields of said object.
 * <p>
 * It also provides a way to regenerate fresh instances of the object with new field contents, allowing for fast
 * "immutable modification" of an immutable object.
 *
 * @param <S> The class being viewed.
 * @param <T> The projected class actually being viewed.
 * @param <A> The field being viewed.
 * @param <B> The projected type of the field actually being viewed.
 */
public sealed class Lens<S, T, A, B> permits SimpleLens {
    /** The accessor function to a field of a class. */
    private final Function<S, A> accessor;

    /** The function that generates new instances of a class given "new" contents of a field. */
    private final BiFunction<B, S, T> replacer;

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
    public Lens(Function<S, A> accessor, BiFunction<B, S, T> replacer) {
        this.accessor = accessor;
        this.replacer = replacer;
    }

    /**
     * View the field in a given class instance.
     *
     * @param instance Instance to peek the field contents of.
     * @return Field contents of instance.
     */
    public A view(S instance) {
        return accessor.apply(instance);
    }

    /**
     * Map over the field (immutably) in a given class instance.
     *
     * @param mapper Mapping function, which is a unary operator.
     * @param instance Instance to map over.
     * @return New instance of class with mapped field contents.
     */
    public T over(Function<A, B> mapper, S instance) {
        return replacer.apply(mapper.apply(view(instance)), instance);
    }

    /**
     * Set the field (immutably) in a given class instance.
     *
     * @param value New value to give the field.
     * @param instance Instance to replace the field contents of.
     * @return New instance of class with replaced field contents.
     */
    public T set(B value, S instance) {
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
     * @param <C> Next real field type.
     * @param <D> Next projected field type.
     */
    public <C, D> Lens<S, T, C, D> andThen(Lens<A, B, C, D> next) {
        return new Lens<>(s -> next.view(view(s)), (d, s) -> set(next.set(d, view(s)), s));
    }
}
