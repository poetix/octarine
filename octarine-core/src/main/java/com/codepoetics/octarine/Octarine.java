package com.codepoetics.octarine;

import com.codepoetics.octarine.api.*;
import com.codepoetics.octarine.keys.Keys;
import com.codepoetics.octarine.records.HashRecord;
import com.codepoetics.octarine.validation.api.Schema;
import com.codepoetics.octarine.validation.api.ValidRecordKey;

import java.util.stream.Stream;

/**
 * Entry point for creating keys and records.
 * There is a short "$" syntax, which some people will find convenient and others consider ugly.
 * If you cannot reconcile this with your coding standards then use the longer method names, which are synonyms.
 */
public final class Octarine {
    private Octarine() {
    }

    /**
     * Create a key, with the given name and metadata.
     *
     * @param name     The key name.
     * @param metadata Metadata for the key.
     * @param <T>      The type of value indexed by the key.
     * @return The key.
     */
    public static <T> Key<T> $(String name, Value... metadata) {
        return Keys.simpleKey(name, $$(metadata));
    }

    /**
     * Create a key, with the given name and metadata.
     *
     * @param name     The key name.
     * @param metadata Metadata for the key.
     * @param <T>      The type of value indexed by the key.
     * @return The key.
     */
    public static <T> Key<T> $(String name, Record metadata) {
        return Keys.simpleKey(name, metadata);
    }

    /**
     * Create a list key, with the given name and metadata.
     *
     * @param name     The key name.
     * @param metadata Metadata for the key.
     * @param <T>      The type of the items in the list value indexed by the key.
     * @return The key.
     */
    public static <T> ListKey<T> $L(String name, Value... metadata) {
        return $L(name, $$(metadata));
    }

    /**
     * Create a list key, with the given name and metadata.
     *
     * @param name     The key name.
     * @param metadata Metadata for the key.
     * @param <T>      The type of the items in the list value indexed by the key.
     * @return The key.
     */
    public static <T> ListKey<T> $L(String name, Record metadata) {
        return Keys.listKey(name, metadata);
    }

    /**
     * Create a record key, with the given name and metadata.
     *
     * @param name     The key name.
     * @param metadata Metadata for the key.
     * @return The key.
     */
    public static RecordKey $R(String name, Value... metadata) {
        return $R(name, $$(metadata));
    }

    /**
     * Create a record key, with the given name and metadata.
     *
     * @param name     The key name.
     * @param metadata Metadata for the key.
     * @return The key.
     */
    public static RecordKey $R(String name, Record metadata) {
        return Keys.recordKey(name, metadata);
    }

    /**
     * Create a map key, with the given name and metadata.
     *
     * @param name     The key name.
     * @param metadata Metadata for the key.
     * @param <T>      The type of the values in the map value indexed by the key.
     * @return The key.
     */
    public static <T> MapKey<T> $M(String name, Value... metadata) {
        return $M(name, $$(metadata));
    }

    /**
     * Create a map key, with the given name and metadata.
     *
     * @param name     The key name.
     * @param metadata Metadata for the key.
     * @param <T>      The type of the values in the map value indexed by the key.
     * @return The key.
     */
    public static <T> MapKey<T> $M(String name, Record metadata) {
        return Keys.mapKey(name, metadata);
    }

    /**
     * Create a valid record key, with the given name and metadata.
     *
     * @param name     The key name.
     * @param schema   The schema to validate values indexed by the key against
     * @param metadata Metadata for the key.
     * @param <T>      The type of the schema.
     * @return The key.
     */
    public static <T> ValidRecordKey<T> $V(String name, Schema<T> schema, Value... metadata) {
        return $V(name, schema, $$(metadata));
    }

    /**
     * Create a valid record key, with the given name and metadata.
     *
     * @param name     The key name.
     * @param schema   The schema to validate values indexed by the key against
     * @param metadata Metadata for the key.
     * @param <T>      The type of the schema.
     * @return The key.
     */
    public static <T> ValidRecordKey<T> $V(String name, Schema<T> schema, Record metadata) {
        return Keys.validRecordKey(name, schema, metadata);
    }

    /**
     * Create a new record, containing the supplied values.
     *
     * @param values The values contained in the record.
     * @return The record.
     */
    public static Record $$(Value... values) {
        return HashRecord.of(values);
    }

