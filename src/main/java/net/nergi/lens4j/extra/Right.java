package net.nergi.lens4j.extra;

/**
 * The Right type.
 * <p>
 * Usually used to store the results of successful computations.
 *
 * @param rightItem Item stored in this right.
 * @param <L> Phantom type used for type-checking the error result.
 * @param <R> Type of item stored in this right.
 */
public record Right<L, R>(R rightItem) implements Either<L, R> {
    @Override
    public L fromLeft() {
        throw new ClassCastException("This is a Right!");
    }

    @Override
    public R fromRight() {
        return rightItem;
    }

    @Override
    public boolean isLeft() {
        return false;
    }

    @Override
    public boolean isRight() {
        return true;
    }
}
