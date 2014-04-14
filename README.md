octarine
========

[![Build Status](https://travis-ci.org/poetix/octarine.svg?branch=master)](https://travis-ci.org/poetix/octarine)

Java 8 magic for record types.

###Quick start

```java
public static interface Person {
        static final KeySet mandatoryKeys = new KeySet();
        static final Key<String> name = mandatoryKeys.add("name");
        static final Key<Integer> age = mandatoryKeys.add("age");

        Schema<Person> schema = (r, v) -> {
            mandatoryKeys.accept(r, v);
            age.from(r).ifPresent(a -> {
              if (a < 0) v.accept("Age must be 0 or greater");
            });
        };

        JsonDeserialiser reader = i ->
                i.add(name, fromString)
                 .add(age, fromInteger);

        JsonSerialiser writer = p ->
            p.add(name, JsonSerialiser.asString)
             .add(age, JsonSerialiser.asInteger);
    }
```

```java
@Test public void
    deserialise_validate_update_serialise() {
        Record person = Person.reader.readFromString("{\"name\": \"Arthur Putey\", \"age\": 42}");

        assertThat(person, ARecord.validAgainst(Person.schema)
            .with(Person.name, "Arthur Putey")
            .with(Person.age, 42));

        Valid<Person> validPerson = Person.schema.validate(person).get();

        Record changed = Person.age.update(validPerson, age -> age.map(a -> a + 1));

        assertThat(Person.writer.toString(changed), equalTo("{\"name\": \"Arthur Putey\", \"age\": 43}"));
    }
```

###What this is for

Octarine's essential premise is as follows: *Java beans suck*. They suck because they're verbose, because they're mutable, and because they wrap a faux-"Object-oriented" pattern around record types with no meaningful encapsulation and no behaviour besides exposing properties.

The only thing that sucks more than Java beans is Java beans with annotations. JPA annotations. JAXB annotations. Annotations which tell frameworks how to pull data out of Java beans, or push data into them.

Octarine provides you with lensable, immutable record types, that can be serialised, deserialised and validated without the use of reflection or annotations.
