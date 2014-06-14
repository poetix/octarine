package com.codepoetics.octarine.paths;

import com.codepoetics.octarine.extractors.Extractor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface Path<T, V> extends Extractor.FromOptionalFunction<T, V> {

    interface Indexed<I, T, V> extends Path<T, V> {
        I index();
        default public void describe(StringBuilder sb) {
            sb.append("[").append(index()).append("]");
        }
    }

    interface Named<T, V> extends Path<T, V> {
        String name();
        default public void describe(StringBuilder sb) {
            if (sb.length() > 0) { sb.append("."); }
            sb.append(name());
        }
    }

    static <T, V> Path<T, V> to(Function<T, Optional<V>> f, String name) {
        return new Named<T, V>() {
            @Override
            public String name() { return name; }

            @Override
            public Optional<V> apply(T t) {
                return f.apply(t);
            }
        };
    }

    static <V> Path<List<V>, V> toIndex(int index) {
        return new Indexed<Integer, List<V>, V>() {
            @Override
            public Integer index() { return index; }

            @Override
            public Optional<V> apply(List<V> vs) {
                return index < vs.size() ? Optional.ofNullable(vs.get(index)) : Optional.empty();
            }
        };
    }

    static <K, V> Indexed<String, Map<K, V>, V> toKey(K key) {
        return new Indexed<String, Map<K, V>, V>() {
            @Override
            public String index() { return "'" + key.toString() + "'"; }

            @Override
            public Optional<V> apply(Map<K, V> kvMap) {
                return Optional.ofNullable(kvMap.get(key));
            }
        };
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
}
