octarine
========

[![Build Status](https://travis-ci.org/poetix/octarine.svg?branch=master)](https://travis-ci.org/poetix/octarine)

### Manifesto

Octarine is a Java 8 library for working with data at the edges of your system: *records* that are loaded from CSV files, deserialised from JSON messages sent over HTTP, or retrieved from databases.

Traditionally, records in Java have been represented by *Beans*: classes which carry a collection of mutable properties, where each property has a *getter* method to read it, and a *setter* method to write it. Bean objects have no "behaviour", in the sense in which objects in Object-Oriented Programming are said to bundle data and behaviour together. They are *degenerate* objects.

Code that works with Beans is often ugly and repetitive, littered with null-checks (because a property that hasn't been set with a setter can always be null), and hampered by the simultaneous verbosity and inflexibility of the Bean protocol. Unit tests for such code are haunted by a sense of their own pointlessness. Anyone who's spent significant time writing "Enterprise" Java in which the Bean pattern predominates will at some point have asked themselves: "why am I wasting my time with this *crap*?"

There is a better way. Actually there are several better ways; Octarine provides an implementation of one of them.

Octarine favours:

   * **Concision** - end-user code is concise, readable and free of boilerplate.
   * **Tolerance** - code that works with records should be forgiving in what it accepts, using schemas and pattern-matching to select and process records that satisfy its requirements.
   * **Immutability** - records are immutable by default.
   * **Composability** - keys can be composed to form paths into nested records.
   * **Transparency** - no "magic" required - especially tricks with reflection and dynamic proxies.

### Basic concepts

An Octarine ```Record``` is a collection of typed key-value pairs, where the ```Key```s are special objects that carry information about the types of the values. A ```Record``` can contain values for any keys whatsoever, but the types of the values must match the types of the keys.

```java
Key<String> name = Key.named("name");
Key<Integer> age = Key.named("age");

Record person = Record.of(name.of("Dominic"), age.of(39));

String personName = name.extract(person);
Integer personAge = age.extract(person);
```

Here we have defined two keys, ```name``` and ```age```, created a ```Record``` with values defined for both keys, and extracted both values from the record in a type-safe way.

#### Absent values

Because a ```Record``` can contain values for any ```Key``` or none, the safe way to read values is via the ```Key::get``` method, which returns an ```Optional``` value:

```java
Optional<String> personName = name.get(person);
int personAge = age.get(person).orElse(0);
```

You can use the ```Key::test``` method to find out whether a ```Record``` contains a particular ```Key```:

```java
if (name.test(person)) {
  System.out.println(name.extract(person));
}
```

A ```Key<T>``` is both a ```Predicate<Record>``` which tests for the presence of that key in a ```Record```, and a ```Function<Record, Optional<T>>```, which tries to retrieve the corresponding value from the ```Record``` and returns ```Optional.empty()``` if it isn't there. ```Key::extract``` returns the value directly (or throws an exception, if it is absent).

This trio of methods - ```test```, ```extract```, ```apply``` - defines an ```Extractor```, a useful general concept.

#### Extractors and pattern-matching

An ```Extractor<S, T>``` may be seen as a *partial function* from ```S``` to ```T```: it can only "extract" a value of type ```T``` from a value of type ```S``` if such a value is present. Any ```Key<T>``` is an ```Extractor<Record, T>```.

Suppose we have a ```Record``` which we know will contain *either* a person's name and date of birth, *or* their social security number. We can use the fact that ```Key```s are ```Extractor```s to test which is the case and respond accordingly:

```java
public Optional<Person> getPerson(Record details) {
  if (name.test(details) && dob.test(details)) {
    return Optional.of(getPersonByNameAndAge(name.extract(record), dob.extract(record)));
  } else if (ssn.test(record)) {
    return Optional.of(getPersonBySocialSecurityNumber(ssn.extract(record)));
  } else {
    return Optional.empty();
  }
}
```

This is a common enough thing to want to do that Octarine supports using *pattern matching* to pick out records having particular keys and extract their values:

```java
public Optional<Person> getPerson(Record details) {
  Matching<Record, Person> matching = Matching.build(m ->
    m.matching(name, dob, (n, d) -> getPersonByNameAndAge(n, d))
     .matching(ssn, s -> getPersonBySocialSecurityNumber(s))
  );
  return matching.apply(details);
}
```


Each of the patterns described by a ```matching``` call is tried in turn, until one is found where all of the listed extractors match the record. The values targeted
 by those extractors are then handed off to the supplied function. (Octarine supports matching patterns of up to four extractors, with functions of up to four arguments).

#### Immutability and updating

Octarine's ```Record```s are immutable: there is no way to change the key/value pairs of a record once it has been created. However, it is possible to create a *copy* of a ```Record``` with one or more key/value pairs added or removed:

```java
Record person = Record.of(name.of("Dominic"));
Record personWithAge = person.with(age.of(39));
Record personWithoutAName = personWithAge.without(name);
```

Alternatively, ```Key```s themselves act as *lenses*, getting and setting values:

```java
Record person = Record.of(name.of("Dominic"));
Record personWithAge = age.set(person, Optional.of(39));
Record personWithoutAName = name.set(Optional.empty());
```

Lenses have the interesting property that they *compose*, which we'll explore later.

#### Mutability: oh, go on then

If you really want a mutable record, you can have one.

```java
MutableRecord mutable = Record.of(
  Person.name.of("Dominic"),
  Person.age.of(39),
  Person.favouriteColour.of(Color.RED),
  Person.address.of(Address.addressLines.of("13 Rue Morgue", "PO3 1TP"))
).mutable();

mutable.set(Person.age.of(40), Person.favouriteColour.of(Color.GRAY));
mutable.unset(Person.address);

Record immutable = mutable.immutable();
````

Note however that a ```MutableRecord``` is a mutable copy of an immutable ```Record```, and cannot be used to mutate the ```Record``` it has cloned.

```MutableRecords``` remember what they've changed: ```MutableRecord::added``` returns a record containing all added or modified key/value pairs, and ```MutableRecord::removed``` returns a set of all removed keys.

### Quick start

We define keys, schemas, serialisers and deserialisers for two record types, `Person` and `Address`.

```java
public static interface Address {
    static final KeySet mandatoryKeys = new KeySet();
    static final ListKey<String> addressLines = mandatoryKeys.addList("addressLines");
    static final Key<String> postcode = mandatoryKeys.add("postcode");

    Schema<Address> schema = mandatoryKeys::accept;

    JsonDeserialiser reader = i ->
            i.add(addressLines, fromString)
             .add(postcode, fromString);

    JsonSerialiser writer = p ->
            p.add(addressLines, asString)
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
