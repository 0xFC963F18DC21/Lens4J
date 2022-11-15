package net.nergi.lens4j;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This is a specialisation of {@link Lens} for simple usecases.
 *
 * @param <T> The class being viewed.
 * @param <F> The type of the field being viewed.
 */
public final class SimpleLens<T, F> extends Lens<T, T, F, F> {
    /**
     * Create a simple lens.
     *
     * @param accessor Getter function for the object.
     * @param replacer Immutable setter function for the object.
     */
    public SimpleLens(Function<T, F> accessor, BiFunction<F, T, T> replacer) {
        super(accessor, replacer);
    }

    /** Like {@link Lens#andThen}, but only for simple lenses. */
    public <G> SimpleLens<T, G> andThenSimple(SimpleLens<F, G> next) {
        return new SimpleLens<>(t -> next.view(view(t)), (g, t) -> set(next.set(g, view(t)), t));
    }
}
