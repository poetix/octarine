package com.codepoetics.octarine.morphisms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CollectionBijection {

    private CollectionBijection() { }

    public static <A> Bijection<List<A>, Set<A>> listToSet() {
        return Bijection.of(
            HashSet::new,
            ArrayList::new
        );
    }
}
