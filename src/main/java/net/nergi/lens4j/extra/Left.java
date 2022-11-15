package net.nergi.lens4j.extra;

/**
 * The Left type.
 * <p>
 * Usually used to encode failures or errors.
 *
 * @param leftItem Item stored in this left.
 * @param <L> The type of item stored in this left.
 * @param <R> Phantom type used for type-checking the successful result.
 */
public record Left<L, R>(L leftItem) implements Either<L, R> {
    @Override
    public L fromLeft() {
        return leftItem;
    }

    @Override
    public R fromRight() {
        throw new ClassCastException("This is a Left!");
    }

    @Override
    public boolean isLeft() {
        return true;
    }

    @Override
    public boolean isRight() {
        return false;
    }
}
