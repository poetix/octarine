package com.codepoetics.octarine.functional.paths;

import com.codepoetics.octarine.functional.extractors.Extractor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface Path<T, V> extends Extractor.FromPartial<T, V> {

    static <T, V> Named<T, V> to(Function<T, Optional<V>> f, String name) {
        return new Named<>(name, f);
    }

    static <V> Indexed<Integer, List<V>, V> toIndex(int index) {
        return new Indexed<>(index, vs -> index < vs.size() ? Optional.ofNullable(vs.get(index)) : Optional.empty());
    }

    static <K, V> Indexed<K, Map<K, V>, V> toKey(K key) {
        return new Indexed<>(key, kvMap -> Optional.ofNullable(kvMap.get(key)));
    }

    void describe(StringBuilder sb);

    default String describe() {
        StringBuilder sb = new StringBuilder();
        describe(sb);
        return sb.toString();
    }

    default <V2> Path<T, V2> join(Path<? super V, V2> next) {
        return new Path<T, V2>() {
            @Override
            public void describe(StringBuilder sb) {
                Path.this.describe(sb);
                next.describe(sb);
            }

            @Override
            public Optional<V2> apply(T t) {
                Optional<V> v = Path.this.apply(t);
                if (v.isPresent()) {
                    return next.apply(v.get());
                }
                return Optional.empty();
            }
        };
    }

    final static class Indexed<I, T, V> implements Path<T, V> {
        private final I index;
        private final Function<T, Optional<V>> f;

        private Indexed(I index, Function<T, Optional<V>> f) {
            this.index = index;
            this.f = f;
        }

        public I index() {
            return index;
        }

        public Optional<V> apply(T value) {
            return f.apply(value);
        }

        @Override
        public void describe(StringBuilder sb) {
            sb.append("[").append(index).append("]");
        }
    }

    final static class Named<T, V> implements Path<T, V> {
        private final String name;
        private final Function<T, Optional<V>> f;

        private Named(String name, Function<T, Optional<V>> f) {
            this.name = name;
            this.f = f;
        }

        public String name() {
            return name;
        }

        @Override
        public Optional<V> apply(T value) {
            return f.apply(value);
        }

        @Override
        public void describe(StringBuilder sb) {
            if (sb.length() > 0) {
                sb.append(".");
            }
            sb.append(name);
        }
    }
}
