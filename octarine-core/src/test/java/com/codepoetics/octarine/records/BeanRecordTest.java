package com.codepoetics.octarine.records;

import com.codepoetics.octarine.functional.extractors.Extractors;
import com.codepoetics.octarine.keys.Key;
import com.codepoetics.octarine.testutils.ARecord;
import org.junit.Test;

import java.time.LocalDate;

import static com.codepoetics.octarine.Octarine.$;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class BeanRecordTest {

    public static class Bean {
        private final String name;
        private final int age;
        private final LocalDate dateOfBirth;

        public Bean(String name, int age, LocalDate dateOfBirth) {
            this.name = name;
            this.age = age;
            this.dateOfBirth = dateOfBirth;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public LocalDate getDateOfBirth() {
            return dateOfBirth;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Bean bean = (Bean) o;

            if (age != bean.age) return false;
            if (!dateOfBirth.equals(bean.dateOfBirth)) return false;
            if (!name.equals(bean.name)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + age;
            result = 31 * result + dateOfBirth.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Bean{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", dateOfBirth=" + dateOfBirth +
                    '}';
        }
    }

    private static final Key<String> name = $("name");
    private static final Key<Integer> age = $("age");
    private static final Key<LocalDate> dob = $("dob", BeanRecord.methodName.of("dateOfBirth"));

    @Test public void
    maps_keys_to_methods() {
        Bean bean = new Bean("Arthur Putey", 23, LocalDate.of(2014, 7, 11));
        Record record = BeanRecord.of(bean, name, age, dob);

        assertThat(record, ARecord.instance()
            .with(name, "Arthur Putey")
            .with(age, 23)
            .with(dob, LocalDate.of(2014, 7, 11)));
    }

    @Test public void
    is_updatable() {
        Bean bean = new Bean("Arthur Putey", 23, LocalDate.of(2014, 7, 11));
        Record record = BeanRecord.of(bean, name, age, dob).with(age.of(34));

        assertThat(record, ARecord.instance()
                .with(name, "Arthur Putey")
                .with(age, 34)
                .with(dob, LocalDate.of(2014, 7, 11)));
    }

    @Test public void
    is_round_trippable() {

        Bean bean = new Bean("Arthur Putey", 23, LocalDate.of(2014, 7, 11));
        Record record = BeanRecord.of(bean, name, age, dob).with(age.of(34));
        Bean newBean = Extractors.join(name, age, dob, Bean::new).extract(record);

        assertThat(newBean.getAge(), equalTo(34));
    }
}
