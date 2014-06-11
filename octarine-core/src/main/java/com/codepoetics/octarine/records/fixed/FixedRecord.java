package com.codepoetics.octarine.records.fixed;

import com.codepoetics.octarine.morphisms.FluentCollection;
import com.codepoetics.octarine.morphisms.FluentMap;
import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.records.Value;
import org.pcollections.PMap;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class FixedRecord implements Record {

    public static Record of(List<Value> values) {
        return of(FluentCollection.from(values).toStream());
    }

    public static Record of(Value...values) {
        return of(FluentCollection.from(values).toStream());
    }

    public static Record of(Stream<Value> values) {
        return of(FluentMap.<Key<?>, Object>from(values.map(Value::toPair)).toPMap());
    }

    public static Record of(PMap<Key<?>, Object> values) {
        Set<Key<?>> keys = values.keySet();
        KeyIndex index = KeyIndex.on(keys);
        return new FixedRecord(index.arrayFrom(values), index);
    }

    private final Object[] values;
    private final KeyIndex index;
    private FixedRecord(Object[] values, KeyIndex index) {
        this.values = values;
        this.index = index;
    }

    @Override
    public <T> Optional<T> get(Key<T> key) {
        Optional<Integer> keyIndex = index.lookup(key);
        return keyIndex.map(i -> (T) values[i]);
    }

    @Override
    public PMap<Key<?>, Object> values() {
        return index.valuesFrom(values);
    }


    @Override
    public String toString() {
        return Record.toString(this);
    }

    @Override
    public int hashCode() { return Record.hashCode(this); }

    @Override
    public boolean equals(Object other) {
        if (other == null) { return false; }
        if (other instanceof FixedRecord) {
            if (Objects.equals(values, ((FixedRecord) other).values) &&
                    Objects.equals(index, ((FixedRecord) other).index)) {
                return true;
            }
        }
        return Record.equals(this, other);
    }
}
