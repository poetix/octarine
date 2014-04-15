octarine
========

[![Build Status](https://travis-ci.org/poetix/octarine.svg?branch=master)](https://travis-ci.org/poetix/octarine)

Java 8 magic for record types.

###Quick start

We define keys, schemas, serialisers and deserialisers for two record types, `Person` and `Address`.

```java
    public static interface Address {
        static final KeySet mandatoryKeys = new KeySet();
        static final ListKey<String> addressLines = mandatoryKeys.addList("addressLines");
        static final Key<String> postcode = mandatoryKeys.add("postcode");

        Schema<Address> schema = mandatoryKeys::accept;

        JsonDeserialiser reader = i ->
                i.add(addressLines, i.fromList(fromString))
                 .add(postcode, fromString);

        JsonSerialiser writer = p ->
                p.add(addressLines, p.asList(asString))
                 .add(postcode, asString);
    }
```

```java
    public static interface Person {
        static final KeySet mandatoryKeys = new KeySet();
        static final Key<String> name = mandatoryKeys.add("name");
        static final Key<Integer> age = mandatoryKeys.add("age");
        static final RecordKey address = mandatoryKeys.addRecord("address");

        Schema<Person> schema = (r, v) -> {
            mandatoryKeys.accept(r, v);
            age.from(r).ifPresent(a -> { if (a < 0) v.accept("Age must be 0 or greater"); });
            address.from(r).ifPresent(a -> Address.schema.accept(a, v));
        };

        JsonDeserialiser reader = i ->
                i.add(name, fromString)
                 .add(age, fromInteger)
                 .add(address, Address.reader);

        JsonSerialiser writer = p ->
            p.add(name, asString)
             .add(age, asInteger)
             .add(address, Address.writer);
    }
```

We can now read a `Person`'s details from Json, validate the record against the schema and make test assertions against it, created an updated copy with one value changed, and write the updated copy back out to Json.

```java
@Test public void
    deserialise_validate_update_serialise() {
        Record record = Person.reader.readFromString(
                "{\"name\": \"Arthur Putey\",\n" +"" +
                " \"age\": 42,\n" +
                " \"address\": {\n" +
                "   \"addressLines\": [\"59 Broad Street\", \"Cirencester\"],\n" +
                "   \"postcode\": \"RA8 81T\"\n" +
                "  }\n" +
                "}");

        assertThat(record, ARecord.validAgainst(Person.schema)
                .with(Person.name, "Arthur Putey")
                .with(Person.age, 42)
                        // Chaining keys
                .with(Person.address.join(Address.addressLines).join(Path.toIndex(0)), "59 Broad Street")
                        // Using a sub-matcher
                .with(Person.address, ARecord.validAgainst(Address.schema).with(Address.postcode, "RA8 81T")));

        Record changed = Person.age.update(
            record, age -> age.map(v -> v + 1));

        assertThat(Person.writer.toString(changed), equalTo(
                "{\"name\":\"Arthur Putey\"," +"" +
                        "\"age\":43," +
                        "\"address\":{" +
                        "\"addressLines\":[\"59 Broad Street\",\"Cirencester\"]," +
                        "\"postcode\":\"RA8 81T\"" +
                        "}}"));
    }
```

###What this is for

Octarine's essential premise is as follows: *Java beans suck*. They suck because they're verbose, because they're mutable, and because they wrap a faux-"Object-oriented" pattern around record types with no meaningful encapsulation and no behaviour besides exposing properties.

The only thing that sucks more than Java beans is Java beans with annotations. JPA annotations. JAXB annotations. Annotations which tell frameworks how to pull data out of Java beans, or push data into them.

Octarine provides you with lensable, immutable record types, that can be serialised, deserialised and validated without the use of reflection or annotations.
