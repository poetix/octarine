package com.codepoetics.octarine.morphisms;

import org.pcollections.HashTreePSet;
import org.pcollections.PSet;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface FluentCollection<T> {

    public static <T> FluentCollection<T> from(Iterable<T> iterable) {
        return new FluentCollection<T>() {
            @Override
            public Collection<T> toCollection() {
                return
            }
        }
    }
    public static <T> FluentCollection<T> from(Collection<T> collection) {
        return new FluentCollection<T>() {
            @Override public Collection<T> toCollection() { return collection; }
        };
    }

    public static <T> FluentCollection<T> from(List<T> list) {
        return new FluentCollection<T>() {
            @Override public Collection<T> toCollection() { return list; }
            @Override public List<T> toList() { return list; }
        };
    }

    public static <T> FluentCollection<T> from(Set<T> set) {
        return new FluentCollection<T>() {
            @Override public Collection<T> toCollection() { return set; }
            @Override public Set<T> toSet() { return set; }
        };
    }

    public static <T> FluentCollection<T> from(T...array) {
        return new FluentCollection<T>() {
            @Override public T[] toArray() { return array; }
            @Override public Stream<T> toStream() { return Arrays.stream(array); }
            @Override public Collection<T> toCollection() { return Arrays.asList(array); }
            @Override public List<T> toList() { return Arrays.asList(array); }
        };
    }

    public static <T> FluentCollection<T> from(PVector<T> pvector) {
        return new FluentCollection<T>() {
            @Override public Collection<T> toCollection() { return pvector; }
            @Override public PVector<T> toPVector() { return pvector; }
        };
    }

    public static <T> FluentCollection<T> from(PSet<T> pset) {
        return new FluentCollection<T>() {
            @Override public Collection<T> toCollection() { return pset; }
            @Override public PSet<T> toPSet() { return pset; }
        };
    }

    public static <T> FluentCollection<T> from(Stream<T> stream) {
        return new FluentCollection<T>() {
            @Override public T[] toArray() { return (T[]) stream.toArray(); }
            @Override public Collection<T> toCollection() { return toList(); }
            @Override public List<T> toList() { return stream.collect(Collectors.toList()); }
        };
    }

    default Stream<T> toStream() { return toCollection().stream(); }

    default T[] toArray() {
        return (T[]) toCollection().toArray();
    }

    Collection<T> toCollection();

    default List<T> toList() {
        return toStream().collect(Collectors.toList());
    }

    default Set<T> toSet() {
        return toStream().collect(Collectors.toSet());
    }

    default PVector<T> toPVector() {
        return TreePVector.from(toCollection());
    }

    default PSet<T> toPSet() {
        return HashTreePSet.from(toCollection());
    }


}
