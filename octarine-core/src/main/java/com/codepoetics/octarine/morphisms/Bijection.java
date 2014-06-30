package com.codepoetics.octarine.morphisms;

import com.codepoetics.octarine.lenses.Lens;

import java.util.function.Function;

public interface Bijection<A, B> extends Function<A, B> {

    static <A, B> Bijection<A, B> of(Function<A, B> in, Function<B, A> out) {
        return new Bijection<A, B>() {
            @Override
            public B apply(A input) {
                return in.apply(input);
            }

            @Override
            public Bijection<B, A> reverse() {
                return Bijection.<B, A>of(out, in);
            }
        };
    }

    Bijection<B, A> reverse();

    default <C> Bijection<A, C> before(Bijection<B, C> next) {
        return of(
                this.andThen(next),
                next.reverse().andThen(this.reverse())
        );
    }

    default <C> Bijection<C, B> after(Bijection<C, A> previous) {
        return previous.before(this);
    }

    default Function<A, A> mapUpdate(Function<B, B> f) {
        Function<B, A> reversed = reverse();
        return a -> reversed.apply(f.apply(apply(a)));
    }

    default A roundTrip(A input) {
        return reverse().apply(apply(input));
    }


    default <T> Lens<T, B> overTarget(Lens<T, A> lens) {
        return Lens.of(
                t -> apply(lens.get(t)),
                (t, v) -> lens.set(t, reverse().apply(v))
        );
    }

    default <V> Lens<A, V> overSource(Lens<B, V> lens) {
        return Lens.of(
                t -> lens.get(apply(t)),
                (t, v) -> reverse().apply(lens.set(apply(t), v))
        );
    }
}
