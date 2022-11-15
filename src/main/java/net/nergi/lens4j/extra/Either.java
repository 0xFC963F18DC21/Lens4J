package net.nergi.lens4j.extra;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The Either sum type, for Java 17+.
 * <p>
 * This sum type is typically used for handling computations that can fail, where some error information will be
 * propagated outwards. Conventionally, {@link Left} is used to encode failures, and {@link Right} is used to
 * encode successful computations.
 * <p>
 * As a consequence, most "continuation" methods for this class will short circuit when encountering a {@link Left}.
 *
 * @param <L> The left type, usually representative of some kind of error.
 * @param <R> The right type, usually representative of the result of a successful computation.
 */
public sealed interface Either<L, R> permits Left, Right {
    /**
     * Unsafely extracts an item from a {@link Left}.
     *
     * @return The item, if this was a left.
     * @throws ClassCastException If this is a {@link Right}.
     */
    L fromLeft();

    /**
     * Unsafely extracts an item from a {@link Right}.
     *
     * @return The item, if this was a right.
     * @throws ClassCastException If this is a {@link Left}.
     */
    R fromRight();

    /** Checks if this is a {@link Left}. */
    boolean isLeft();

    /** Checks if this is a {@link Right}. */
    boolean isRight();

    /**
     * Create a new {@link Left} from some item.
     *
     * @param item Item to put into the left.
     * @return The left instance created.
     * @param <L> The type of the item put into the left.
     * @param <R> Phantom type used for type-checking the successful result.
     */
    static <L, R> Left<L, R> toLeft(L item) {
        return new Left<>(item);
    }

    /**
     * Create a new {@link Right} from some item.
     *
     * @param item Item to put into the right.
     * @return The right instance created.
     * @param <L> Phantom type used for type-checking the error result.
     * @param <R> The type of the item put into the right.
     */
    static <L, R> Right<L, R> toRight(R item) {
        return new Right<>(item);
    }

    /**
     * An alias of {@link #toRight} to comply with Applicative rules.
     */
    static <L, R> Right<L, R> pure(R item) {
        return toRight(item);
    }

    /**
     * An implementation for Alternative, where {@link Left} is discarded for an {@link Right}.
     * <p>
     * Think <code>&&</code>, but not for booleans.
     *
     * @param other The alternative either to consider.
     * @return The first {@link Right} instance in <code>this.or(other)</code>, but the rightmost {@link Left} otherwise.
     */
    default Either<L, R> or(Either<L, R> other) {
        return (this instanceof Left<L, R>) ? other : this;
    }

    /**
     * Alternate version of {@link #or} that is lazy on the argument.
     * <p>
     * Giving a zero-ary lambda as the argument is the recommended use of this function.
     */
    default Either<L, R> lazyOr(Supplier<Either<L, R>> other) {
        return (this instanceof Left<L, R>) ? other.get() : this;
    }

    /**
     * An implementation for Functor, where one can immutably map over the value stored in an either instance, but only
     * if it is a {@link Right}.
     *
     * @param mapper Function to map over the item with.
     * @return A new either instance with the map result if this is a {@link Right}. Otherwise, returns <code>this</code>.
     * @param <S> New type stored in mapped {@link Right} instance.
     */
    default <S> Either<L, S> map(Function<? super R, ? extends S> mapper) {
        if (this instanceof Left<L, R> left) {
            return new Left<>(left.leftItem());
        }

        // Must be a Right.
        final Right<L, R> right = (Right<L, R>) this;
        return new Right<>(mapper.apply(right.rightItem()));
    }

    /**
     * An implementation for Monad, where one can sequence either-producing functions together.
     *
     * @param mapper Function that produces new either instances from an item.
     * @return The result of the function if this is a {@link Right}. Otherwise, returns <code>this</code>.
     * @param <S> New type stored in fresh {@link Right} instance.
     */
    default <S> Either<L, S> flatMap(Function<? super R, ? extends Either<L, S>> mapper) {
        if (this instanceof Left<L, R> left) {
            return new Left<>(left.leftItem());
        }

        // Must be a right.
        final Right<L, R> right = (Right<L, R>) this;
        return mapper.apply(right.rightItem());
    }
}