    /**
     * Create a record extending a source record with additional values.
     *
     * @param source The source record.
     * @param values The additional values.
     * @return The extended record.
     */
    public static Record $$(Record source, Value... values) {
        return source.with(values);
    }

    /**
     * Create a record merging multiple records.
     *
     * @param records The records to merge.
     * @return The merged record.
     */
    public static Record $$(Record... records) {
        return Stream.of(records).reduce(HashRecord.empty(), Record::with);
    }

    /**
     * Create a key, with the given name and metadata.
     *
     * @param name     The key name.
     * @param metadata Metadata for the key.
     * @param <T>      The type of value indexed by the key.
     * @return The key.
     */
    public static <T> Key<T> key(String name, Value... metadata) {
        return $(name, metadata);
    }

    /**
     * Create a key, with the given name and metadata.
     *
     * @param name     The key name.
     * @param metadata Metadata for the key.
     * @param <T>      The type of value indexed by the key.
     * @return The key.
     */
    public static <T> Key<T> key(String name, Record metadata) {
        return $(name, metadata);
    }

    /**
     * Create a list key, with the given name and metadata.
     *
     * @param name     The key name.
     * @param metadata Metadata for the key.
     * @param <T>      The type of the items in the list value indexed by the key.
     * @return The key.
     */
    public static <T> ListKey<T> listKey(String name, Value... metadata) {
        return $L(name, $$(metadata));
    }

    /**
     * Create a list key, with the given name and metadata.
     *
     * @param name     The key name.
     * @param metadata Metadata for the key.
     * @param <T>      The type of the items in the list value indexed by the key.
     * @return The key.
     */
    public static <T> ListKey<T> listKey(String name, Record metadata) {
        return $L(name, metadata);
    }

    /**
     * Create a record key, with the given name and metadata.
     *
     * @param name     The key name.
     * @param metadata Metadata for the key.
     * @return The key.
     */
    public static RecordKey recordKey(String name, Value... metadata) {
        return $R(name, $$(metadata));
    }

    /**
     * Create a record key, with the given name and metadata.
     *
     * @param name     The key name.
     * @param metadata Metadata for the key.
     * @return The key.
     */
    public static RecordKey recordKey(String name, Record metadata) {
        return $R(name, metadata);
    }

    /**
     * Create a map key, with the given name and metadata.
     *
     * @param name     The key name.
     * @param metadata Metadata for the key.
     * @param <T>      The type of the values in the map value indexed by the key.
     * @return The key.
     */
    public static <T> MapKey<T> mapKey(String name, Value... metadata) {
        return $M(name, $$(metadata));
    }

    /**
     * Create a map key, with the given name and metadata.
     *
     * @param name     The key name.
     * @param metadata Metadata for the key.
     * @param <T>      The type of the values in the map value indexed by the key.
     * @return The key.
     */
    public static <T> MapKey<T> mapKey(String name, Record metadata) {
        return $M(name, metadata);
    }

    /**
     * Create a valid record key, with the given name and metadata.
     *
     * @param name     The key name.
     * @param schema   The schema to validate values indexed by the key against
     * @param metadata Metadata for the key.
     * @param <T>      The type of the schema.
     * @return The key.
     */
    public static <T> ValidRecordKey<T> validKey(String name, Schema<T> schema, Value... metadata) {
        return $V(name, schema, $$(metadata));
    }

    /**
     * Create a valid record key, with the given name and metadata.
     *
     * @param name     The key name.
     * @param schema   The schema to validate values indexed by the key against
     * @param metadata Metadata for the key.
     * @param <T>      The type of the schema.
     * @return The key.
     */
    public static <T> ValidRecordKey<T> validKey(String name, Schema<T> schema, Record metadata) {
        return $V(name, schema, metadata);
    }

    /**
     * Create a new record, containing the supplied values.
     *
     * @param values The values contained in the record.
     * @return The record.
     */
    public static Record record(Value... values) {
        return $$(values);
    }

    /**
     * Create a record extending a source record with additional values.
     *
     * @param source The source record.
     * @param values The additional values.
     * @return The extended record.
     */
    public static Record record(Record source, Value... values) {
        return $$(source, values);
    }

    /**
     * Create a record merging multiple records.
     *
     * @param records The records to merge.
     * @return The merged record.
     */
    public static Record record(Record... records) {
        return $$(records);
    }

}
